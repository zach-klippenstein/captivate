package com.zachklipp.wispr_android.test.captive_portal;

import java.util.ArrayList;

import com.zachklipp.wispr_android.captive_portal.PortalInfo;
import com.zachklipp.wispr_android.util.Observable;
import com.zachklipp.wispr_android.util.Observer;

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
