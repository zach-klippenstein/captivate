package com.zachklipp.captivate;

import com.zachklipp.captivate.util.Log;

public class Application extends android.app.Application
{
  @Override
  public void onCreate()
  {
    initializeLogging();
  }
  
  private void initializeLogging()
  {
    Log.setDefaultTag("captivate");
    
    if (!BuildConfig.DEBUG)
    {
      Log.v("Configured for release, disabling debug logging");
      Log.setMinPriority(android.util.Log.INFO);
    }
    else
    {
      Log.v("Configured for dev, enabling debug logging");
    }
  }
}
