package com.android.mycax.floatingvolume.utils;

public interface Constants {
    String PREF_KEY_LAST_POSITION_X = "last_position_x";
    String PREF_KEY_LAST_POSITION_Y = "last_position_y";
    String PREF_KEY_LAST_POSITION_X_EXPANDED = "last_position_x_expanded";
    String PREF_KEY_LAST_POSITION_Y_EXPANDED = "last_position_y_expanded";
    String PREF_THEME_VALUE = "pref_themes_value";
    String PREF_CUSTOM_THEME = "custom_theme_pref";
    String PREF_SAVE_LAST_POSITION = "settings_save_last_position";
    String PREF_DIALOG_STYLE = "pref_dialog_style";
    String PREF_DISABLE_FIXED_UI = "disable_fixed_ui";
    String PREF_ITEMS_TO_SHOW = "items_to_show_in_dialog_pref";
    String PREF_ABOUT_ME = "pref_about_me";
    String PREF_HEAD_OPACITY = "pref_head_opacity";
    String PREF_SETTINGS_START_ON_BOOT = "pref_settings_start_on_boot";
    String PREF_DIALOG_POSTITION = "pref_dialog_postition";
    String PREF_SHOW_MODE_SWITCH = "pref_show_mode_switch";
    String PREF_DIALOG_TIMEOUT = "pref_dialog_timeout";
    String PREF_FLOATING_ICON_SIZE = "pref_floating_icon_size";
    /* Spectrum */
    String PREF_SPECTRUM_COLOR_PRIMARY = "pref_spectrum_color_primary";
    String PREF_SPECTRUM_COLOR_ACCENT = "pref_spectrum_color_accent";
    String PREF_SPECTRUM_COLOR_DIALOG = "pref_spectrum_color_dialog";
    String PREF_SPECTRUM_COLOR_BACKGROUND = "pref_spectrum_color_background";
    String PREF_SPECTRUM_COLOR_ICONS = "pref_spectrum_color_icons";
    /* Spectrum color values */
    String PREF_COLOR_PRIMARY = "pref_color_primary";
    String PREF_COLOR_ACCENT = "pref_color_accent";
    String PREF_COLOR_DIALOG = "pref_color_dialog";
    String PREF_COLOR_BACKGROUND = "pref_color_background";
    String PREF_COLOR_ICONS = "pref_color_icons";
    /* end */
    String ARM64_V8A = "arm64-v8a";
    String ARMEABI_V7A = "armeabi-v7a";
    String X86 = "x86";
    String X86_64 = "x86_64";
    String CHANNEL_ID = "Floating Volume Channel";
    String SEEKBAR_MEDIA = "1";
    String SEEKBAR_RINGER = "2";
    String SEEKBAR_ALARM = "3";
    String SEEKBAR_VOICE_CALL = "4";
    String SEEKBAR_NOTICIATION = "5";
    String SIZE_24DP = "24";
    String SIZE_34DP = "34";
    String SIZE_44DP = "44";
    String SIZE_54DP = "54";
    String SIZE_64DP = "64";
    String SIZE_74DP = "74";
    int OVERLAY_PERMISSION_REQUEST = 1;
    int NOTIFICATION_POLICY_PERMISSION_REQUEST = 2;
    int THEME_PREFRENCES_REQUEST = 3;
    int NOTIFICATION_ID = 27;
    int THEME_LIGHT = 1;
    int THEME_DARK = 2;
    int THEME_CUSTOM = 3;
    int STYLE_DEFAULT = 1;
    int STYLE_SLIM = 2;
    int STYLE_VERTICAL = 3;
    int STYLE_P = 4;
    int RINGER_STYLE_IMAGE = 1;
    int RINGER_STYLE_FANCY = 2;
    int DIALOG_POSITION_LEFT = 1;
    int DIALOG_POSITION_CENTER = 2;
    int DIALOG_POSITION_RIGHT = 3;
    long MOVE_TO_EDGE_DURATION = 500L;
    float MOVE_TO_EDGE_OVERSHOOT_TENSION = 1.25f;
}
