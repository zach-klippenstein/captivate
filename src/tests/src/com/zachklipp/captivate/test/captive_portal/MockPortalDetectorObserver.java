package com.zachklipp.captivate.test.captive_portal;

import java.util.ArrayList;

import com.zachklipp.captivate.captive_portal.PortalInfo;
import com.zachklipp.captivate.util.Observable;
import com.zachklipp.captivate.util.Observer;

public class MockPortalDetectorObserver implements Observer<PortalInfo>
{
  private ArrayList<PortalInfo> mDetectedPortals = new ArrayList<PortalInfo>();
  private long mDetectedNoPortalCount = 0;

  public PortalInfo[] getDetectedPortals()
  {
    PortalInfo[] portals = new PortalInfo[mDetectedPortals.size()];
    return mDetectedPortals.toArray(portals);
  }
  
  public long GetDetectedNoPortalCount()
  {
    return mDetectedNoPortalCount;
  }
  
  @Override
  public void update(Observable<PortalInfo> observable, PortalInfo portal)
  {
    if (portal != null)
    {
      mDetectedPortals.add(portal);
    }
    else
    {
      mDetectedNoPortalCount++;
    }
  }
}
