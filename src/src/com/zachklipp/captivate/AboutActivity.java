package com.zachklipp.captivate;

import com.actionbarsherlock.app.SherlockActivity;
import com.zachklipp.captivate.util.Log;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

public class AboutActivity extends SherlockActivity
{
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.about_layout);
      
      setTitle(getString(R.string.about_title, getString(R.string.app_name)));
      
      TextView versionName = (TextView) findViewById(R.id.version_name_view);
      versionName.setText(getString(R.string.version_name_format, getVersionName()));
      
      WebView view = (WebView) findViewById(R.id.about_content_view);
      
      // This is to prevent some devices from showing white artifacts at the edge of the screen.
      view.setBackgroundColor(0);
      
      view.loadUrl("file:///android_res/raw/about.html");
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
      Log.v(e.getMessage());
    }
    
    return versionName;
  }
}
