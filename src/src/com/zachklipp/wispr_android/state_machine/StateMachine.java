package com.zachklipp.wispr_android.state_machine;

import com.zachklipp.wispr_android.util.*;

public class StateMachine extends Observable<TransitionEvent>
{
  private SetMap<State> mTransitionMatrix;
  private State mCurrentState;
  
  public StateMachine(State[][] transitionMatrix)
  {
    mTransitionMatrix = new SetMap<State>(transitionMatrix);
    mCurrentState = transitionMatrix[0][0];
  }
  
  public State getCurrentState()
  {
    return mCurrentState;
  }
  
  /*
   * Set the current state to state if allowed, and update
   * observers, passing the old state as the event.
   */
  public void transitionTo(State state)
  {
    assert(state != null);
    
    if (!mCurrentState.equals(state) && canTransition(mCurrentState, state))
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
  
  private boolean canTransition(State fromState, State toState)
  {
    return mTransitionMatrix.get(fromState).contains(toState);
  }
}
