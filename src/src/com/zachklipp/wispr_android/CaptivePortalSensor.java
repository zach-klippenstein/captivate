package com.zachklipp.wispr_android;

public interface CaptivePortalSensor
{
  // Should eventually call detector.reportCaptivePortal().
  public void checkForCaptivePortal(CaptivePortalDetector detector);
}
