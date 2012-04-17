package com.zachklipp.wispr_android.captive_portal;

import com.zachklipp.wispr_android.util.Observable;

import android.util.Log;

// See http://erratasec.blogspot.com/2010/09/apples-secret-wispr-request.html
public abstract class PortalDetector extends Observable<PortalInfo>
{
  private static final String LOG_TAG = "wispr-android";
  
  private PortalInfo mPortal;
  
  // Should eventually call reportPortal
  public abstract void checkForPortal();
  
  public boolean isOnPortal()
  {
    return (mPortal != null);
  }
  
  public PortalInfo getPortal()
  {
    return mPortal;
  }
  
  protected void reportPortal(PortalInfo portal)
  {
    Log.d(LOG_TAG, String.format("Reporting captive portal to handlers..."));
    
    mPortal = portal;
    notifyObservers(portal);
  }
}
