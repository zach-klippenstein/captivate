package com.zachklipp.captivate.util;

import java.util.Locale;

import android.content.Context;

public final class StringHelper
{
  public static String stringOrEmpty(String str)
  {
    return stringOrDefault(str, "");
  }
  
  public static String stringOrDefault(String str, String defaultValue)
  {
    return str == null ? defaultValue : str;
  }
  
  public static String formatWithResourceStrings(Context context, CharSequence format, int... args)
  {
    Object[] strArgs = new Object[args.length];
    
    for (int i = 0; i < args.length; i++)
    {
      strArgs[i] = context.getString(args[i]);
    }
    
    return format(context, format.toString(), strArgs);
  }
  
  public static String format(Context context, CharSequence format, Object... args)
  {
    Locale locale = context.getResources().getConfiguration().locale;
    return String.format(locale, format.toString(), args);
  }
  
  private StringHelper() { }
}
