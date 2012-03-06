package com.zachklipp.wispr_android;

import com.zachklipp.wispr_android.PortalStateTracker.PortalStateChangedHandler;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CaptivePortalDetectorService extends IntentService
{
  // Extras for start intent
  public static final String EXTRA_PORTAL_DETECTOR_FACTORY = "com.zachklipp.wispr-android.EXTRA_PORTAL_DETECTOR_FACTORY";
  
  // For broadcast intent
  public static final String ACTION_PORTAL_STATE_CHANGED = "com.zachklipp.wispr-android.ACTION_PORTAL_STATE_CHANGED";
  public static final String EXTRA_CAPTIVE_PORTAL_STATE = "com.zachklipp.wispr-android.EXTRA_CAPTIVE_PORTAL_STATE";
  public static final String EXTRA_CAPTIVE_PORTAL_INFO = "com.zachklipp.wispr-android.EXTRA_CAPTIVE_PORTAL_INFO";
  
  // Captive portal states
  public static final int STATE_UNKNOWN = 0;
  public static final int STATE_NO_PORTAL = 1;
  public static final int STATE_NEEDS_SIGNIN = 2;
  public static final int STATE_SIGNING_IN = 3;
  public static final int STATE_SIGNED_IN = 4;

  private static final int CONNECTED_NOTIFICATION_ID = 1;
  private static final String LOG_TAG = "wispr-android";
  
  private final PortalStateTracker mPortalStateTracker = new PortalStateTracker();
  private final CaptivePortalHandler mPortalHandler = new CaptivePortalHandler()
  {
    @Override
    public void onCaptivePortalDetected(Context context, CaptivePortalInfo portal)
    {
      Log.d(LOG_TAG, "Captive portal detected.");
      mPortalStateTracker.setPortalInfo(portal);
    }

    @Override
    public void onNoCaptivePortalDetected(Context context)
    {
      mPortalStateTracker.clearPortalInfo();
    }
  };
  private final PortalStateChangedHandler mPortalStateChangedHandler = new PortalStateChangedHandler()
  {
    @Override
    public void onStateChanged(int newState, CaptivePortalInfo portalInfo)
    {
      Intent intent = createStateChangedBroadcastIntent(newState, portalInfo);
      
      showNotification(portalInfo);
      
      Log.i(LOG_TAG, String.format("Broadcasting portal state change. new state=%d, portal=%s", newState, portalInfo));
      getBaseContext().sendBroadcast(intent);
    }
  };
  
  public static Intent createStartIntent(Context context, CaptivePortalDetectorFactory detectorFactory)
  {
    Intent serviceIntent = new Intent(context, CaptivePortalDetectorService.class);
    serviceIntent.putExtra(CaptivePortalDetectorService.EXTRA_PORTAL_DETECTOR_FACTORY, detectorFactory);
    
    return serviceIntent;
  }

  public CaptivePortalDetectorService()
  {
    super("CaptivePortalMonitorThread");
    
    mPortalStateTracker.setStateChangedHandler(mPortalStateChangedHandler);
  }

  @Override
  protected void onHandleIntent(Intent intent)
  {
    Log.d(LOG_TAG, "Creating detector from factory from intent");
    CaptivePortalDetector detector = createDetectorFromIntent(intent);
    
    detector.addCaptivePortalHandler(mPortalHandler);
    
    // Assume we were called immediately after wifi connected, and if there is
    // a captive portal, we'll see it first.
    detector.checkForCaptivePortal(getBaseContext());
  }
  
  private static CaptivePortalDetector createDetectorFromIntent(Intent intent)
  {
    CaptivePortalDetectorFactory factory = (CaptivePortalDetectorFactory) intent.getSerializableExtra(EXTRA_PORTAL_DETECTOR_FACTORY);
    Log.d(LOG_TAG, String.format("Creating detector from factory %s", factory.getClass().getName()));
    
    return factory.createDetector();
  }
  
  private static Intent createStateChangedBroadcastIntent(int state, CaptivePortalInfo portal)
  {
    Intent intent = new Intent(CaptivePortalDetectorService.ACTION_PORTAL_STATE_CHANGED);
    
    intent.putExtra(CaptivePortalDetectorService.EXTRA_CAPTIVE_PORTAL_STATE, state);
    
    if (portal != null)
    {
      intent.putExtra(CaptivePortalDetectorService.EXTRA_CAPTIVE_PORTAL_INFO, portal);
    }
    
    return intent;
  }
  
  private void showNotification(CaptivePortalInfo portalInfo)
  {
    Context context = getBaseContext();
    Intent showPortalIntent = portalInfo.getShowPortalIntent();
    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, showPortalIntent, 0);
    
    Notification notification = new Notification(R.drawable.ic_launcher, context.getString(R.string.ticker_text), System.currentTimeMillis());
    notification.flags |= Notification.FLAG_AUTO_CANCEL;
    
    notification.setLatestEventInfo(context, context.getString(R.string.notification_title), context.getString(R.string.notification_text), contentIntent);

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(CONNECTED_NOTIFICATION_ID, notification);
  }
}
