package com.zachklipp.captivate.state_machine;

import com.zachklipp.captivate.captive_portal.PortalDetector;
import com.zachklipp.captivate.captive_portal.PortalInfo;
import com.zachklipp.captivate.util.Log;
import com.zachklipp.captivate.util.Observable;
import com.zachklipp.captivate.util.Observer;

public class PortalStateMachine extends StateMachine
{
  public final static class State extends com.zachklipp.captivate.state_machine.State
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
  
  public static final State[][] TRANSITION_MATRIX = new State[][] {
      new State[] {State.UNKNOWN, State.NOT_CAPTIVE, State.NEEDS_SIGNIN},
      new State[] {State.NOT_CAPTIVE, State.UNKNOWN},
      new State[] {State.NEEDS_SIGNIN, State.UNKNOWN, State.SIGNING_IN, State.SIGNED_IN},
      new State[] {State.SIGNING_IN, State.UNKNOWN, State.SIGNED_IN},
      new State[] {State.SIGNED_IN, State.UNKNOWN, State.NEEDS_SIGNIN, State.NOT_CAPTIVE},
    };

  private PortalDetector mPortalDetector;

  public PortalStateMachine(PortalDetector detector)
  {
    super(State.UNKNOWN, TRANSITION_MATRIX);
    initialize(detector);
  }
  
  public PortalStateMachine(PortalDetector detector, String initialStateName)
  {
    super(initialStateName, TRANSITION_MATRIX);
    initialize(detector);
  }
  
  private void initialize(PortalDetector detector)
  {
    assert(detector != null);
    mPortalDetector = detector;
    
    mPortalDetector.addObserver(new Observer<PortalInfo>() {
      @Override
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
    });
    
    Log.d(String.format("Portal state machine initialized to state %s", getCurrentState().getName()));
  }
  
  public void startSignIn()
  {
    transitionTo(State.SIGNING_IN);
  }
  
  public void onDisabled()
  {
    transitionTo(State.UNKNOWN);
  }
  
  private void needsSignin()
  {
    if (getCurrentState() != State.NEEDS_SIGNIN)
    {
      Log.d("Captive portal detected.");
      
      transitionTo(State.NEEDS_SIGNIN);
    }
  }
  
  private void noLongerNeedsSignin()
  {
    if (getCurrentState() == State.SIGNING_IN || getCurrentState() == State.NEEDS_SIGNIN)
    {
      Log.d("Portal signed in.");
      transitionTo(State.SIGNED_IN);
    }
    else
    {
      Log.d("No portal detected.");
      transitionTo(State.NOT_CAPTIVE);
    }
  }
}
