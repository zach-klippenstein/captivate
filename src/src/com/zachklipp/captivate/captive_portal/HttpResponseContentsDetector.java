package com.zachklipp.captivate.captive_portal;

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

import com.zachklipp.captivate.util.Log;

public class HttpResponseContentsDetector extends PortalDetector
{
  private static final String USER_AGENT = "CaptiveNetworkSupport/1.0 wispr";
  private static final String URL = "http://www.apple.com/library/test/success.html";
  private static final Pattern NO_PORTAL_PATTERN = Pattern.compile("^\\s*Success\\s*$");
  
  public static PortalDetector createDetector()
  {
    Log.d("Creating HttpResponseContentsDetector");
    
    return new HttpResponseContentsDetector(USER_AGENT, URL, NO_PORTAL_PATTERN);
  }
  
  private String mUserAgent;
  private String mUrl;
  private Pattern mNoPortalPattern;
  
  public HttpResponseContentsDetector(String userAgent, String url, Pattern noPortalPattern)
  {
    assert(userAgent != null && userAgent.length() > 0);
    assert(url != null && url.length() > 0);
    assert(noPortalPattern != null);
    
    mUserAgent = userAgent;
    mUrl = url;
    mNoPortalPattern = noPortalPattern;
  }
  
  @Override
  protected void onCheckForPortal()
  {
    HttpClient client = new DefaultHttpClient();
    HttpUriRequest request = new HttpGet(mUrl);
    request.setHeader("User-Agent", mUserAgent);
    
    HttpResponse response = executeRequestOrThrow(client, request);
    
    Log.d(String.format("Response (%d bytes): %s",
        response.getEntity().getContentLength(),
        response.getStatusLine()));
    
    if (doesResponseIndicatePortal(response.getEntity()))
    {
      reportPortal(new PortalInfo(mUrl));
    }
    else
    {
      reportNoPortal();
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
    boolean onPortal = true;
    
    while ((inputLine = reader.readLine()) != null && onPortal)
    {
      if (doesResponseLineIndicateNoPortal(inputLine))
        onPortal = false;
    }
    
    return onPortal;
  }
  
  private boolean doesResponseLineIndicateNoPortal(String response)
  {
    return mNoPortalPattern.matcher(response).matches();
  }
}
