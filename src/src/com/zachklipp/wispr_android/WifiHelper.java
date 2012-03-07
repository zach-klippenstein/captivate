package com.zachklipp.wispr_android;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public final class WifiHelper
{
  public static boolean isConnectedFromContext(Context context)
  {
    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = manager.getActiveNetworkInfo();
    
    return isConnectedFromNetworkInfo(networkInfo);
  }
  
  public static boolean isConnectedFromNetworkStateChangedIntent(Intent intent)
  {
    assert(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION));
    
    NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
    
    return isConnectedFromNetworkInfo(networkInfo);
  }
  
  public static boolean isDisconnectedFromNetworkStateChangedIntent(Intent intent)
  {
    assert(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION));
    
    NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
    
    return (networkInfo == null);
  }
  
  private static boolean isConnectedFromNetworkInfo(NetworkInfo networkInfo)
  {
    return (networkInfo != null && networkInfo.isConnected());
  }
}
