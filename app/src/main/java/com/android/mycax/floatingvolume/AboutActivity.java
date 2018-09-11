package com.android.mycax.floatingvolume;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.android.mycax.floatingvolume.utils.AppUtils;
import com.android.mycax.floatingvolume.utils.Constants;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

public class AboutActivity extends AppCompatActivity {
    private int theme;
    private AppUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        utils = new AppUtils(this);
        theme = Integer.valueOf(sharedPref.getString(Constants.PREF_THEME_VALUE, "1"));
        utils.onActivityCreateSetTheme(this, theme);
        if (theme == 3) {
            utils.setActionBarTextColor(getSupportActionBar());
        }
        setContentView(R.layout.activity_about);
        loadAbout();
    }

    private int setCardBackgroundColor() {
        switch (theme) {
            case 1:
                return R.color.white;
            case 2:
                return R.color.black;
            case 3:
                return utils.getCardBackgroundColor();
        }
        return R.color.white;
    }

    private void loadAbout() {
        final FrameLayout flHolder = findViewById(R.id.about);

        AboutBuilder builder = AboutBuilder.with(this)
                .setAppIcon(R.drawable.splash_screen_icon)
                .setAppName(R.string.app_name)
                .setPhoto(R.drawable.icon_profile)
                .setCover(R.mipmap.profile_cover)
                .setLinksAnimated(true)
                .setDividerDashGap(0)
                .setName(R.string.dev_name)
                .setSubTitle(R.string.dev_sub)
                .setLinksColumnsCount(3)
                .addFacebookLink(R.string.name_fb_ig)
                .addTwitterLink(R.string.name_twitter)
                .addInstagramLink(R.string.name_fb_ig)
                .addGooglePlusLink(R.string.name_googleplus)
                .addYoutubeChannelLink(R.string.name_youtube)
                .addEmailLink(R.string.name_email)
                .addGitHubLink(R.string.name_github)
                .addWebsiteLink(R.string.url_fv_website)
                .addLink(R.mipmap.github, R.string.git_project, getString(R.string.url_fv_repo))
                .setVersionNameAsAppSubTitle()
                .addShareAction(R.string.app_name, R.string.url_xdalabs_link)
                .setActionsColumnsCount(2)
                .addFeedbackAction(R.string.name_email)
                .setBackgroundColor(setCardBackgroundColor())
                .setWrapScrollView(true)
                .setLinksAnimated(true)
                .setShowAsCard(true);

        AboutView view = builder.build();

        flHolder.addView(view);
    }
}
