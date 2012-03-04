package com.zachklipp.wispr_android.test;

import android.content.Context;
import android.net.Uri;

import com.zachklipp.wispr_android.CaptivePortal;
import com.zachklipp.wispr_android.CaptivePortalDetector;

class MockCaptivePortalDetector extends CaptivePortalDetector
{
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
      reportCaptivePortal(context, new CaptivePortal(Uri.EMPTY));
    }
  }
}