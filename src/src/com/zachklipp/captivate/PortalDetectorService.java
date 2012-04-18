package com.zachklipp.captivate;

import com.zachklipp.captivate.captive_portal.*;
import com.zachklipp.captivate.state_machine.*;
import com.zachklipp.captivate.util.Observable;
import com.zachklipp.captivate.util.Observer;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class PortalDetectorService extends IntentService implements Observer<TransitionEvent>
{
  private static final String INTENT_NAMESPACE = "com.zachklipp.captivate.";
  
  // For broadcast intent
  public static final String ACTION_PORTAL_STATE_CHANGED = INTENT_NAMESPACE + "ACTION_PORTAL_STATE_CHANGED";
  public static final String EXTRA_CAPTIVE_PORTAL_STATE = INTENT_NAMESPACE + "EXTRA_CAPTIVE_PORTAL_STATE";
  public static final String EXTRA_CAPTIVE_PORTAL_INFO = INTENT_NAMESPACE + "EXTRA_CAPTIVE_PORTAL_INFO";
  
  private static PortalDetector sSeedPortalDetector = HttpResponseContentsDetector.createDetector();
  
  private static final String LOG_TAG = "captivate";
  
  /*
   * Set the detector to be used when the service is next started.
   * Must be called before the service is started.
   */
  public static void setPortalDetector(PortalDetector detector)
  {
    assert(detector != null);
    
    Log.w(LOG_TAG, "Setting custom portal detector...");
    
    sSeedPortalDetector = detector;
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
    Log.d(LOG_TAG, "Using portal detector " + sSeedPortalDetector.getClass().getName());
    
    mPortalDetector = sSeedPortalDetector;
    
    mStateMachine = (PortalStateMachine) new StateMachineStorage(
        new PortalStateMachineStorageBackend(getApplicationContext(), mPortalDetector))
    .loadOrCreate();
    
    mStateMachine.addObserver(this);
    
    super.onCreate();
  }
  
  public void update(Observable<TransitionEvent> observable, TransitionEvent event)
  {
    updateNotification();
    sendStateChangedBroadcast();
  }
  
  @Override
  protected void onHandleIntent(Intent intent)
  {
    mPortalDetector.checkForPortal();
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
    
    Log.i(LOG_TAG, String.format("Broadcasting portal state change. new state=%s, portal=%s",
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
}
