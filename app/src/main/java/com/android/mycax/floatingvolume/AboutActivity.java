package com.android.mycax.floatingvolume;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.android.mycax.floatingvolume.utils.Constants;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(Constants.PREF_ENABLE_DARK_MODE, false) ? R.style.AppTheme_Dark : R.style.AppTheme);
        setContentView(R.layout.activity_about);
        loadAbout();
    }

    private void loadAbout() {
        final FrameLayout flHolder = findViewById(R.id.about);

        AboutBuilder builder = AboutBuilder.with(this)
                .setAppIcon(R.drawable.ic_floating_head)
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
                .setWrapScrollView(true)
                .setShowAsCard(true);

        AboutView view = builder.build();

        flHolder.addView(view);
    }
}
