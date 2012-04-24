package com.zachklipp.captivate.service;

import com.zachklipp.captivate.ConnectedNotification;
import com.zachklipp.captivate.captive_portal.*;
import com.zachklipp.captivate.state_machine.*;
import com.zachklipp.captivate.util.Log;
import com.zachklipp.captivate.util.Observable;
import com.zachklipp.captivate.util.Observer;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PortalDetectorService extends IntentService implements Observer<TransitionEvent>
{
  public static interface StorageBackendFactory
  {
    public StateMachineStorage.StorageBackend create(Context context, PortalDetector detector);
  }
  
  private static final String INTENT_NAMESPACE = "com.zachklipp.captivate.";
  
  // For broadcast intent
  public static final String ACTION_PORTAL_STATE_CHANGED = INTENT_NAMESPACE + "ACTION_PORTAL_STATE_CHANGED";
  public static final String EXTRA_CAPTIVE_PORTAL_STATE = INTENT_NAMESPACE + "EXTRA_CAPTIVE_PORTAL_STATE";
  public static final String EXTRA_CAPTIVE_PORTAL_INFO = INTENT_NAMESPACE + "EXTRA_CAPTIVE_PORTAL_INFO";
  
  public static final String ENABLED_KEY = "enabled";
  
  private static PortalDetector sSeedPortalDetector = HttpResponseContentsDetector.createDetector();

  private static StorageBackendFactory sStorageBackendFactory = new StorageBackendFactory()
  {
    @Override
    public StateMachineStorage.StorageBackend create(Context context, PortalDetector detector)
    {
      return new PortalStateMachineStorageBackend(context, detector);
    }
  };
  
  /*
   * Set the detector to be used when the service is next started.
   * Must be called before the service is started.
   */
  public static void setPortalDetector(PortalDetector detector)
  {
    assert(detector != null);
    
    Log.w("Setting custom portal detector...");
    
    sSeedPortalDetector = detector;
  }
  
  public static void setStorageBackendFactory(StorageBackendFactory factory)
  {
    sStorageBackendFactory = factory;
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
    Log.d("Using portal detector " + sSeedPortalDetector.getClass().getName());
    
    mPortalDetector = sSeedPortalDetector;
    
    mStateMachine = (PortalStateMachine) new StateMachineStorage(
        sStorageBackendFactory.create(getApplicationContext(), mPortalDetector))
    .loadOrCreate();
    
    mStateMachine.addObserver(this);
    
    Log.d("Calling IntentService.onCreate()");
    super.onCreate();
    Log.d("Back from IntentService.onCreate()");
  }
  
  @Override
  protected void onHandleIntent(Intent intent)
  {
    if (isEnabled())
    {
      Log.v("Service updating portal status...");
      mPortalDetector.checkForPortal(this);
    }
    else
    {
      Log.v("Service started, but disabled, so doing nothing.");
      mStateMachine.onDisabled();
    }
  }
  
  @Override
  public void onDestroy()
  {
    super.onDestroy();
  }
  
  @Override
  public void update(Observable<TransitionEvent> observable, TransitionEvent event)
  {
    updateNotification();
    sendStateChangedBroadcast();
  }
  
  private void updateNotification()
  {
    if (PortalStateMachine.State.NEEDS_SIGNIN == mStateMachine.getCurrentState())
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
    
    sendBroadcast(intent);
  }
  
  private static Intent createStateChangedBroadcastIntent(State state, PortalInfo portal)
  {
    Intent intent = new Intent(PortalDetectorService.ACTION_PORTAL_STATE_CHANGED);
    
    intent.putExtra(PortalDetectorService.EXTRA_CAPTIVE_PORTAL_STATE, state.getName());
    
    if (portal != null)
    {
      intent.putExtra(PortalDetectorService.EXTRA_CAPTIVE_PORTAL_INFO, portal);
    }
    
    return intent;
  }
  
  private boolean isEnabled()
  {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    return preferences.getBoolean(ENABLED_KEY, true);
  }
}
