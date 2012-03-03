package com.zachklipp.wispr_android;

import android.app.Activity;
import android.os.Bundle;

public class AboutActivity extends Activity
{
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
      this.setContentView(R.layout.about_layout);
  }
}
