package com.zachklipp.captivate.test.captive_portal;

import com.zachklipp.captivate.captive_portal.PortalDetector;

public class MockPortalDetector extends PortalDetector
{
  @Override
  protected void onCheckForPortal()
  {
    throw new IllegalStateException("Mock portal detector override not set");
  }
}