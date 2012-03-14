package com.zachklipp.wispr_android;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

// See http://erratasec.blogspot.com/2010/09/apples-secret-wispr-request.html
public class CaptivePortalDetector
{
  private static final String LOG_TAG = "wispr-android";
  
  private Context mContext;
  private CaptivePortalSensor mSensor;
  private CaptivePortalInfo mPortal;
  private ArrayList<CaptivePortalHandler> mHandlers = new ArrayList<CaptivePortalHandler>();
  
  public CaptivePortalDetector(Context context, CaptivePortalSensor sensor)
  {
    assert(sensor != null);
    
    mSensor = sensor;
  }
  
  public void checkForCaptivePortal()
  {
    assert(mSensor != null);
    
    try
    {
      // This should result in reportCaptivePortal getting called.
      mSensor.checkForCaptivePortal(this);
    }
    catch (Exception ex)
    {
      Log.w(LOG_TAG, "sensor error", ex);
    }
  }
  
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
  
  public void reportCaptivePortal(CaptivePortalInfo portal)
  {
    Log.d(LOG_TAG, String.format("Reporting captive portal to %d handlers...", mHandlers.size()));
    
    mPortal = portal;
    triggerHandlers();
  }
  
  private void triggerHandlers()
  {
    for (CaptivePortalHandler handler : mHandlers)
    {
      handler.onCaptivePortalDetected(mContext, mPortal);
    }
  }
}
