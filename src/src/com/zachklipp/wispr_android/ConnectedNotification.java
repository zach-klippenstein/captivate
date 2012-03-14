package com.zachklipp.wispr_android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public final class ConnectedNotification
{
  private static final int CONNECTED_NOTIFICATION_ID = 1;

  public static void showNotification(Context context, CaptivePortalInfo portalInfo)
  {
    Intent showPortalIntent = portalInfo.getShowPortalIntent();
    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, showPortalIntent, 0);
    
    Notification notification = new Notification(R.drawable.ic_launcher, context.getString(R.string.ticker_text), System.currentTimeMillis());
    notification.flags |= Notification.FLAG_AUTO_CANCEL;
    
    notification.setLatestEventInfo(context, context.getString(R.string.notification_title), context.getString(R.string.notification_text), contentIntent);

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(CONNECTED_NOTIFICATION_ID, notification);
  }
  
  public static void hideNotification(Context context)
  {
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(CONNECTED_NOTIFICATION_ID);
  }
}
