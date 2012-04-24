package com.zachklipp.captivate;

import com.zachklipp.captivate.util.Log;
import com.zachklipp.captivate.util.SafeIntentSender;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

public class PreferenceActivity extends android.preference.PreferenceActivity
{
  private static final String ENABLED_PREFERENCE_KEY = "enabled_pref";
  private static final String FEEDBACK_PREFERENCE_KEY = "feedback_pref";
  private static final String ABOUT_PREFERENCE_KEY = "about_pref";
  
  private static final int NO_FEEDBACK_RECEIVER_DIALOG = 0;
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    
    addPreferencesFromResource(R.xml.preferences);
    
    formatStrings();
    
    initializeFeedbackIntent();
  }
  
  @Override
  protected Dialog onCreateDialog(int which)
  {
    Dialog dialog = null;
    
    switch (which)
    {
      case NO_FEEDBACK_RECEIVER_DIALOG:
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
          .setMessage(getString(
              R.string.no_feedback_receiver_message, getString(R.string.feedback_email)))
          .setNeutralButton(R.string.ok, null);
        dialog = builder.create();
        break;
        
      default:
        Log.w(String.format("Attempted to show invalid dialog: %d", which));
    }
    
    return dialog;
  }
  
  private void formatStrings()
  {
    formatPreferenceTitle(FEEDBACK_PREFERENCE_KEY, R.string.feedback_email);
    formatPreferenceTitle(ABOUT_PREFERENCE_KEY, R.string.app_name);
    
    CheckBoxPreference enabledPref = (CheckBoxPreference)
        getPreferenceManager().findPreference(ENABLED_PREFERENCE_KEY);
    enabledPref.setSummaryOn(
        formatCharSequence(enabledPref.getSummaryOn(), R.string.app_name));
    enabledPref.setSummaryOff(
        formatCharSequence(enabledPref.getSummaryOff(), R.string.app_name));
  }
  
  private void initializeFeedbackIntent()
  {
    Preference feedbackPreference = getPreferenceScreen().findPreference(FEEDBACK_PREFERENCE_KEY);
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
        showDialog(NO_FEEDBACK_RECEIVER_DIALOG);
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
    pref.setTitle(formatCharSequence(pref.getTitle(), args));
  }
  
  private CharSequence formatCharSequence(CharSequence format, int... args)
  {
    Object[] strArgs = new Object[args.length];
    
    for (int i = 0; i < args.length; i++)
    {
      strArgs[i] = getString(args[i]);
    }
    
    return String.format(format.toString(), strArgs);
  }
}
