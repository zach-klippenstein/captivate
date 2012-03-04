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

import android.content.Context;
import android.net.Uri;
import android.util.Log;

public abstract class AndroidHttpClientCaptivePortalDetector extends CaptivePortalDetector
{
  private static final String LOG_TAG = "wispr-android";
  private static final String USER_AGENT = "CaptiveNetworkSupport/1.0 wispr";
  
  protected abstract String getDetectUri();
  protected abstract boolean doesResponseIndicatePortal(HttpResponse response);

  // If behind a captive portal, set portalUri to the portal
  // login page, else set it to null.
  @Override
  public void checkForCaptivePortal(Context context)
  {
    //AndroidHttpClient client = AndroidHttpClient.newInstance(USER_AGENT);
    HttpClient client = new DefaultHttpClient();
    HttpUriRequest request = new HttpGet(getDetectUri());
    request.setHeader("User-Agent", USER_AGENT);
    
    HttpResponse response = executeRequestOrThrow(client, request);
    
    Log.d(LOG_TAG, String.format("Response (%d bytes): %s",
        response.getEntity().getContentLength(),
        response.getStatusLine()));
    
    if (doesResponseIndicatePortal(response))
    {
      reportCaptivePortal(context, new CaptivePortal(Uri.parse(getDetectUri())));
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
