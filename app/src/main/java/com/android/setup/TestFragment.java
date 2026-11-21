package com.android.setup;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class TestFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.test_preferences, rootKey);
    }
    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        if ("opentest222".equals(preference.getKey())) {
            Intent intent = new Intent(getActivity(),TfeActivity.class);
            startActivity(intent);
        } else if ("test145".equals(preference.getKey())) {
            Intent intent = new Intent(getActivity(), TestSetupActivity.class);
            startActivity(intent);
        }
        return super.onPreferenceTreeClick(preference);
    }
}