package com.zachklipp.wispr_android.test;


import com.zachklipp.wispr_android.CaptivePortalDetector;
import com.zachklipp.wispr_android.CaptivePortalDetectorFactory;
import com.zachklipp.wispr_android.CaptivePortalDetectorService;

import android.content.IntentFilter;
import android.test.ServiceTestCase;

public class CaptivePortalDetectorServiceTest extends ServiceTestCase<CaptivePortalDetectorService>
{
  public CaptivePortalDetectorServiceTest()
  {
    super(CaptivePortalDetectorService.class);
  }

  public void testSendsBroadcastIntentOnPortalDetected()
  {
    CaptivePortalDetectorFactory factory = new CaptivePortalDetectorFactory()
    {
      private static final long serialVersionUID = 5319030221033139374L;

      @Override
      public CaptivePortalDetector createDetector()
      {
        MockCaptivePortalDetector detector = new MockCaptivePortalDetector();
        detector.setDetectFakePortal(true);
        return detector;
      }
    };
    MockBroadcastReceiver broadcastReceiver = new MockBroadcastReceiver();
    IntentFilter intentFilter = new IntentFilter(CaptivePortalDetectorService.ACTION_PORTAL_STATE_CHANGED);
    
    getSystemContext().registerReceiver(broadcastReceiver, intentFilter);
    
    startService(CaptivePortalDetectorService.createStartIntent(getSystemContext(), factory));
    
    broadcastReceiver.waitForIntents(1, 1000);
    getSystemContext().unregisterReceiver(broadcastReceiver);
    
    assertEquals(1, broadcastReceiver.getReceivedIntents().length);
  }
}
