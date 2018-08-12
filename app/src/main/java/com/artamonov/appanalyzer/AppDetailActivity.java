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
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.artamonov.appanalyzer.data.database.AppList;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


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

   /* private ArrayList<String> appRequestedPermissions;
    private ArrayList<String> appGrantedPermissions;
    private ArrayList<String> requestedPermissionsProtectionLevel;
    private ArrayList<String> grantedPermissionsProtectionLevel;
    private TextView tvAppGrantedPermissions;
    private TextView tvAppRequestedPermissions;
    private TextView tvAppRequestedPermissionsLabel;
    private TextView tvAppGrantedPermissionsLabel;*/

    public static String getSimplifiedTrustLevel(String appSource, String gpInstalls, String gpPeople,
                                                 String gpRating) throws ParseException {
        Double sourceScore;
        if (appSource.equals("Google Play")) {
            sourceScore = 1.0;
        } else sourceScore = 0.3;

        if (TextUtils.isEmpty(gpInstalls) || TextUtils.isEmpty(gpPeople) ||
                TextUtils.isEmpty(gpRating)) {
            return String.valueOf(sourceScore * 60);
        }

        if (gpInstalls.length() > 0 && gpInstalls.charAt(gpInstalls.length() - 1) == '+') {
            gpInstalls = gpInstalls.substring(0, gpInstalls.length() - 1);
        }


        final String gpInstallsnew = gpInstalls.replace(",", " ");
        final String gpPeoplenew = gpPeople.replace(",", " ");


        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
        Number n = format.parse(gpRating);
        double rating = n.doubleValue();

        //double downloads = number.doubleValue();

        double downloads = Double.parseDouble(gpInstallsnew);
        double people = Double.parseDouble(gpPeoplenew);
        //double rating = Number.

        double GPScore = Math.log(downloads) + Math.log(people / rating);
        double normalizedGPScore = (GPScore - 6) / (15 - 6);
        double weightedNormalizedGPScore = normalizedGPScore * 0.4;

        return String.valueOf(100 * (0.6 * weightedNormalizedGPScore + 0.3 * sourceScore));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_app_analyzer);
        ButterKnife.bind(this);
/*
    appDetailViewModel = ViewModelProviders.of(this).get(AppDetailViewModel.class);
        appDetailViewModel.getAllApps().observe(this, new Observer<List<AppList>>() {
            @Override
            public void onChanged(@Nullable final List<AppList> logList) {
                // Update the cached copy of the applications in the adapter.
                for (int i = 0; i < logList.size(); i++) {
                    Log.i(MainActivity.TAG, "   LiveData<List<AppList>> from DB:  " + logList.get(i).getGpPeople());
                }
            }
        });*/


        appDetailViewModel = ViewModelProviders.of(this).get(AppDetailViewModel.class);
        appDetailViewModel.getGpData(MainActivity.appList.getPackageName()).observe(this, new Observer<AppList>() {
            @Override
            public void onChanged(@Nullable final AppList logList) {
                // Update the cached copy of the applications in the adapter.
                if (logList != null) {
                    Log.w(MainActivity.TAG, " AppDetail: onChanged: " + logList.getGpRating());
                    appGPApp = logList;
                } else {
                    Log.w(MainActivity.TAG, " AppDetail: onChanged: logList is Empty");
                    appGPApp = null;
                }

            }
        });



        /*appRequestedPermissions = intent.getStringArrayListExtra("requested permissions");
        appGrantedPermissions = intent.getStringArrayListExtra("granted permissions");
        requestedPermissionsProtectionLevel = intent.getStringArrayListExtra("requested permissions protection levels");
        grantedPermissionsProtectionLevel = intent.getStringArrayListExtra("granted permissions protection levels");*/


        // appRequestedPermissions = intent.getStringExtra("requested permissions");
        // appGrantedPermissions = intent.getStringExtra("granted permissions");
        //  Log.i(MainActivity.TAG, "AppDetailActivity: appSource: " + appSource);
        //  Log.i(MainActivity.TAG, "AppDetailActivity: appFirstInstallTime: " + appFirstInstallTime);
        Bundle extras = getIntent().getExtras();
        byte[] byteLogo = extras.getByteArray("logo");
        Bitmap appLogo = BitmapFactory.decodeByteArray(byteLogo, 0, byteLogo.length);


        // tvAppRequestedPermissions = findViewById(R.id.requested_app_permissions);
        // tvAppRequestedPermissions.setText(appRequestedPermissions);

    /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvAppGrantedPermissions = findViewById(R.id.granted_app_permissions);
            tvAppGrantedPermissionsLabel = findViewById(R.id.granted_app_permissions_label);
            tvAppGrantedPermissionsLabel.setText(getResources().getString(R.string.granted_app_permissions));
            //  tvAppGrantedPermissions.setText(appGrantedPermissions);
        }*/

        ImageView ivLogo = findViewById(R.id.detailed_app_icon);
        tvAppName.setText(MainActivity.appList.getName());
        tvAppVersion.setText(MainActivity.appList.getVersion());
        //  tvFirstInstallTime.setText(appFirstInstallTime);
        // tvLastRunTime.setText(appLastRunTime);
        //  tvLastUpdatedTime.setText(appLastUpdatedTime);
        ivLogo.setImageBitmap(appLogo);
        //    tvAppSource.setText(appSource);
        tvTrustLevel = findViewById(R.id.trust_level);
        tvTrustLevel.setText(MainActivity.appList.getTrustLevel());

        tabLayout.setupWithViewPager(viewPager);
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.setPageTitles(getResources().getString(R.string.first_tab));
        adapter.setPageTitles(getResources().getString(R.string.second_tab));
        adapter.setPageTitles(getResources().getString(R.string.third_tab));
        viewPager.setAdapter(adapter);
        try {
            tvTrustLevel.setText(getSimplifiedTrustLevel(MainActivity.appList.getAppSource(), MainActivity.appList.getGpInstalls(),
                    MainActivity.appList.getGpPeople(), MainActivity.appList.getGpRating()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
