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
import androidx.annotation.ColorRes;

import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import com.android.mycax.floatingvolume.R;
import com.android.mycax.floatingvolume.services.FloatingVolumeService;
import com.android.mycax.floatingvolume.services.VolumeKeyService;

import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class AppUtils {

    private final Context context;
    private final SharedPreferences sharedPref;

    public AppUtils(Context context) {
        this.context = context;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * method is used getting log tag with line number.
     *
     * @return String tag in (filename.java:XX) format
     */
    public static String getTag() {
        String tag = "";
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        for (int i = 0; i < ste.length; i++) {
            if (ste[i].getMethodName().equals("getTag")) {
                tag = "("+ste[i + 1].getFileName() + ":" + ste[i + 1].getLineNumber()+")";
            }
        }
        return tag;
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

    public boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + VolumeKeyService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(getTag(), "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(getTag(), "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(getTag(), "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(getTag(), "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(getTag(), "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }


    public void manageService(boolean state) {
        Intent intent = new Intent(context, FloatingVolumeService.class);
        if (state) {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                context.startService(intent);
            } else context.startForegroundService(intent);
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
                activity.getTheme().applyStyle(setTextColor(sharedPref.getInt(Constants.PREF_COLOR_BACKGROUND, -1),
                        R.style.AlertDialogTextColor_Black, R.style.AlertDialogTextColor_White), true); /* alert dialog text color */
                activity.getTheme().applyStyle(getAlertDialogAccentColor(), true); /* Alert dialog accent */
                activity.getTheme().applyStyle(getAlertDialogColor(), true); /* Alert dialog */
                activity.getTheme().applyStyle(getPrimaryColor(), true); /* Primary */
                activity.getTheme().applyStyle(getAccentColor(), true); /* Accent */
                activity.getTheme().applyStyle(getDrawableColor(), true); /* Drawables */
                activity.getTheme().applyStyle(getAboutCardBackgroundColor(), true); /* AboutActivity card */
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

    public void setActionBarTextColor(androidx.appcompat.app.ActionBar actionBar) {
        Spannable text = new SpannableString(actionBar.getTitle());
        text.setSpan(new ForegroundColorSpan(setTextColor(sharedPref.getInt(Constants.PREF_COLOR_PRIMARY, -12627531),
                getColorInt(R.color.black), getColorInt(R.color.white))),
                0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        actionBar.setTitle(text);
    }

    public int getSeekbarTintColor() {
        return setTextColor(sharedPref.getInt(Constants.PREF_COLOR_DIALOG, -1),
                R.style.SeekbarTheme_LightGreySeekbar, R.style.SeekbarTheme_DarkGreySeekbar);
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

    private int getAboutCardBackgroundColor() {
        int color = sharedPref.getInt(Constants.PREF_COLOR_BACKGROUND, -1);
        if (color == getColorInt(R.color.white)) {
            return R.style.AboutCardBackground_White;
        } else if (color == getColorInt(R.color.red)) {
            return R.style.AboutCardBackground_Red;
        } else if (color == getColorInt(R.color.pink)) {
            return R.style.AboutCardBackground_Pink;
        } else if (color == getColorInt(R.color.purple)) {
            return R.style.AboutCardBackground_Purple;
        } else if (color == getColorInt(R.color.deep_purple)) {
            return R.style.AboutCardBackground_Deep_Purple;
        } else if (color == getColorInt(R.color.indigo)) {
            return R.style.AboutCardBackground_Indigo;
        } else if (color == getColorInt(R.color.blue)) {
            return R.style.AboutCardBackground_Blue;
        } else if (color == getColorInt(R.color.light_blue)) {
            return R.style.AboutCardBackground_Light_Blue;
        } else if (color == getColorInt(R.color.cyan)) {
            return R.style.AboutCardBackground_Cyan;
        } else if (color == getColorInt(R.color.teal)) {
            return R.style.AboutCardBackground_Teal;
        } else if (color == getColorInt(R.color.green)) {
            return R.style.AboutCardBackground_Green;
        }else if (color == getColorInt(R.color.light_green)) {
            return R.style.AboutCardBackground_Light_Green;
        } else if (color == getColorInt(R.color.lime)) {
            return R.style.AboutCardBackground_Lime;
        } else if (color == getColorInt(R.color.yellow)) {
            return R.style.AboutCardBackground_Yellow;
        } else if (color == getColorInt(R.color.amber)) {
            return R.style.AboutCardBackground_Amber;
        } else if (color == getColorInt(R.color.orange)) {
            return R.style.AboutCardBackground_Orange;
        } else if (color == getColorInt(R.color.deep_orange)) {
            return R.style.AboutCardBackground_Deep_Orange;
        } else if (color == getColorInt(R.color.brown)) {
            return R.style.AboutCardBackground_Brown;
        } else if (color == getColorInt(R.color.grey)) {
            return R.style.AboutCardBackground_Grey;
        } else if (color == getColorInt(R.color.grey_900)) {
            return R.style.AboutCardBackground_Grey_900;
        } else if (color == getColorInt(R.color.blue_grey)) {
            return R.style.AboutCardBackground_Blue_Grey;
        } else if (color == getColorInt(R.color.blue_grey_900)) {
            return R.style.AboutCardBackground_Blue_Grey_900;
        } else if (color == getColorInt(R.color.black)) {
            return R.style.AboutCardBackground_Black;
        }
        return R.style.AboutCardBackground_White;
    }

    private int getColorInt(@ColorRes int color) {
        return context.getResources().getColor(color);
    }
}
