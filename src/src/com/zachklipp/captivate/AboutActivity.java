package com.zachklipp.captivate;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class AboutActivity extends Activity
{
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
      setContentView(R.layout.about_layout);
      
      WebView view = (WebView) findViewById(R.id.about_view);
      
      // This is to prevent some devices from showing white artifacts at the edge of the screen.
      view.setBackgroundColor(0);
      
      view.loadUrl("file:///android_res/raw/about.html");
  }
}
