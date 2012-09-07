package com.zachklipp.captivate.util;

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
  
  private StringHelper() { }
}
