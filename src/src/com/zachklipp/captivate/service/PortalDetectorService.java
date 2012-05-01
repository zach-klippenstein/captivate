package com.zachklipp.captivate.service;

import com.zachklipp.captivate.BuildConfig;
import com.zachklipp.captivate.ConnectedNotification;
import com.zachklipp.captivate.Manifest;
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
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;

public class PortalDetectorService extends StickyIntentService
  implements Observer<TransitionEvent>
{
  private static final String INTENT_NAMESPACE = "com.zachklipp.captivate.intent.";
  
  // For start intent
  static final String EXTRA_ASSUME_WIFI_CONNECTED = INTENT_NAMESPACE + "EXTRA_ASSUME_WIFI_CONNECTED";
  
  // For broadcast intent
  public static final String ACTION_PORTAL_STATE_CHANGED = INTENT_NAMESPACE + "ACTION_PORTAL_STATE_CHANGED";
  public static final String EXTRA_PORTAL_STATE = INTENT_NAMESPACE + "EXTRA_PORTAL_STATE";
  public static final String EXTRA_PORTAL_URL = INTENT_NAMESPACE + "EXTRA_PORTAL_URL";
  
  public static final String ENABLED_PREFERENCE_KEY = "detector_enabled_pref";
  public static final String DEBUG_OVERRIDE_PREFERENCE_KEY = "debug_override_pref";
  public static final String STATE_REFRESH_INTERVAL_SECONDS_PREFERENCE_KEY
    = "state_refresh_interval_seconds";
  
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
    return startService(context, false);
  }
  
  /*
   * Helper to send the necessary intent to start this service.
   */
  public static ComponentName startService(Context context, boolean assumeWifiConnected)
  {
    Intent intent = new Intent(context, PortalDetectorService.class);
    
    intent.putExtra(EXTRA_ASSUME_WIFI_CONNECTED, assumeWifiConnected);
    
    return context.startService(intent);
  }
  
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
    boolean assumeWifiConnected = false;
    
    if (isEnabled())
    {
      if (null != intent)
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
        isDebugOverrideEnabled() ? OverrideMode.ALWAYS_DETECT : OverrideMode.NONE);
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
    if (isScreenOn() && mStateMachine.getCurrentPortalState().isBlocked())
    {
      Log.i(String.format("Scheduling state refresh in %d seconds",
          getStateRefreshIntervalSeconds()));
      
      getHandler().postDelayed(mStartServiceRunnable,
          getStateRefreshIntervalSeconds() * 1000);
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
    
    Log.i(String.format("Broadcasting portal state change. new state=%s, portal=%s",
        state, portal));
    
    sendBroadcast(intent, Manifest.permission.ACCESS_PORTAL_STATE);
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
  
  private boolean isEnabled()
  {
    return getPreferences().getBoolean(ENABLED_PREFERENCE_KEY, true);
  }
  
  private boolean isDebugOverrideEnabled()
  {
    return BuildConfig.DEBUG && getPreferences().getBoolean(DEBUG_OVERRIDE_PREFERENCE_KEY, false);
  }
  
  private int getStateRefreshIntervalSeconds()
  {
    return getPreferences().getInt(STATE_REFRESH_INTERVAL_SECONDS_PREFERENCE_KEY, 30);
  }
  
  private SharedPreferences getPreferences()
  {
    return PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
  }
}
