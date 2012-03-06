package com.zachklipp.wispr_android.test;

import junit.framework.TestCase;

import android.net.Uri;

import com.zachklipp.wispr_android.CaptivePortalDetectorService;
import com.zachklipp.wispr_android.CaptivePortalInfo;
import com.zachklipp.wispr_android.PortalStateTracker;

public class PortalStateTrackerTest extends TestCase
{
  private PortalStateTracker mTracker;
  
  public void setUp() throws Exception
  {
    super.setUp();
    
    mTracker = new PortalStateTracker();
  }
  
  public void testDefaults()
  {
    assertEquals(CaptivePortalDetectorService.STATE_UNKNOWN, mTracker.getPortalState());
    assertNull(mTracker.getPortalInfo());
  }

  public void testSetPortalInfo()
  {
    CaptivePortalInfo portal = new CaptivePortalInfo(Uri.EMPTY);
    
    mTracker.setPortalInfo(portal);
    
    assertEquals(CaptivePortalDetectorService.STATE_NEEDS_SIGNIN, mTracker.getPortalState());
    assertSame(portal, mTracker.getPortalInfo());
  }

  public void testClearPortalInfo()
  {
    CaptivePortalInfo portal = new CaptivePortalInfo(Uri.EMPTY);
    
    mTracker.setPortalInfo(portal);
    mTracker.clearPortalInfo();
    
    assertEquals(CaptivePortalDetectorService.STATE_SIGNED_IN, mTracker.getPortalState());
    assertNull(mTracker.getPortalInfo());
  }

}
