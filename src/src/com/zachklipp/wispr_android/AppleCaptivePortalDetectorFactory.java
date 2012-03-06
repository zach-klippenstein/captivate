package com.zachklipp.wispr_android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import android.util.Log;

public class AppleCaptivePortalDetectorFactory implements CaptivePortalDetectorFactory
{
  // For Serializable interface.
  private static final long serialVersionUID = 4461544796516726296L;
  private static final String LOG_TAG = "wispr-android";

  @Override
  public CaptivePortalDetector createDetector()
  {
    Log.d(LOG_TAG, "Creating AppleCaptivePortalDetector");
    return new AppleCaptivePortalDetector();
  }
}

class AppleCaptivePortalDetector extends AndroidHttpClientCaptivePortalDetector
{
  private static final String DETECT_URI = "http://www.apple.com/library/test/success.html";
  private static final Pattern SUCCESS_PATTERN = Pattern.compile("^\\s*Success\\s*$");
  
  protected String getDetectUri()
  {
    return DETECT_URI;
  }
  
  protected boolean doesResponseIndicatePortal(HttpResponse response)
  {
    return !doesEntityMatch(response.getEntity(), SUCCESS_PATTERN);
  }
  
  private boolean doesEntityMatch(HttpEntity entity, Pattern pattern)
  {
    try
    {
      BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
      String inputLine;
      
      while ((inputLine = reader.readLine()) != null)
      {
        if (pattern.matcher(inputLine).matches())
          return true;
      }
      
      return false;
    }
    catch (IOException ex)
    {
      throw new RuntimeException("Error reading response", ex);
    }
  }
}
