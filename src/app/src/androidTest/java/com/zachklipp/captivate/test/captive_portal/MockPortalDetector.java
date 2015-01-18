package com.zachklipp.captivate.test.captive_portal;

import android.util.Log;

import com.zachklipp.captivate.captive_portal.PortalDetector;

public class MockPortalDetector extends PortalDetector
{
  private static final String LOG_TAG = "MockPortalDetector";
  
  /*
   * Disable for testing.
   * (non-Javadoc)
   * @see com.zachklipp.captivate.captive_portal.PortalDetector#setPortalOverride(com.zachklipp.captivate.captive_portal.PortalDetector.OverrideMode)
   */
  @Override
  public void setPortalOverride(OverrideMode mode)
  {
    Log.w(LOG_TAG, "Tests should call setTestingOverride() instead of setPortalOverride()");
  }
  
  public void setTestingOverride(OverrideMode mode)
  {
    super.setPortalOverride(mode);
  }
  
  @Override
  protected void onCheckForPortal()
  {
    throw new IllegalStateException("Mock portal detector override not set");
  }
}