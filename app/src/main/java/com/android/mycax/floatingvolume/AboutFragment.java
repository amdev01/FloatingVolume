package com.android.mycax.floatingvolume;

import android.content.Context;
import android.os.Bundle;

import android.view.View;

import com.marcoscg.easyabout.EasyAboutFragment;
import com.marcoscg.easyabout.helpers.AboutItemBuilder;
import com.marcoscg.easyabout.items.AboutCard;
import com.marcoscg.easyabout.items.NormalAboutItem;
import com.marcoscg.easyabout.items.PersonAboutItem;
import com.marcoscg.licenser.Library;
import com.marcoscg.licenser.License;
import com.marcoscg.licenser.LicenserDialog;

public class AboutFragment extends EasyAboutFragment {

    @Override
    protected void configureFragment(final Context context, View rootView, Bundle savedInstanceState) {
        addCard(new AboutCard.Builder(context)
                .addItem(AboutItemBuilder.generateAppTitleItem(context)
                        .setSubtitle(R.string.app_author))
                .addItem(AboutItemBuilder.generateAppVersionItem(context, true)
                        .setIcon(R.drawable.ic_info_black_24dp))
                .addItem(new NormalAboutItem.Builder(context)
                        .setTitle(R.string.licenses)
                        .setIcon(R.drawable.ic_description_black_24dp)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new LicenserDialog(context)
                                        .setTitle(R.string.licenses)
                                        .setLibrary(new Library("Fancy Button",
                                                "https://github.com/medyo/Fancybuttons",
                                                License.MIT))
                                        .setLibrary(new Library("Spectrum",
                                                "https://github.com/the-blue-alliance/spectrum",
                                                License.MIT))
                                        .setLibrary(new Library("Dexter",
                                                "https://github.com/Karumi/Dexter",
                                                License.APACHE))
                                        .setLibrary(new Library("Floating View",
                                                "https://github.com/recruit-lifestyle/FloatingView",
                                                License.APACHE))
                                        .setLibrary(new Library("Easy About",
                                                "https://github.com/marcoscgdev/EasyAbout",
                                                License.MIT))
                                        .setLibrary(new Library("Licenser",
                                                "https://github.com/marcoscgdev/Licenser",
                                                License.MIT))
                                        .setPositiveButton(android.R.string.ok, null)
                                        .show();
                            }
                        })
                        .build())
                .addItem(AboutItemBuilder.generateLinkItem(context, "https://myczkowskiadam.github.io/FloatingVolume/")
                        .setTitle(R.string.visit_website)
                        .setIcon(R.drawable.ic_web_black_24dp))
                .build());

        addCard(new AboutCard.Builder(context)
                .setTitle(R.string.author)
                .addItem(new PersonAboutItem.Builder(context)
                        .setTitle(R.string.author_name)
                        .setSubtitle(R.string.author_location)
                        .setIcon(R.drawable.icon_profile)
                        .build())
                .addItem(AboutItemBuilder.generateLinkItem(context, "https://github.com/MyczkowskiAdam/FloatingVolume")
                        .setTitle(R.string.github_fork)
                        .setIcon(R.drawable.ic_social_github_black_24dp))
                .addItem(AboutItemBuilder.generateEmailItem(context, "mycaxd511@gmail.com")
                        .setTitle(R.string.send_email)
                        .setIcon(R.drawable.ic_mail_outline_black_24dp))
                .addItem(AboutItemBuilder.generateLinkItem(context, "https://www.youtube.com/channel/UCxQZUuxF0N7Aj0SNlLcEpgw")
                        .setTitle(R.string.youtube_channel)
                        .setIcon(R.drawable.ic_social_youtube_black_24dp))
                .build());

        addCard(new AboutCard.Builder(context)
                .setTitle(R.string.support)
                .addItem(AboutItemBuilder.generatePlayStoreItem(context)
                        .setTitle(R.string.rate_application)
                        .setIcon(R.drawable.ic_star_border_black_24dp))
                .addItem(AboutItemBuilder.generateLinkItem(context, "https://github.com/MyczkowskiAdam/FloatingVolume/issues/new")
                        .setTitle(R.string.bug_report)
                        .setIcon(R.drawable.ic_bug_report_black_24dp))
                .build());
    }
}
