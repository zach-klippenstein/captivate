package com.zachklipp.captivate.captive_portal;

import com.zachklipp.captivate.util.Log;
import com.zachklipp.captivate.util.Observable;

// See http://erratasec.blogspot.com/2010/09/apples-secret-wispr-request.html
public abstract class PortalDetector extends Observable<PortalInfo>
{
  public interface Factory
  {
    public PortalDetector create();
  }
  
  public enum OverrideMode
  {
    NONE,
    ALWAYS_DETECT,
    NEVER_DETECT
  }
  
  private static final String LOG_TAG = "PortalDetector";
  
  private PortalInfo mPortal;
  private OverrideMode mOverrideMode = OverrideMode.NONE;
  
  public PortalDetector()
  {
    super();
  }
  
  public PortalDetector(PortalInfo portal)
  {
    super();
    
    mPortal = portal;
  }

  /*
   *  Should eventually call reportPortal or reportNoPortal
   */
  protected abstract void onCheckForPortal();
  
  public void checkForPortal()
  {
    switch (mOverrideMode)
    {
      case NONE:
        tryCheckForPortal();
        break;
        
      case ALWAYS_DETECT:
        Log.i(LOG_TAG, "Overriding captive portal detector");
        reportPortal(new PortalInfo("http://www.google.com"));
        break;
        
      case NEVER_DETECT:
        Log.i(LOG_TAG, "Overriding captive portal detector with no portal");
        reportNoPortal();
        break;
    }
  }
  
  public boolean isOnPortal()
  {
    return (mPortal != null);
  }
  
  public PortalInfo getPortal()
  {
    return mPortal;
  }
  
  public void setPortalOverride(OverrideMode mode)
  {
    if (null == mode)
    {
      mode = OverrideMode.NONE;
    }
    
    if (mode != mOverrideMode)
    {
      if (OverrideMode.ALWAYS_DETECT == mode)
      {
        mPortal = new PortalInfo();
      }
      else
      {
        mPortal = null;
      }
      
      Log.d(LOG_TAG, "Setting override mode to " + mode);
      
      mOverrideMode = mode;
    }
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
  
  private void tryCheckForPortal()
  {
    try
    {
      onCheckForPortal();
    }
    catch (Exception e)
    {
      Log.w(LOG_TAG, "Error checking for portal", e);
      reportNoPortal();
    }
  }
}
