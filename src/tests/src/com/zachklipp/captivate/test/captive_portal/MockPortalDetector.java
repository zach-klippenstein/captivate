package com.zachklipp.captivate.test.captive_portal;

import com.zachklipp.captivate.captive_portal.PortalDetector;

public class MockPortalDetector extends PortalDetector
{
  /*
   * Disable for testing.
   * (non-Javadoc)
   * @see com.zachklipp.captivate.captive_portal.PortalDetector#setPortalOverride(com.zachklipp.captivate.captive_portal.PortalDetector.OverrideMode)
   */
  @Override
  public void setPortalOverride(OverrideMode mode)
  {
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