package com.zachklipp.captivate.test.state_machine;

import com.zachklipp.captivate.state_machine.*;

import junit.framework.TestCase;

public class StateMachineTest extends TestCase
{
  private StateMachine machine;
  
  public void testNoStates()
  {
    try
    {
      new StateMachine((State) null, new State[][]{});
      fail("Empty state machine created");
    }
    catch (IllegalArgumentException ex)
    {
      // pass
    }
  }

  public void testSingleState()
  {
    State startState = new State("start");
    machine = MockStateMachine.createWithSingleState(startState);
    
    assertSame(startState, machine.getCurrentState());
  }

  public void testImplicitLoops()
  {
    State startState = new State("start");
    machine = MockStateMachine.createWithSingleState(startState);
    
    machine.transitionTo(startState);
    
    assertSame(startState, machine.getCurrentState());
  }

  public void testSingleStateByName()
  {
    State startState = new State("start");
    machine = new MockStateMachine(startState.getName(), new State[][]{new State[]{startState}});
    
    assertSame(startState, machine.getCurrentState());
  }

}
