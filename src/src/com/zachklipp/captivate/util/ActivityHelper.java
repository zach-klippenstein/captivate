package com.zachklipp.captivate.util;

import android.app.Activity;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

public class ActivityHelper
{
  public static void initializeAsDialogWhenSwIs(Activity activity, int smallestWidthDp)
  {
    Point size = getDisplaySize(activity.getWindowManager().getDefaultDisplay());
    int screenWidth = size.x;
    int screenHeight = size.y;

    if (Math.min(screenWidth, screenHeight) >= smallestWidthDp)
    {
      activity.requestWindowFeature(Window.FEATURE_ACTION_BAR);
      
      activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
          WindowManager.LayoutParams.FLAG_DIM_BEHIND);

      WindowManager.LayoutParams p = activity.getWindow().getAttributes();
      p.height = WindowManager.LayoutParams.WRAP_CONTENT;
      p.width = smallestWidthDp;
      p.alpha = 1f;
      p.dimAmount = 0.6f;
      activity.getWindow().setAttributes(p);
    }
  }
  
  private static Point getDisplaySize(Display display)
  {
    Point size = new Point();
    DisplayMetrics dm = new DisplayMetrics();
    
    display.getMetrics(dm);
    size.x = dm.widthPixels;
    size.y = dm.heightPixels;
    
    return size;
  }

  private ActivityHelper() { }
}
