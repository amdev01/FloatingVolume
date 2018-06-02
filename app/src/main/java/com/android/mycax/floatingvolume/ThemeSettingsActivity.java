package com.android.mycax.floatingvolume;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.android.mycax.floatingvolume.utils.AppUtils;
import com.android.mycax.floatingvolume.utils.Constants;
import com.thebluealliance.spectrum.SpectrumPreference;

import mehdi.sakout.fancybuttons.FancyButton;

@SuppressWarnings("deprecation")
public class ThemeSettingsActivity extends AppCompatPreferenceActivity implements Button.OnClickListener {
    private AppUtils utils;
    private SpectrumPreference spectrumPrimary, spectrumAccent, spectrumDialog, spectrumBackground, spectrumDrawables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        utils = new AppUtils(this);
        int theme = Integer.valueOf(sharedPref.getString(Constants.PREF_THEME_VALUE, "1"));
        utils.onActivityCreateSetTheme(this, theme);
        if (theme == 3) {
            utils.setActionBarTextColor(getSupportActionBar());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_settings);
        FancyButton applyThemeButton = findViewById(R.id.apply_theme_button);
        applyThemeButton.setOnClickListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_custom_theme);
        spectrumPrimary = (SpectrumPreference) findPreference(Constants.PREF_SPECTRUM_COLOR_PRIMARY);
        spectrumAccent = (SpectrumPreference) findPreference(Constants.PREF_SPECTRUM_COLOR_ACCENT);
        spectrumDialog = (SpectrumPreference) findPreference(Constants.PREF_SPECTRUM_COLOR_DIALOG);
        spectrumBackground = (SpectrumPreference) findPreference(Constants.PREF_SPECTRUM_COLOR_BACKGROUND);
        spectrumDrawables = (SpectrumPreference) findPreference(Constants.PREF_SPECTRUM_COLOR_ICONS);
    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putInt(Constants.PREF_COLOR_PRIMARY, spectrumPrimary.getColor());
        editor.putInt(Constants.PREF_COLOR_ACCENT, spectrumAccent.getColor());
        editor.putInt(Constants.PREF_COLOR_DIALOG, spectrumDialog.getColor());
        editor.putInt(Constants.PREF_COLOR_BACKGROUND, spectrumBackground.getColor());
        editor.putInt(Constants.PREF_COLOR_ICONS, spectrumDrawables.getColor());
        editor.apply();
        utils.applyTheme(this);
    }

}
