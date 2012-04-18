package com.zachklipp.captivate.test.service;

import java.util.ArrayList;

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
        actualCount = mReceivedIntents.size();
      }
    }
    
    return (actualCount == expectedCount);
  }
  
  public Intent[] getReceivedIntents()
  {
    Intent[] intents;
    
    synchronized(mReceivedIntents)
    {
      intents = new Intent[mReceivedIntents.size()];
      mReceivedIntents.toArray(intents);
    }
    
    return intents;
  }

  @Override
  public void onReceive(Context context, Intent intent)
  {
    Log.d(LOG_TAG, String.format("Received intent: %s", intent.getAction()));
    synchronized(mReceivedIntents)
    {
      mReceivedIntents.add(intent);
    }
  }
}