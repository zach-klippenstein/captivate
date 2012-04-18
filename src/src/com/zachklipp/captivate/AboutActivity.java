package com.zachklipp.captivate;

import com.zachklipp.captivate.util.VersionNameInjector;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class AboutActivity extends Activity
{
  private final static String ABOUT_CHARSET = "UTF-8";
  
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
          ABOUT_CHARSET,
          null);
  }
  
  private String getAboutHtml()
  {
    VersionNameInjector injector = new VersionNameInjector(this, ABOUT_CHARSET);
    return injector.injectVersionNameIntoHtmlResource(R.raw.about);
  }
}
