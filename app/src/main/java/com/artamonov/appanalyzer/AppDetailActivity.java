package com.artamonov.appanalyzer;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    public static AppDetailViewModel appDetailViewModel2;
    public static AppList appGPApp;
    @BindView(R.id.detailed_app_name)
    TextView tvAppName;
    @BindView(R.id.detailed_app_version)
    TextView tvAppVersion;
    //@BindView(R.id.trust_level)
    TextView tvTrustLevel;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private AdView mAdView;

    public static double getOverallTrustLevel(long updatedTime, long runTime, String appSource, String gpInstalls, String gpPeople,
                                              String gpRating, String gpPublished, String permissionsAmount) {

        Log.i(MainActivity.TAG, "getOverallTrustLevel: updatedTime: " + updatedTime);
        double daysAfterLastUpdate = dateDiff(updatedTime);
        Log.i(MainActivity.TAG, "getOverallTrustLevel: daysAfterLastUpdate: " + daysAfterLastUpdate);
        Log.i(MainActivity.TAG, "getOverallTrustLevel: runTime: " + runTime);
        double daysAfterLastRun = dateDiff(runTime);
        Log.i(MainActivity.TAG, "getOverallTrustLevel: daysAfterLastRun: " + daysAfterLastRun);

        int daysAfterLastUpdateOnGp = dateDiffGp(gpPublished);
        String gpPeopleCut = gpPeople.replace(",", "");
        int gpPeopleInt = Integer.parseInt(gpPeopleCut);

        double gpRatingInt = Double.valueOf(gpRating);

        double permissionsAmountDouble = Double.valueOf(permissionsAmount);
        Log.i(MainActivity.TAG, "permissionsAmountDouble: " + permissionsAmountDouble);

        int gpInstallsInt;
        if (gpInstalls.substring(gpInstalls.length() - 1).equals("+")) {
            String gpInstallsCut = gpInstalls.substring(0, gpInstalls.length() - 1);
            String gpInstallsCommaCut = gpInstallsCut.replace(",", "");
            gpInstallsInt = Integer.parseInt(gpInstallsCommaCut);
        } else {
            gpInstallsInt = 1;
        }
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

        //String upd = String.valueOf(updatedGpTrust);

        Log.i(MainActivity.TAG, "getOverallTrustLevel: updatedTrust: ");
        Log.i(MainActivity.TAG, "getOverallTrustLevel: updatedTrust: " + updatedGpTrust);
        Log.i(MainActivity.TAG, "getOverallTrustLevel: runTimeTrust: " + runTimeTrust);
        Log.i(MainActivity.TAG, "getOverallTrustLevel: metaData: " + normalizedMetaData);
        Log.i(MainActivity.TAG, "getOverallTrustLevel: updatedGpTrust: " + updatedGpTrust);
        Log.i(MainActivity.TAG, "getOverallTrustLevel: sourceTrust: " + sourceTrust);
        Log.i(MainActivity.TAG, "getOverallTrustLevel: permissionsTrust: " + permissionsTrust);

        double overallTrust = (0.15 * updatedTrust + 0.05 * runTimeTrust + 0.25 * normalizedMetaData +
                0.05 * updatedGpTrust + 0.2 * sourceTrust + 0.3 * permissionsTrust) * 100;
        return (double) Math.round(overallTrust * 100) / 100;

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_app_analyzer);
        ButterKnife.bind(this);


        appDetailViewModel = ViewModelProviders.of(this).get(AppDetailViewModel.class);
        appDetailViewModel.getGpData(MainActivity.appList.getPackageName()).observe(this, new Observer<AppList>() {
            @Override
            public void onChanged(@Nullable final AppList logList) {
                // Update the cached copy of the applications in the adapter.
                if (logList != null) {
                    Log.w(MainActivity.TAG, " AppDetail: onChanged: Rating from DB: " + logList.getGpRating());
                    Log.w(MainActivity.TAG, " AppDetail: onChanged: Rating from DB: toString:" + logList.toString());
                    appGPApp = logList;
                } else {
                    Log.w(MainActivity.TAG, " AppDetail: onChanged: App does not exist in DB");
                    appGPApp = null;
                }

            }
        });


        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Bundle extras = getIntent().getExtras();
        byte[] byteLogo = extras.getByteArray("logo");
        Bitmap appLogo = BitmapFactory.decodeByteArray(byteLogo, 0, byteLogo.length);

        ImageView ivLogo = findViewById(R.id.detailed_app_icon);
        tvAppName.setText(MainActivity.appList.getName());
        tvAppVersion.setText(MainActivity.appList.getVersion());
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
