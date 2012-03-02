package com.zachklipp.wispr_android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.net.wifi.*;
import android.util.Log;

public class WifiStateChangedReceiver extends BroadcastReceiver
{
  private static final int CONNECTED_NOTIFICATION_ID = 1;
  private static final String LOG_TAG = "wispr-android";

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
    NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
    SupplicantState wifiState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
    
    try
    {
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
      onWifiConnected(context);
    }
  }
  
  private void onWifiConnected(Context context)
  {
    Log.d(LOG_TAG, "Checking for captive portal...");
    
    Wispr wispr = Wispr.checkForCaptivePortal();
    
    if (wispr.isOnCaptivePortal())
    {
      Log.d(LOG_TAG, "Captive portal detected.");
      
      onCaptivePortalDetected(context, wispr.getPortalUri());
    }
  }
  
  private void onCaptivePortalDetected(Context context, Uri portalUri)
  {
    Intent showPortalIntent = getShowPortalIntent(context, portalUri);
    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, showPortalIntent, 0);
    
    Notification notification = new Notification(R.drawable.ic_launcher, "Hotspot detected", System.currentTimeMillis());
    notification.flags |= Notification.FLAG_AUTO_CANCEL;
    
    notification.setLatestEventInfo(context, "Hotspot detected", "You need to login.", contentIntent);

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(CONNECTED_NOTIFICATION_ID, notification);
  }
  
  private Intent getShowPortalIntent(Context context, Uri portalUri)
  {
    Intent showPortalIntent = new Intent(Intent.ACTION_VIEW);
    showPortalIntent.setData(portalUri);
    
    return showPortalIntent;
  }

}
