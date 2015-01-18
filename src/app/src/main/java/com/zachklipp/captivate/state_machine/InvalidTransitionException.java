package com.zachklipp.captivate.state_machine;

public class InvalidTransitionException extends RuntimeException
{
  private static final long serialVersionUID = 8436427871991226824L;

  public InvalidTransitionException(State fromState, State toState)
  {
    super(String.format("State %s cannot transition to state %s", fromState.getName(), toState.getName()));
  }

}
