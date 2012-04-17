package com.zachklipp.captivate.test.state_machine;

import com.zachklipp.captivate.state_machine.State;
import com.zachklipp.captivate.state_machine.StateMachine;

public class MockStateMachine extends StateMachine
{
  
  public static MockStateMachine createWithSingleState()
  {
    return createWithSingleState(new State("single_state"));
  }
  
  public static MockStateMachine createWithSingleState(State singleState)
  {
    return new MockStateMachine(singleState, matrix(transition(singleState)));
  }
  
  public static State[][] matrix(State[]... transitions)
  {
    return transitions;
  }
  
  public static State[] transition(State... states)
  {
    return states;
  }

  public MockStateMachine(State initialState, State[][] transitionMatrix)
  {
    super(initialState, transitionMatrix);
  }
  
  public MockStateMachine(String initialStateName, State[][] transitionMatrix)
  {
    super(initialStateName, transitionMatrix);
  }

}
