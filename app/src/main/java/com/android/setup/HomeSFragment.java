package com.android.setup;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class HomeSFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.home_test_preferences, rootKey);
        Preference openStandard = findPreference("open_standard");
        if (openStandard != null) {
            openStandard.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(requireContext(), StandardActivity.class));
                return true;
            });
        }
    }
}