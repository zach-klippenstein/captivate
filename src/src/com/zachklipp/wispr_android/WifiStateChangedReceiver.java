package com.zachklipp.wispr_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class WifiStateChangedReceiver extends BroadcastReceiver
{
  private static final CaptivePortalDetectorFactory DETECTOR_FACTORY = new AppleCaptivePortalDetectorFactory();

  @Override
  public void onReceive(Context context, Intent intent)
  {
    if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
    {
      onNetworkStateChanged(context, intent);
    }
  }

  private void onNetworkStateChanged(Context context, Intent intent)
  {
    if (WifiHelper.isConnectedFromNetworkStateChangedIntent(intent))
    {
      context.startService(CaptivePortalDetectorService.createStartIntent(context, DETECTOR_FACTORY));
    }
  }
}
