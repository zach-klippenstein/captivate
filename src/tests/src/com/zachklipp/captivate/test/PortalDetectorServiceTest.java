package com.zachklipp.captivate.test;

import com.zachklipp.captivate.PortalDetectorService;
import com.zachklipp.captivate.test.captive_portal.MockPortalDetector;

import android.content.Intent;
import android.content.IntentFilter;
import android.test.ServiceTestCase;
import android.util.Log;

public class PortalDetectorServiceTest extends ServiceTestCase<PortalDetectorService>
{
  private static final String LOG_TAG = "captivate-tests";
  
  private MockBroadcastReceiver mBroadcastReceiver;
  private IntentFilter mPortalStateChangedIntentFilter;
  
  public PortalDetectorServiceTest()
  {
    super(PortalDetectorService.class);
  }
  
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    
    mBroadcastReceiver = new MockBroadcastReceiver();
    mPortalStateChangedIntentFilter = new IntentFilter(
        PortalDetectorService.ACTION_PORTAL_STATE_CHANGED);
    
    assertNotNull(mBroadcastReceiver);
    assertNotNull(mContext);
    assertNotNull(mPortalStateChangedIntentFilter);
    
    Log.d(LOG_TAG, "context class: " + mContext.getClass().getName());
  }

  public void testSendsBroadcastIntentOnPortalDetected()
  {
    assertNotNull(mBroadcastReceiver);
    assertNotNull(mContext);
    assertNotNull(mPortalStateChangedIntentFilter);
    mContext.registerReceiver(mBroadcastReceiver, mPortalStateChangedIntentFilter);
    
    MockPortalDetector detector = new MockPortalDetector();
    detector.setDetectFakePortal(true);
    
    PortalDetectorService.setPortalDetector(detector);
    startService(new Intent(mContext, PortalDetectorService.class));
    
    // Wait for max 5 minutes
    mBroadcastReceiver.waitForIntents(1, 300000);

    mContext.unregisterReceiver(mBroadcastReceiver);
    
    assertEquals(1, mBroadcastReceiver.getReceivedIntents().length);
  }
}
