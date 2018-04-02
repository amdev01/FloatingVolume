package com.android.mycax.floatingvolume;

import android.annotation.TargetApi;
import android.os.Build;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import com.android.mycax.floatingvolume.utils.AppUtils;

@TargetApi(Build.VERSION_CODES.N)
public class FloatingTile extends TileService {
    public FloatingTile() {
    }

    private Tile tile;
    private AppUtils utils;

    @Override
    public void onTileAdded() {
        Log.d("Floating Tile", " Tile added");

    }

    @Override
    public void onStartListening() {
        utils = new AppUtils(this);
        tile = getQsTile();
        if (!Settings.canDrawOverlays(this)) {
            tile.setState(Tile.STATE_UNAVAILABLE);
        } else tile.setState(utils.isServiceRunning(FloatingVolumeService.class) ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
        Log.d("Floating Tile", " Started listening");
    }


    @Override
    public void onClick() {
        if(tile.getState() == Tile.STATE_ACTIVE) {
            utils.manageService(false);
            tile.setState(Tile.STATE_INACTIVE);
            Log.d("Floating Tile", " Stopping service");
            tile.updateTile();
        }
        else if (tile.getState() == Tile.STATE_INACTIVE) {
            utils.manageService(true);
            tile.setState(Tile.STATE_ACTIVE);
            Log.d("Floating Tile", " Starting service");
            tile.updateTile();
        }
    }

    @Override
    public void onStopListening() {
        Log.d("Floating Tile", " Stopping listening");
    }

    @Override
    public void onTileRemoved() {
        Log.d("Floating Tile", " Tile removed");
    }
}
