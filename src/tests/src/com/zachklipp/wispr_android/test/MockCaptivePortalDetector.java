package com.zachklipp.wispr_android.test;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.zachklipp.wispr_android.CaptivePortalInfo;
import com.zachklipp.wispr_android.CaptivePortalDetector;

public class MockCaptivePortalDetector extends CaptivePortalDetector
{
  private static final String LOG_TAG = "wispr-android-tests";
  
  private boolean mDetectFakePortal = false;
  
  public void setDetectFakePortal(boolean enabled)
  {
    mDetectFakePortal = enabled;
  }
  
  @Override
  public void checkForCaptivePortal(Context context)
  {
    if (mDetectFakePortal)
    {
      Log.d(LOG_TAG, "Faking captive portal detection");
      reportCaptivePortal(context, new CaptivePortalInfo(Uri.EMPTY));
    }
    else
    {
      Log.d(LOG_TAG, "Not detecting captive portal");
    }
  }
}