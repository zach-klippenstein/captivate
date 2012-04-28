package com.zachklipp.captivate.test.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.util.Log;

import com.zachklipp.captivate.util.StickyIntentService;

public class MockStickyIntentService extends StickyIntentService
{
  public static final String THREAD_NAME = "MockStickyIntentService";
  
  public static final String EXTRA_COMMAND = "com.zachklipp.captivate.test.intent.EXTRA_COMMAND";
  
  private static List<String> sReceivedCommands = new ArrayList<String>();
  
  public MockStickyIntentService()
  {
    super(THREAD_NAME);
  }
  
  public static String[] getReceivedCommands(boolean clear)
  {
    String[] commands;
    
    synchronized(sReceivedCommands)
    {
      commands = new String[sReceivedCommands.size()];
      sReceivedCommands.toArray(commands);
      
      if (clear)
      {
        sReceivedCommands.clear();
      }
    }
    
    return commands;
  }
  
  /*
   * Wait for count commands to be received for a maximum of timeout milliseconds.
   */
  public static boolean waitForCommands(int expectedCount, long timeout)
  {
    int actualCount = 0;
    long startTime = System.currentTimeMillis();
    
    while (actualCount < expectedCount && System.currentTimeMillis() - startTime < timeout)
    {
      synchronized(sReceivedCommands)
      {
        try
        {
          sReceivedCommands.wait(startTime + timeout - System.currentTimeMillis());
        }
        catch (InterruptedException e)
        {
          Log.d(THREAD_NAME, "Interrupted waiting for commands", e);
        }
        
        actualCount = sReceivedCommands.size();
      }
    }
    
    return actualCount == expectedCount;
  }

  @Override
  protected void onHandleIntent(Intent intent)
  {
    String command = intent.getStringExtra(EXTRA_COMMAND);
    
    if (command != null)
      Log.d(THREAD_NAME, "Started with command: " + command);
    else
      Log.d(THREAD_NAME, "Started with no command");
    
    synchronized(sReceivedCommands)
    {
      sReceivedCommands.add(command);
      sReceivedCommands.notify();
    }
  }
  
}