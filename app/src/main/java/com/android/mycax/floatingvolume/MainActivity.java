package com.android.mycax.floatingvolume;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import com.android.mycax.floatingvolume.utils.AppUtils;
import com.basel.DualButton.DualButton;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatPreferenceActivity implements SwitchPreference.OnPreferenceChangeListener, DualButton.OnDualClickListener{
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private DualButton FloatingService;
    private static final String PREF_ENABLE_DARK_MODE = "enable_dark_mode_switch";
    private AppUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_ENABLE_DARK_MODE,false) ? R.style.AppTheme_Dark : R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        utils = new AppUtils(this);
        FloatingService = findViewById(R.id.dualBtn);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else if (Settings.canDrawOverlays(this)){
            initializeView();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
        SwitchPreference darkMode = (SwitchPreference) findPreference(PREF_ENABLE_DARK_MODE);
        darkMode.setOnPreferenceChangeListener(this);
    }

    private void initializeView() {
        FloatingService.setDualClickListener(this);
    }

    @Override
    public void onClickFirst(Button btn) {
       utils.manageService(true);
    }

    @Override
    public void onClickSecond(Button btn) {
        utils.manageService(false);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object object) {
        if (preference == findPreference(PREF_ENABLE_DARK_MODE)) {
            finish();
            final Intent intent = getIntent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (resultCode == RESULT_OK) {
                initializeView();
            } else { //Permission is not available
                Toast.makeText(this,
                        R.string.draw_other_app_permission_denied,
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
