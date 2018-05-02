package com.android.mycax.floatingvolume.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.android.mycax.floatingvolume.R;
import com.android.mycax.floatingvolume.audio.AudioVolumeObserver;
import com.android.mycax.floatingvolume.audio.OnAudioVolumeChangedListener;
import com.android.mycax.floatingvolume.utils.Constants;

import java.util.Objects;
import java.util.Set;

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
    private BroadcastReceiver RingerModeReceiver;
    private boolean isDarkThemeEnabled, isDisableStaticUiEnabled, isUseLastPosition, isBounceEnabled;
    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin, style;
    private final Point szWindow = new Point();
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPref;
    private Set<String> seekbarSelections;
    private static int OVERLAY_TYPE;

    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            for (String androidArch : Build.SUPPORTED_ABIS) {
                switch (androidArch) {
                    case Constants.ARM64V8A:
                        OVERLAY_TYPE = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
                        break;
                    case Constants.ARMEABIV7A:
                        OVERLAY_TYPE = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
                        break;
                }
            }
        } else {
            OVERLAY_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
    }

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

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final DisplayMetrics metrics = new DisplayMetrics();
        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Objects.requireNonNull(windowManager).getDefaultDisplay().getMetrics(metrics);
        final LayoutInflater inflater = LayoutInflater.from(this);
        iconView = (ImageView) inflater.inflate(R.layout.floating_head, null, false);
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandView(inflater, metrics);
            }
        });

        mFloatingViewManager = new FloatingViewManager(this, this);
        mFloatingViewManager.setFixedTrashIconImage(R.drawable.ic_delete_white_24dp);
        mFloatingViewManager.setDisplayMode(FloatingViewManager.DISPLAY_MODE_SHOW_ALWAYS);
        final FloatingViewManager.Options options = loadOptions(metrics);
        mFloatingViewManager.addViewToWindow(iconView, options);

        return START_REDELIVER_INTENT;
    }

    private void expandView(LayoutInflater inflater, DisplayMetrics displayMetrics) {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        isDarkThemeEnabled = sharedPref.getBoolean(Constants.PREF_ENABLE_DARK_MODE, false);
        isDisableStaticUiEnabled = sharedPref.getBoolean(Constants.PREF_DISABLE_FIXED_UI, false);
        isUseLastPosition = sharedPref.getBoolean(Constants.PREF_SAVE_LAST_POSITION, false);
        isBounceEnabled = sharedPref.getBoolean(Constants.PREF_ENABLE_BOUNCE, false);
        seekbarSelections = sharedPref.getStringSet(Constants.PREF_ITEMS_TO_SHOW, null);

        setTheme(isDarkThemeEnabled ? R.style.AppTheme_Dark_Dialog : R.style.AppTheme_Dialog);

        addFloatingWidgetView(inflater, displayMetrics);
        if (isDisableStaticUiEnabled) implementTouchListenerToFloatingWidgetView(this);

        implementVolumeFeatures();

        mFloatingWidgetView.findViewById(R.id.close_expanded_view).setOnClickListener(this);
        iconView.setVisibility(View.GONE);
    }

    @SuppressLint("InflateParams")
    private void addFloatingWidgetView(LayoutInflater inflater, DisplayMetrics displayMetrics) {
        getWindowManagerDefaultDisplay();
        mFloatingWidgetView = inflater.inflate(getDialogLayout(), null, false);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                OVERLAY_TYPE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        if (isDisableStaticUiEnabled) {
            params.gravity = Gravity.TOP | Gravity.START;
            if (isUseLastPosition) {
                params.x = sharedPref.getInt(Constants.PREF_KEY_LAST_POSITION_X_EXPANDED, 0);
                params.y = sharedPref.getInt(Constants.PREF_KEY_LAST_POSITION_Y_EXPANDED, 0);
            } else {
                int height = displayMetrics.heightPixels;
                params.x = displayMetrics.widthPixels - mFloatingWidgetView.getWidth();
                params.y = (height / 4);
            }
        } else {
            params.gravity = Gravity.CENTER;
            params.x = 0;
            params.y = 0;
        }
        params.windowAnimations = android.R.style.Animation_Dialog;

        mWindowManager.addView(mFloatingWidgetView, params);
    }

    private void implementVolumeFeatures() {
        mediaControl = mFloatingWidgetView.findViewById(R.id.SeekBarMedia);
        mAudioVolumeObserverMedia = new AudioVolumeObserver(this);
        seekbarSetup(Constants.SEEKBAR_MEDIA, mediaControl, AudioManager.STREAM_MUSIC, mAudioVolumeObserverMedia, R.id.textViewMedia,
                R.id.SeekBarMediaRotator, R.id.ImageMedia, R.id.linearLayoutMedia);

        ringerControl = mFloatingWidgetView.findViewById(R.id.SeekBarRinger);
        mAudioVolumeObserverRinger = new AudioVolumeObserver(this);
        seekbarSetup(Constants.SEEKBAR_RINGER, ringerControl, AudioManager.STREAM_RING, mAudioVolumeObserverRinger, R.id.textViewRinger,
                R.id.SeekBarRingerRotator, R.id.ImageRinger, R.id.linearLayoutRinger);

        alarmControl = mFloatingWidgetView.findViewById(R.id.SeekBarAlarm);
        mAudioVolumeObserverAlarm = new AudioVolumeObserver(this);
        seekbarSetup(Constants.SEEKBAR_ALARM, alarmControl, AudioManager.STREAM_ALARM, mAudioVolumeObserverAlarm, R.id.textViewAlarm,
                R.id.SeekBarAlarmRotator, R.id.ImageAlarm, R.id.linearLayoutAlarm);

        mAudioVolumeObserverVoiceCall = new AudioVolumeObserver(this);
        voiceCallControl = mFloatingWidgetView.findViewById(R.id.SeekBarVoiceCall);
        seekbarSetup(Constants.SEEKBAR_VOICE_CALL, voiceCallControl, AudioManager.STREAM_VOICE_CALL, mAudioVolumeObserverVoiceCall, R.id.textViewVoiceCall,
                R.id.SeekBarVoiceCallRotator, R.id.ImageVoiceCall, R.id.linearLayoutVoiceCall);

        change_ringer_mode = mFloatingWidgetView.findViewById(R.id.imageViewModeSwitch);
        final Animation fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open_0_to_1);
        RingerModeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                change_ringer_mode.setImageResource(getCurrentRingerModeDrawable());
                change_ringer_mode.startAnimation(fab_open);
            }
        };
        IntentFilter filter = new IntentFilter(
                AudioManager.RINGER_MODE_CHANGED_ACTION);
        registerReceiver(RingerModeReceiver, filter);
        change_ringer_mode.setOnClickListener(this);
    }

    private void seekbarSetup(String enabled, SeekBar seekBar, int streamType, AudioVolumeObserver audioVolumeObserver,
                              int textView, int rotator, int imageView, int linearLayout) {
        if (seekbarSelections.contains(enabled)) {
            if (seekBar.getId() == R.id.SeekBarVoiceCall && audioManager.getMode() == AudioManager.MODE_IN_CALL) {
                if (style == 3) {
                    mFloatingWidgetView.findViewById(textView).setVisibility(View.VISIBLE);
                    mFloatingWidgetView.findViewById(rotator).setVisibility(View.VISIBLE);
                    mFloatingWidgetView.findViewById(imageView).setVisibility(View.VISIBLE);
                } else {
                    mFloatingWidgetView.findViewById(linearLayout).setVisibility(View.VISIBLE);
                    mFloatingWidgetView.findViewById(textView).setVisibility(View.VISIBLE);
                }
                seekBar.setMax(Objects.requireNonNull(audioManager).getStreamMaxVolume(streamType));
                seekBar.setProgress(audioManager.getStreamVolume(streamType));
                seekBar.setOnSeekBarChangeListener(this);
                audioVolumeObserver.register(streamType, this);
            } else {
                seekBar.setMax(Objects.requireNonNull(audioManager).getStreamMaxVolume(streamType));
                seekBar.setProgress(audioManager.getStreamVolume(streamType));
                seekBar.setOnSeekBarChangeListener(this);
                audioVolumeObserver.register(streamType, this);
            }
        } else {
            if (style == 3) {
                mFloatingWidgetView.findViewById(textView).setVisibility(View.GONE);
                mFloatingWidgetView.findViewById(rotator).setVisibility(View.GONE);
                mFloatingWidgetView.findViewById(imageView).setVisibility(View.GONE);
            } else {
                mFloatingWidgetView.findViewById(textView).setVisibility(View.GONE);
                mFloatingWidgetView.findViewById(linearLayout).setVisibility(View.GONE);
            }
        }
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
                return isDarkThemeEnabled ? R.drawable.ic_volume_up_white_24dp : R.drawable.ic_volume_up_black_24dp;
            case AudioManager.RINGER_MODE_VIBRATE:
                return isDarkThemeEnabled ? R.drawable.ic_vibration_white_24dp : R.drawable.ic_vibration_black_24dp;
            case AudioManager.RINGER_MODE_SILENT:
                return isDarkThemeEnabled ? R.drawable.ic_do_not_disturb_on_white_24dp : R.drawable.ic_do_not_disturb_on_black_24dp;
        }
        return -1;
    }

    private void setNewRingerMode() {
        int ringerMode = audioManager.getRingerMode();
        Animation fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open_0_to_1);
        switch (ringerMode) {
            case AudioManager.RINGER_MODE_NORMAL:
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                change_ringer_mode.setImageResource(isDarkThemeEnabled ? R.drawable.ic_vibration_white_24dp : R.drawable.ic_vibration_black_24dp);
                change_ringer_mode.startAnimation(fab_open);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                change_ringer_mode.setImageResource(isDarkThemeEnabled ? R.drawable.ic_do_not_disturb_on_white_24dp : R.drawable.ic_do_not_disturb_on_black_24dp);
                change_ringer_mode.startAnimation(fab_open);
                break;
            case AudioManager.RINGER_MODE_SILENT:
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                change_ringer_mode.setImageResource(isDarkThemeEnabled ? R.drawable.ic_volume_up_white_24dp : R.drawable.ic_volume_up_black_24dp);
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
                if (seekbarSelections.contains(Constants.SEEKBAR_MEDIA)) {
                    mAudioVolumeObserverMedia.unregister();
                }
                if (seekbarSelections.contains(Constants.SEEKBAR_RINGER)) {
                    mAudioVolumeObserverRinger.unregister();
                }
                if (seekbarSelections.contains(Constants.SEEKBAR_ALARM)) {
                    mAudioVolumeObserverAlarm.unregister();
                }
                if (seekbarSelections.contains(Constants.SEEKBAR_VOICE_CALL)) {
                    mAudioVolumeObserverVoiceCall.unregister();
                }
                unregisterReceiver(RingerModeReceiver);
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
        if (!isFinishing) {
            editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putInt(Constants.PREF_KEY_LAST_POSITION_X, x);
            editor.putInt(Constants.PREF_KEY_LAST_POSITION_Y, y);
            editor.apply();
        }
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
    public void onAudioVolumeChanged(AudioVolumeObserver audioVolumeObserver) {
        if (audioVolumeObserver.equals(mAudioVolumeObserverRinger))
            ringerControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_RING));
        else if (audioVolumeObserver.equals(mAudioVolumeObserverMedia))
            mediaControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        else if (audioVolumeObserver.equals(mAudioVolumeObserverVoiceCall))
            voiceCallControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
        else if (audioVolumeObserver.equals(mAudioVolumeObserverAlarm))
            alarmControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_ALARM));
    }

    private FloatingViewManager.Options loadOptions(DisplayMetrics metrics) {
        final FloatingViewManager.Options options = new FloatingViewManager.Options();
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

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

    private void implementTouchListenerToFloatingWidgetView(final Context context) {
        mFloatingWidgetView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mFloatingWidgetView.getLayoutParams();

                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();

                int x_cord_Destination, y_cord_Destination;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        x_init_cord = x_cord;
                        y_init_cord = y_cord;

                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;

                        return true;
                    case MotionEvent.ACTION_UP:
                        boolean isClicked = false;
                        int x_diff = x_cord - x_init_cord;
                        int y_diff = y_cord - y_init_cord;

                        if (Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5) isClicked = true;

                        y_cord_Destination = y_init_margin + y_diff;

                        int barHeight = getStatusBarHeight();
                        if (y_cord_Destination < 0) y_cord_Destination = 0;
                        else if (y_cord_Destination + (mFloatingWidgetView.getHeight() + barHeight) > szWindow.y) {
                            y_cord_Destination = szWindow.y - (mFloatingWidgetView.getHeight() + barHeight);
                        }

                        layoutParams.y = y_cord_Destination;

                        if (!isClicked) resetPosition(x_cord);

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int x_diff_move = x_cord - x_init_cord;
                        int y_diff_move = y_cord - y_init_cord;

                        x_cord_Destination = x_init_margin + x_diff_move;
                        y_cord_Destination = y_init_margin + y_diff_move;

                        layoutParams.x = x_cord_Destination;
                        layoutParams.y = y_cord_Destination;

                        mWindowManager.updateViewLayout(mFloatingWidgetView, layoutParams);
                        editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                        editor.putInt(Constants.PREF_KEY_LAST_POSITION_X_EXPANDED, layoutParams.x);
                        editor.putInt(Constants.PREF_KEY_LAST_POSITION_Y_EXPANDED, layoutParams.y);
                        editor.apply();
                        return true;
                }
                return false;
            }
        });
    }

    private void resetPosition(int x_cord_now) {
        if (x_cord_now <= szWindow.x / 2) moveToLeft(x_cord_now);
        else moveToRight(x_cord_now);
    }

    private void moveToLeft(final int current_x_cord) {
        new CountDownTimer(500, 5) {
            final WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) mFloatingWidgetView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;

                mParams.x = 0 - (int) (current_x_cord * current_x_cord * step);

                if (isBounceEnabled)
                    mParams.x = 0 - (int) (double) bounceValue(step, current_x_cord);

                mWindowManager.updateViewLayout(mFloatingWidgetView, mParams);
            }

            public void onFinish() {
                mParams.x = 0;

                mWindowManager.updateViewLayout(mFloatingWidgetView, mParams);
            }
        }.start();
    }

    private void moveToRight(final int current_x_cord) {

        new CountDownTimer(500, 5) {
            final WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) mFloatingWidgetView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;

                mParams.x = (int) (szWindow.x + (current_x_cord * current_x_cord * step) - mFloatingWidgetView.getWidth());

                if (isBounceEnabled)
                    mParams.x = szWindow.x + (int) (double) bounceValue(step, current_x_cord) - mFloatingWidgetView.getWidth();

                mWindowManager.updateViewLayout(mFloatingWidgetView, mParams);
            }

            public void onFinish() {
                mParams.x = szWindow.x - mFloatingWidgetView.getWidth();

                mWindowManager.updateViewLayout(mFloatingWidgetView, mParams);
            }
        }.start();
    }

    private int getStatusBarHeight() {
        return (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
    }

    private double bounceValue(long step, long scale) {
        return scale * Math.exp(-0.055 * step) * Math.cos(0.08 * step);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mFloatingWidgetView != null) {
            getWindowManagerDefaultDisplay();

            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mFloatingWidgetView.getLayoutParams();

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (layoutParams.y + (mFloatingWidgetView.getHeight() + getStatusBarHeight()) > szWindow.y) {
                    layoutParams.y = szWindow.y - (mFloatingWidgetView.getHeight() + getStatusBarHeight());
                    mWindowManager.updateViewLayout(mFloatingWidgetView, layoutParams);
                }
                if (layoutParams.x != 0 && layoutParams.x < szWindow.x) resetPosition(szWindow.x);
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (layoutParams.x > szWindow.x) resetPosition(szWindow.x);
            }
        }
    }

    private void getWindowManagerDefaultDisplay() {
        mWindowManager.getDefaultDisplay().getSize(szWindow);
    }

    private int getDialogLayout() {
        style = Integer.valueOf(sharedPref.getString(Constants.PREF_DIALOG_STYLE, "1"));
        switch (style) {
            case 1:
                return R.layout.floating_layout;
            case 2:
                return R.layout.floating_layout_slim;
            case 3:
                return R.layout.floating_layout_vertical;
        }
        return R.layout.floating_layout;
    }
}