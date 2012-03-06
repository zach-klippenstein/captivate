package com.zachklipp.wispr_android;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class WisprPreferencesActivity extends PreferenceActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.preferences);
    }
}
