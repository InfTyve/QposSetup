package com.android.setup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SetupFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setup_preferences, rootKey);
    }
    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        if ("opentest".equals(preference.getKey())) {
            Intent intent = new Intent(getActivity(), TestSetupActivity.class);
            startActivity(intent);
        } else if ("test145".equals(preference.getKey())) {
            Intent intent = new Intent(getActivity(), TestSetupActivity.class);
            startActivity(intent);
        }
        return super.onPreferenceTreeClick(preference);
    }
}