package com.zachklipp.captivate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences
{
  public static final String FEEDBACK_PREFERENCE_KEY = "feedback_pref";
  public static final String ABOUT_PREFERENCE_KEY = "about_pref";
  
  public static final String ENABLED_PREFERENCE_KEY = "detector_enabled_pref";
  public static final String DEBUG_OVERRIDE_PREFERENCE_KEY = "debug_override_pref";
  public static final String STATE_REFRESH_INTERVAL_SECONDS_PREFERENCE_KEY
    = "state_refresh_interval_seconds";
  
  private SharedPreferences mPreferences;
  
  public static void showPreferences(Context context)
  {
    context.startActivity(new Intent(context, PreferenceActivity.class));
  }
  
  public static Preferences getPreferences(Context context)
  {
    return new Preferences(
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()));
  }

  private Preferences(SharedPreferences preferences)
  {
    mPreferences = preferences;
  }
  
  public boolean isEnabled()
  {
    return mPreferences.getBoolean(ENABLED_PREFERENCE_KEY, true);
  }
  
  public boolean isDebugOverrideEnabled()
  {
    return BuildConfig.DEBUG && mPreferences.getBoolean(DEBUG_OVERRIDE_PREFERENCE_KEY, false);
  }
  
  public int getStateRefreshIntervalSeconds()
  {
    return mPreferences.getInt(STATE_REFRESH_INTERVAL_SECONDS_PREFERENCE_KEY, 30);
  }

}
