package com.zachklipp.captivate.test.service;

import com.zachklipp.captivate.Preferences;
import com.zachklipp.captivate.captive_portal.PortalDetector;
import com.zachklipp.captivate.captive_portal.PortalDetector.OverrideMode;
import com.zachklipp.captivate.service.PortalDetectorService;
import com.zachklipp.captivate.state_machine.PortalStateMachine;
import com.zachklipp.captivate.state_machine.StateMachine;
import com.zachklipp.captivate.state_machine.PortalStateMachine.StorageBackendFactory;
import com.zachklipp.captivate.state_machine.StateMachineStorage.StorageBackend;
import com.zachklipp.captivate.test.captive_portal.MockPortalDetector;
import com.zachklipp.captivate.test.state_machine.MockStorageBackend;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ServiceTestCase;
import android.util.Log;

/*
 * These tests currently require Wifi to be on on the testing device.
 */
public class PortalDetectorServiceTest extends ServiceTestCase<PortalDetectorService>
{
  private static final String LOG_TAG = "captivate-tests";
  
  private MockBroadcastReceiver mBroadcastReceiver;
  private MockPortalDetector mDetector;
  private IntentFilter mPortalStateChangedIntentFilter;
  
  PortalDetector.Factory mDetectorFactory = new PortalDetector.Factory()
  {
    @Override
    public PortalDetector create()
    {
      return mDetector;
    }
  };
  
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
    
    mDetector = new MockPortalDetector();
    
    mContext.registerReceiver(mBroadcastReceiver, mPortalStateChangedIntentFilter);
    
    MockPortalStorageBackend.Factory storageFactory = new MockPortalStorageBackend.Factory();
    storageFactory.mLoadFromSave = true;
    
    PortalDetectorService.setPortalDetectorFactory(mDetectorFactory);
    PortalDetectorService.setStorageBackendFactory(storageFactory);
  }
  
  @Override
  protected void tearDown()
  {
    mContext.unregisterReceiver(mBroadcastReceiver);
  }

  public void testSendsBroadcastIntentOnPortalDetected()
  {
    mDetector.setTestingOverride(OverrideMode.ALWAYS_DETECT);
    startService();
    
    assertChangedToState(PortalStateMachine.State.SIGNIN_REQUIRED);
  }

  /*
  public void testDetectionStateChange()
  {
    mDetector.setTestingOverride(OverrideMode.ALWAYS_DETECT);
    startService();
    assertChangedToState(PortalStateMachine.State.SIGNIN_REQUIRED);

    mDetector.setTestingOverride(OverrideMode.NEVER_DETECT);
    startService();
    assertChangedToState(PortalStateMachine.State.SIGNED_IN);
  }

  public void testDisablePreference()
  {
    mDetector.setTestingOverride(OverrideMode.ALWAYS_DETECT);
    startService();
    assertChangedToState(PortalStateMachine.State.SIGNIN_REQUIRED);
    
    // Disable via preferences
    setServiceEnabled(false);
    startService();
    assertChangedToState(PortalStateMachine.State.UNKNOWN);
    
    setServiceEnabled(true);
    startService();
    assertChangedToState(PortalStateMachine.State.SIGNIN_REQUIRED);
    
    // Disable via preferences
    setServiceEnabled(false);
    startService();
    assertChangedToState(PortalStateMachine.State.UNKNOWN);
    
    setServiceEnabled(true);
    mDetector.setTestingOverride(OverrideMode.NEVER_DETECT);
    startService();
    assertChangedToState(PortalStateMachine.State.NO_PORTAL);
  }
  */
  
  private void startService()
  {
    startService(new Intent(mContext, PortalDetectorService.class));
  }

  private void setServiceEnabled(boolean enabled)
  {
    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
    editor.putBoolean(Preferences.ENABLED_KEY, enabled);
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
        PortalDetectorService.EXTRA_PORTAL_STATE);
    
    assertEquals(expectedState.getName(), stateName);
  }
}

class MockPortalStorageBackend extends MockStorageBackend
{
  public static class Factory implements StorageBackendFactory
  {
    public boolean mLoadFromSave = false;
    
    @Override
    public StorageBackend create(Context context, PortalDetector detector)
    {
      MockPortalStorageBackend backend = new MockPortalStorageBackend(detector);
      
      backend.setLoadFromSave(mLoadFromSave);
      
      return backend;
    }
  }
  
  private PortalDetector mDetector;
  
  public MockPortalStorageBackend(PortalDetector detector)
  {
    mDetector = detector;
  }

  @Override
  public StateMachine create()
  {
    super.create();
    
    return new PortalStateMachine(mDetector);
  }
}
