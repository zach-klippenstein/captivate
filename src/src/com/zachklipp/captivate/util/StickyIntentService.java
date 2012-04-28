package com.zachklipp.captivate.util;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;

/*
 * Similar to the built-in IntentService, but doesn't automatically stop
 * the service after all queued intents have been processed.
 */
public abstract class StickyIntentService extends Service
{
  private final String mWorkerThreadName;
  private WorkerThread mWorkerThread;
  private int mIntentRedelivery;
  private Handler mHandler;
  
  public StickyIntentService(String name)
  {
    mWorkerThreadName = name;
    
    setIntentRedelivery(false);
  }
  
  /*
   * Runs on worker thread.
   */
  protected abstract void onHandleIntent(Intent intent);
  
  /*
   * (non-Javadoc)
   * @see android.app.IntentService#setIntentRedelivery(boolean)
   */
  public void setIntentRedelivery(boolean enabled)
  {
    mIntentRedelivery = enabled ? Service.START_REDELIVER_INTENT : Service.START_NOT_STICKY;
  }
  
  /*
   * Allow concrete classes to schedule messages.
   */
  protected Handler getHandler()
  {
    return mHandler;
  }
  
  @Override
  public void onCreate()
  {
    super.onCreate();
    
    mWorkerThread = new WorkerThread(mWorkerThreadName);
    mWorkerThread.start();
  }
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId)
  {
    mWorkerThread.sendIntent(intent);
    
    return mIntentRedelivery;
  }
  
  @Override
  public void onDestroy()
  {
    super.onDestroy();
    
    mWorkerThread.quit();
  }

  @Override
  public IBinder onBind(Intent intent)
  {
    return null;
  }

  private class WorkerThread extends HandlerThread implements Handler.Callback
  {
    public WorkerThread(String name)
    {
      super(name);
    }
    
    @Override
    public void start()
    {
      super.start();
      
      mHandler = new Handler(getLooper(), this);
    }
    
    /*
     * Will run on calling thread (probably main).
     */
    public void sendIntent(Intent intent)
    {
      Message msg = mHandler.obtainMessage(0, intent);
      
      mHandler.sendMessage(msg);
    }

    /*
     * Will run on worker thread.
     * (non-Javadoc)
     * @see android.os.Handler.Callback#handleMessage(android.os.Message)
     */
    @Override
    public boolean handleMessage(Message msg)
    {
      onHandleIntent((Intent) msg.obj);
      
      return true;
    }
  }
}
