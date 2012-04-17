package com.zachklipp.captivate;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class AboutActivity extends Activity
{
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
      setContentView(R.layout.about_layout);
      
      TextView view = (TextView) findViewById(R.id.about_view);
      view.setText(Html.fromHtml(getString(R.string.about_text)));
  }
}
