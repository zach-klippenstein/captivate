package com.zachklipp.captivate.test.captive_portal;

import android.content.Context;
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
  
  public void checkForPortal()
  {
    checkForPortal(null);
  }

  /*
   * Disable Wifi checks for testing.
   */
  @Override
  public void checkForPortal(Context context)
  {
    onCheckForPortal();
  }

  @Override
  protected void onCheckForPortal()
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