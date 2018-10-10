package com.example.user.ipcis.preferences;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.user.ipcis.R;

public class CurrencySettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_currency);
//        ActionBar actionBar = getActivity().getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
