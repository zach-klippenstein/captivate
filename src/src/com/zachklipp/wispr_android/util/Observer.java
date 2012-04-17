package com.zachklipp.wispr_android.util;

public interface Observer<Event>
{
  void update(Observable<Event> observable, Event event);
}
