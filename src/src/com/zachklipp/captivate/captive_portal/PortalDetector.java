package com.zachklipp.captivate.captive_portal;

import com.zachklipp.captivate.util.Observable;

import android.util.Log;

// See http://erratasec.blogspot.com/2010/09/apples-secret-wispr-request.html
public abstract class PortalDetector extends Observable<PortalInfo>
{
  private static final String LOG_TAG = "wispr-android";
  
  private PortalInfo mPortal;
  
  public PortalDetector()
  {
    super();
  }
  
  public PortalDetector(PortalInfo portal)
  {
    super();
    
    mPortal = portal;
  }
  
  // Should eventually call reportPortal or reportNoPortal
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
    Log.d(LOG_TAG, String.format("Reporting captive portal to handlers: %s", portal));
    
    mPortal = portal;
    notifyObservers(mPortal);
  }
  
  protected void reportNoPortal()
  {
    Log.d(LOG_TAG, "Reporting NO captive portal to handlers");
    
    mPortal = null;
    notifyObservers(mPortal);
  }
}
