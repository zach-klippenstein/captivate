package com.zachklipp.captivate.test.captive_portal;

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
    mDetector.setDetectFakePortal(false);
    mDetector.checkForPortal();
    
    assertCurrentState(State.NO_PORTAL);
  }

  public void testPortalDetected()
  {
    mDetector.setDetectFakePortal(true);
    mDetector.checkForPortal();
    
    assertCurrentState(State.SIGNIN_REQUIRED);
    
    mDetector.checkForPortal();
    assertCurrentState(State.SIGNIN_REQUIRED);
  }

  public void testPortalDetectedThenSignedIn()
  {
    mDetector.setDetectFakePortal(true);
    mDetector.checkForPortal();
    
    assertCurrentState(State.SIGNIN_REQUIRED);
    
    mDetector.setDetectFakePortal(false);
    mDetector.checkForPortal();
    
    assertCurrentState(State.SIGNED_IN);
  }

  public void testPortalDetectedThenSigningIn()
  {
    mDetector.setDetectFakePortal(true);
    mDetector.checkForPortal();
    
    assertCurrentState(State.SIGNIN_REQUIRED);
    
    machine.startSignIn();
    assertCurrentState(State.SIGNING_IN);
    
    mDetector.setDetectFakePortal(false);
    mDetector.checkForPortal();
    assertCurrentState(State.SIGNED_IN);
  }

  private void assertCurrentState(State expected)
  {
    assertSame(expected, machine.getCurrentState());
  }
}
