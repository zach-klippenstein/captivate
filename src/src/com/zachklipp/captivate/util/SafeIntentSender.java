package com.zachklipp.captivate.util;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

/*
 * Allows sending implicit intents without crashing when there
 * are no available receivers. 
 */
public class SafeIntentSender
{
  public static interface OnNoReceiverListener
  {
    public void onNoReceiver(Intent primary);
  }
  
  private static final String LOG_TAG = "SafeIntentSender";
  
  private static final OnNoReceiverListener sDefaultNoReceiverListener = new OnNoReceiverListener()
  {
    @Override
    public void onNoReceiver(Intent primary)
    {
      Log.w(LOG_TAG, String.format("No receiver for intent %s", primary.getAction()));
    }
  };
  
  private Context mContext;
  private PackageManager mPackageManager;
  private OnNoReceiverListener mNoReceiverHandler;
  
  public SafeIntentSender(Context context)
  {
    mContext = context;
    mPackageManager = mContext.getPackageManager();
  }
  
  public SafeIntentSender setNoReceiverHandler(OnNoReceiverListener handler)
  {
    mNoReceiverHandler = handler;
    return this;
  }
  
  public void startActivity(Intent primary, Intent... fallbacks)
  {
    List<ResolveInfo> activities = mPackageManager.queryIntentActivities(primary, 0);
    Intent intent = primary;
    
    for (int i = 0; activities.size() == 0 && i < fallbacks.length; i++)
    {
      intent = fallbacks[i];
      activities = mPackageManager.queryIntentActivities(primary, 0);
    }
    
    if (activities.size() > 0)
    {
      mContext.startActivity(intent);
    }
    else
    {
      onNoReceiver(primary);
    }
  }
  
  private void onNoReceiver(Intent primary)
  {
    OnNoReceiverListener listener = mNoReceiverHandler;
    
    if (listener == null)
      listener = sDefaultNoReceiverListener;
    
    listener.onNoReceiver(primary);
  }
}
