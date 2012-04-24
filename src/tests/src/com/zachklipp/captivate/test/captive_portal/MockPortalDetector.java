package com.zachklipp.captivate.test.captive_portal;

import android.net.Uri;
import android.util.Log;

import com.zachklipp.captivate.captive_portal.PortalDetector;
import com.zachklipp.captivate.captive_portal.PortalInfo;

public class MockPortalDetector extends PortalDetector
{
  private static final String LOG_TAG = "captivate-tests";
  
  private boolean mDetectFakePortal = false;
  
  public void setDetectFakePortal(boolean enabled)
  {
    mDetectFakePortal = enabled;
    Log.d(LOG_TAG, "Portal detection " + (enabled ? "enabled" : "disabled"));
  }

  @Override
  public void checkForPortal()
  {
    if (mDetectFakePortal)
    {
      Log.d(LOG_TAG, "Faking captive portal detection");
      reportPortal(new PortalInfo(Uri.EMPTY));
    }
    else
    {
      Log.d(LOG_TAG, "Not detecting captive portal");
      reportNoPortal();
    }
  }
}