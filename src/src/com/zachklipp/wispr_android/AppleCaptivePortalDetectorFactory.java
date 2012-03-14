package com.zachklipp.wispr_android;

import java.util.regex.Pattern;

import android.util.Log;

public class AppleCaptivePortalDetectorFactory implements CaptivePortalSensorFactory
{
  // For Serializable interface.
  private static final long serialVersionUID = 4461544796516726296L;
  private static final String LOG_TAG = "wispr-android";

  private static final String USER_AGENT = "CaptiveNetworkSupport/1.0 wispr";
  private static final String URL = "http://www.apple.com/library/test/success.html";
  private static final Pattern NO_PORTAL_PATTERN = Pattern.compile("^\\s*Success\\s*$");

  @Override
  public CaptivePortalSensor createSensor()
  {
    Log.d(LOG_TAG, "Creating AppleCaptivePortalDetector");
    return new HttpResponseContentsSensor(USER_AGENT, URL, NO_PORTAL_PATTERN);
  }
}
