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
                .addFacebookLink("mycax6")
                .addTwitterLink("Adam_Myczkowski")
                .addInstagramLink("mycax6")
                .addGooglePlusLink("112043897899708921734")
                .addYoutubeChannelLink("UCxQZUuxF0N7Aj0SNlLcEpgw")
                .addEmailLink("mycaxd511@gmail.com")
                .addGitHubLink("MyczkowskiAdam")
                .addWebsiteLink("https://myczkowskiadam.github.io/FloatingVolume/")
                .addLink(R.mipmap.github, R.string.git_project, "https://github.com/MyczkowskiAdam/FloatingVolume")
                .setVersionNameAsAppSubTitle()
                .addShareAction(R.string.app_name)
                .setActionsColumnsCount(2)
                .addFeedbackAction("mycaxd511@gmail.com")
                .setWrapScrollView(true)
                .setShowAsCard(true);

        AboutView view = builder.build();

        flHolder.addView(view);
    }
}
