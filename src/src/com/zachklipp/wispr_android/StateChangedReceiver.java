package com.zachklipp.wispr_android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class StateChangedReceiver extends BroadcastReceiver
{
  private static final String LOG_TAG = "wispr-android";
  private static final CaptivePortalDetectorFactory DETECTOR_FACTORY = new AppleCaptivePortalDetectorFactory();
  private static final int CONNECTED_NOTIFICATION_ID = 1;

  @Override
  public void onReceive(Context context, Intent intent)
  {
    String action = intent.getAction();
    
    if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
    {
      onNetworkStateChanged(context, intent);
    }
    else if (action.equals(CaptivePortalDetectorService.ACTION_PORTAL_STATE_CHANGED))
    {
      onPortalStateChanged(context, intent);
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
      hideNotification(context);
    }
  }
  
  private void onPortalStateChanged(Context context, Intent intent)
  {
    int portalState = intent.getIntExtra(CaptivePortalDetectorService.EXTRA_CAPTIVE_PORTAL_STATE, CaptivePortalDetectorService.STATE_UNKNOWN);
    CaptivePortalInfo portalInfo = intent.getParcelableExtra(CaptivePortalDetectorService.EXTRA_CAPTIVE_PORTAL_INFO);
    
    if (portalState == CaptivePortalDetectorService.STATE_NEEDS_SIGNIN)
    {
      showNotification(context, portalInfo);
    }
    else
    {
      hideNotification(context);
    }
  }
  
  private void showNotification(Context context, CaptivePortalInfo portalInfo)
  {
    Intent showPortalIntent = portalInfo.getShowPortalIntent();
    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, showPortalIntent, 0);
    
    Notification notification = new Notification(R.drawable.ic_launcher, context.getString(R.string.ticker_text), System.currentTimeMillis());
    notification.flags |= Notification.FLAG_AUTO_CANCEL;
    
    notification.setLatestEventInfo(context, context.getString(R.string.notification_title), context.getString(R.string.notification_text), contentIntent);

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(CONNECTED_NOTIFICATION_ID, notification);
  }
  
  private void hideNotification(Context context)
  {
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(CONNECTED_NOTIFICATION_ID);
  }
}
