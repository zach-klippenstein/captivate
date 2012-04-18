package com.zachklipp.captivate.service;

import com.zachklipp.captivate.ConnectedNotification;
import com.zachklipp.captivate.util.WifiHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiStateChangedReceiver extends BroadcastReceiver
{
  private static final String LOG_TAG = "captivate";
  
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
      
      context.startService(new Intent(context, PortalDetectorService.class));
    }
    else if (WifiHelper.isDisconnectedFromNetworkStateChangedIntent(intent))
    {
      Log.d(LOG_TAG, "Wifi disconnected.");
      ConnectedNotification.hideNotification(context);
    }
  }
}
