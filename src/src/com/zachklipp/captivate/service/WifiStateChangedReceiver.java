package com.zachklipp.captivate.service;

import com.zachklipp.captivate.ConnectedNotification;
import com.zachklipp.captivate.util.Log;
import com.zachklipp.captivate.util.WifiHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class WifiStateChangedReceiver extends BroadcastReceiver
{
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
      Log.d("Wifi connected, starting service...");
      
      context.startService(new Intent(context, PortalDetectorService.class));
    }
    else if (WifiHelper.isDisconnectedFromNetworkStateChangedIntent(intent))
    {
      Log.d("Wifi disconnected.");
      ConnectedNotification.hideNotification(context);
    }
  }
}