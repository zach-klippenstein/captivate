package com.zachklipp.wispr_android.test.util;

import com.zachklipp.wispr_android.util.Observable;

public class MockObservable<Event> extends Observable<Event>
{
  public void trigger(Event event)
  {
    notifyObservers(event);
  }
}
