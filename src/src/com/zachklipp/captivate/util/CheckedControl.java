package com.zachklipp.captivate.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class CheckedControl<TButton extends CompoundButton, TContent extends View>
{
  private TButton mButton;
  private TContent mContent;
  
  private SharedPreferences mPreferences;
  private String mPreferenceKey;
  private boolean mPreferenceDefault;
  
  private final OnSharedPreferenceChangeListener mPreferenceChangeListener
    = new OnSharedPreferenceChangeListener()
    {
      @Override
      public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
          String key)
      {
        boolean newValue = sharedPreferences.getBoolean(key, mPreferenceDefault);
        mButton.setChecked(newValue);
      }
    };
  
  public CheckedControl(TButton button, TContent content)
  {
    mButton = button;
    mContent = content;
    
    updateContentEnabled();
    
    // Wire checked events to the spinner enabled status
    mButton.setOnCheckedChangeListener(new OnCheckedChangeListener()
    {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
        updateContentEnabled();
      }
    });
  }
  
  public TButton getButton()
  {
    return mButton;
  }
  
  public TContent getContent()
  {
    return mContent;
  }
  
  public void bindToPreference(SharedPreferences preferences, String key, boolean defaultValue)
  {
    if (preferences == null || key == null)
    {
      throw new IllegalArgumentException("Must specify SharedPreferences and key");
    }
    
    unbindFromPreference();
    
    mPreferenceKey = key;
    mPreferenceDefault = defaultValue;
    mPreferences = preferences;
    
    preferences.registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
  }
  
  public void unbindFromPreference()
  {
    if (mPreferences != null)
    {
      mPreferences.unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
      mPreferences = null;
    }
  }
  
  private void updateContentEnabled()
  {
    mContent.setEnabled(mButton.isChecked());
    
    if (mPreferences != null)
    {
      mPreferences.edit()
        .putBoolean(mPreferenceKey, mButton.isChecked())
        .commit();
    }
  }
}
