package com.zachklipp.wispr_android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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

class HttpResponseContentsSensor implements CaptivePortalSensor
{
  private static final String LOG_TAG = "wispr-android";
  
  private String mUserAgent;
  private String mUrl;
  private Pattern mNoPortalPattern;
  
  public HttpResponseContentsSensor(String userAgent, String url, Pattern noPortalPattern)
  {
    assert(userAgent != null && userAgent.length() > 0);
    assert(url != null && url.length() > 0);
    assert(noPortalPattern != null);
    
    mUserAgent = userAgent;
    mUrl = url;
    mNoPortalPattern = noPortalPattern;
  }
  
  // If behind a captive portal, set portalUri to the portal
  // login page, else set it to null.
  public void checkForCaptivePortal(CaptivePortalDetector detector)
  {
    HttpClient client = new DefaultHttpClient();
    HttpUriRequest request = new HttpGet(mUrl);
    request.setHeader("User-Agent", mUserAgent);
    
    HttpResponse response = executeRequestOrThrow(client, request);
    
    Log.d(LOG_TAG, String.format("Response (%d bytes): %s",
        response.getEntity().getContentLength(),
        response.getStatusLine()));
    
    if (doesResponseIndicatePortal(response.getEntity()))
    {
      detector.reportCaptivePortal(new CaptivePortalInfo(Uri.parse(mUrl)));
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
  
  private boolean doesResponseIndicatePortal(HttpEntity response)
  {
    try
    {
      return doesResponseContentIndicatePortal(response.getContent());
    }
    catch (IOException e)
    {
      throw new RuntimeException("Error reading response", e);
    }
  }
  
  private boolean doesResponseContentIndicatePortal(InputStream response) throws IOException
  {
    BufferedReader reader = new BufferedReader(new InputStreamReader(response));
    String inputLine;
    
    while ((inputLine = reader.readLine()) != null)
    {
      if (doesResponseLineIndicatePortal(inputLine))
        return true;
    }
    
    return false;
  }
  
  private boolean doesResponseLineIndicatePortal(String response)
  {
    return mNoPortalPattern.matcher(response).matches();
  }
}
