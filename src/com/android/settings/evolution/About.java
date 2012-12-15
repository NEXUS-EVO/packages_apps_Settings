package com.android.settings.evolution;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;

public class About extends SettingsPreferenceFragment {

    public static final String TAG = "About";
    
    Preference mSiteUrl;
    Preference mSourceUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs_about);
        mSiteUrl = findPreference("darkhorse_website");
        mSourceUrl = findPreference("evolution_source");
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mSiteUrl) {
            launchUrl("http://rootzwiki.com/topic/25545-theme-darkhorse-revolution-codenamegummy-updated-05-20-12/");
        } else if (preference == mSourceUrl) {
            launchUrl("https://github.com/NEXUS-EVOLUTION");    
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void launchUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent donate = new Intent(Intent.ACTION_VIEW, uriUrl);
        getActivity().startActivity(donate);
    }
}
