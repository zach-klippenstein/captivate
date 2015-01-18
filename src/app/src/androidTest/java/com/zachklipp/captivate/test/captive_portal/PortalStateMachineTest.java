package com.zachklipp.captivate.test.captive_portal;

import com.zachklipp.captivate.captive_portal.PortalDetector.OverrideMode;
import com.zachklipp.captivate.state_machine.PortalStateMachine;
import com.zachklipp.captivate.state_machine.PortalStateMachine.State;

import junit.framework.TestCase;

public class PortalStateMachineTest extends TestCase
{
  private MockPortalDetector mDetector = new MockPortalDetector();
  private PortalStateMachine machine;
  
  @Override
  protected void setUp()
  {
    machine = new PortalStateMachine(mDetector);
  }
  
  public void testInitialState()
  {
    assertCurrentState(State.UNKNOWN);
  }

  public void testNoPortalDetected()
  {
    mDetector.setTestingOverride(OverrideMode.NEVER_DETECT);
    mDetector.checkForPortal();
    
    assertCurrentState(State.NO_PORTAL);
  }

  public void testPortalDetected()
  {
    mDetector.setTestingOverride(OverrideMode.ALWAYS_DETECT);
    mDetector.checkForPortal();
    
    assertCurrentState(State.SIGNIN_REQUIRED);
    
    mDetector.checkForPortal();
    assertCurrentState(State.SIGNIN_REQUIRED);
  }

  public void testPortalDetectedThenSignedIn()
  {
    mDetector.setTestingOverride(OverrideMode.ALWAYS_DETECT);
    mDetector.checkForPortal();
    
    assertCurrentState(State.SIGNIN_REQUIRED);

    mDetector.setTestingOverride(OverrideMode.NEVER_DETECT);
    mDetector.checkForPortal();
    
    assertCurrentState(State.SIGNED_IN);
  }

  public void testPortalDetectedThenSigningIn()
  {
    mDetector.setTestingOverride(OverrideMode.ALWAYS_DETECT);
    mDetector.checkForPortal();
    
    assertCurrentState(State.SIGNIN_REQUIRED);
    
    machine.startSignIn();
    assertCurrentState(State.SIGNING_IN);

    mDetector.setTestingOverride(OverrideMode.NEVER_DETECT);
    mDetector.checkForPortal();
    assertCurrentState(State.SIGNED_IN);
  }

  private void assertCurrentState(State expected)
  {
    assertSame(expected, machine.getCurrentState());
  }
}
