package com.zachklipp.wispr_android;

import java.io.Serializable;

public interface CaptivePortalDetectorFactory extends Serializable
{
  public CaptivePortalDetector createDetector();
}
