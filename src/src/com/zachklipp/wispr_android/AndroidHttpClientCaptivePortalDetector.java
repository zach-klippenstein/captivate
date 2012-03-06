package com.zachklipp.wispr_android;

import java.io.IOException;

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
      reportCaptivePortal(context, new CaptivePortalInfo(Uri.parse(getDetectUri())));
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
