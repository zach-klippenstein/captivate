package com.zachklipp.captivate.test.service;

import android.content.Intent;
import android.test.ServiceTestCase;
import android.util.Log;

public class StickyIntentServiceTest extends ServiceTestCase<MockStickyIntentService>
{
  private static final String LOG_TAG = "captivate-test";
  
  public StickyIntentServiceTest()
  {
    super(MockStickyIntentService.class);
  }
  
  @Override
  protected void tearDown() throws Exception
  {
    super.tearDown();
    
    // Give the thread a chance to die.
    // If tests fail in this method, try increasing this value.
    Thread.sleep(100);
    
    Thread workerThread = findThreadByName(MockStickyIntentService.THREAD_NAME);
    assertNull(workerThread);
  }

  public void testStartsThreadWithName()
  {
    startService();
    
    Thread workerThread = findThreadByName(MockStickyIntentService.THREAD_NAME);
    assertNotNull(workerThread);
    
    // Defer to tearDown()
  }
  
  public void testHandleIntent()
  {
    String[] commands = new String[] {"one", "two", "three"};
    String[] receivedCommands;
    
    for (String command : commands)
    {
      startServiceWithCommand(command);
    }
    
    assertTrue(MockStickyIntentService.waitForCommands(commands.length, 30000));
    receivedCommands = MockStickyIntentService.getReceivedCommands(true);
    assertEquals(commands.length, receivedCommands.length);
    
    for (int i = 0; i < commands.length; i++)
    {
      assertEquals(commands[i], receivedCommands[i]);
    }
  }
  
  private Intent startService()
  {
    return startServiceWithCommand(null);
  }
  
  private Intent startServiceWithCommand(String command)
  {
    Intent intent = new Intent(mContext, MockStickyIntentService.class);
    intent.putExtra(MockStickyIntentService.EXTRA_COMMAND, command);
    
    Log.d(LOG_TAG, "Starting service with command " + command);
    
    startService(intent);
    
    return intent;
  }
  
  private Thread findThreadByName(String name)
  {
    // Leave some extra space in case new threads are started before enumeration.
    Thread[] threads = new Thread[Thread.activeCount() * 2];
    int numThreads = Thread.enumerate(threads);
    
    for (int i = 0; i < numThreads; i++)
    {
      if (name.equals(threads[i].getName()))
        return threads[i];
    }
    
    return null;
  }
}
