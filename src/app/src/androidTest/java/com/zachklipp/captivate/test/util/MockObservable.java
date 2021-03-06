package com.zachklipp.captivate.test.util;

import com.zachklipp.captivate.util.Observable;

public class MockObservable<Event> extends Observable<Event>
{
  public void trigger(Event event)
  {
    notifyObservers(event);
  }
}
