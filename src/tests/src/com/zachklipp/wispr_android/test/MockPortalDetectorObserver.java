package com.zachklipp.wispr_android.test;

import java.util.ArrayList;

import com.zachklipp.wispr_android.captive_portal.PortalInfo;
import com.zachklipp.wispr_android.util.Observable;
import com.zachklipp.wispr_android.util.Observer;

public class MockPortalDetectorObserver implements Observer<PortalInfo>
{
  private ArrayList<PortalInfo> mDetectedPortals = new ArrayList<PortalInfo>();

  public PortalInfo[] getDetectedPortals()
  {
    PortalInfo[] portals = new PortalInfo[mDetectedPortals.size()];
    return mDetectedPortals.toArray(portals);
  }
  
  @Override
  public void update(Observable<PortalInfo> observable, PortalInfo portal)
  {
    if (portal != null)
    {
      mDetectedPortals.add(portal);
    }
  }
}
