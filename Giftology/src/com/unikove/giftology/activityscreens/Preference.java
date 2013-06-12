package com.unikove.giftology.activityscreens;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.unikove.giftology.R;

public class Preference extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.layout.setting_menu);

    }
   
}

