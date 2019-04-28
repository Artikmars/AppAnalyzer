package com.artamonov.appanalyzer;

import static com.artamonov.appanalyzer.MainActivity.appList;
import static com.artamonov.appanalyzer.MainActivity.dateDiff;
import static com.artamonov.appanalyzer.MainActivity.dateDiffGp;
import static com.artamonov.appanalyzer.MainActivity.getSourceTrust;
import static com.artamonov.appanalyzer.MainActivity.mainTrustFormula;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.artamonov.appanalyzer.adapter.SectionPageAdapter;
import com.artamonov.appanalyzer.contract.AppDetailContract;
import com.artamonov.appanalyzer.data.database.AppList;
import com.artamonov.appanalyzer.network.GPDetailPageParser;
import com.artamonov.appanalyzer.presenter.AppDetailPresenter;
import com.artamonov.appanalyzer.utils.NetworkUtils;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;

public class AppDetailActivity extends AppCompatActivity
        implements AppDetailContract.AppDetailView {

    private static final String TAG = "myLogs";
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
    // private AppDetailContract.AppDetailPresenter appDetailPresenter;
    private AppDetailPresenter appDetailPresenter;

    public static double getOverallTrustLevel(
            long updatedTime,
            long runTime,
            String appSource,
            String gpInstalls,
            String gpPeople,
            String gpRating,
            String gpPublished,
            String permissionsAmount) {

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
        Log.w(MainActivity.TAG, "sourceTrust in formula: " + appSource);
        int sourceTrust = getSourceTrust(appSource);
        Log.w(MainActivity.TAG, "sourceTrust score: " + sourceTrust);

        double runTimeTrust = daysAfterLastRun / (2.0 * 14.0);
        if (runTimeTrust > 1) {
            runTimeTrust = 1;
        }
        // double metaData = (Math.log(gpPeopleInt / gpRatingInt) + Math.log(gpInstallsInt));
        // double normalizedMetaData = (metaData - 8) / (46 - 8);
        double metaData = gpRatingInt * (Math.log(gpPeopleInt) + Math.log(gpInstallsInt));
        double normalizedMetaData = metaData / 160;

        Log.w(
                MainActivity.TAG,
                "OVERALL: updated time: "
                        + 20 * updatedTrust
                        + ", runTime: "
                        + 5 * runTimeTrust
                        + ", normalizedMetaData: "
                        + 20 * normalizedMetaData
                        + "updatedGpTrust: "
                        + 5 * updatedGpTrust
                        + ", permissions: "
                        + 30 * permissionsTrust
                        + ", source: "
                        + 20 * sourceTrust);
        // double overallTrust = (0.2 * updatedTrust + 0.05 * runTimeTrust + 0.2 *
        // normalizedMetaData +
        //         0.05 * updatedGpTrust + 0.2 * sourceTrust + 0.3 * permissionsTrust) * 100;
        double overallTrust =
                (0.25 * updatedTrust
                                        + 0.05 * runTimeTrust
                                        + 0.4 * sourceTrust
                                        + 0.3 * permissionsTrust)
                                * 75
                        + (0.9 * normalizedMetaData + 0.1 * updatedGpTrust) * 25;
        Log.w(MainActivity.TAG, "Overall trust: " + overallTrust);
        Log.w(
                MainActivity.TAG,
                "Overall trust rounded: " + (double) Math.round(overallTrust * 100) / 100);
        return (double) Math.round(overallTrust * 100) / 100;
    }

    public static double getOnlineTrustLevel(
            String installs, String peopleVoted, String rating, String updated) {

        int gpPeopleInt = Integer.parseInt(peopleVoted);
        double gpRatingInt = Double.valueOf(rating);
        int gpInstallsInt = Integer.parseInt(installs);
        int daysAfterLastUpdateOnGp = dateDiffGp(updated);
        double updatedGpTrust = mainTrustFormula(14, 7, daysAfterLastUpdateOnGp);
        double metaData = gpRatingInt * (Math.log(gpPeopleInt) + Math.log(gpInstallsInt));
        double normalizedMetaData = metaData / 1.6;
        double onlineTrust = 0.9 * normalizedMetaData + 0.1 * updatedGpTrust;
        Log.w(
                MainActivity.TAG,
                "ONLINE: normalizedMetaData time: "
                        + 0.9 * normalizedMetaData
                        + ", updatedGpTrust: "
                        + 0.1 * updatedGpTrust);
        Log.w(MainActivity.TAG, "Online trust: " + onlineTrust);
        Log.w(
                MainActivity.TAG,
                "Online trust rounded: " + (double) Math.round(onlineTrust * 100) / 100);
        return (double) Math.round(onlineTrust * 100) / 100;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_app_analyzer);
        Log.i(TAG, "ApPDetailActivity: onCreate");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        appDetailPresenter = new AppDetailPresenter(this);
        ButterKnife.bind(this);

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        // MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        // AdView mAdView = findViewById(R.id.adView);
        // AdRequest adRequest = new AdRequest.Builder().build();
        // mAdView.loadAd(adRequest);

        Bundle extras = getIntent().getExtras();
        byte[] byteLogo = extras.getByteArray(getString(R.string.item_logo));
        Bitmap appLogo = BitmapFactory.decodeByteArray(byteLogo, 0, byteLogo.length);

        ImageView ivLogo = findViewById(R.id.detailed_app_icon);
        tvAppName.setText(MainActivity.appList.getName());

        if (appList.getVersion() != null && MainActivity.appList.getVersion().length() >= 15) {
            String shortenedVersion = MainActivity.appList.getVersion().substring(0, 15);
            tvAppVersion.setText(shortenedVersion);
        } else {
            tvAppVersion.setText(MainActivity.appList.getVersion());
        }

        ivLogo.setImageBitmap(appLogo);

        tabLayout.setupWithViewPager(viewPager);
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.setPageTitles(getResources().getString(R.string.first_tab));

        if (!NetworkUtils.isNetworkAvailable(getApplicationContext())
                || appList.getAppSource().equals("Google Play")) {
            adapter.setPageTitles(getResources().getString(R.string.second_tab));
        }
        adapter.setPageTitles(getResources().getString(R.string.third_tab));
        viewPager.setAdapter(adapter);
        Log.i(TAG, "ApPDetailActivity: adapter " + adapter);
        Log.i(TAG, "ApPDetailActivity: adapter " + viewPager);

        tvTrustLevel = findViewById(R.id.trust_level);
        tvTrustLevel.setText(String.valueOf(MainActivity.appList.getOfflineTrust()));
        // double offlineTrust = MainActivity.appList.getOfflineTrust();
        // String offlineTrustString = String.valueOf(offlineTrust);

        if (NetworkUtils.isNetworkAvailable(getApplicationContext())
                && appList.getAppSource().equals("Google Play")) {
            appDetailPresenter.parseGPData(getApplicationContext());
        }

        appDetailViewModel = ViewModelProviders.of(this).get(AppDetailViewModel.class);
        appDetailViewModel
                .getGpData(MainActivity.appList.getPackageName())
                .observe(
                        this,
                        new Observer<AppList>() {
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
    }

    @Override
    public void showProgressDialog() {}

    @Override
    public void dismissProgressDialog() {}

    @Override
    public void populateOverallTrust() {
        tvTrustLevel.setText(GPDetailPageParser.parsedAppList.getOverallTrust());
    }

    @Override
    public void setSearchAppsAdapter(
            ArrayList<String> arrayAppNames, ArrayList<String> arrayLinks) {}

    @Override
    public void populateOnlineTrust() {}
}
