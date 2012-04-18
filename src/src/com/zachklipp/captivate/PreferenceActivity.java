package com.zachklipp.captivate;

import android.os.Bundle;

public class PreferenceActivity extends android.preference.PreferenceActivity
{
  private static final String FEEDBACK_PREFERENCE_KEY = "feedbackKey";
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    
    addPreferencesFromResource(R.xml.preferences);
    
    this.getPreferenceScreen().findPreference(FEEDBACK_PREFERENCE_KEY).getIntent()
      .putExtra(android.content.Intent.EXTRA_EMAIL,
          new String[] {getString(R.string.feedback_email)});
  }
}
