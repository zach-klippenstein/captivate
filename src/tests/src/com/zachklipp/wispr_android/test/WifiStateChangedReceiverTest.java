package com.zachklipp.wispr_android.test;

import java.util.ArrayList;

import com.zachklipp.wispr_android.CaptivePortal;
import com.zachklipp.wispr_android.CaptivePortalHandler;
import com.zachklipp.wispr_android.WifiStateChangedReceiver;

import android.content.Context;
import android.test.AndroidTestCase;

// See http://stackoverflow.com/questions/5769315/unit-testing-a-broadcast-receiver
public class WifiStateChangedReceiverTest extends AndroidTestCase
{
  private WifiStateChangedReceiver mReceiver;
  private TestPortalHandler mPortalHandler;

  protected void setUp() throws Exception
  {
    super.setUp();
    
    mPortalHandler = new TestPortalHandler();
  }

  public void testOnWifiConnectedInvokesDetector()
  {
    TestDetector detector = new TestDetector();
    mReceiver = new WifiStateChangedReceiver(detector);
    
    mReceiver.onWifiConnected();
    
    assertEquals(1, detector.getNumberTimesCheckCalled());
  }

  public void testOnWifiConnectedInvokesHandler()
  {
    MockCaptivePortalDetector detector = new MockCaptivePortalDetector();
    mReceiver = new WifiStateChangedReceiver(detector, mPortalHandler);
    
    mReceiver.onWifiConnected();
    assertEquals(0, mPortalHandler.getDetectedPortals().size());

    detector.setDetectFakePortal(true);
    mReceiver.onWifiConnected();
    assertEquals(1, mPortalHandler.getDetectedPortals().size());
  }

}

class TestDetector extends MockCaptivePortalDetector
{
  private int mNumberTimesCheckCalled = 0;
  
  @Override
  public void checkForCaptivePortal(Context context)
  {
    mNumberTimesCheckCalled++;
  }
  
  public int getNumberTimesCheckCalled()
  {
    return mNumberTimesCheckCalled;
  }
}

class TestPortalHandler implements CaptivePortalHandler
{
  private ArrayList<CaptivePortal> mDetectedPortals = new ArrayList<CaptivePortal>();

  @Override
  public void onCaptivePortalDetected(Context context, CaptivePortal portal)
  {
    mDetectedPortals.add(portal);
  }
  
  public ArrayList<CaptivePortal> getDetectedPortals()
  {
    return mDetectedPortals;
  }
}
