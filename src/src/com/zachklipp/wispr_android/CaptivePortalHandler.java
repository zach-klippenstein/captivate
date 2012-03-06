package com.zachklipp.wispr_android;

import android.content.Context;

public interface CaptivePortalHandler
{
  public void onCaptivePortalDetected(Context context, CaptivePortalInfo portal);
  public void onNoCaptivePortalDetected(Context context);
}
