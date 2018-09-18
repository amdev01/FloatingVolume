package com.android.mycax.floatingvolume.services;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.mycax.floatingvolume.MainActivity;
import com.android.mycax.floatingvolume.R;
import com.android.mycax.floatingvolume.interfaces.OnExpandedVolumeDialogClosed;
import com.android.mycax.floatingvolume.utils.Constants;
import com.android.mycax.floatingvolume.utils.ExpandedVolumeDialog;

import java.util.Objects;

import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

public class FloatingVolumeService extends Service implements FloatingViewListener, OnExpandedVolumeDialogClosed {
    private FloatingViewManager mFloatingViewManager;
    private boolean isUseLastPosition;
    private SharedPreferences sharedPref;
    private ImageView iconView;
    private ExpandedVolumeDialog expandedVolumeDialog;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("InflateParams")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mFloatingViewManager != null) {
            return START_STICKY;
        }

        runAsForeground();
        Animation fab_open_0_to_1 = AnimationUtils.loadAnimation(this, R.anim.fab_open_0_to_1);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        isUseLastPosition = sharedPref.getBoolean(Constants.PREF_SAVE_LAST_POSITION, false);
        expandedVolumeDialog = new ExpandedVolumeDialog(this);
        expandedVolumeDialog.setOnCloseListener(this);
        final DisplayMetrics metrics = new DisplayMetrics();
        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Objects.requireNonNull(windowManager).getDefaultDisplay().getMetrics(metrics);
        final LayoutInflater inflater = LayoutInflater.from(this);
        iconView = (ImageView) inflater.inflate(R.layout.floating_head, null, false);
        iconView.setAlpha(Float.valueOf(sharedPref.getString(Constants.PREF_HEAD_OPACITY, "1f")));
        iconView.startAnimation(fab_open_0_to_1);
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandedVolumeDialog.expandView(inflater, metrics);
                iconView.setVisibility(View.GONE);
            }
        });

        mFloatingViewManager = new FloatingViewManager(this, this);
        mFloatingViewManager.setFixedTrashIconImage(R.drawable.ic_delete_white_24dp);
        mFloatingViewManager.setDisplayMode(FloatingViewManager.DISPLAY_MODE_SHOW_ALWAYS);
        final FloatingViewManager.Options options = loadOptions(metrics);
        mFloatingViewManager.addViewToWindow(iconView, options);

        return START_REDELIVER_INTENT;
    }

    private void runAsForeground(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_volume_up__white_24dp)
                .setContentText(getString(R.string.service_runnig))
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(Constants.CHANNEL_ID);
            createNotificationChannel();
        }

        Notification notification = notificationBuilder.build();

        startForeground(Constants.NOTIFICATION_ID, notification);

    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, getString(R.string.service_runnig), NotificationManager.IMPORTANCE_NONE);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void onFinishFloatingView() {
        stopSelf();
    }

    @Override
    public void onTouchFinished(boolean isFinishing, int x, int y) {
        if (!isFinishing) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putInt(Constants.PREF_KEY_LAST_POSITION_X, x);
            editor.putInt(Constants.PREF_KEY_LAST_POSITION_Y, y);
            editor.apply();
        }
    }

    @Override
    public void onDestroy() {
        if (mFloatingViewManager != null) {
            mFloatingViewManager.removeAllViewToWindow();
            mFloatingViewManager = null;
        }
        expandedVolumeDialog.removeExpandedView();
        super.onDestroy();
    }

    private FloatingViewManager.Options loadOptions(DisplayMetrics metrics) {
        final FloatingViewManager.Options options = new FloatingViewManager.Options();

        if (isUseLastPosition) {
            final int defaultX = options.floatingViewX;
            final int defaultY = options.floatingViewY;
            options.floatingViewX = sharedPref.getInt(Constants.PREF_KEY_LAST_POSITION_X, defaultX);
            options.floatingViewY = sharedPref.getInt(Constants.PREF_KEY_LAST_POSITION_Y, defaultY);
        } else {
            int height = metrics.heightPixels;
            options.floatingViewX = metrics.widthPixels;
            options.floatingViewY = height - (height / 2);
        }

        options.moveDirection = FloatingViewManager.MOVE_DIRECTION_NONE;

        return options;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        expandedVolumeDialog.onConfigurationChanged(newConfig);
    }

    @Override
    public void notifyExpandedVolumeDialogClosed() {
        iconView.setVisibility(View.VISIBLE);
    }
}