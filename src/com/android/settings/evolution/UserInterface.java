package com.android.settings.evolution;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceCategory;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.R;

public class UserInterface extends SettingsPreferenceFragment {

    public static final String TAG = "UserInterface";

    private static final String PREF_STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";
    private static final String STATUS_BAR_BRIGHTNESS_CONTROL = "status_bar_brightness_control";
    private static final String PREF_CUSTOM_CARRIER_LABEL = "custom_carrier_label";
    private static final String KEY_POWER_BUTTON_TORCH = "power_button_torch";

    CheckBoxPreference mStatusBarNotifCount;
    private CheckBoxPreference mStatusBarBrightnessControl;
    Preference mCustomLabel;
    private CheckBoxPreference mPowerButtonTorch;
    private PreferenceCategory mPrefCategoryGeneral;

    String mCustomLabelText = null;

    private boolean torchSupported() {
        return getResources().getBoolean(R.bool.has_led_flash);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_ui);
        
        PreferenceScreen prefSet = getPreferenceScreen();

        mStatusBarNotifCount = (CheckBoxPreference) findPreference(PREF_STATUS_BAR_NOTIF_COUNT);
        mStatusBarNotifCount.setChecked(Settings.System.getBoolean(mContext
                .getContentResolver(), Settings.System.STATUSBAR_NOTIF_COUNT,
                false));

        mStatusBarBrightnessControl = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_BRIGHTNESS_CONTROL);
        
        mStatusBarBrightnessControl.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, 0) == 1));

        try {
            if (Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                mStatusBarBrightnessControl.setEnabled(false);
                mStatusBarBrightnessControl.setSummary(R.string.status_bar_toggle_info);
            }
        } catch (SettingNotFoundException e) {
        }
        
        mCustomLabel = findPreference(PREF_CUSTOM_CARRIER_LABEL);
        updateCustomLabelTextSummary();
        
        if (Utils.isTablet(getActivity())) {
            mPrefCategoryGeneral.removePreference(mStatusBarBrightnessControl);
        }
        
        mPowerButtonTorch = (CheckBoxPreference) findPreference(KEY_POWER_BUTTON_TORCH);
        if (torchSupported()) {
            mPowerButtonTorch.setChecked((Settings.System.getInt(getActivity().
                    getApplicationContext().getContentResolver(),
                    Settings.System.POWER_BUTTON_TORCH, 0) == 1));
        } else {
            getPreferenceScreen().removePreference(mPowerButtonTorch);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            final Preference preference) {
        if (preference == mStatusBarNotifCount) {
            Settings.System.putBoolean(mContext.getContentResolver(),
                    Settings.System.STATUSBAR_NOTIF_COUNT,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mStatusBarBrightnessControl) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;   
        } else if (preference == mPowerButtonTorch) {
                   boolean enabled = mPowerButtonTorch.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.POWER_BUTTON_TORCH,
                    enabled ? 1 : 0);
            return true;
        } else if (preference == mCustomLabel) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            alert.setTitle(R.string.custom_carrier_label_title);
            alert.setMessage(R.string.custom_carrier_label_explain);

            // Set an EditText view to get user input
            final EditText input = new EditText(getActivity());
            input.setText(mCustomLabelText != null ? mCustomLabelText : "");
            alert.setView(input);

            alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = ((Spannable) input.getText()).toString();
                    Settings.System.putString(getActivity().getContentResolver(),
                            Settings.System.CUSTOM_CARRIER_LABEL, value);
                    updateCustomLabelTextSummary();
                    Intent i = new Intent();
                    i.setAction("com.android.settings.LABEL_CHANGED");
                    mContext.sendBroadcast(i);
                }
            });
            alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void updateCustomLabelTextSummary() {
        mCustomLabelText = Settings.System.getString(getActivity().getContentResolver(),
                Settings.System.CUSTOM_CARRIER_LABEL);
        if (mCustomLabelText == null || mCustomLabelText.length() == 0) {
            mCustomLabel.setSummary(R.string.custom_carrier_label_notset);
        } else {
            mCustomLabel.setSummary(mCustomLabelText);
        }
    }
} 
