package com.zachklipp.wispr_android;

import java.io.Serializable;

// Extends Serializable instead of Parcelable because Serializable is simpler
// to implement for classes that don't need to store any state, which most
// factories shouldn't.
public interface CaptivePortalDetectorFactory extends Serializable
{
  public CaptivePortalDetector createDetector();
}
