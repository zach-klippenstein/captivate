package com.zachklipp.wispr_android.state_machine;

import android.util.Log;

import com.zachklipp.wispr_android.captive_portal.PortalDetector;
import com.zachklipp.wispr_android.captive_portal.PortalInfo;
import com.zachklipp.wispr_android.util.Observable;
import com.zachklipp.wispr_android.util.Observer;

public class PortalStateMachine extends StateMachine implements Observer<PortalInfo>
{
  public final static class State extends com.zachklipp.wispr_android.state_machine.State
  {
    public static final State UNKNOWN = new State("unknown");
    public static final State NOT_CAPTIVE = new State("no_portal");
    public static final State NEEDS_SIGNIN = new State("needs_signin");
    public static final State SIGNING_IN = new State("signing_in");
    public static final State SIGNED_IN = new State("signed_in");
    
    private State(String name)
    {
      super(name);
    }
  }

  private static final String LOG_TAG = "wispr-android";
  
  private PortalDetector mPortalDetector;

  public PortalStateMachine(PortalDetector detector)
  {
    super(new State[][] {
        new State[] {State.UNKNOWN, State.NOT_CAPTIVE, State.NEEDS_SIGNIN},
        new State[] {State.NOT_CAPTIVE},
        new State[] {State.NEEDS_SIGNIN, State.SIGNING_IN, State.SIGNED_IN},
        new State[] {State.SIGNING_IN, State.SIGNED_IN},
        new State[] {State.SIGNED_IN, State.NEEDS_SIGNIN, State.NOT_CAPTIVE},
      });
    
    assert(detector != null);
    mPortalDetector = detector;
    mPortalDetector.addObserver(this);
  }
  
  public void update(Observable<PortalInfo> observable, PortalInfo portal)
  {
    if (portal == null)
    {
      noLongerNeedsSignin();
    }
    else
    {
      needsSignin();
    }
  }
  
  public void startSignIn()
  {
    transitionTo(State.SIGNING_IN);
  }
  
  private void needsSignin()
  {
    if (getCurrentState() != State.NEEDS_SIGNIN && getCurrentState() != State.SIGNING_IN)
    {
      Log.d(LOG_TAG, "Captive portal detected.");
      
      transitionTo(State.NEEDS_SIGNIN);
    }
  }
  
  private void noLongerNeedsSignin()
  {
    Log.d(LOG_TAG, "No portal detected.");
    
    if (getCurrentState() == State.SIGNING_IN || getCurrentState() == State.NEEDS_SIGNIN)
    {
      transitionTo(State.SIGNED_IN);
    }
    else
    {
      transitionTo(State.NOT_CAPTIVE);
    }
  }
}
