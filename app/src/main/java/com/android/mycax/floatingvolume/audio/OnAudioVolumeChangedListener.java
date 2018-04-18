package com.android.mycax.floatingvolume.audio;

public interface OnAudioVolumeChangedListener {
    void onAudioVolumeChanged(AudioVolumeObserver audioVolumeObserver, int currentVolume, int maxVolume);
}
