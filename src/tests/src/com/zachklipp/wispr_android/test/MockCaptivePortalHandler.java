package com.zachklipp.wispr_android.test;

import java.util.ArrayList;

import android.content.Context;

import com.zachklipp.wispr_android.CaptivePortalInfo;
import com.zachklipp.wispr_android.CaptivePortalHandler;

public class MockCaptivePortalHandler implements CaptivePortalHandler
{
  private ArrayList<CaptivePortalInfo> mDetectedPortals = new ArrayList<CaptivePortalInfo>();

  @Override
  public void onCaptivePortalDetected(Context context, CaptivePortalInfo portal)
  {
    mDetectedPortals.add(portal);
  }
  
  public CaptivePortalInfo[] getDetectedPortals()
  {
    CaptivePortalInfo[] portals = new CaptivePortalInfo[mDetectedPortals.size()];
    return mDetectedPortals.toArray(portals);
  }

  @Override
  public void onNoCaptivePortalDetected(Context context)
  {
  }
}
