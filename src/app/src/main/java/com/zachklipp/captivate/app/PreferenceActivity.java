package com.zachklipp.captivate.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;

import com.zachklipp.captivate.BuildConfig;
import com.zachklipp.captivate.Preferences;
import com.zachklipp.captivate.R;
import com.zachklipp.captivate.service.PortalDetectorService;
import com.zachklipp.captivate.util.Log;
import com.zachklipp.captivate.util.SafeIntentSender;
import com.zachklipp.captivate.util.StringHelper;

@SuppressWarnings("deprecation")
public class PreferenceActivity extends android.preference.PreferenceActivity
{
  
  private static final int DIALOG_NO_FEEDBACK_RECEIVER = 0;

  private static final IntentFilter sDebugIntentFilter = new IntentFilter(
      PortalDetectorService.ACTION_PORTAL_STATE_CHANGED);
  
  private final BroadcastReceiver mDebugReceiver = new BroadcastReceiver()
  {
    @Override
    public void onReceive(Context context, Intent intent)
    {
      if (intent.getAction().equals(PortalDetectorService.ACTION_PORTAL_STATE_CHANGED))
      {
        if (mDebugStatePreference != null)
        {
          mDebugStatePreference.setSummary(
              intent.getStringExtra(PortalDetectorService.EXTRA_PORTAL_STATE));
        }
      }
    }
  };
  
  private Preference mDebugStatePreference;
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    
    addPreferencesFromResource(R.xml.preferences);
    
    CheckBoxPreference enabledPref = (CheckBoxPreference)
        getPreferenceManager().findPreference(Preferences.ENABLED_KEY);
    enabledPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
    {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue)
      {
        startService(new Intent(PreferenceActivity.this, PortalDetectorService.class));
        return true;
      }
    });
    
    if (BuildConfig.DEBUG)
    {
      createDebugPreferences(getPreferenceScreen());
      registerReceiver(mDebugReceiver, sDebugIntentFilter);
    }
    
    formatStrings();
    
    initializeFeedbackIntent();
  }
  
  @Override
  public void onDestroy()
  {
    super.onDestroy();
    
    if (BuildConfig.DEBUG)
    {
      unregisterReceiver(mDebugReceiver);
    }
  }
  
  @Override
  protected Dialog onCreateDialog(int which)
  {
    Dialog dialog = null;
    
    switch (which)
    {
      case DIALOG_NO_FEEDBACK_RECEIVER:
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
          .setMessage(getString(
              R.string.no_feedback_receiver_message, getString(R.string.feedback_email)))
          .setNeutralButton(R.string.ok, null);
        dialog = builder.create();
        break;
        
      default:
        Log.w("Attempted to show invalid dialog: %d", which);
    }
    
    return dialog;
  }
  
  private void formatStrings()
  {
    formatPreferenceTitle(Preferences.FEEDBACK_KEY, R.string.feedback_email);
    formatPreferenceTitle(Preferences.ABOUT_KEY, R.string.app_name);
    
    CheckBoxPreference enabledPref = (CheckBoxPreference)
        getPreferenceManager().findPreference(Preferences.ENABLED_KEY);
    
    enabledPref.setSummaryOn(StringHelper.formatWithResourceStrings(
        this, enabledPref.getSummaryOn(), R.string.app_name));
    
    enabledPref.setSummaryOff(StringHelper.formatWithResourceStrings(
        this, enabledPref.getSummaryOff(), R.string.app_name));
  }
  
  private void initializeFeedbackIntent()
  {
    Preference feedbackPreference = getPreferenceScreen().findPreference(
        Preferences.FEEDBACK_KEY);
    Intent primaryIntent = feedbackPreference.getIntent();
    final SafeIntentSender sender = new SafeIntentSender(this);
    
    primaryIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
        getString(R.string.feedback_subject, getString(R.string.app_name)));
    
    primaryIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
        new String[] {getString(R.string.feedback_email)});
    
    sender.setNoReceiverHandler(new SafeIntentSender.OnNoReceiverListener()
    {
      @Override
      public void onNoReceiver(Intent primary)
      {
        // Can't use DialogFragment since there's no such thing as
        // SherlockPreferenceFragmentActivity.
        showDialog(DIALOG_NO_FEEDBACK_RECEIVER);
      }
    });
    
    feedbackPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference)
      {
        sender.startActivity(preference.getIntent());
        return true;
      }
    });
  }
  
  private void formatPreferenceTitle(CharSequence key, int... args)
  {
    Preference pref = getPreferenceManager().findPreference(key);
    pref.setTitle(StringHelper.formatWithResourceStrings(this, pref.getTitle(), args));
  }
  
  private void createDebugPreferences(PreferenceGroup parent)
  {
    PreferenceCategory debugCategory = new PreferenceCategory(this);
    debugCategory.setTitle("Debug");
    parent.addPreference(debugCategory);
    
    mDebugStatePreference = new Preference(this);
    mDebugStatePreference.setTitle("Portal state (tap to refresh)");
    mDebugStatePreference.setSelectable(true);
    mDebugStatePreference.setOnPreferenceClickListener(new OnPreferenceClickListener()
    {
      @Override
      public boolean onPreferenceClick(Preference preference)
      {
        PortalDetectorService.startService(PreferenceActivity.this);
        return true;
      }
    });
    debugCategory.addPreference(mDebugStatePreference);
    
    Preferences preferences = Preferences.getPreferences(this);
    debugCategory.addPreference(preferences.getSigninCheckSecondsPreference());
    debugCategory.addPreference(preferences.getDebugOverridePreference());

    debugCategory.addPreference(preferences.getResetToDefaultsPreference());
  }
}
