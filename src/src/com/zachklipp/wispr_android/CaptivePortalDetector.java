package com.zachklipp.wispr_android;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

// See http://erratasec.blogspot.com/2010/09/apples-secret-wispr-request.html
public abstract class CaptivePortalDetector
{
  private static final String LOG_TAG = "wispr-android";
  
  // Set by checkForCaptivePortal().
  private CaptivePortalInfo mPortal;
  private ArrayList<CaptivePortalHandler> mHandlers = new ArrayList<CaptivePortalHandler>();
  
  // Should eventually cause reportCaptivePortal() to be called, followed
  // by triggerHandlers().
  public abstract void checkForCaptivePortal(Context context);
  
  public boolean isOnCaptivePortal()
  {
    return (mPortal != null);
  }
  
  public CaptivePortalInfo getCaptivePortal()
  {
    return mPortal;
  }
  
  public void addCaptivePortalHandler(CaptivePortalHandler handler)
  {
    if (handler != null)
    {
      mHandlers.add(handler);
    }
  }
  
  protected void reportCaptivePortal(Context context, CaptivePortalInfo portal)
  {
    Log.d(LOG_TAG, String.format("Reporting captive portal to %d handlers...", mHandlers.size()));
    
    mPortal = portal;
    triggerHandlers(context);
  }
  
  private void triggerHandlers(Context context)
  {
    for (CaptivePortalHandler handler : mHandlers)
    {
      handler.onCaptivePortalDetected(context, mPortal);
    }
  }
}
