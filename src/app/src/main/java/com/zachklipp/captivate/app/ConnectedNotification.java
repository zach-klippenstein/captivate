package com.zachklipp.captivate.app;

import com.zachklipp.captivate.R;
import com.zachklipp.captivate.captive_portal.PortalInfo;
import com.zachklipp.captivate.util.BitmapHelper.ImageLoadListener;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.Toast;

public final class ConnectedNotification
{
  private static final int CONNECTED_NOTIFICATION_ID = 1;

  public static void showNotification(final Context context, final PortalInfo portalInfo)
  {
    Intent showPortalIntent = PortalSigninActivity.getStartIntent(context, portalInfo);
    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, showPortalIntent, 0);
    
    final Builder builder = new NotificationCompat.Builder(context)
      .setSmallIcon(R.drawable.notification_icon)
      .setTicker(context.getString(R.string.ticker_text))
      .setContentTitle(context.getString(R.string.notification_title))
      .setContentText(context.getString(R.string.notification_text))
      .setContentIntent(contentIntent);
    
    final NotificationManager notificationManager = (NotificationManager)
        context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(CONNECTED_NOTIFICATION_ID, builder.getNotification());
    
    portalInfo.getFavicon(new ImageLoadListener()
    {
      public void onImageLoaded(Bitmap favicon)
      {
        if (favicon != null)
        {
          builder.setLargeIcon(favicon);
          notificationManager.notify(CONNECTED_NOTIFICATION_ID, builder.getNotification());
        }
      }
    });
  }
  
  public static void hideNotification(Context context)
  {
    NotificationManager notificationManager = (NotificationManager)
        context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(CONNECTED_NOTIFICATION_ID);
  }
  
  public static void showSignedInToast(Context context)
  {
    Toast.makeText(context, R.string.signed_in_toast, Toast.LENGTH_SHORT).show();
  }
}
