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
  @Override
  public void onReceive(Context context, Intent intent)
  {
    String action = intent.getAction();
    
    if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action) ||
        WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action))
    {
      onNetworkStateChanged(context, intent);
    }
  }

  private void onNetworkStateChanged(Context context, Intent intent)
  {
    if (WifiHelper.isWifiFinishedConnectingOrDisconnecting(intent))
    {
      Log.d("Wifi connection state changed, starting service...");
      
      ComponentName service = PortalDetectorService.startService(context.getApplicationContext());
      
      Log.d("Started service: " + service);
    }
  }
}
