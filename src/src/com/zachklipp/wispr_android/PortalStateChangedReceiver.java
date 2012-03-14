package com.zachklipp.wispr_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PortalStateChangedReceiver extends BroadcastReceiver
{
  @Override
  public void onReceive(Context context, Intent intent)
  {
    String action = intent.getAction();
    
    if (action.equals(CaptivePortalDetectorService.ACTION_PORTAL_STATE_CHANGED))
    {
      onPortalStateChanged(context, intent);
    }
  }
  
  private void onPortalStateChanged(Context context, Intent intent)
  {
    int portalState = intent.getIntExtra(CaptivePortalDetectorService.EXTRA_CAPTIVE_PORTAL_STATE, CaptivePortalDetectorService.STATE_UNKNOWN);
    CaptivePortalInfo portalInfo = intent.getParcelableExtra(CaptivePortalDetectorService.EXTRA_CAPTIVE_PORTAL_INFO);
    
    if (portalState == CaptivePortalDetectorService.STATE_NEEDS_SIGNIN)
    {
      ConnectedNotification.showNotification(context, portalInfo);
    }
    else
    {
      ConnectedNotification.hideNotification(context);
    }
  }
}
