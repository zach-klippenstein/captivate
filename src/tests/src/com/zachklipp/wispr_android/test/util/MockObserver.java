package com.zachklipp.wispr_android.test.util;

import java.util.ArrayList;

import com.zachklipp.wispr_android.util.Observable;
import com.zachklipp.wispr_android.util.Observer;

public class MockObserver<Event> implements Observer<Event>
{
  private ArrayList<Event> mObservedEvents = new ArrayList<Event>();

  @Override
  public void update(Observable<Event> observable, Event event)
  {
    mObservedEvents.add(event);
  }
  
  public int getObservedEventCount()
  {
    return mObservedEvents.size();
  }
  
  public Event[] getObservedEvents(Event[] observedEvents)
  {
    return mObservedEvents.toArray(observedEvents);
  }

}
