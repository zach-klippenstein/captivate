package com.zachklipp.wispr_android.state_machine;

public class State
{
  private String mName;
  
  public State(String name)
  {
    assert(name != null);
    
    mName = name;
  }
  
  public String getName()
  {
    return mName;
  }
  
  @Override
  public String toString()
  {
    return getName();
  }
}
