package com.zachklipp.wispr_android;

import com.zachklipp.wispr_android.captive_portal.PortalInfo;
import com.zachklipp.wispr_android.state_machine.PortalStateMachine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PortalStateChangedReceiver extends BroadcastReceiver
{
  @Override
  public void onReceive(Context context, Intent intent)
  {
    String action = intent.getAction();
    
    if (action.equals(PortalDetectorService.ACTION_PORTAL_STATE_CHANGED))
    {
      onPortalStateChanged(context, intent);
    }
  }
  
  private void onPortalStateChanged(Context context, Intent intent)
  {
    String portalState = intent.getStringExtra(PortalDetectorService.EXTRA_CAPTIVE_PORTAL_STATE);
    assert(portalState != null);
    
    PortalInfo portalInfo = intent.getParcelableExtra(PortalDetectorService.EXTRA_CAPTIVE_PORTAL_INFO);
    
    if (portalState.equals(PortalStateMachine.State.NEEDS_SIGNIN.getName()))
    {
      ConnectedNotification.showNotification(context, portalInfo);
    }
    else
    {
      ConnectedNotification.hideNotification(context);
    }
  }
}
