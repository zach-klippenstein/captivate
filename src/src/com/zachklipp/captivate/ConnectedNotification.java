package com.zachklipp.captivate;

import com.zachklipp.captivate.captive_portal.PortalInfo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

public final class ConnectedNotification
{
  private static final int CONNECTED_NOTIFICATION_ID = 1;

  public static void showNotification(Context context, PortalInfo portalInfo)
  {
    Intent showPortalIntent = PortalSigninActivity.getStartIntent(context, portalInfo);
    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, showPortalIntent, 0);
    
    Builder builder = new NotificationCompat.Builder(context)
      .setSmallIcon(R.drawable.notification_icon)
      .setTicker(context.getString(R.string.ticker_text))
      .setContentTitle(context.getString(R.string.notification_title))
      .setContentText(context.getString(R.string.notification_text))
      .setContentIntent(contentIntent);
    
    NotificationManager notificationManager = (NotificationManager)
        context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(CONNECTED_NOTIFICATION_ID, builder.getNotification());
  }
  
  public static void hideNotification(Context context)
  {
    NotificationManager notificationManager = (NotificationManager)
        context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(CONNECTED_NOTIFICATION_ID);
  }
}
