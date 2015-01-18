package com.zachklipp.captivate.test.state_machine;

import android.test.AndroidTestCase;

import com.zachklipp.captivate.state_machine.PortalStateMachine;
import com.zachklipp.captivate.state_machine.PortalStateMachineStorageBackend;
import com.zachklipp.captivate.test.captive_portal.MockPortalDetector;

public class PortalStateMachineStorageBackendTest extends AndroidTestCase
{
  private PortalStateMachine mStateMachine;
  private PortalStateMachineStorageBackend mBackend;
  
  @Override
  protected void setUp()
  {
    MockPortalDetector detector = new MockPortalDetector();
    
    mStateMachine = new PortalStateMachine(detector);
    
    mBackend = new PortalStateMachineStorageBackend(mContext, new MockPortalDetector());
    mBackend.clear();
  }
  
  @Override
  protected void tearDown()
  {
    if (mBackend != null)
    {
      mBackend.clear();
    }
  }

  public void testSaveThenLoad()
  {
    mStateMachine.transitionTo(PortalStateMachine.State.SIGNIN_REQUIRED);
    mBackend.save(mStateMachine);
    mStateMachine = (PortalStateMachine) mBackend.load();
    
    assertNotNull(mStateMachine);
    assertEquals(PortalStateMachine.State.SIGNIN_REQUIRED, mStateMachine.getCurrentState());
  }

  public void testCanLoad()
  {
    assertFalse(mBackend.canLoad());
    
    mBackend.save(mStateMachine);
    
    assertTrue(mBackend.canLoad());
    
    mBackend.clear();
    
    assertFalse(mBackend.canLoad());
  }

  public void testCreate()
  {
    mStateMachine = (PortalStateMachine) mBackend.create();
    
    assertNotNull(mStateMachine);
    assertSame(mStateMachine.getCurrentState(), PortalStateMachine.State.UNKNOWN);
  }

}
