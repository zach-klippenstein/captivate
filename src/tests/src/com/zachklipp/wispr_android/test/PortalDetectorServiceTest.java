package com.zachklipp.wispr_android.test;

import com.zachklipp.wispr_android.PortalDetectorService;
//import com.zachklipp.wispr_android.captive_portal.PortalDetector;

import android.content.Intent;
import android.content.IntentFilter;
import android.test.ServiceTestCase;
import android.util.Log;

public class PortalDetectorServiceTest extends ServiceTestCase<PortalDetectorService>
{
  private static final String LOG_TAG = "wispr-android-tests";
  
  /*private static final CaptivePortalSensorFactory mFactory = new CaptivePortalSensorFactory()
  {
    private static final long serialVersionUID = 5319030221033139374L;

    @Override
    public CaptivePortalSensor createSensor()
    {
      MockPortalDetector sensor = new MockPortalDetector();
      sensor.setDetectFakePortal(true);
      return sensor;
    }
  };*/
  
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
