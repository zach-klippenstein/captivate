package com.zachklipp.captivate;

import java.nio.charset.Charset;

import com.zachklipp.captivate.util.HtmlResourceTemplate;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class AboutActivity extends Activity
{
  private final static String LOG_TAG = "captivate";
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
      setContentView(R.layout.about_layout);
      
      WebView view = (WebView) findViewById(R.id.about_view);
      
      // This is to prevent some devices from showing white artifacts at the edge of the screen.
      view.setBackgroundColor(0);
      
      // Must use this variation of load() to get image to load.
      view.loadDataWithBaseURL("file:///android_res",
          getAboutHtml(),
          "text/html",
          Charset.defaultCharset().name(),
          null);
  }
  
  private String getAboutHtml()
  {
    return new HtmlResourceTemplate()
      .withValues("version-name", getVersionName())
      .render(this, R.raw.about);
  }
  
  private String getVersionName()
  {
    String versionName = "unknown";
    
    try
    {
      versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
    }
    catch (NameNotFoundException e)
    {
      Log.v(LOG_TAG, e.getMessage());
    }
    
    return versionName;
  }
}
