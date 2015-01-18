package com.zachklipp.captivate.test.state_machine;

import com.zachklipp.captivate.state_machine.StateMachine;
import com.zachklipp.captivate.state_machine.StateMachineStorage;

import junit.framework.TestCase;

public class StateMachineStorageTest extends TestCase
{
  private StateMachine mStateMachine = MockStateMachine.createWithSingleState();
  private MockStorageBackend mStorageBackend;
  private StateMachineStorage mStorageUnderTest;
  
  @Override
  protected void setUp()
  {
    mStorageBackend = new MockStorageBackend();
    mStorageUnderTest = new StateMachineStorage(mStorageBackend);
  }

  public void testCreateWhenCannotLoad()
  {
    mStorageBackend.setCanLoad(false);
    mStorageBackend.setStateMachineToCreate(mStateMachine);
    
    assertSame(mStateMachine, mStorageUnderTest.loadOrCreate());
    assertEquals(1, mStorageBackend.getCanLoadCallCount());
    assertEquals(0, mStorageBackend.getLoadCallCount());
    assertEquals(1, mStorageBackend.getCreateCallCount());
  }
  
  public void testLoad()
  {
    mStorageBackend.setCanLoad(true);
    mStorageBackend.setStateMachineToLoad(mStateMachine);
    
    assertSame(mStateMachine, mStorageUnderTest.loadOrCreate());
    assertEquals(1, mStorageBackend.getCanLoadCallCount());
    assertEquals(1, mStorageBackend.getLoadCallCount());
    assertEquals(0, mStorageBackend.getCreateCallCount());
  }

  public void testSave()
  {
    mStorageUnderTest.save(mStateMachine);
    StateMachine[] savedMachines = mStorageBackend.getSavedMachines();
    
    assertEquals(1, savedMachines.length);
    assertSame(mStateMachine, savedMachines[0]);
  }

}
