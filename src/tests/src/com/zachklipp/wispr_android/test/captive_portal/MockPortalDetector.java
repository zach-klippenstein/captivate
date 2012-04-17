package com.zachklipp.wispr_android.test.captive_portal;

import android.net.Uri;
import android.util.Log;

import com.zachklipp.wispr_android.captive_portal.PortalDetector;
import com.zachklipp.wispr_android.captive_portal.PortalInfo;

public class MockPortalDetector extends PortalDetector
{
  private static final String LOG_TAG = "wispr-android-tests";
  
  private boolean mDetectFakePortal = false;
  
  public void setDetectFakePortal(boolean enabled)
  {
    mDetectFakePortal = enabled;
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