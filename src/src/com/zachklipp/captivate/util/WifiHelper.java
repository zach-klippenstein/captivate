package com.zachklipp.captivate.util;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiManager;

public final class WifiHelper
{
  public static boolean isConnectedFromContext(Context context)
  {
    WifiManager wManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    
    return WifiManager.WIFI_STATE_ENABLED == wManager.getWifiState();
  }
  
  public static boolean isWifiFinishedConnectingOrDisconnecting(Intent intent)
  {
    assert(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION));
    
    if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction()))
    {
      NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
      
      if (networkInfo != null)
      {
        DetailedState detailedState = networkInfo.getDetailedState();
        
        return detailedState == DetailedState.CONNECTED
            || detailedState == DetailedState.DISCONNECTED;
      }
    }
    else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction()))
    {
      // This will always be present, default value doesn't matter
      int currState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
      
      // This one may not be present
      int prevState = intent.getIntExtra(
          WifiManager.EXTRA_PREVIOUS_WIFI_STATE, WifiManager.WIFI_STATE_DISABLING);
      
      return (currState == WifiManager.WIFI_STATE_DISABLED
          && prevState == WifiManager.WIFI_STATE_DISABLING);
    }
    
    return false;
  }
  
  public static boolean isDisconnectedFromNetworkStateChangedIntent(Intent intent)
  {
    assert(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION));
    
    NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
    
    return (networkInfo == null);
  }
}
