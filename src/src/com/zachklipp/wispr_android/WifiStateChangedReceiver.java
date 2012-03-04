package com.zachklipp.wispr_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.*;
import android.util.Log;

public class WifiStateChangedReceiver extends BroadcastReceiver
{
  private static final String LOG_TAG = "wispr-android";
  
  private Context mContext;
  private CaptivePortalDetector mDetector = CaptivePortalDetector.createDetector();
  private CaptivePortalHandler mPortalHandler = new NotificationCaptivePortalHandler();
  
  public WifiStateChangedReceiver()
  {
    super();
    
    initialize();
  }
  
  public WifiStateChangedReceiver(CaptivePortalDetector portalDetector)
  {
    super();
    
    mDetector = portalDetector;
    
    initialize();
  }
  
  public WifiStateChangedReceiver(CaptivePortalDetector portalDetector, CaptivePortalHandler portalHandler)
  {
    super();
    
    mDetector = portalDetector;
    mPortalHandler = portalHandler;
    
    initialize();
  }
  
  private void initialize()
  {
    mDetector.addCaptivePortalHandler(mPortalHandler);
  }

  @Override
  public void onReceive(Context context, Intent intent)
  {
    if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
    {
      mContext = context;
      onNetworkStateChanged(intent);
    }
  }

  private void onNetworkStateChanged(Intent intent)
  {
    NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
    
    try
    {
      SupplicantState wifiState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
      
      Log.d(LOG_TAG, "NEW_STATE:" + wifiState);
      Log.d(LOG_TAG, "PREVIOUS_STATE: " + intent.getParcelableExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE));
      Log.d(LOG_TAG, "SUPPLICANT_CONNECTED: " + intent.getParcelableExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED));
      Log.d(LOG_TAG, "WIFI_STATE: " + intent.getParcelableExtra(WifiManager.EXTRA_WIFI_STATE));
      Log.d(LOG_TAG, "Network type: " + networkInfo.getTypeName() + "." + networkInfo.getSubtypeName());
      Log.d(LOG_TAG, "NetworkInfo State: " + networkInfo.getState());
      Log.d(LOG_TAG, "Is connected? " + networkInfo.isConnected());
    }
    catch (Exception e)
    {}
    
    if (networkInfo != null && networkInfo.isConnected())
    {
      onWifiConnected();
    }
  }
  
  public void onWifiConnected()
  {
    Log.d(LOG_TAG, "Checking for captive portal...");

    mDetector.checkForCaptivePortal(mContext);
  }

}
