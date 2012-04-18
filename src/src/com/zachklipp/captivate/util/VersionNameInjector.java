package com.zachklipp.captivate.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/*
 * Helper for using the about HTML file as a template, and injecting the
 * version name as set in AndroidManifest.xml.
 */
public class VersionNameInjector
{
  private final static String LOG_TAG = "captivate";
  
  private String mCharsetName;
  private Context mContext;
  
  public VersionNameInjector(Context context, String charsetName)
  {
    mContext = context;
    mCharsetName = charsetName;
  }
  
  public String injectVersionNameIntoHtmlResource(int resId)
  {
    return injectVersionNameIntoHtmlStream(mContext.getResources().openRawResource(resId));
  }
  
  private String injectVersionNameIntoHtmlStream(InputStream input)
  {
    return injectVersionNameIntoHtml(readStream(input));
  }
  
  // TODO this whole chain should return InputStreams if possible
  private String injectVersionNameIntoHtml(String html)
  {
    try
    {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document document = builder.parse(new ByteArrayInputStream(html.getBytes(mCharsetName)));
      Element versionElement = document.getElementById("version-number");
      versionElement.setTextContent(getVersionName());
      
      TransformerFactory transFactory = TransformerFactory.newInstance();
      Transformer transformer = transFactory.newTransformer();
      StringWriter buffer = new StringWriter();
      transformer.transform(new DOMSource(document), new StreamResult(buffer));
      html = buffer.toString();
    }
    catch (Exception e)
    {
      Log.v(LOG_TAG, e.getMessage());
    }
    
    return html;
  }
  
  private String readStream(InputStream input)
  {
    String contents = "";
    
    try
    {
      char[] buffer = new char[input.available()];
      new InputStreamReader(input, mCharsetName).read(buffer);
      contents = new String(buffer);
    }
    catch (IOException e)
    {
      Log.v(LOG_TAG, e.getMessage());
    }
    
    return contents;
  }
  
  private String getVersionName()
  {
    String versionName = "unknown";
    
    try
    {
      versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
    }
    catch (NameNotFoundException e)
    {
      Log.v(LOG_TAG, e.getMessage());
    }
    
    return versionName;
  }
}
