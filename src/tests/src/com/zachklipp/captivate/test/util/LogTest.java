package com.zachklipp.captivate.test.util;

import com.zachklipp.captivate.util.Log;

import junit.framework.TestCase;

public class LogTest extends TestCase
{
  private static final String LOG_TAG = "tag";
  private static final String LOG_MSG = "msg";
  
  @Override
  protected void setUp()
  {
    Log.resetPriority();
    Log.setDefaultTag(LOG_TAG);
  }

  public void testW()
  {
    assertEquals(calcMsgBytes(LOG_MSG), Log.w(LOG_MSG));
  }

  public void testI()
  {
    assertEquals(calcMsgBytes(LOG_MSG), Log.i(LOG_MSG));
  }

  public void testD()
  {
    assertEquals(calcMsgBytes(LOG_MSG), Log.d(LOG_MSG));
  }

  public void testV()
  {
    assertEquals(calcMsgBytes(LOG_MSG), Log.v(LOG_MSG));
  }

  public void testFiltering()
  {
    Log.setMinPriority(android.util.Log.INFO);
    assertEquals(0, Log.d(LOG_MSG));
    assertEquals(0, Log.v(LOG_MSG));

    Log.setMinPriority(android.util.Log.DEBUG);
    assertEquals(calcMsgBytes(LOG_MSG), Log.d(LOG_MSG));
    assertEquals(0, Log.v(LOG_MSG));
    
    Log.setMinPriority(android.util.Log.VERBOSE);
    assertEquals(calcMsgBytes(LOG_MSG), Log.d(LOG_MSG));
    assertEquals(calcMsgBytes(LOG_MSG), Log.v(LOG_MSG));
  }
  
  private int calcMsgBytes(String msg)
  {
    return msg.length() + LOG_TAG.length() + 3;
  }

}
