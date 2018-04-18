package com.android.mycax.floatingvolume.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.android.mycax.floatingvolume.services.FloatingVolumeService;

import java.util.Objects;

public class AppUtils {

    private final Context context;

    public AppUtils(Context context) {
        this.context = context;
    }

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : Objects.requireNonNull(manager).getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void manageService(boolean state) {
        Intent intent = new Intent(context, FloatingVolumeService.class);
        if (state) {
            context.startService(intent);
        }
        else context.stopService(intent);
    }
}
