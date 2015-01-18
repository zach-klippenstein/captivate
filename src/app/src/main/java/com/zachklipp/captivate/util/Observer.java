package com.zachklipp.captivate.util;

public interface Observer<Event>
{
  void update(Observable<Event> observable, Event event);
}
