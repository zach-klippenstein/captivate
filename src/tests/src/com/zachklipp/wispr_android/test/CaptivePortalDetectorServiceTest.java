package com.zachklipp.wispr_android.test;

import com.zachklipp.wispr_android.CaptivePortalSensor;
import com.zachklipp.wispr_android.CaptivePortalSensorFactory;
import com.zachklipp.wispr_android.CaptivePortalDetectorService;

import android.content.Context;
import android.content.IntentFilter;
import android.test.ServiceTestCase;
import android.util.Log;

public class CaptivePortalDetectorServiceTest extends ServiceTestCase<CaptivePortalDetectorService>
{
  private static final String LOG_TAG = "wispr-android-tests";
  
  private static final CaptivePortalSensorFactory mFactory = new CaptivePortalSensorFactory()
  {
    private static final long serialVersionUID = 5319030221033139374L;

    @Override
    public CaptivePortalSensor createSensor()
    {
      MockCaptivePortalSensor sensor = new MockCaptivePortalSensor();
      sensor.setDetectFakePortal(true);
      return sensor;
    }
  };
  
  private MockBroadcastReceiver mBroadcastReceiver;
  private IntentFilter mPortalStateChangedIntentFilter;
  
  public CaptivePortalDetectorServiceTest()
  {
    super(CaptivePortalDetectorService.class);
  }
  
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    
    mBroadcastReceiver = new MockBroadcastReceiver();
    mPortalStateChangedIntentFilter = new IntentFilter(
        CaptivePortalDetectorService.ACTION_PORTAL_STATE_CHANGED);
    
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
    
    startServiceWithMock();
    mBroadcastReceiver.waitForIntents(1, 2000);

    mContext.unregisterReceiver(mBroadcastReceiver);
    
    assertEquals(1, mBroadcastReceiver.getReceivedIntents().length);
  }
  
  private void startServiceWithMock()
  {
    startService(CaptivePortalDetectorService.createStartIntent(mContext, mFactory));
  }
}
