package com.android.mycax.floatingvolume.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.android.mycax.floatingvolume.R;
import com.android.mycax.floatingvolume.services.FloatingVolumeService;

import java.util.Objects;

public class AppUtils {

    private final Context context;
    private final SharedPreferences sharedPref;

    public AppUtils(Context context) {
        this.context = context;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
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
        } else context.stopService(intent);
    }

    public void applyTheme(Activity activity) {
        activity.recreate();
    }

    public void onActivityCreateSetTheme(Activity activity, int theme) {
        switch (theme) {
            default:
            case Constants.THEME_LIGHT:
                activity.getTheme().applyStyle(R.style.AppTheme, true);
                break;
            case Constants.THEME_DARK:
                activity.getTheme().applyStyle(R.style.AppTheme_Dark, true);
                break;
            case Constants.THEME_CUSTOM:
                activity.getWindow().getDecorView().setBackgroundColor(sharedPref.getInt(Constants.PREF_COLOR_BACKGROUND, -1));
                activity.getTheme().applyStyle(getAlertDialogAccentColor(), true); /* Alert dialog accent */
                activity.getTheme().applyStyle(getAlertDialogColor(), true); /* Alert dialog */
                activity.getTheme().applyStyle(getPrimaryColor(), true); /* Primary */
                activity.getTheme().applyStyle(getAccentColor(), true); /* Accent */
                activity.getTheme().applyStyle(getDrawableColor(), true); /* Drawables */
                activity.getTheme().applyStyle(setTextColor(sharedPref.getInt(Constants.PREF_COLOR_BACKGROUND, -1),
                        R.style.TextColor_Black, R.style.TextColor_White), true); /* Primary and secondary text colors */
                activity.getTheme().applyStyle(setTextColor(sharedPref.getInt(Constants.PREF_COLOR_ACCENT, -1499549),
                        R.style.ButtonTextColor_Black, R.style.ButtonTextColor_White), true); /* Button text colors */
                activity.getWindow().setStatusBarColor(sharedPref.getInt(Constants.PREF_COLOR_PRIMARY, -12627531));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) setStatusBar(activity);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setStatusBar(Activity activity) {
        if (getPrimaryColor() == R.style.Primary_White || getPrimaryColor() == R.style.Primary_Yellow)
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        else
            activity.getWindow().getDecorView().setSystemUiVisibility(0);
    }

    private int setTextColor(int background, int blackStyle, int whiteStyle) {
        int red = Color.red(background);
        int green = Color.green(background);
        int blue = Color.blue(background);
        double lum = (((0.299 * red) + ((0.587 * green) + (0.114 * blue))));
        return lum > 186 ? blackStyle : whiteStyle;
    }

    public void setActionBarTextColor(android.support.v7.app.ActionBar actionBar) {
        Spannable text = new SpannableString(actionBar.getTitle());
        text.setSpan(new ForegroundColorSpan(setTextColor(sharedPref.getInt(Constants.PREF_COLOR_PRIMARY, -12627531),
                getColorInt(R.color.black), getColorInt(R.color.white))),
                0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        actionBar.setTitle(text);
    }

    private int getPrimaryColor() {
        int color = sharedPref.getInt(Constants.PREF_COLOR_PRIMARY, -12627531);
        if (color == getColorInt(R.color.white)) {
            return R.style.Primary_White;
        } else if (color == getColorInt(R.color.red)) {
            return R.style.Primary_Red;
        } else if (color == getColorInt(R.color.pink)) {
            return R.style.Primary_Pink;
        } else if (color == getColorInt(R.color.purple)) {
            return R.style.Primary_Purple;
        } else if (color == getColorInt(R.color.deep_purple)) {
            return R.style.Primary_Deep_Purple;
        } else if (color == getColorInt(R.color.indigo)) {
            return R.style.Primary_Indigo;
        } else if (color == getColorInt(R.color.blue)) {
            return R.style.Primary_Blue;
        } else if (color == getColorInt(R.color.light_blue)) {
            return R.style.Primary_Light_Blue;
        } else if (color == getColorInt(R.color.cyan)) {
            return R.style.Primary_Cyan;
        } else if (color == getColorInt(R.color.teal)) {
            return R.style.Primary_Teal;
        } else if (color == getColorInt(R.color.green)) {
            return R.style.Primary_Green;
        }else if (color == getColorInt(R.color.light_green)) {
            return R.style.Primary_Light_Green;
        } else if (color == getColorInt(R.color.lime)) {
            return R.style.Primary_Lime;
        } else if (color == getColorInt(R.color.yellow)) {
            return R.style.Primary_Yellow;
        } else if (color == getColorInt(R.color.amber)) {
            return R.style.Primary_Amber;
        } else if (color == getColorInt(R.color.orange)) {
            return R.style.Primary_Orange;
        } else if (color == getColorInt(R.color.deep_orange)) {
            return R.style.Primary_Deep_Orange;
        } else if (color == getColorInt(R.color.brown)) {
            return R.style.Primary_Brown;
        } else if (color == getColorInt(R.color.grey)) {
            return R.style.Primary_Grey;
        } else if (color == getColorInt(R.color.grey_900)) {
            return R.style.Primary_Grey_900;
        } else if (color == getColorInt(R.color.blue_grey)) {
            return R.style.Primary_Blue_Grey;
        } else if (color == getColorInt(R.color.blue_grey_900)) {
            return R.style.Primary_Blue_Grey_900;
        } else if (color == getColorInt(R.color.black)) {
            return R.style.Primary_Black;
        }
        return R.style.Primary_Indigo;
    }

    public int getAccentColor() {
        int color = sharedPref.getInt(Constants.PREF_COLOR_ACCENT, -1499549);
        if (color == getColorInt(R.color.white)) {
            return R.style.Accent_White;
        } else if (color == getColorInt(R.color.red)) {
            return R.style.Accent_Red;
        } else if (color == getColorInt(R.color.pink)) {
            return R.style.Accent_Pink;
        } else if (color == getColorInt(R.color.purple)) {
            return R.style.Accent_Purple;
        } else if (color == getColorInt(R.color.deep_purple)) {
            return R.style.Accent_Deep_Purple;
        } else if (color == getColorInt(R.color.indigo)) {
            return R.style.Accent_Indigo;
        } else if (color == getColorInt(R.color.blue)) {
            return R.style.Accent_Blue;
        } else if (color == getColorInt(R.color.light_blue)) {
            return R.style.Accent_Light_Blue;
        } else if (color == getColorInt(R.color.cyan)) {
            return R.style.Accent_Cyan;
        } else if (color == getColorInt(R.color.teal)) {
            return R.style.Accent_Teal;
        } else if (color == getColorInt(R.color.green)) {
            return R.style.Accent_Green;
        }else if (color == getColorInt(R.color.light_green)) {
            return R.style.Accent_Light_Green;
        } else if (color == getColorInt(R.color.lime)) {
            return R.style.Accent_Lime;
        } else if (color == getColorInt(R.color.yellow)) {
            return R.style.Accent_Yellow;
        } else if (color == getColorInt(R.color.amber)) {
            return R.style.Accent_Amber;
        } else if (color == getColorInt(R.color.orange)) {
            return R.style.Accent_Orange;
        } else if (color == getColorInt(R.color.deep_orange)) {
            return R.style.Accent_Deep_Orange;
        } else if (color == getColorInt(R.color.brown)) {
            return R.style.Accent_Brown;
        } else if (color == getColorInt(R.color.grey)) {
            return R.style.Accent_Grey;
        } else if (color == getColorInt(R.color.grey_900)) {
            return R.style.Accent_Grey_900;
        } else if (color == getColorInt(R.color.blue_grey)) {
            return R.style.Accent_Blue_Grey;
        } else if (color == getColorInt(R.color.blue_grey_900)) {
            return R.style.Accent_Blue_Grey_900;
        } else if (color == getColorInt(R.color.black)) {
            return R.style.Accent_Black;
        }
        return R.style.Accent_Pink;
    }

    public int getDrawableColor() {
        int color = sharedPref.getInt(Constants.PREF_COLOR_ICONS, -16777216);
        if (color == getColorInt(R.color.white)) {
            return R.style.Drawables_White;
        } else if (color == getColorInt(R.color.red)) {
            return R.style.Drawables_Red;
        } else if (color == getColorInt(R.color.pink)) {
            return R.style.Drawables_Pink;
        } else if (color == getColorInt(R.color.purple)) {
            return R.style.Drawables_Purple;
        } else if (color == getColorInt(R.color.deep_purple)) {
            return R.style.Drawables_Deep_Purple;
        } else if (color == getColorInt(R.color.indigo)) {
            return R.style.Drawables_Indigo;
        } else if (color == getColorInt(R.color.blue)) {
            return R.style.Drawables_Blue;
        } else if (color == getColorInt(R.color.light_blue)) {
            return R.style.Drawables_Light_Blue;
        } else if (color == getColorInt(R.color.cyan)) {
            return R.style.Drawables_Cyan;
        } else if (color == getColorInt(R.color.teal)) {
            return R.style.Drawables_Teal;
        } else if (color == getColorInt(R.color.green)) {
            return R.style.Drawables_Green;
        }else if (color == getColorInt(R.color.light_green)) {
            return R.style.Drawables_Light_Green;
        } else if (color == getColorInt(R.color.lime)) {
            return R.style.Drawables_Lime;
        } else if (color == getColorInt(R.color.yellow)) {
            return R.style.Drawables_Yellow;
        } else if (color == getColorInt(R.color.amber)) {
            return R.style.Drawables_Amber;
        } else if (color == getColorInt(R.color.orange)) {
            return R.style.Drawables_Orange;
        } else if (color == getColorInt(R.color.deep_orange)) {
            return R.style.Drawables_Deep_Orange;
        } else if (color == getColorInt(R.color.brown)) {
            return R.style.Drawables_Brown;
        } else if (color == getColorInt(R.color.grey)) {
            return R.style.Drawables_Grey;
        } else if (color == getColorInt(R.color.grey_900)) {
            return R.style.Drawables_Grey_900;
        } else if (color == getColorInt(R.color.blue_grey)) {
            return R.style.Drawables_Blue_Grey;
        } else if (color == getColorInt(R.color.blue_grey_900)) {
            return R.style.Drawables_Blue_Grey_900;
        } else if (color == getColorInt(R.color.black)) {
            return R.style.Drawables_Black;
        }
        return R.style.Drawables_Black;
    }

    public int getDialogColor() {
        int color = sharedPref.getInt(Constants.PREF_COLOR_DIALOG, -1);
        if (color == getColorInt(R.color.white)) {
            return R.style.Dialog_White;
        } else if (color == getColorInt(R.color.red)) {
            return R.style.Dialog_Red;
        } else if (color == getColorInt(R.color.pink)) {
            return R.style.Dialog_Pink;
        } else if (color == getColorInt(R.color.purple)) {
            return R.style.Dialog_Purple;
        } else if (color == getColorInt(R.color.deep_purple)) {
            return R.style.Dialog_Deep_Purple;
        } else if (color == getColorInt(R.color.indigo)) {
            return R.style.Dialog_Indigo;
        } else if (color == getColorInt(R.color.blue)) {
            return R.style.Dialog_Blue;
        } else if (color == getColorInt(R.color.light_blue)) {
            return R.style.Dialog_Light_Blue;
        } else if (color == getColorInt(R.color.cyan)) {
            return R.style.Dialog_Cyan;
        } else if (color == getColorInt(R.color.teal)) {
            return R.style.Dialog_Teal;
        } else if (color == getColorInt(R.color.green)) {
            return R.style.Dialog_Green;
        }else if (color == getColorInt(R.color.light_green)) {
            return R.style.Dialog_Light_Green;
        } else if (color == getColorInt(R.color.lime)) {
            return R.style.Dialog_Lime;
        } else if (color == getColorInt(R.color.yellow)) {
            return R.style.Dialog_Yellow;
        } else if (color == getColorInt(R.color.amber)) {
            return R.style.Dialog_Amber;
        } else if (color == getColorInt(R.color.orange)) {
            return R.style.Dialog_Orange;
        } else if (color == getColorInt(R.color.deep_orange)) {
            return R.style.Dialog_Deep_Orange;
        } else if (color == getColorInt(R.color.brown)) {
            return R.style.Dialog_Brown;
        } else if (color == getColorInt(R.color.grey)) {
            return R.style.Dialog_Grey;
        } else if (color == getColorInt(R.color.grey_900)) {
            return R.style.Dialog_Grey_900;
        } else if (color == getColorInt(R.color.blue_grey)) {
            return R.style.Dialog_Blue_Grey;
        } else if (color == getColorInt(R.color.blue_grey_900)) {
            return R.style.Dialog_Blue_Grey_900;
        } else if (color == getColorInt(R.color.black)) {
            return R.style.Dialog_Black;
        }
        return R.style.Dialog_White;
    }

    private int getAlertDialogColor() {
        int color = sharedPref.getInt(Constants.PREF_COLOR_BACKGROUND, -1);
        if (color == getColorInt(R.color.white)) {
            return R.style.AlertDialogStyle_White;
        } else if (color == getColorInt(R.color.red)) {
            return R.style.AlertDialogStyle_Red;
        } else if (color == getColorInt(R.color.pink)) {
            return R.style.AlertDialogStyle_Pink;
        } else if (color == getColorInt(R.color.purple)) {
            return R.style.AlertDialogStyle_Purple;
        } else if (color == getColorInt(R.color.deep_purple)) {
            return R.style.AlertDialogStyle_Deep_Purple;
        } else if (color == getColorInt(R.color.indigo)) {
            return R.style.AlertDialogStyle_Indigo;
        } else if (color == getColorInt(R.color.blue)) {
            return R.style.AlertDialogStyle_Blue;
        } else if (color == getColorInt(R.color.light_blue)) {
            return R.style.AlertDialogStyle_Light_Blue;
        } else if (color == getColorInt(R.color.cyan)) {
            return R.style.AlertDialogStyle_Cyan;
        } else if (color == getColorInt(R.color.teal)) {
            return R.style.AlertDialogStyle_Teal;
        } else if (color == getColorInt(R.color.green)) {
            return R.style.AlertDialogStyle_Green;
        }else if (color == getColorInt(R.color.light_green)) {
            return R.style.AlertDialogStyle_Light_Green;
        } else if (color == getColorInt(R.color.lime)) {
            return R.style.AlertDialogStyle_Lime;
        } else if (color == getColorInt(R.color.yellow)) {
            return R.style.AlertDialogStyle_Yellow;
        } else if (color == getColorInt(R.color.amber)) {
            return R.style.AlertDialogStyle_Amber;
        } else if (color == getColorInt(R.color.orange)) {
            return R.style.AlertDialogStyle_Orange;
        } else if (color == getColorInt(R.color.deep_orange)) {
            return R.style.AlertDialogStyle_Deep_Orange;
        } else if (color == getColorInt(R.color.brown)) {
            return R.style.AlertDialogStyle_Brown;
        } else if (color == getColorInt(R.color.grey)) {
            return R.style.AlertDialogStyle_Grey;
        } else if (color == getColorInt(R.color.grey_900)) {
            return R.style.AlertDialogStyle_Grey_900;
        } else if (color == getColorInt(R.color.blue_grey)) {
            return R.style.AlertDialogStyle_Blue_Grey;
        } else if (color == getColorInt(R.color.blue_grey_900)) {
            return R.style.AlertDialogStyle_Blue_Grey_900;
        } else if (color == getColorInt(R.color.black)) {
            return R.style.AlertDialogStyle_Black;
        }
        return R.style.AlertDialogStyle_White;
    }

    private int getAlertDialogAccentColor() {
        int color = sharedPref.getInt(Constants.PREF_COLOR_ACCENT, -1499549);
        if (color == getColorInt(R.color.white)) {
            return R.style.AlertDialogAccent_White;
        } else if (color == getColorInt(R.color.red)) {
            return R.style.AlertDialogAccent_Red;
        } else if (color == getColorInt(R.color.pink)) {
            return R.style.AlertDialogAccent_Pink;
        } else if (color == getColorInt(R.color.purple)) {
            return R.style.AlertDialogAccent_Purple;
        } else if (color == getColorInt(R.color.deep_purple)) {
            return R.style.AlertDialogAccent_Deep_Purple;
        } else if (color == getColorInt(R.color.indigo)) {
            return R.style.AlertDialogAccent_Indigo;
        } else if (color == getColorInt(R.color.blue)) {
            return R.style.AlertDialogAccent_Blue;
        } else if (color == getColorInt(R.color.light_blue)) {
            return R.style.AlertDialogAccent_Light_Blue;
        } else if (color == getColorInt(R.color.cyan)) {
            return R.style.AlertDialogAccent_Cyan;
        } else if (color == getColorInt(R.color.teal)) {
            return R.style.AlertDialogAccent_Teal;
        } else if (color == getColorInt(R.color.green)) {
            return R.style.AlertDialogAccent_Green;
        }else if (color == getColorInt(R.color.light_green)) {
            return R.style.AlertDialogAccent_Light_Green;
        } else if (color == getColorInt(R.color.lime)) {
            return R.style.AlertDialogAccent_Lime;
        } else if (color == getColorInt(R.color.yellow)) {
            return R.style.AlertDialogAccent_Yellow;
        } else if (color == getColorInt(R.color.amber)) {
            return R.style.AlertDialogAccent_Amber;
        } else if (color == getColorInt(R.color.orange)) {
            return R.style.AlertDialogAccent_Orange;
        } else if (color == getColorInt(R.color.deep_orange)) {
            return R.style.AlertDialogAccent_Deep_Orange;
        } else if (color == getColorInt(R.color.brown)) {
            return R.style.AlertDialogAccent_Brown;
        } else if (color == getColorInt(R.color.grey)) {
            return R.style.AlertDialogAccent_Grey;
        } else if (color == getColorInt(R.color.grey_900)) {
            return R.style.AlertDialogAccent_Grey_900;
        } else if (color == getColorInt(R.color.blue_grey)) {
            return R.style.AlertDialogAccent_Blue_Grey;
        } else if (color == getColorInt(R.color.blue_grey_900)) {
            return R.style.AlertDialogAccent_Blue_Grey_900;
        } else if (color == getColorInt(R.color.black)) {
            return R.style.AlertDialogAccent_Black;
        }
        return R.style.AlertDialogAccent_White;
    }

    public int getCardBackgroundColor() {
        int color = sharedPref.getInt(Constants.PREF_COLOR_BACKGROUND, -1);
        if (color == getColorInt(R.color.white)) {
            return R.color.white;
        } else if (color == getColorInt(R.color.red)) {
            return R.color.red;
        } else if (color == getColorInt(R.color.pink)) {
            return R.color.pink;
        } else if (color == getColorInt(R.color.purple)) {
            return R.color.purple;
        } else if (color == getColorInt(R.color.deep_purple)) {
            return R.color.deep_purple;
        } else if (color == getColorInt(R.color.indigo)) {
            return R.color.indigo;
        } else if (color == getColorInt(R.color.blue)) {
            return R.color.blue;
        } else if (color == getColorInt(R.color.light_blue)) {
            return R.color.light_blue;
        } else if (color == getColorInt(R.color.cyan)) {
            return R.color.cyan;
        } else if (color == getColorInt(R.color.teal)) {
            return R.color.teal;
        } else if (color == getColorInt(R.color.green)) {
            return R.color.green;
        }else if (color == getColorInt(R.color.light_green)) {
            return R.color.light_green;
        } else if (color == getColorInt(R.color.lime)) {
            return R.color.lime;
        } else if (color == getColorInt(R.color.yellow)) {
            return R.color.yellow;
        } else if (color == getColorInt(R.color.amber)) {
            return R.color.amber;
        } else if (color == getColorInt(R.color.orange)) {
            return R.color.orange;
        } else if (color == getColorInt(R.color.deep_orange)) {
            return R.color.deep_orange;
        } else if (color == getColorInt(R.color.brown)) {
            return R.color.brown;
        } else if (color == getColorInt(R.color.grey)) {
            return R.color.grey;
        } else if (color == getColorInt(R.color.grey_900)) {
            return R.color.grey_900;
        } else if (color == getColorInt(R.color.blue_grey)) {
            return R.color.blue_grey;
        } else if (color == getColorInt(R.color.blue_grey_900)) {
            return R.color.blue_grey_900;
        } else if (color == getColorInt(R.color.black)) {
            return R.color.black;
        }
        return R.color.white;
    }

    private int getColorInt(@ColorRes int color) {
        return context.getResources().getColor(color);
    }
}
