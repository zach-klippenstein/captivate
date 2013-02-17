package com.zachklipp.captivate.service;

import com.zachklipp.captivate.BuildConfig;
import com.zachklipp.captivate.ConnectedNotification;
import com.zachklipp.captivate.Preferences;
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
import com.zachklipp.captivate.util.StickyIntentService;
import com.zachklipp.captivate.util.WifiHelper;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

public class PortalDetectorService extends StickyIntentService
  implements Observer<TransitionEvent>
{
  private static final String STRING_NAMESPACE = "com.zachklipp.captivate.";
  private static final String INTENT_NAMESPACE = STRING_NAMESPACE + "intent.";
  
  // For start intent
  static final String EXTRA_ASSUME_WIFI_CONNECTED = INTENT_NAMESPACE + "EXTRA_ASSUME_WIFI_CONNECTED";
  
  // For broadcast intent
  public static final String PERMISSION_ACCESS_PORTAL_STATE = STRING_NAMESPACE + "permission.ACCESS_PORTAL_STATE";
  public static final String ACTION_PORTAL_STATE_CHANGED = INTENT_NAMESPACE + "ACTION_PORTAL_STATE_CHANGED";
  public static final String EXTRA_PORTAL_STATE = INTENT_NAMESPACE + "EXTRA_PORTAL_STATE";
  public static final String EXTRA_PORTAL_URL = INTENT_NAMESPACE + "EXTRA_PORTAL_URL";
  public static final String EXTRA_FAVICON_URL = INTENT_NAMESPACE + "EXTRA_FAVICON_URL";
  
  private static class StartServiceReceiver extends BroadcastReceiver
  {
    @Override
    public void onReceive(Context context, Intent intent)
    {
      PortalDetectorService.startService(context);
    }
  };
  
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
  
  private static Intent createStartServiceIntent(Context context)
  {
    return new Intent(context, PortalDetectorService.class);
  }
  
  private Preferences mPreferences;
  private PortalDetector mPortalDetector;
  private PortalStateMachine mStateMachine;
  private BroadcastReceiver mScreenOnReceiver;
  
  private final Runnable mStartServiceRunnable = new Runnable()
  {
    @Override
    public void run()
    {
      startService(PortalDetectorService.this);
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
      
      scheduleTimedRefreshIfBlocked();
    }
    else
    {
      Log.d("Wifi not connected, reporting no portal");
      mStateMachine.onNoWifi();
    }
    
    if (!mStateMachine.getCurrentPortalState().isBehindPortal())
    {
      stopSelf();
    }
  }
  
  @Override
  public void update(Observable<TransitionEvent> observable, TransitionEvent event)
  {
    scheduleRefreshWhenScreenTurnedOn();
    updateNotification();
    sendStateChangedBroadcast();
  }
  
  private void updateDetectorOverrideFromPrefs()
  {
    mPortalDetector.setPortalOverride(
        mPreferences.isDebugOverrideEnabled() ? OverrideMode.ALWAYS_DETECT : OverrideMode.NONE);
  }
  
  private void scheduleRefreshWhenScreenTurnedOn()
  {
    if (mStateMachine.getCurrentPortalState().isBehindPortal())
    {
      registerScreenOnReceiver();
    }
    else
    {
      unregisterScreenOnReceiver();
    }
  }
  
  private void scheduleTimedRefreshIfBlocked()
  {
    int refreshInterval = mPreferences.getStateRefreshIntervalSeconds();
    
    if (isScreenOn() && mStateMachine.getCurrentPortalState().isBlocked())
    {
      Log.i("Scheduling state refresh in %d seconds", refreshInterval);
      
      getHandler().postDelayed(mStartServiceRunnable, refreshInterval * 1000);
    }
  }
  
  @Override
  public void onDestroy()
  {
    super.onDestroy();
    
    unregisterScreenOnReceiver();
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
  
  private void registerScreenOnReceiver()
  {
    if (mScreenOnReceiver == null)
    {
      mScreenOnReceiver = new StartServiceReceiver();
      registerReceiver(mScreenOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
    }
  }
  
  private void unregisterScreenOnReceiver()
  {
    if (mScreenOnReceiver != null)
    {
      unregisterReceiver(mScreenOnReceiver);
      mScreenOnReceiver = null;
    }
  }
  
  private boolean isScreenOn()
  {
    PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    return powerManager.isScreenOn();
  }
}
