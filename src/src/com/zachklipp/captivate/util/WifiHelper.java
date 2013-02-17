package com.zachklipp.captivate.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public final class WifiHelper
{
  private static final String LOG_TAG = "WifiHelper";
  
  public static boolean isWifiConnectedFromContext(Context context)
  {
    ConnectivityManager cManager = (ConnectivityManager)
        context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo network = cManager.getActiveNetworkInfo();
    
    boolean isWifiConnected = false;
    
    if (null != network)
    {
      Log.d(LOG_TAG, "Type = %s, isConnected() = %s", network.getType(), network.isConnected());
      
      isWifiConnected = ConnectivityManager.TYPE_WIFI == network.getType() && network.isConnected(); 
    }
    
    Log.d(LOG_TAG, "isWifiConnectedFromContext() = " + isWifiConnected);
    
    return isWifiConnected;
  }
  
  public static boolean isWifiFinishedConnectingOrDisconnecting(Intent intent)
  {
    boolean isFinished = false;
    
    Log.d(LOG_TAG, "isWifiFinishedConnectingOrDisconnecting(%s)", intent);
    
    isFinished = hasWifiNetworkConnectedOrDisconnected(intent)
        || hasWifiBeenDisabled(intent);

    Log.d(LOG_TAG, "isWifiFinishedConnectingOrDisconnecting() = " + isFinished);
    
    return isFinished;
  }
  
  public static boolean isWifiDisconnectedFromNetworkStateChangedIntent(Intent intent)
  {
    Log.d(LOG_TAG, "isWifiDisconnectedFromNetworkStateChangedIntent(%s)", intent);
    
    NetworkInfo networkInfo = null;
    
    if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
    {
      networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
    }
    
    return (networkInfo == null);
  }
  
  public static boolean isWifiConnectedFromNetworkStateChangedIntent(Intent intent)
  {
    Log.d(LOG_TAG, "isWifiConnectedFromNetworkStateChangedIntent(%s)", intent);
    
    NetworkInfo networkInfo = null;
    
    if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
    {
      networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
    }
    
    return (networkInfo != null && networkInfo.isConnected());
  }
  
  @SuppressWarnings("incomplete-switch")
  private static boolean hasWifiNetworkConnectedOrDisconnected(Intent intent)
  {
    if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction()))
    {
      // Could also do with WifiManager.getWifiState();
      NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
      
      Log.d(LOG_TAG, "Wifi network state changed network info = " + networkInfo);
      
      if (networkInfo != null)
      {
        NetworkInfo.State state = networkInfo.getState();
        
        Log.d(LOG_TAG, "Wifi state = " + state);
        
        switch (state)
        {
          case CONNECTED:
          case DISCONNECTED:
          case SUSPENDED:
            return true;
        }
      }
      else
      {
        Log.d(LOG_TAG, "No wifi network info");
      }
    }
    
    return false;
  }
  
  private static boolean hasWifiBeenDisabled(Intent intent)
  {
    if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction()))
    {
      // Could also be done with !WifiManager.isConnected() maybe
      
      // This will always be present, default value doesn't matter
      int currState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
      
      Log.d(LOG_TAG, "Wifi enabled state = " + currState);
      
      // This one may not be present
      int prevState = intent.getIntExtra(
          WifiManager.EXTRA_PREVIOUS_WIFI_STATE, WifiManager.WIFI_STATE_DISABLING);
      
      return (currState == WifiManager.WIFI_STATE_DISABLED
          && prevState == WifiManager.WIFI_STATE_DISABLING);
    }
    
    return false;
  }
  
  private WifiHelper() { }
}
