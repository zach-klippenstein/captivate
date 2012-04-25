package com.zachklipp.captivate.state_machine;

import com.zachklipp.captivate.util.*;

public class StateMachine extends Observable<TransitionEvent>
{
  private SetMap<State> mTransitionMatrix;
  private State mCurrentState;
  
  public StateMachine(State initialState, State[][] transitionMatrix)
  {
    mTransitionMatrix = new SetMap<State>(transitionMatrix);
    mCurrentState = initialState;
    
    validatePostConstructor();
  }
  
  public StateMachine(String initialStateName, State[][] transitionMatrix)
  {
    mTransitionMatrix = new SetMap<State>(transitionMatrix);
    
    State initialState = findStateByName(initialStateName);
    mCurrentState = initialState;
    
    validatePostConstructor();
  }
  
  private void validatePostConstructor()
  {
    if (mCurrentState == null || mTransitionMatrix.size() == 0)
    {
      throw new IllegalArgumentException("Non-null initial state and non-empty transitionMatrix required");
    }
  }
  
  public State getCurrentState()
  {
    return mCurrentState;
  }
  
  public State findStateByName(String name)
  {
    for (State state : mTransitionMatrix.getKeys())
    {
      if (name.equals(state.getName()))
        return state;
    }
    
    return null;
  }
  
  /*
   * Set the current state to state if allowed, and update
   * observers, passing the old state as the event.
   */
  public void transitionTo(State state)
  {
    assert(state != null);
    
    if (!mCurrentState.equals(state))
    {
      if (canTransition(mCurrentState, state))
      {
        TransitionEvent event = new TransitionEvent(mCurrentState, state);
        
        mCurrentState = state;
        
        notifyObservers(event);
      }
      else
      {
        throw new InvalidTransitionException(mCurrentState, state);
      }
    }
  }
  
  private boolean canTransition(State fromState, State toState)
  {
    return mTransitionMatrix.get(fromState).contains(toState);
  }
}
