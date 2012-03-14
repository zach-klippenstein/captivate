package com.zachklipp.wispr_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiStateChangedReceiver extends BroadcastReceiver
{
  private static final String LOG_TAG = "wispr-android";
  private static final CaptivePortalDetectorFactory DETECTOR_FACTORY = new AppleCaptivePortalDetectorFactory();

  @Override
  public void onReceive(Context context, Intent intent)
  {
    String action = intent.getAction();
    
    if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
    {
      onNetworkStateChanged(context, intent);
    }
  }

  private void onNetworkStateChanged(Context context, Intent intent)
  {
    if (WifiHelper.isConnectedFromNetworkStateChangedIntent(intent))
    {
      Log.d(LOG_TAG, "Wifi connected, starting service...");
      context.startService(CaptivePortalDetectorService.createStartIntent(context, DETECTOR_FACTORY));
    }
    else if (WifiHelper.isDisconnectedFromNetworkStateChangedIntent(intent))
    {
      Log.d(LOG_TAG, "Wifi disconnected.");
      ConnectedNotification.hideNotification(context);
    }
  }
}
