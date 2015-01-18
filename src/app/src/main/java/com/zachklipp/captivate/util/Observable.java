package com.zachklipp.captivate.util;

import java.util.HashSet;
import java.util.Set;

public abstract class Observable<Event>
{
  private Set<Observer<Event>> mObservers = new HashSet<Observer<Event>>();
  
  public boolean addObserver(Observer<Event> o)
  {
    return mObservers.add(o);
  }
  
  public void deleteObserver(Observer<Event> o)
  {
    mObservers.remove(o);
  }
  
  protected void notifyObservers(Event event)
  {
    for (Observer<Event> observer : mObservers)
    {
      observer.update(this, event);
    }
  }
}
