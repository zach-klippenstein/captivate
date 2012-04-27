package com.zachklipp.captivate.service;

import com.zachklipp.captivate.ConnectedNotification;
import com.zachklipp.captivate.captive_portal.*;
import com.zachklipp.captivate.state_machine.*;
import com.zachklipp.captivate.state_machine.PortalStateMachine.StorageBackendFactory;
import com.zachklipp.captivate.state_machine.StateMachineStorage.StorageBackend;
import com.zachklipp.captivate.util.Log;
import com.zachklipp.captivate.util.Observable;
import com.zachklipp.captivate.util.Observer;
import com.zachklipp.captivate.util.WifiHelper;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PortalDetectorService extends IntentService implements Observer<TransitionEvent>
{
  private static final String INTENT_NAMESPACE = "com.zachklipp.captivate.intent.";
  
  // For broadcast intent
  public static final String ACCESS_PORTAL_STATE_PERMISSION = "com.zachklipp.captivate.permission.ACCESS_PORTAL_STATE";
  public static final String ACTION_PORTAL_STATE_CHANGED = INTENT_NAMESPACE + "ACTION_PORTAL_STATE_CHANGED";
  public static final String EXTRA_PORTAL_STATE = INTENT_NAMESPACE + "EXTRA_PORTAL_STATE";
  public static final String EXTRA_PORTAL_INFO = INTENT_NAMESPACE + "EXTRA_PORTAL_INFO";
  
  public static final String ENABLED_PREFERENCE_KEY = "detector_enabled_pref";
  
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
  
  /*
   * Helper to send the necessary intent to start this service.
   */
  public static ComponentName startService(Context context)
  {
    return context.startService(new Intent(context, PortalDetectorService.class));
  }
  
  private PortalDetector mPortalDetector;
  private PortalStateMachine mStateMachine;

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
    if (isEnabled())
    {
      Log.d("Service updating portal status...");
      
      if (WifiHelper.isConnectedFromContext(this))
      {
        try
        {
          mPortalDetector.checkForPortal(this);
        }
        catch (Exception e)
        {
          Log.e("Error checking for portal: " + e.getMessage());
        }
      }
      else
      {
        Log.d("Wifi not connected, reporting no portal");
        mStateMachine.onNoWifi();
      }
    }
    else
    {
      Log.d("Service started, but disabled, so doing nothing.");
      mStateMachine.onDisabled();
    }
  }
  
  @Override
  public void update(Observable<TransitionEvent> observable, TransitionEvent event)
  {
    updateNotification();
    sendStateChangedBroadcast();
  }
  
  private void updateNotification()
  {
    if (PortalStateMachine.State.SIGNIN_REQUIRED == mStateMachine.getCurrentState())
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
    State state = mStateMachine.getCurrentState();
    PortalInfo portal = mPortalDetector.getPortal();
    
    Intent intent = createStateChangedBroadcastIntent(state, portal);
    
    Log.i(String.format("Broadcasting portal state change. new state=%s, portal=%s",
        state, portal));
    
    sendBroadcast(intent, ACCESS_PORTAL_STATE_PERMISSION);
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
  
  private boolean isEnabled()
  {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
        getApplicationContext());
    return preferences.getBoolean(ENABLED_PREFERENCE_KEY, true);
  }
}
