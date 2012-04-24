package com.zachklipp.captivate.test.service;

import java.util.ArrayList;

import com.zachklipp.captivate.service.PortalDetectorService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

// Thread-safe
class MockBroadcastReceiver extends BroadcastReceiver
{
  private static final String LOG_TAG = "captivate-tests";
  
  private ArrayList<Intent> mReceivedIntents = new ArrayList<Intent>();
  
  // Return true if expectedCount intents were received before timeoutMillis,
  // else false.
  public boolean waitForIntents(final int expectedCount, final long timeoutMillis)
  {
    int actualCount = 0;
    final long startTime = System.currentTimeMillis();
    
    while (actualCount < expectedCount && System.currentTimeMillis() - startTime < timeoutMillis)
    {
      synchronized(mReceivedIntents)
      {
        Log.d(LOG_TAG, "Waiting for intent in thread " + Thread.currentThread().getName());
        
        try
        {
          mReceivedIntents.wait(startTime + timeoutMillis - System.currentTimeMillis());
          Log.d(LOG_TAG, "Received an intent while waiting! now have " + mReceivedIntents.size());
        }
        catch (InterruptedException e)
        {
          Log.d(LOG_TAG, "Interrupted while waiting for intents!");
        }
        
        actualCount = mReceivedIntents.size();
      }
    }
    
    Log.d(LOG_TAG, String.format("Done waiting for intents: expected %d got %d", expectedCount, actualCount));
    
    return (actualCount == expectedCount);
  }
  
  public Intent[] getReceivedIntentsAndClear()
  {
    Intent[] intents;
    
    synchronized(mReceivedIntents)
    {
      intents = new Intent[mReceivedIntents.size()];
      mReceivedIntents.toArray(intents);
      mReceivedIntents.clear();
    }
    
    return intents;
  }

  @Override
  public void onReceive(Context context, Intent intent)
  {
    StringBuilder message = new StringBuilder(String.format("Received intent: %s", intent.getAction()));
    
    if (PortalDetectorService.ACTION_PORTAL_STATE_CHANGED.equals(intent.getAction()))
      message.append(" new state=" + intent.getExtras().getString(PortalDetectorService.EXTRA_CAPTIVE_PORTAL_STATE));
    
    message.append(String.format("\non thread %s", Thread.currentThread().getName()));
    
    Log.d(LOG_TAG, message.toString());
    
    synchronized(mReceivedIntents)
    {
      mReceivedIntents.add(intent);
      mReceivedIntents.notify();
    }
  }
}