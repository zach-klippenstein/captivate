package com.zachklipp.captivate.test.util;

import java.util.ArrayList;
import java.util.List;

import com.zachklipp.captivate.util.Observable;
import com.zachklipp.captivate.util.Observer;

import junit.framework.TestCase;

public class ObservableTest extends TestCase
{

  public void testAddObserver()
  {
    Observable<Object> observable = new MockObservable<Object>();
    Observer<Object> observer = new MockObserver<Object>();
    
    observable.addObserver(observer);
    observable.deleteObserver(observer);
  }
  
  public void testNotifyObservers()
  {
    MockObservable<Object> observable = new MockObservable<Object>();
    List<MockObserver<Object>> observers = createObserverList(3, observable);
    
    observable.trigger(null);
    observable.trigger(observable);

    Object[] events = new Object[2];
    for (MockObserver<Object> observer : observers)
    {
      assertEquals(2, observer.getObservedEventCount());
      
      observer.getObservedEvents(events);
      assertNull(events[0]);
      assertSame(observable, events[1]);
    }
  }

  private static <Event> List<MockObserver<Event>> createObserverList(int count, Observable<Event> observable)
  {
    List<MockObserver<Event>> observers = new ArrayList<MockObserver<Event>>();
    
    for (int i = 0; i < 3; i++)
    {
      MockObserver<Event> observer = new MockObserver<Event>();
      observers.add(observer);
      observable.addObserver(observer);
    }
    
    return observers;
  }
}
