package com.zachklipp.captivate.captive_portal;

import com.zachklipp.captivate.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class HttpResponseContentsDetector extends PortalDetector
{
  private static final String URL = "http://www.apple.com/library/test/success.html";
  private static final Pattern NO_PORTAL_PATTERN = Pattern.compile(
      "<TITLE>Success</TITLE></HEAD><BODY>Success</BODY>");
  
  public static PortalDetector createDetector()
  {
    Log.d("Creating HttpResponseContentsDetector");
    
    return new HttpResponseContentsDetector(URL, NO_PORTAL_PATTERN);
  }
  
  private String mUrl;
  private Pattern mNoPortalPattern;
  
  public HttpResponseContentsDetector(String url, Pattern noPortalPattern)
  {
    assert(url != null && url.length() > 0);
    assert(noPortalPattern != null);
    
    mUrl = url;
    mNoPortalPattern = noPortalPattern;
  }
  
  @Override
  protected void onCheckForPortal()
  {
    HttpClient client = new DefaultHttpClient();
    HttpUriRequest request = new HttpGet(mUrl);
    
    HttpResponse response = executeRequestOrThrow(client, request);
    
    Log.d("Response (%d bytes): %s",
        response.getEntity().getContentLength(),
        response.getStatusLine());
    
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
    // Don't use matches(), cause that requires the ENTIRE string to match
    return mNoPortalPattern.matcher(response).find();
  }
}
