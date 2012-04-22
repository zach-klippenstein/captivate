package com.zachklipp.captivate;

import com.zachklipp.captivate.util.Log;

import android.content.pm.ApplicationInfo;

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
    
    if (isReleaseBuild())
    {
      Log.setMinPriority(android.util.Log.INFO);
    }
    else
    {
      Log.v("Configured for dev, enabling debug logging");
    }
  }
  
  private boolean isReleaseBuild()
  {
    return (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) == 0;
  }
}
