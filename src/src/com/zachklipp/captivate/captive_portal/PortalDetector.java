package com.zachklipp.captivate.captive_portal;

import android.content.Context;

import com.zachklipp.captivate.util.Log;
import com.zachklipp.captivate.util.Observable;
import com.zachklipp.captivate.util.WifiHelper;

// See http://erratasec.blogspot.com/2010/09/apples-secret-wispr-request.html
public abstract class PortalDetector extends Observable<PortalInfo>
{
  public interface Factory
  {
    public PortalDetector create();
  }
  
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
  protected abstract void onCheckForPortal();
  
  public void checkForPortal(Context context)
  {
    if (WifiHelper.isConnectedFromContext(context))
    {
      onCheckForPortal();
    }
    else
    {
      Log.d("Not on wifi, skipping portal check");
      
      reportNoPortal();
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
  
  protected void reportPortal(PortalInfo portal)
  {
    Log.d(String.format("Reporting captive portal to handlers: %s", portal));
    
    mPortal = portal;
    notifyObservers(mPortal);
  }
  
  protected void reportNoPortal()
  {
    Log.d("Reporting NO captive portal to handlers");
    
    mPortal = null;
    notifyObservers(mPortal);
  }
}
