package com.zachklipp.captivate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

public class WebView extends android.webkit.WebView
{
  public WebView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
  }
  
  /**
   * Sets whether the WebView should display on-screen zoom controls when using the built-in
   * zoom mechanisms.
   * If the SDK version is < 11, does nothing.
   * See WebViewSettings.setDisplayZoomControls().
   * @param enabled
   */
  @SuppressLint("NewApi")
  public void setDisplayZoomControls(boolean enabled)
  {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
    {
      getSettings().setDisplayZoomControls(enabled);
    }
  }
}
