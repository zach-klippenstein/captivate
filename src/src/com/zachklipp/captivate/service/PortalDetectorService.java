package com.zachklipp.captivate.service;

import com.zachklipp.captivate.BuildConfig;
import com.zachklipp.captivate.Preferences;
import com.zachklipp.captivate.app.ConnectedNotification;
import com.zachklipp.captivate.captive_portal.HttpResponseContentsDetector;
import com.zachklipp.captivate.captive_portal.PortalDetector;
import com.zachklipp.captivate.captive_portal.PortalDetector.OverrideMode;
import com.zachklipp.captivate.captive_portal.PortalInfo;
import com.zachklipp.captivate.state_machine.PortalStateMachine;
import com.zachklipp.captivate.state_machine.PortalStateMachine.State;
import com.zachklipp.captivate.state_machine.PortalStateMachine.StorageBackendFactory;
import com.zachklipp.captivate.state_machine.PortalStateMachineStorageBackend;
import com.zachklipp.captivate.state_machine.StateMachineStorage;
import com.zachklipp.captivate.state_machine.StateMachineStorage.StorageBackend;
import com.zachklipp.captivate.state_machine.TransitionEvent;
import com.zachklipp.captivate.util.Log;
import com.zachklipp.captivate.util.Observable;
import com.zachklipp.captivate.util.Observer;
import com.zachklipp.captivate.util.WifiHelper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class PortalDetectorService extends StickyIntentService
  implements Observer<TransitionEvent>
{
  private static final String STRING_NAMESPACE = "com.zachklipp.captivate.";
  private static final String INTENT_NAMESPACE = STRING_NAMESPACE + "intent.";
  
  /**
   * If set to true when starting service, the actual state of any wifi connection isn't checked.
   */
  static final String EXTRA_ASSUME_WIFI_CONNECTED = INTENT_NAMESPACE + "EXTRA_ASSUME_WIFI_CONNECTED";
  
  /**
   * Permission required to receive broadcast intents when the portal state changes.
   */
  public static final String PERMISSION_ACCESS_PORTAL_STATE = STRING_NAMESPACE + "permission.ACCESS_PORTAL_STATE";
  
  /**
   * Action for intents broadcast when portal state changes.
   */
  public static final String ACTION_PORTAL_STATE_CHANGED = INTENT_NAMESPACE + "ACTION_PORTAL_STATE_CHANGED";
  
  public static final String EXTRA_PORTAL_STATE = INTENT_NAMESPACE + "EXTRA_PORTAL_STATE";
  public static final String EXTRA_PORTAL_URL = INTENT_NAMESPACE + "EXTRA_PORTAL_URL";
  public static final String EXTRA_FAVICON_URL = INTENT_NAMESPACE + "EXTRA_FAVICON_URL";
  
  //private static PortalDetector sSeedPortalDetector = HttpResponseContentsDetector.createDetector();
  private static PortalDetector.Factory sPortalDetectorFactory
    = new PortalDetector.Factory()
  {
    @Override
    public PortalDetector create()
    {
      return HttpResponseContentsDetector.createDetector();
    }
  };

  private static StorageBackendFactory sStorageBackendFactory
    = new StorageBackendFactory()
  {
    @Override
    public StorageBackend create(Context context, PortalDetector detector)
    {
      return new PortalStateMachineStorageBackend(context, detector);
    }
  };
  
  /*
   * Set the detector to be used when the service is next started.
   */
  public static void setPortalDetectorFactory(PortalDetector.Factory factory)
  {
    sPortalDetectorFactory = factory;
  }
  
  /*
   * Set the storage backend to be used when the service is next started.
   */
  public static void setStorageBackendFactory(StorageBackendFactory factory)
  {
    sStorageBackendFactory = factory;
  }
  
  public static ComponentName startService(Context context)
  {
    return context.startService(createStartServiceIntent(context));
  }
  
  /*
   * Helper to send the necessary intent to start this service.
   */
  public static ComponentName startService(Context context, boolean assumeWifiConnected)
  {
    Intent intent = createStartServiceIntent(context);
    
    intent.putExtra(EXTRA_ASSUME_WIFI_CONNECTED, assumeWifiConnected);
    
    return context.startService(intent);
  }
  
  private Preferences mPreferences;
  private PortalDetector mPortalDetector;
  private PortalStateMachine mStateMachine;
  private AlarmManager mAlarmManager;
  private boolean mSessionTimeoutCheckEnabled = false;
  private PendingIntent mSessionTimeoutCheckPendingIntent;
  
  private final BroadcastReceiver mScreenOnReceiver = new BroadcastReceiver()
  {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        context.startService(createStartServiceIntent(context));
    }
  };
  
  /**
   * @see Preferences.SIGNIN_CHECK_SECONDS_DEFAULT
   */
  private final Runnable mSigninCheckRunnable = new Runnable()
  {
    @Override
    public void run()
    {
      // Called from a handler on this service's worker thread, so we don't
      // need to call startService().
      onHandleIntent(createStartServiceIntent(PortalDetectorService.this));
    }
  };

  public PortalDetectorService()
  {
    super("PortalMonitorThread");
  }
  
  @Override
  public void onCreate()
  {
    mPreferences = Preferences.getPreferences(this);
    
    mPortalDetector = sPortalDetectorFactory.create();
    assert(mPortalDetector != null);
    
    Log.d("Using portal detector " + mPortalDetector.getClass().getName());
    
    mStateMachine = (PortalStateMachine) new StateMachineStorage(
        sStorageBackendFactory.create(getApplicationContext(), mPortalDetector))
    .loadOrCreate();
    
    mStateMachine.addObserver(this);
    
    mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    mSessionTimeoutCheckPendingIntent = PendingIntent.getService(
        this, 0, createStartServiceIntent(this), PendingIntent.FLAG_CANCEL_CURRENT);
    
    // Called at end so thread doesn't get started before we're initialized
    super.onCreate();
  }
  
  @Override
  protected void onHandleIntent(Intent intent)
  {
    boolean assumeWifiConnected = BuildConfig.DEBUG;
    
    if (mPreferences.isEnabled())
    {
      if (null != intent && intent.hasExtra(EXTRA_ASSUME_WIFI_CONNECTED))
      {
        assumeWifiConnected = intent.getBooleanExtra(EXTRA_ASSUME_WIFI_CONNECTED, assumeWifiConnected);
      }
      
      checkForPortal(assumeWifiConnected);
    }
    else
    {
      Log.d("Service started, but disabled, so doing nothing.");
      mStateMachine.onDisabled();
    }
  }
  
  private void checkForPortal(boolean assumeWifiConnected)
  {
    updateDetectorOverrideFromPrefs();
    
    Log.d("Service updating portal status...");
    
    if (assumeWifiConnected || WifiHelper.isWifiConnectedFromContext(this))
    {
      if (assumeWifiConnected)
      {
        Log.d("Intent suggests wifi is connected, going with that");
      }
      
      mPortalDetector.checkForPortal();
      
      scheduleSigninCheckIfBlocked();
    }
    else
    {
      Log.d("Wifi not connected, reporting no portal");
      mStateMachine.onNoWifi();
    }
    
    // If there's no portal, we aren't checking anything periodically so
    // we can shutdown.
    if (!mStateMachine.getCurrentPortalState().isBehindPortal())
    {
      Log.d("Not behind a portal, stopping detector service");
      stopSelf();
    }
  }
  
  @Override
  public void update(Observable<TransitionEvent> observable, TransitionEvent event)
  {
    scheduleSessionTimeoutCheckIfNecessary();
    updateNotification();
    sendStateChangedBroadcast();
  }
  
  private void updateDetectorOverrideFromPrefs()
  {
    mPortalDetector.setPortalOverride(
        mPreferences.isDebugOverrideEnabled() ? OverrideMode.ALWAYS_DETECT : OverrideMode.NONE);
  }
  
  private void scheduleSessionTimeoutCheckIfNecessary()
  {
    if (mStateMachine.getCurrentPortalState().isBehindPortal())
    {
      registerSessionTimeoutCheckReceiver();
    }
    else
    {
      unregisterSessionTimeoutCheckReceiver();
    }
  }
  
  private void scheduleSigninCheckIfBlocked()
  {
    int refreshInterval = mPreferences.getSigninCheckSeconds();
    
    if (mStateMachine.getCurrentPortalState().isBlocked())
    {
      Log.i("Scheduling state refresh in %d seconds", refreshInterval);
      
      getHandler().postDelayed(mSigninCheckRunnable, refreshInterval * 1000);
    }
  }
  
  @Override
  public void onDestroy()
  {
    super.onDestroy();
    
    unregisterSessionTimeoutCheckReceiver();
    
    Log.d("PortalDetectorService destroyed.");
  }
  
  private static Intent createStartServiceIntent(Context context)
  {
    return new Intent(context, PortalDetectorService.class);
  }
  
  private void updateNotification()
  {
    if (State.SIGNIN_REQUIRED == mStateMachine.getCurrentState())
    {
      ConnectedNotification.showNotification(this, mPortalDetector.getPortal());
    }
    else
    {
      ConnectedNotification.hideNotification(this);
    }
  }
  
  private void sendStateChangedBroadcast()
  {
    State state = (State) mStateMachine.getCurrentState();
    PortalInfo portal = mPortalDetector.getPortal();
    
    Intent intent = createStateChangedBroadcastIntent(state, portal);
    
    Log.i("Broadcasting portal state change. new state=%s, portal=%s", state, portal);
    
    sendBroadcast(intent, PERMISSION_ACCESS_PORTAL_STATE);
  }
  
  private static Intent createStateChangedBroadcastIntent(State state, PortalInfo portal)
  {
    Intent intent = new Intent(PortalDetectorService.ACTION_PORTAL_STATE_CHANGED);
    
    intent.putExtra(PortalDetectorService.EXTRA_PORTAL_STATE, state.getName());
    
    if (portal != null)
    {
      portal.saveToIntent(intent);
    }
    
    return intent;
  }
  
  private void registerSessionTimeoutCheckReceiver()
  {
    if (!mSessionTimeoutCheckEnabled)
    {
      mSessionTimeoutCheckEnabled = true;

      IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
      registerReceiver(mScreenOnReceiver, filter);
  
      int intervalSeconds = mPreferences.getSessionTimeoutCheckMinutes();
      long intervalMillis = intervalSeconds * 1000;
      
      mAlarmManager.setRepeating(
          AlarmManager.ELAPSED_REALTIME, intervalMillis, intervalMillis,
          mSessionTimeoutCheckPendingIntent);
      
      Log.d("Checking for session timeout whenever screen is turned on and every %d seconds",
          intervalSeconds);
    }
  }
  
  private void unregisterSessionTimeoutCheckReceiver()
  {
    if (mSessionTimeoutCheckEnabled)
    {
      try
      {
        unregisterReceiver(mScreenOnReceiver);
      }
      catch (Exception e)
      {
        Log.w("Error unregistering for screen on broadcast", e);
      }
      
      try
      {
        mAlarmManager.cancel(mSessionTimeoutCheckPendingIntent);
      }
      catch (Exception e)
      {
        Log.w("Error cancelling session timeout check alarm", e);
      }
      
      mSessionTimeoutCheckEnabled = false;
      
      Log.d("No longer periodically checking for session timeout");
    }
  }
}
