package com.android.mycax.floatingvolume;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.android.mycax.floatingvolume.utils.AppUtils;
import com.android.mycax.floatingvolume.utils.Constants;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        AppUtils utils = new AppUtils(this);
        int theme = Integer.valueOf(Objects.requireNonNull(sharedPref.getString(Constants.PREF_THEME_VALUE, "1")));
        utils.onActivityCreateSetTheme(this, theme);
        if (theme == Constants.THEME_CUSTOM) {
            utils.setActionBarTextColor(getSupportActionBar());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        if (getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
