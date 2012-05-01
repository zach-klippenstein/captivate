package com.zachklipp.captivate.util;

/*
 * Wraps android.util.Log to filter by priority.
 * TODO move to library project
 */
public final class Log
{
  private static final int DEFAULT_MIN_PRIORITY = ~0;
  
  private static String sDefaultTag;
  
  private static int sMinPriority = DEFAULT_MIN_PRIORITY;
  
  public static void setDefaultTag(String tag)
  {
    sDefaultTag = tag;
  }
  
  public static void setMinPriority(int priority)
  {
    sMinPriority = priority;
  }
  public static void resetPriority()
  {
    sMinPriority = DEFAULT_MIN_PRIORITY;
  }
  
  public static int e(String msg)
  {
    return e(null, msg);
  }
  public static int e(String tag, String msg)
  {
    return println(android.util.Log.ERROR, tag, msg);
  }
  
  public static int w(String msg)
  {
    return w(null, msg);
  }
  public static int w(String tag, String msg)
  {
    return w(tag, msg, null);
  }
  public static int w(String tag, String msg, Throwable e)
  {
    return println(android.util.Log.WARN, tag, msg, e);
  }
  
  public static int i(String msg)
  {
    return i(null, msg);
  }
  public static int i(String tag, String msg)
  {
    return println(android.util.Log.INFO, tag, msg);
  }
  
  public static int d(String msg)
  {
    return d(null, msg);
  }
  public static int d(String tag, String msg)
  {
    return println(android.util.Log.DEBUG, tag, msg);
  }
  
  public static int v(String msg)
  {
    return v(null, msg);
  }
  public static int v(String tag, String msg)
  {
    return println(android.util.Log.VERBOSE, tag, msg);
  }
  
  public static int println(int priority, String tag, String msg)
  {
    int bytesWritten = 0;
    
    if (priority >= sMinPriority)
    {
      if (tag == null)
      {
        tag = sDefaultTag;
      }
  
      bytesWritten = android.util.Log.println(priority, tag, msg);
    }
    
    return bytesWritten;
  }
  
  private static int println(int priority, String tag, String msg, Throwable e)
  {
    if (e != null)
    {
      msg += "\n" + android.util.Log.getStackTraceString(e);
    }
    
    return println(priority, tag, msg);
  }
}
