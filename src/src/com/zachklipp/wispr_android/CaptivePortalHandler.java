package com.zachklipp.wispr_android;

import android.content.Context;

public interface CaptivePortalHandler
{
  public void onCaptivePortalDetected(Context context, CaptivePortal portal);
}
