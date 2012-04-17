package com.zachklipp.captivate.util;

import java.util.LinkedList;
import java.util.List;

public abstract class Observable<Event>
{
  private List<Observer<Event>> mObservers = new LinkedList<Observer<Event>>();
  
  public void addObserver(Observer<Event> o)
  {
    mObservers.add(o);
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
