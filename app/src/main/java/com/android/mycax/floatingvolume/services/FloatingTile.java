package com.android.mycax.floatingvolume.services;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.android.mycax.floatingvolume.utils.AppUtils;

import java.util.Objects;

@TargetApi(Build.VERSION_CODES.N)
public class FloatingTile extends TileService {
    public FloatingTile() {
    }

    private Tile tile;
    private AppUtils utils;

    @Override
    public void onStartListening() {
        utils = new AppUtils(this);
        tile = getQsTile();
        if (!Settings.canDrawOverlays(this)) {
            tile.setState(Tile.STATE_UNAVAILABLE);
        } else tile.setState(utils.isServiceRunning(FloatingVolumeService.class) ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }


    @Override
    public void onClick() {
        if(tile.getState() == Tile.STATE_ACTIVE) {
            utils.manageService(false);
            tile.setState(Tile.STATE_INACTIVE);
            tile.updateTile();
        }
        else if (tile.getState() == Tile.STATE_INACTIVE) {
            utils.manageService(true);
            tile.setState(Tile.STATE_ACTIVE);
            tile.updateTile();
        }
    }
}
