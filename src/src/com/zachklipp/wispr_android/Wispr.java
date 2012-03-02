package com.zachklipp.wispr_android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.net.Uri;
import android.util.Log;

// See http://erratasec.blogspot.com/2010/09/apples-secret-wispr-request.html
public class Wispr
{
  private static final String LOG_TAG = "wispr-android";
  private static final String USER_AGENT = "CaptiveNetworkSupport/1.0 wispr";
  
  // Set by checkForCaptivePortal().
  private Uri portalUri = null;
  private WisprDetector detector;
  
  public static Wispr checkForCaptivePortal()
  {
    Wispr wispr = new Wispr(new AppleWisprDetector());
    wispr.checkForPortalUsingAndroidHttpClient();
    
    return wispr;
  }
  
  private Wispr(WisprDetector detector)
  {
    this.detector = detector;
  }
  
  public boolean isOnCaptivePortal()
  {
    return (portalUri != null);
  }
  
  public Uri getPortalUri()
  {
    return portalUri;
  }

  // If behind a captive portal, set portalUri to the portal
  // login page, else set it to null.
  private void checkForPortalUsingAndroidHttpClient()
  {
    //AndroidHttpClient client = AndroidHttpClient.newInstance(USER_AGENT);
    HttpClient client = new DefaultHttpClient();
    HttpUriRequest request = new HttpGet(detector.getDetectUri());
    request.setHeader("User-Agent", USER_AGENT);
    
    HttpResponse response = executeRequestOrThrow(client, request);
    
    Log.d(LOG_TAG, String.format("Response (%d bytes): %s",
        response.getEntity().getContentLength(),
        response.getStatusLine()));
    
    if (detector.doesResponseIndicatePortal(response))
    {
      portalUri = Uri.parse(detector.getDetectUri());
    }
  }
  private HttpResponse executeRequestOrThrow(HttpClient client, HttpUriRequest request)
  {
    try
    {
      return client.execute(request);
    }
    catch (IOException ex)
    {
      throw new RuntimeException("Error executing request", ex);
    }
  }
}

interface WisprDetector
{
  public String getDetectUri();
  public boolean doesResponseIndicatePortal(HttpResponse response);
}

class AppleWisprDetector implements WisprDetector
{
  private static final String DETECT_URI = "http://www.apple.com/library/test/success.html";
  private static final Pattern SUCCESS_PATTERN = Pattern.compile("^\\s*Success\\s*$");
  
  public String getDetectUri()
  {
    return DETECT_URI;
  }
  
  public boolean doesResponseIndicatePortal(HttpResponse response)
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
