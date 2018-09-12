package com.android.mycax.floatingvolume.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.mycax.floatingvolume.utils.AppUtils;
import com.android.mycax.floatingvolume.utils.Constants;

import androidx.preference.PreferenceManager;

public class startOnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())
                && PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.PREF_SETTINGS_START_ON_BOOT, false)) {
            AppUtils utils = new AppUtils(context);
            utils.manageService(true);
        }
    }
}
