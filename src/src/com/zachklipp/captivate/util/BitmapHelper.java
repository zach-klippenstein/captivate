package com.zachklipp.captivate.util;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class BitmapHelper
{
  public static interface ImageLoadListener
  {
    void onImageLoaded(Bitmap image);
  }
  
  public static void loadImage(final String url, final ImageLoadListener listener)
  {
    new AsyncTask<Void, Void, Bitmap>()
    {
      protected Bitmap doInBackground(Void... empty)
      {
        try
        {
          DefaultHttpClient client = new DefaultHttpClient();
          HttpGet httpGet = new HttpGet(url);
          HttpResponse httpResponse = client.execute(httpGet);
          InputStream is = (java.io.InputStream) httpResponse.getEntity().getContent();
          return BitmapFactory.decodeStream(is);
        }
        catch (Exception e)
        { }
        
        return null;
      }
      
      protected void onPostExecute(Bitmap result)
      {
        listener.onImageLoaded(result);
      }
    }.execute();
  }

  private BitmapHelper() { }
}
