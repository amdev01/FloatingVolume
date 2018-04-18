package com.android.mycax.floatingvolume.audio;

import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;

public class AudioVolumeContentObserver extends ContentObserver {

    private final OnAudioVolumeChangedListener mListener;
    private final AudioManager mAudioManager;
    private final int mAudioStreamType;
    private int mLastVolume;
    private AudioVolumeObserver audioVolumeObserver;

    public AudioVolumeContentObserver(
            @NonNull Handler handler,
            @NonNull AudioManager audioManager,
            int audioStreamType,
            @NonNull OnAudioVolumeChangedListener listener,
            @NonNull AudioVolumeObserver volumeObserver) {

        super(handler);
        mAudioManager = audioManager;
        mAudioStreamType = audioStreamType;
        mListener = listener;
        mLastVolume = audioManager.getStreamVolume(mAudioStreamType);
        audioVolumeObserver = volumeObserver;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        if (mAudioManager != null && mListener != null) {
            int maxVolume = mAudioManager.getStreamMaxVolume(mAudioStreamType);
            int currentVolume = mAudioManager.getStreamVolume(mAudioStreamType);
            if (currentVolume != mLastVolume) {
                mLastVolume = currentVolume;
                mListener.onAudioVolumeChanged(audioVolumeObserver, currentVolume, maxVolume);
            }
        }
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }
}