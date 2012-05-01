package com.zachklipp.captivate.state_machine;

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
  
  @Override
  public boolean equals(Object other)
  {
    if (other instanceof String)
    {
      return mName.equals(other);
    }
    
    return super.equals(other);
  }
  
  @Override
  public int hashCode()
  {
    return mName.hashCode();
  }
}
