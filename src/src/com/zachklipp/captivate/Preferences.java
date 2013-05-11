package com.zachklipp.captivate;

import com.zachklipp.captivate.app.PreferenceActivity;
import com.zachklipp.captivate.util.StringHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.Toast;

public class Preferences
{
  public static final String FEEDBACK_KEY = "feedback_pref";
  public static final String ABOUT_KEY = "about_pref";
  
  public static final String ENABLED_KEY = "detector_enabled_pref";
  public static final boolean ENABLED_DEFAULT = true;
  
  public static final String DEBUG_OVERRIDE_KEY = "debug_override_pref";
  public static final boolean DEBUG_OVERRIDE_DEFAULT = false;
  
  /**
   * Determines the number of seconds between checks for successful sign-in
   * when behind a blocked portal.
   */
  public static final String SIGNIN_CHECK_SECONDS_PREFERENCE_KEY
    = "state_refresh_interval_seconds";
  public static final int SIGNIN_CHECK_SECONDS_DEFAULT = 15;
  
  /**
   * Determines the number of minutes between checks for session timeout
   * when behind a signed-in portal.
   */
  public static final String SESSION_TIMEOUT_CHECK_SECONDS_KEY = "session_timeout_check_seconds";
  // Default is an hour and a minute
  public static final int SESSION_TIMEOUT_CHECK_SECONDS_DEFAULT = 61 * 60;
  
  private Context mContext;
  private SharedPreferences mPreferences;
  
  public static void showPreferences(Context context)
  {
    context.startActivity(new Intent(context, PreferenceActivity.class));
  }
  
  public static Preferences getPreferences(Context context)
  {
    return new Preferences(context,
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()));
  }

  private Preferences(Context context, SharedPreferences preferences)
  {
    mContext = context;
    mPreferences = preferences;
  }
  
  public boolean isEnabled()
  {
    return mPreferences.getBoolean(ENABLED_KEY, ENABLED_DEFAULT);
  }
  
  public boolean isDebugOverrideEnabled()
  {
    return BuildConfig.DEBUG && mPreferences.getBoolean(DEBUG_OVERRIDE_KEY, DEBUG_OVERRIDE_DEFAULT);
  }
  
  public Preference getDebugOverridePreference()
  {
    CheckBoxPreference overridePref = new CheckBoxPreference(mContext);
    overridePref.setKey(Preferences.DEBUG_OVERRIDE_KEY);
    overridePref.setDefaultValue(Preferences.DEBUG_OVERRIDE_DEFAULT);
    overridePref.setTitle("Override portal");
    overridePref.setSummaryOn("A fake portal will always be detected.");
    overridePref.setSummaryOff("The portal detector will look for a portal.");
    
    return overridePref;
  }
  
  public int getSigninCheckSeconds()
  {
    return mPreferences.getInt(SIGNIN_CHECK_SECONDS_PREFERENCE_KEY, SIGNIN_CHECK_SECONDS_DEFAULT);
  }
  
  public Preference getSigninCheckSecondsPreference()
  {
    final EditIntPreference signinCheckPreference = new EditIntPreference(mContext);
    final OnPreferenceChangeListener changeListener = new OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference preference, Object newValue)
      {
        int intValue = Integer.parseInt(newValue.toString());
        signinCheckPreference.setSummary(StringHelper.format(mContext,
            "When blocked portal is detected, check for signin every %d seconds.", intValue));
          
        return true;
      }
    };
    
    signinCheckPreference.setKey(Preferences.SIGNIN_CHECK_SECONDS_PREFERENCE_KEY);
    signinCheckPreference.setDefaultValue(Preferences.SIGNIN_CHECK_SECONDS_DEFAULT);
    signinCheckPreference.setTitle("Signin check interval (seconds)");
    signinCheckPreference.setDialogTitle("Signin Check Interval");
    signinCheckPreference.setOnPreferenceChangeListener(changeListener);
    
    changeListener.onPreferenceChange(signinCheckPreference, getSigninCheckSeconds());
    
    return signinCheckPreference;
  }

  public int getSessionTimeoutCheckMinutes()
  {
    return mPreferences.getInt(SESSION_TIMEOUT_CHECK_SECONDS_KEY, SESSION_TIMEOUT_CHECK_SECONDS_DEFAULT);
  }
  
  public Preference getResetToDefaultsPreference()
  {
    Preference resetPreference = new Preference(mContext);
    resetPreference.setTitle("Reset to Defaults");
    resetPreference.setSelectable(true);
    resetPreference.setOnPreferenceClickListener(new OnPreferenceClickListener()
    {
      @Override
      public boolean onPreferenceClick(Preference preference)
      {
        resetAllPreferencesToDefaults();
        return true;
      }
    });
    
    return resetPreference;
  }
  
  private void resetAllPreferencesToDefaults()
  {
    mPreferences.edit()
      .clear()
      .commit();
    
    PreferenceManager.setDefaultValues(mContext, R.xml.preferences, true);
    
    Toast.makeText(mContext, "Preferences cleared\nRestart activity to see values", Toast.LENGTH_SHORT).show();
  }
  
  /**
   * Used for a debug preference, doesn't do error-handling, DON'T USE FOR PRODUCTION.
   *
   */
  private static class EditIntPreference extends EditTextPreference
  {
    public EditIntPreference(Context context)
    {
      super(context);
      init(context, null, 0);
    }
  
    public EditIntPreference(Context context, AttributeSet attrs)
    {
      super(context, attrs);
      init(context, attrs, 0);
    }
  
    public EditIntPreference(Context context, AttributeSet attrs, int defStyle)
    {
      super(context, attrs, defStyle);
      init(context, attrs, defStyle);
    }
    
    private void init(Context context, AttributeSet attrs, int defStyle)
    {
      if (!BuildConfig.DEBUG)
      {
        throw new RuntimeException("EditIntPreference is not release-ready!");
      }
    }
    
    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue)
    {
      if (defaultValue != null)
      {
        defaultValue = defaultValue.toString();
      }
      
      super.onSetInitialValue(restoreValue, defaultValue);
    }
  
    @Override
    protected String getPersistedString(String defaultReturnValue)
    {
      int defaultInt = 0;
      
      if (defaultReturnValue != null)
      {
        defaultInt = Integer.parseInt(defaultReturnValue);
      }
      
      return String.valueOf(getPersistedInt(defaultInt));
    }
    
    @Override
    protected boolean persistString(String value)
    {
      return persistInt(Integer.parseInt(value));
    }
  }
}
