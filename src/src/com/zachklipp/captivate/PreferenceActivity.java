package com.zachklipp.captivate;

import android.os.Bundle;

public class PreferenceActivity extends android.preference.PreferenceActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.preferences);
    }
}
