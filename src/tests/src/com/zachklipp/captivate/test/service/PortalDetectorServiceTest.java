package com.zachklipp.captivate.test.service;

import com.zachklipp.captivate.service.PortalDetectorService;
import com.zachklipp.captivate.state_machine.PortalStateMachine;
import com.zachklipp.captivate.test.captive_portal.MockPortalDetector;
import com.zachklipp.captivate.test.state_machine.MockStorageBackend;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ServiceTestCase;
import android.util.Log;

public class PortalDetectorServiceTest extends ServiceTestCase<PortalDetectorService>
{
  private static final String LOG_TAG = "captivate-tests";
  
  private MockBroadcastReceiver mBroadcastReceiver;
  private MockPortalDetector mDetector;
  private IntentFilter mPortalStateChangedIntentFilter;
  
  public PortalDetectorServiceTest()
  {
    super(PortalDetectorService.class);
  }
  
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    
    setServiceEnabled(true);
    
    mBroadcastReceiver = new MockBroadcastReceiver();
    mPortalStateChangedIntentFilter = new IntentFilter(
        PortalDetectorService.ACTION_PORTAL_STATE_CHANGED);
    
    assertNotNull(mBroadcastReceiver);
    assertNotNull(mContext);
    assertNotNull(mPortalStateChangedIntentFilter);
    
    Log.d(LOG_TAG, "context class: " + mContext.getClass().getName());
    
    mContext.registerReceiver(mBroadcastReceiver, mPortalStateChangedIntentFilter);
    
    mDetector = new MockPortalDetector();
    
    MockStorageBackend.Factory storageFactory = new MockStorageBackend.Factory();
    storageFactory.mLoadFromSave = true;
    storageFactory.mMachineToCreate = new PortalStateMachine(mDetector);
    
    PortalDetectorService.setPortalDetector(mDetector);
    PortalDetectorService.setStorageBackendFactory(storageFactory);
  }
  
  @Override
  protected void tearDown()
  {
    mContext.unregisterReceiver(mBroadcastReceiver);
  }

  public void testSendsBroadcastIntentOnPortalDetected()
  {
    mDetector.setDetectFakePortal(true);
    startService();
    
    assertChangedToState(PortalStateMachine.State.NEEDS_SIGNIN);
  }

  public void testDetectionStateChange()
  {
    mDetector.setDetectFakePortal(true);
    startService();
    assertChangedToState(PortalStateMachine.State.NEEDS_SIGNIN);
    
    mDetector.setDetectFakePortal(false);
    startService();
    assertChangedToState(PortalStateMachine.State.SIGNED_IN);
  }

  public void testDisablePreference()
  {
    mDetector.setDetectFakePortal(true);
    startService();
    assertChangedToState(PortalStateMachine.State.NEEDS_SIGNIN);
    
    // Disable via preferences
    setServiceEnabled(false);
    startService();
    assertChangedToState(PortalStateMachine.State.UNKNOWN);
    
    setServiceEnabled(true);
    startService();
    assertChangedToState(PortalStateMachine.State.NEEDS_SIGNIN);
    
    // Disable via preferences
    setServiceEnabled(false);
    startService();
    assertChangedToState(PortalStateMachine.State.UNKNOWN);
    
    setServiceEnabled(true);
    mDetector.setDetectFakePortal(false);
    startService();
    assertChangedToState(PortalStateMachine.State.NOT_CAPTIVE);
  }
  
  private void startService()
  {
    startService(new Intent(mContext, PortalDetectorService.class));
  }

  private void setServiceEnabled(boolean enabled)
  {
    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
    editor.putBoolean(PortalDetectorService.ENABLED_PREFERENCE_KEY, enabled);
    editor.commit();
  }
  
  private void assertChangedToState(PortalStateMachine.State expectedState)
  {
    mBroadcastReceiver.waitForIntents(1, 30000);
    
    Intent[] receivedIntents = mBroadcastReceiver.getReceivedIntentsAndClear();
    
    assertEquals(1, receivedIntents.length);
    assertState(expectedState, receivedIntents[0]);
  }
  
  private void assertState(PortalStateMachine.State expectedState, Intent receivedIntent)
  {
    String stateName = receivedIntent.getStringExtra(
        PortalDetectorService.EXTRA_CAPTIVE_PORTAL_STATE);
    
    assertEquals(expectedState.getName(), stateName);
  }
}
