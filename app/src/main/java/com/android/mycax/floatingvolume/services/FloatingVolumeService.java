package com.android.mycax.floatingvolume.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.media.AudioManager;

import com.android.mycax.floatingvolume.R;
import com.android.mycax.floatingvolume.audio.AudioVolumeObserver;
import com.android.mycax.floatingvolume.audio.OnAudioVolumeChangedListener;

import java.util.Objects;

import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

public class FloatingVolumeService extends Service implements FloatingViewListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener, OnAudioVolumeChangedListener {
    private WindowManager mWindowManager;
    private View mFloatingWidgetView;
    private ImageView change_ringer_mode, ImageToAnimate, iconView;
    private AudioManager audioManager;
    private FloatingViewManager mFloatingViewManager;
    private AudioVolumeObserver mAudioVolumeObserverMedia, mAudioVolumeObserverVoiceCall, mAudioVolumeObserverRinger, mAudioVolumeObserverAlarm;
    private SeekBar mediaControl, ringerControl, alarmControl, voiceCallControl;

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

        final DisplayMetrics metrics = new DisplayMetrics();
        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Objects.requireNonNull(windowManager).getDefaultDisplay().getMetrics(metrics);
        final LayoutInflater inflater = LayoutInflater.from(this);
        iconView = (ImageView) inflater.inflate(R.layout.floating_head, null, false);
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandView(inflater);
            }
        });

        mFloatingViewManager = new FloatingViewManager(this, this);
        mFloatingViewManager.setFixedTrashIconImage(R.drawable.ic_delete_white_24dp);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        FloatingViewManager.Options options = new FloatingViewManager.Options();
        options.floatingViewX = displayMetrics.widthPixels;
        options.floatingViewY = height - (height / 2);
        mFloatingViewManager.addViewToWindow(iconView, options);

        return START_REDELIVER_INTENT;
    }

    private void expandView(LayoutInflater inflater) {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        addFloatingWidgetView(inflater);
        implementVolumeFeatures();
        mAudioVolumeObserverRinger = new AudioVolumeObserver(this);
        mAudioVolumeObserverRinger.register(AudioManager.STREAM_RING, this);

        mAudioVolumeObserverMedia = new AudioVolumeObserver(this);
        mAudioVolumeObserverMedia.register(AudioManager.STREAM_MUSIC, this);

        mAudioVolumeObserverVoiceCall = new AudioVolumeObserver(this);
        mAudioVolumeObserverVoiceCall.register(AudioManager.STREAM_VOICE_CALL, this);

        mAudioVolumeObserverAlarm = new AudioVolumeObserver(this);
        mAudioVolumeObserverAlarm.register(AudioManager.STREAM_ALARM, this);

        mFloatingWidgetView.findViewById(R.id.close_expanded_view).setOnClickListener(this);
        iconView.setVisibility(View.GONE);
    }

    @SuppressLint("InflateParams")
    private void addFloatingWidgetView(LayoutInflater inflater) {
        mFloatingWidgetView = inflater.inflate(R.layout.floating_layout, null, false);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT < Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY :
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 0;
        params.windowAnimations = android.R.style.Animation_Dialog;

        mWindowManager.addView(mFloatingWidgetView, params);
    }

    private void implementVolumeFeatures() {
        mediaControl = mFloatingWidgetView.findViewById(R.id.SeekBarMedia);
        mediaControl.setMax(Objects.requireNonNull(audioManager).getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        mediaControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        mediaControl.setOnSeekBarChangeListener(this);

        ringerControl = mFloatingWidgetView.findViewById(R.id.SeekBarRinger);
        ringerControl.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
        ringerControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_RING));
        ringerControl.setOnSeekBarChangeListener(this);

        alarmControl = mFloatingWidgetView.findViewById(R.id.SeekBarAlarm);
        alarmControl.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM));
        alarmControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_ALARM));
        alarmControl.setOnSeekBarChangeListener(this);

        LinearLayout linearLayoutInCall = mFloatingWidgetView.findViewById(R.id.linearLayoutVoiceCall);
        TextView textViewInCall = mFloatingWidgetView.findViewById(R.id.textViewVoiceCall);
        if (audioManager.getMode() == AudioManager.MODE_IN_CALL) {
            linearLayoutInCall.setVisibility(View.VISIBLE);
            textViewInCall.setVisibility(View.VISIBLE);
            voiceCallControl = mFloatingWidgetView.findViewById(R.id.SeekBarVoiceCall);
            voiceCallControl.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL));
            voiceCallControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
            voiceCallControl.setOnSeekBarChangeListener(this);
        } else {
            linearLayoutInCall.setVisibility(View.GONE);
            textViewInCall.setVisibility(View.GONE);
        }

        change_ringer_mode = mFloatingWidgetView.findViewById(R.id.imageViewModeSwitch);
        change_ringer_mode.setImageResource(getCurrentRingerModeDrawable());
        change_ringer_mode.setOnClickListener(this);
    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fab_close_13_to_1);
        switch (arg0.getId()) {
            case R.id.SeekBarMedia:
                ImageToAnimate = mFloatingWidgetView.findViewById(R.id.ImageMedia);
                break;
            case R.id.SeekBarRinger:
                ImageToAnimate = mFloatingWidgetView.findViewById(R.id.ImageRinger);
                break;
            case R.id.SeekBarAlarm:
                ImageToAnimate = mFloatingWidgetView.findViewById(R.id.ImageAlarm);
                break;
            case R.id.SeekBarVoiceCall:
                ImageToAnimate = mFloatingWidgetView.findViewById(R.id.ImageVoiceCall);
                break;
        }
        ImageToAnimate.startAnimation(animation);
    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fab_open_1_to_13);
        switch (arg0.getId()) {
            case R.id.SeekBarMedia:
                ImageToAnimate = mFloatingWidgetView.findViewById(R.id.ImageMedia);
                break;
            case R.id.SeekBarRinger:
                ImageToAnimate = mFloatingWidgetView.findViewById(R.id.ImageRinger);
                break;
            case R.id.SeekBarAlarm:
                ImageToAnimate = mFloatingWidgetView.findViewById(R.id.ImageAlarm);
                break;
            case R.id.SeekBarVoiceCall:
                ImageToAnimate = mFloatingWidgetView.findViewById(R.id.ImageVoiceCall);
                break;
        }
        ImageToAnimate.startAnimation(animation);
    }

    @Override
    public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
        switch (arg0.getId()) {
            case R.id.SeekBarMedia:
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, arg1, 0);
                break;
            case R.id.SeekBarRinger:
                audioManager.setStreamVolume(AudioManager.STREAM_RING, arg1, 0);
                break;
            case R.id.SeekBarAlarm:
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, arg1, 0);
                break;
            case R.id.SeekBarVoiceCall:
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, arg1, 0);
                break;
        }
    }

    private int getCurrentRingerModeDrawable() {
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                return R.drawable.ic_volume_up_black_24dp;
            case AudioManager.RINGER_MODE_VIBRATE:
                return R.drawable.ic_vibration_black_24dp;
            case AudioManager.RINGER_MODE_SILENT:
                return R.drawable.ic_do_not_disturb_on_black_24dp;
        }
        return -1;
    }

    private void setNewRingerMode() {
        int ringerMode = audioManager.getRingerMode();
        Animation fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open_0_to_1);
        switch (ringerMode) {
            case AudioManager.RINGER_MODE_NORMAL:
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                change_ringer_mode.setImageResource(R.drawable.ic_vibration_black_24dp);
                change_ringer_mode.startAnimation(fab_open);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                change_ringer_mode.setImageResource(R.drawable.ic_do_not_disturb_on_black_24dp);
                change_ringer_mode.startAnimation(fab_open);
                break;
            case AudioManager.RINGER_MODE_SILENT:
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                change_ringer_mode.setImageResource(R.drawable.ic_volume_up_black_24dp);
                change_ringer_mode.startAnimation(fab_open);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_expanded_view:
                removeExpandedView();
                iconView.setVisibility(View.VISIBLE);
                mAudioVolumeObserverRinger.unregister();
                mAudioVolumeObserverMedia.unregister();
                mAudioVolumeObserverVoiceCall.unregister();
                mAudioVolumeObserverAlarm.unregister();
                break;
            case R.id.imageViewModeSwitch:
                setNewRingerMode();
                break;
        }
    }

    @Override
    public void onFinishFloatingView() {
        stopSelf();
    }

    @Override
    public void onTouchFinished(boolean isFinishing, int x, int y) {

    }

    private void removeExpandedView() {
        if (mFloatingWidgetView != null) {
            mWindowManager.removeView(mFloatingWidgetView);
            mFloatingWidgetView = null;
        }
    }

    @Override
    public void onDestroy() {
        if (mFloatingViewManager != null) {
            mFloatingViewManager.removeAllViewToWindow();
            mFloatingViewManager = null;
        }
        removeExpandedView();
        super.onDestroy();
    }

    @Override
    public void onAudioVolumeChanged(AudioVolumeObserver audioVolumeObserver, int currentVolume, int maxVolume) {
        if (audioVolumeObserver.equals(mAudioVolumeObserverRinger))
            ringerControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_RING));
        else if (audioVolumeObserver.equals(mAudioVolumeObserverMedia))
            mediaControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        else if (audioVolumeObserver.equals(mAudioVolumeObserverVoiceCall))
            voiceCallControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
        else if (audioVolumeObserver.equals(mAudioVolumeObserverAlarm))
            alarmControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_ALARM));
    }
}