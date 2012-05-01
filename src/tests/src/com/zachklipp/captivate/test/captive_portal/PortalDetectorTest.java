package com.zachklipp.captivate.test.captive_portal;

import com.zachklipp.captivate.captive_portal.PortalDetector.OverrideMode;

import junit.framework.TestCase;

public class PortalDetectorTest extends TestCase
{
  private MockPortalDetector mDetector;
  private MockPortalDetectorObserver mObserver;
  
  @Override
  protected void setUp()
  {
    mObserver = new MockPortalDetectorObserver();
    mDetector = new MockPortalDetector();
    
    mDetector.addObserver(mObserver);
  }

  public void testOverrideAlwaysDetect()
  {
    mDetector.setTestingOverride(OverrideMode.ALWAYS_DETECT);
    mDetector.checkForPortal();
    
    assertEquals(1, mObserver.getDetectedPortals().length);
    assertEquals(0, mObserver.getDetectedNoPortalCount());
  }
  
  public void testOverrideNeverDetect()
  {
    mDetector.setTestingOverride(OverrideMode.NEVER_DETECT);
    mDetector.checkForPortal();
    
    assertEquals(0, mObserver.getDetectedPortals().length);
    assertEquals(1, mObserver.getDetectedNoPortalCount());
  }
  
  public void testExceptionHandling()
  {
    @SuppressWarnings("serial")
    final RuntimeException testException = new RuntimeException()
    {
      
    };
    
    mDetector = new MockPortalDetector()
    {
      @Override
      protected void onCheckForPortal()
      {
        throw testException;
      }
    };
    
    mDetector.addObserver(mObserver);
    
    try
    {
      mDetector.checkForPortal();
    }
    catch (RuntimeException e)
    {
      if (testException == e)
      {
        fail("Exception not handled, is thrown instead");
      }
      else
      {
        throw e;
      }
    }

    assertEquals(0, mObserver.getDetectedPortals().length);
    assertEquals(1, mObserver.getDetectedNoPortalCount());
  }

}
