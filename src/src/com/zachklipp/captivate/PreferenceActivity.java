package com.zachklipp.captivate;

import com.zachklipp.captivate.util.SafeIntentSender;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

public class PreferenceActivity extends android.preference.PreferenceActivity
{
  private static final String FEEDBACK_PREFERENCE_KEY = "feedbackKey";
  private static final int NO_FEEDBACK_RECEIVER_DIALOG = 0;
  
  private static final String LOG_TAG = "captivate";
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    
    addPreferencesFromResource(R.xml.preferences);
    
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
          .setMessage(R.string.no_feedback_receiver_message)
          .setNeutralButton(R.string.ok, null);
        dialog = builder.create();
        break;
        
      default:
        Log.w(LOG_TAG, String.format("Attempted to show invalid dialog: %d", which));
    }
    
    return dialog;
  }
  
  private void initializeFeedbackIntent()
  {
    Preference feedbackPreference = getPreferenceScreen().findPreference(FEEDBACK_PREFERENCE_KEY);
    Intent primaryIntent = feedbackPreference.getIntent();
    final SafeIntentSender sender = new SafeIntentSender(this);
    
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
}
