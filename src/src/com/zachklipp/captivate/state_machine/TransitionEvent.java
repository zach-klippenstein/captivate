package com.zachklipp.captivate.state_machine;

public class TransitionEvent
{
  private State mFromState;
  private State mToState;
  
  public TransitionEvent(State fromState, State toState)
  {
    mFromState = fromState;
    mToState = toState;
  }
  
  public State getFromState() { return mFromState; }
  public State getToState() { return mToState; }
}