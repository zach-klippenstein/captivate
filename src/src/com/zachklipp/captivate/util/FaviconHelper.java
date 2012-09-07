package com.zachklipp.captivate.util;

import android.net.Uri;

public class FaviconHelper
{
  public static String createFaviconUrl(String url)
  {
    return Uri.parse(url).buildUpon().path("favicon.ico").toString();
  }
  
  private FaviconHelper() { }
}
