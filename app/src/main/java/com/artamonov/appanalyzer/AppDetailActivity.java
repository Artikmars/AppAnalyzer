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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    public static double getOfflineTrustLevel(long updatedTime, long runTime, String appSource,
                                              String permissionsAmount) {

        Log.i(MainActivity.TAG, "getOfflineTrustLevel: updatedTime: " + updatedTime);
        double daysAfterLastUpdate = dateDiff(updatedTime);
        Log.i(MainActivity.TAG, "getOfflineTrustLevel: daysAfterLastUpdate: " + daysAfterLastUpdate);
        Log.i(MainActivity.TAG, "getOfflineTrustLevel: runTime: " + runTime);
        double daysAfterLastRun = dateDiff(runTime);
        Log.i(MainActivity.TAG, "getOfflineTrustLevel: daysAfterLastRun: " + daysAfterLastRun);

        double permissionsAmountDouble = Double.valueOf(permissionsAmount);
        Log.i(MainActivity.TAG, "getOfflineTrustLevel: permissionsAmountDouble: " + permissionsAmountDouble);


        double updatedTrust = mainTrustFormula(14, 7, daysAfterLastUpdate);
        double permissionsTrust = mainTrustFormula(7, 3, permissionsAmountDouble);

        int sourceTrust = getSourceTrust(appSource);

        double runTimeTrust = daysAfterLastRun / (2.0 * 14.0);
        if (runTimeTrust > 1) {
            runTimeTrust = 1;
        }

        Log.i(MainActivity.TAG, "getOfflineTrustLevel: updatedTrust: ");
        Log.i(MainActivity.TAG, "getOfflineTrustLevel: runTimeTrust: " + runTimeTrust);
        Log.i(MainActivity.TAG, "getOfflineTrustLevel: sourceTrust: " + sourceTrust);
        Log.i(MainActivity.TAG, "getOfflineTrustLevel: permissionsTrust: " + permissionsTrust);

        double offlineTrust = (0.25 * updatedTrust + 0.05 * runTimeTrust + 0.3 * sourceTrust + 0.4 * permissionsTrust) * 100;
        Log.i(MainActivity.TAG, "getOfflineTrustLevel: offlineTrust: " + offlineTrust);
        return (double) Math.round(offlineTrust * 100) / 100;
        // return Double.parseDouble(offlineTrustString);


      /*  if (TextUtils.isEmpty(gpInstalls) || TextUtils.isEmpty(gpPeople) ||
                TextUtils.isEmpty(gpRating)) {
            return String.valueOf(sourceScore * 60);
        }*/

       /* if (gpInstalls.length() > 0 && gpInstalls.charAt(gpInstalls.length() - 1) == '+') {
            gpInstalls = gpInstalls.substring(0, gpInstalls.length() - 1);
        }*/

/*
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
        Number n = format.parse(gpRating);
        double rating = n.doubleValue();*/

        //double downloads = number.doubleValue();


    }

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

    private static int dateDiffGp(String gpPublished) {
        if (gpPublished == null) {
            return 0;
        }
        String[] gpPublishedArray = gpPublished.split(" ", 3);

        String month = gpPublishedArray[0];
        Date date = null;
        try {
            date = new SimpleDateFormat("MMMM", Locale.GERMAN).parse(month);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            int day = Integer.parseInt(gpPublishedArray[1].substring(0, gpPublishedArray[1].length() - 1));
            int year = Integer.parseInt(gpPublishedArray[2]);
            Calendar thatDay = Calendar.getInstance();
            thatDay.set(Calendar.DAY_OF_MONTH, day);
            thatDay.set(Calendar.MONTH, cal.get(Calendar.MONTH));
            thatDay.set(Calendar.YEAR, year);

            Calendar today = Calendar.getInstance();

            return (int) ((today.getTimeInMillis() - thatDay.getTimeInMillis()) / (60 * 60 * 24 * 1000));
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

    }

    private static int getSourceTrust(String appSource) {
        if (appSource.equals("Google Play")) {
            return 1;
        } else return 0;
    }

    private static double dateDiff(long timestamp) {
        //timestamp - 23.08.2018 at 02:45:48
        if (timestamp == 0) {
            return 0;
        }
        Calendar today = Calendar.getInstance();
        int days = (int) ((today.getTimeInMillis() - timestamp) / (60 * 60 * 24 * 1000));
        return (double) days;
    }

    private static double mainTrustFormula(double day1, double day2, double z) {
        Log.i(MainActivity.TAG, "mainTrustFormula: day1: " + day1);
        Log.i(MainActivity.TAG, "mainTrustFormula: day2: " + day2);
        Log.i(MainActivity.TAG, "mainTrustFormula: daysAfterLastUpdate: " + z);
        double rest = 0.05;
        double exp = Math.log(2.0) / Math.log(1.0 + (day2 / day1));
        Log.i(MainActivity.TAG, "mainTrustFormula: exp: " + exp);
        double exp2 = Math.pow((z / day1), exp);
        Log.i(MainActivity.TAG, "mainTrustFormula: exp2: " + exp2);
        return (1.0 - rest) * Math.pow(0.5, exp2) + rest;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_app_analyzer);
        ButterKnife.bind(this);

     /*   appDetailViewModel = ViewModelProviders.of(this).get(AppDetailViewModel.class);
        appDetailViewModel.getAllApps().observe(this, new Observer<List<AppList>>() {
            @Override
            public void onChanged(@Nullable final List<AppList> logList) {
                // Update the cached copy of the applications in the adapter.
                for (int i = 0; i < logList.size(); i++) {
                    Log.w(MainActivity.TAG, "   LiveData<List<AppList>> from DB:  " + logList.get(i).getGpPeople());
                }
            }
        });*/

        //WORKABLE

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

        String offlineTrust = String.valueOf(getOfflineTrustLevel(MainActivity.appList.getLastUpdateTimeInMilliseconds(),
                MainActivity.appList.getLastRunTimeInMilliseconds(), MainActivity.appList.getAppSource(),
                MainActivity.appList.getDangerousPermissionsAmount()));
        tvTrustLevel.setText(offlineTrust);


    }
}
