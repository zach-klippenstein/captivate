package com.zachklipp.captivate.util;

/*
 * Wraps android.util.Log to filter by priority.
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

  /**
   * Log a message with the default tag.
   * @param msg
   * @param args
   * @return
   */
  public static int e(String msg, Object... args)
  {
    return e(null, msg, args);
  }
  public static int e(String tag, String msg, Object... args)
  {
    return println(android.util.Log.ERROR, tag, msg, args);
  }

  /**
   * Log a message with the default tag.
   * @param msg
   * @param args
   * @return
   */
  public static int w(String msg, Object... args)
  {
    return w(null, msg, args);
  }
  public static int w(String tag, String msg, Object... args)
  {
    return w(tag, msg, null, args);
  }
  public static int w(String tag, String msg, Throwable e, Object... args)
  {
    return println(android.util.Log.WARN, tag, msg, e, args);
  }

  /**
   * Log a message with the default tag.
   * @param msg
   * @param args
   * @return
   */
  public static int i(String msg, Object... args)
  {
    return i(null, msg, args);
  }
  public static int i(String tag, String msg, Object... args)
  {
    return println(android.util.Log.INFO, tag, msg, args);
  }
  
  /**
   * Log a message with the default tag.
   * @param msg
   * @param args
   * @return
   */
  public static int d(String msg, Object... args)
  {
    return d(null, msg, args);
  }
  public static int d(String tag, String msg, Object... args)
  {
    return println(android.util.Log.DEBUG, tag, msg, args);
  }

  /**
   * Log a message with the default tag.
   * @param msg
   * @param args
   * @return
   */
  public static int v(String msg, Object... args)
  {
    return v(null, msg, args);
  }
  public static int v(String tag, String msg, Object... args)
  {
    return println(android.util.Log.VERBOSE, tag, msg, args);
  }
  
  public static int println(int priority, String tag, String msg, Object... args)
  {
    int bytesWritten = 0;
    
    if (priority >= sMinPriority)
    {
      if (tag == null)
      {
        tag = sDefaultTag;
      }
      
      // Don't use StringHelper.format because we don't want log messages
      // in other languages.
      bytesWritten = android.util.Log.println(priority, tag, String.format(msg, args));
    }
    
    return bytesWritten;
  }
  
  private static int println(int priority, String tag, String msg, Throwable e, Object... args)
  {
    if (e != null)
    {
      msg += "\n" + android.util.Log.getStackTraceString(e);
    }
    
    return println(priority, tag, msg, args);
  }
}
