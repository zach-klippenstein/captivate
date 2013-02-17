package com.zachklipp.captivate.service;

import com.zachklipp.captivate.util.Log;
import com.zachklipp.captivate.util.WifiHelper;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class WifiStateChangedReceiver extends BroadcastReceiver
{
  private final static String LOG_TAG = "WifiStateChangedReceiver";
  
  @Override
  public void onReceive(Context context, Intent intent)
  {
    String action = intent.getAction();
    
    Log.d(LOG_TAG, "onReceive(%s, %s)", context, intent);
    
    if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action) ||
        WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action))
    {
      onNetworkStateChanged(context, intent);
    }
  }

  private void onNetworkStateChanged(Context context, Intent intent)
  {
    Log.d(LOG_TAG, "onNetworkStateChanged(%s, %s)", context, intent);
    
    if (WifiHelper.isWifiFinishedConnectingOrDisconnecting(intent))
    {
      Log.d(LOG_TAG, "Wifi connection state changed, starting service...");
      
      ComponentName service = PortalDetectorService.startService(context,
          WifiHelper.isWifiConnectedFromNetworkStateChangedIntent(intent));
      
      Log.d(LOG_TAG, "Started service: " + service);
    }
    else
    {
      Log.d(LOG_TAG, "Wifi state not significantly changed, not starting service");
    }
  }
}
