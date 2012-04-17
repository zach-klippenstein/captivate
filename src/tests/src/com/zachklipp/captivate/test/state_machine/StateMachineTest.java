package com.zachklipp.captivate.test.state_machine;

import com.zachklipp.captivate.state_machine.*;

import junit.framework.TestCase;

public class StateMachineTest extends TestCase
{
  private StateMachine machine;

  public void testSingleState()
  {
    State startState = new State("start");
    machine = new StateMachine(matrix(
        transition(startState)));
    
    assertSame(startState, machine.getCurrentState());
    
    assertTransitionFails(startState);
  }
  
  private static State[][] matrix(State[]... transitions)
  {
    return transitions;
  }
  
  private static State[] transition(State... states)
  {
    return states;
  }
  
  private void assertTransitionFails(State toState)
  {
    try
    {
      machine.transitionTo(toState);
      fail(String.format("Transition from '%s' to '%s' allowed.", machine.getCurrentState().getName(), toState.getName()));
    }
    catch (InvalidTransitionException ex)
    {
      // pass
    }
    // any other exceptions are errors.
  }

}
