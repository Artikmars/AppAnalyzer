package com.artamonov.appanalyzer;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.artamonov.appanalyzer.data.database.AppList;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.artamonov.appanalyzer.MainActivity.dateDiff;
import static com.artamonov.appanalyzer.MainActivity.dateDiffGp;
import static com.artamonov.appanalyzer.MainActivity.getSourceTrust;
import static com.artamonov.appanalyzer.MainActivity.mainTrustFormula;


public class AppDetailActivity extends AppCompatActivity {

    public static AppDetailViewModel appDetailViewModel;
    public static AppList appGPApp;
    @BindView(R.id.detailed_app_name)
    TextView tvAppName;
    @BindView(R.id.detailed_app_version)
    TextView tvAppVersion;
    TextView tvTrustLevel;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    public static double getOverallTrustLevel(long updatedTime, long runTime, String appSource, String gpInstalls, String gpPeople,
                                              String gpRating, String gpPublished, String permissionsAmount) {

        double daysAfterLastUpdate = dateDiff(updatedTime);
        double daysAfterLastRun = dateDiff(runTime);
        int daysAfterLastUpdateOnGp = dateDiffGp(gpPublished);

        int gpPeopleInt = Integer.parseInt(gpPeople);

        double gpRatingInt = Double.valueOf(gpRating);

        double permissionsAmountDouble = Double.valueOf(permissionsAmount);
        int gpInstallsInt = Integer.parseInt(gpInstalls);

        double updatedTrust = mainTrustFormula(14, 7, daysAfterLastUpdate);
        double updatedGpTrust = mainTrustFormula(14, 7, daysAfterLastUpdateOnGp);
        double permissionsTrust = mainTrustFormula(7, 3, permissionsAmountDouble);

        int sourceTrust = getSourceTrust(appSource);

        double runTimeTrust = daysAfterLastRun / (2.0 * 14.0);
        if (runTimeTrust > 1) {
            runTimeTrust = 1;
        }
        double metaData = (Math.log(gpPeopleInt / gpRatingInt) + Math.log(gpInstallsInt));
        double normalizedMetaData = (metaData - 8) / (46 - 8);

        double overallTrust = (0.15 * updatedTrust + 0.05 * runTimeTrust + 0.25 * normalizedMetaData +
                0.05 * updatedGpTrust + 0.2 * sourceTrust + 0.3 * permissionsTrust) * 100;
        return (double) Math.round(overallTrust * 100) / 100;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_app_analyzer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);


        appDetailViewModel = ViewModelProviders.of(this).get(AppDetailViewModel.class);
        appDetailViewModel.getGpData(MainActivity.appList.getPackageName()).observe(this, new Observer<AppList>() {
            @Override
            public void onChanged(@Nullable final AppList logList) {
                // Update the cached copy of the applications in the adapter.
                if (logList != null) {
                    appGPApp = logList;
                } else {
                    appGPApp = null;
                }

            }
        });


        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Bundle extras = getIntent().getExtras();
        byte[] byteLogo = extras.getByteArray(getString(R.string.item_logo));
        Bitmap appLogo = BitmapFactory.decodeByteArray(byteLogo, 0, byteLogo.length);

        ImageView ivLogo = findViewById(R.id.detailed_app_icon);
        tvAppName.setText(MainActivity.appList.getName());

        if (MainActivity.appList.getVersion().length() >= 15) {
            String shortenedVersion = MainActivity.appList.getVersion().substring(0, 15);
            tvAppVersion.setText(shortenedVersion);
        } else {
            tvAppVersion.setText(MainActivity.appList.getVersion());
        }


        ivLogo.setImageBitmap(appLogo);

        tabLayout.setupWithViewPager(viewPager);
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.setPageTitles(getResources().getString(R.string.first_tab));
        adapter.setPageTitles(getResources().getString(R.string.second_tab));
        adapter.setPageTitles(getResources().getString(R.string.third_tab));
        viewPager.setAdapter(adapter);

        tvTrustLevel = findViewById(R.id.trust_level);
        double offlineTrust = MainActivity.appList.getOfflineTrust();
        String offlineTrustString = String.valueOf(offlineTrust);
        tvTrustLevel.setText(offlineTrustString);


    }
}
