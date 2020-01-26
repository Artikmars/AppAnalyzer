package com.artamonov.appanalyzer;

import android.app.ProgressDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.artamonov.appanalyzer.adapter.AppRecyclerViewAdapter;
import com.artamonov.appanalyzer.adapter.MainPageAdapter;
import com.artamonov.appanalyzer.adapter.SectionPageAdapter;
import com.artamonov.appanalyzer.data.database.AppList;
import com.artamonov.appanalyzer.utils.NetworkUtils;
import com.artamonov.appanalyzer.widget.ApplicationsWidgetProvider;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener,
        AppRecyclerViewAdapter.ItemClickListener {


    public static final String TAG = "myLogs";

    public static List<AppList> installedApps = new ArrayList<>();
    public static AppList appList;
    public static List<AppList> applicationsWidgetListUnsorted;
    private static Integer highOfflineScoreApps;
    private static Integer middleOfflineScoreApps;
    private static Integer lowOfflineScoreApps;
    public List<UsageStats> mListUsageStats;
    public long lastTimeExecuted;
    private String permissionGroupAmount;
    private boolean isFirstRun = true;
    private ProgressDialog progressDialog;
    private AppRecyclerViewAdapter appRecyclerViewAdapter;
    private RecyclerView recyclerView;
    @BindView(R.id.main_tab_layout)
    TabLayout mainTabLayout;
    @BindView(R.id.main_view_pager)
    ViewPager mainViewPager;


    public static double getOfflineTrustLevel(long updatedTime, long runTime, String appSource,
                                              String permissionsAmount) {

        double daysAfterLastUpdate = dateDiff(updatedTime);
        double daysAfterLastRun = dateDiff(runTime);
        double permissionsAmountDouble;
        if (permissionsAmount != null) {
            permissionsAmountDouble = Double.valueOf(permissionsAmount);
        } else {
            permissionsAmountDouble = 30;
        }
        double updatedTrust = mainTrustFormula(14, 7, daysAfterLastUpdate);
        double permissionsTrust = mainTrustFormula(7, 3, permissionsAmountDouble);

        int sourceTrust = getSourceTrust(appSource);

        double runTimeTrust = daysAfterLastRun / (2.0 * 14.0);
        if (runTimeTrust > 1) {
            runTimeTrust = 1;
        }
        double offlineTrust = (0.25 * updatedTrust + 0.05 * runTimeTrust + 0.3 * sourceTrust + 0.4 * permissionsTrust) * 100;
        Log.w(MainActivity.TAG, "OFFLINE: updatedTrust: " + 0.25 * updatedTrust + ", runTimeTrust: "
                + 0.05 * runTimeTrust + ", sourceTrust: " + 0.3 * sourceTrust + ", permissionsTrust: " + 0.4 * permissionsTrust);
        return (double) Math.round(offlineTrust * 100) / 100;

    }

    public static void getHighOfflineScoreApps() {
        int count = 0;
        for (int i = 0; i < installedApps.size(); i++) {
            if (installedApps.get(i).getOfflineTrust() >= 80) {
                count++;
            }
        }
        Log.i(TAG, "highOfflineScoreApps: " + highOfflineScoreApps);
        highOfflineScoreApps = count;
    }

    public static void getMiddleOfflineScoreApps() {
        int count = 0;
        for (int i = 0; i < installedApps.size(); i++) {
            if (installedApps.get(i).getOfflineTrust() >= 60 && installedApps.get(i).getOfflineTrust() < 80) {
                count++;
            }
        }
        middleOfflineScoreApps = count;
    }

    public static void getLowOfflineScoreApps() {
        int count = 0;
        for (int i = 0; i < installedApps.size(); i++) {
            if (installedApps.get(i).getOfflineTrust() < 60) {
                count++;
            }
        }
        lowOfflineScoreApps = count;
    }


    static int dateDiffGp(String gpPublished) {
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

    static int getSourceTrust(String appSource) {

        if (appSource.equals("Google Play")) {
            return 1;
        } else return 0;
    }

    static double dateDiff(long timestamp) {
        //timestamp - 23.08.2018 at 02:45:48
        if (timestamp == 0) {
            return 0;
        }
        Calendar today = Calendar.getInstance();
        int days = (int) ((today.getTimeInMillis() - timestamp) / (60 * 60 * 24 * 1000));
        return (double) days;
    }

    static double mainTrustFormula(double day1, double day2, double z) {
        double rest = 0.05;
        double exp = Math.log(2.0) / Math.log(1.0 + (day2 / day1));
        double exp2 = Math.pow((z / day1), exp);
        return (1.0 - rest) * Math.pow(0.5, exp2) + rest;
    }

    public byte[] drawableToByte(Drawable drawable) {


        // Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        final Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Crashlytics
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        // progressBar = findViewById(R.id.progressBar);
        //  progressBar.setVisibility(View.VISIBLE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Retrieving the list of installed applications ...");
        progressDialog.setTitle("In Progress");
        progressDialog.setCancelable(false);
        // progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);


        isFirstRun = checkFirstRun();

        mainTabLayout.setupWithViewPager(mainViewPager);
        MainPageAdapter adapter = new MainPageAdapter(getSupportFragmentManager());
        adapter.setPageTitles(getResources().getString(R.string.phone));
        adapter.setPageTitles(getResources().getString(R.string.applications));
        mainViewPager.setAdapter(adapter);

//        appRecyclerViewAdapter = new AppRecyclerViewAdapter(MainActivity.this, installedApps, this);
//        recyclerView = findViewById(R.id.app_list);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        recyclerView.setAdapter(appRecyclerViewAdapter);

        new getInstalledApplicationsTask().execute();
        setupSharedPreferences();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private boolean checkFirstRun() {
        boolean isFirstRun = false;
        Log.w(MainActivity.TAG, "onCheck: isFirstRun: " + isFirstRun);
        final String PREFS_NAME = "FirstTimeUsedPrefs";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOES_NOT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOES_NOT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            // This is just a normal run
            Log.w(MainActivity.TAG, "onCheck: normal run: " + isFirstRun);

        } else if (savedVersionCode == DOES_NOT_EXIST && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
            isFirstRun = true;
            Log.w(MainActivity.TAG, "onCheck: savedVersionCode == DOES_NOT_EXIST " + isFirstRun);
        }
        //upgrade case  Log.w(MainActivity.TAG, "onCheck: currentVersionCode > savedVersionCode: " + isFirstRun);
        else if (currentVersionCode > savedVersionCode) {
            Log.w(MainActivity.TAG, "onCheck: currentVersionCode > savedVersionCode: " + isFirstRun);
        }
        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
        return isFirstRun;
    }

    private void setupSharedPreferences() {

        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager
                .getDefaultSharedPreferences(this);
        loadSourceFromPreferences(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.overview:
                Log.i(TAG, "IN OVERVIEW MENU");
                getHighOfflineScoreApps();
                getMiddleOfflineScoreApps();
                getLowOfflineScoreApps();
                Intent intent = new Intent(this, OverviewActivity.class);
                Log.i(TAG, "applications_amount: " + installedApps.size() + " high_offline_score_apps: " + highOfflineScoreApps
                        + "middle_offline_score_apps: " + middleOfflineScoreApps);
                intent.putExtra("applications_amount", installedApps.size());
                intent.putExtra("high_offline_score_apps", highOfflineScoreApps);
                intent.putExtra("middle_offline_score_apps", middleOfflineScoreApps);
                intent.putExtra("low_offline_score_apps", lowOfflineScoreApps);
                startActivity(intent);
                break;

            case R.id.search_gp:
                Log.i(TAG, "IN SEARCH_GP MENU");
                Intent intent_search = new Intent(this, SearchActivity.class);
                startActivity(intent_search);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private String[] getRequestedPermissions(final String appPackage) {
        String[] requestedPermissions = null;
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(appPackage, PackageManager.GET_PERMISSIONS);
            requestedPermissions = pi.requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return requestedPermissions;
    }

    private ArrayList<String> getDangerousPermissions(String[] permissionsArray) {

        ArrayList<String> dangerousPermissionsList = new ArrayList<>();
        for (int i = 0; i < permissionsArray.length; i++) {
            try {
                PermissionInfo permissionInfo = getPackageManager().getPermissionInfo(permissionsArray[i], PackageManager.GET_META_DATA);
                switch (permissionInfo.protectionLevel) {
                    case PermissionInfo.PROTECTION_DANGEROUS:
                        dangerousPermissionsList.add(permissionsArray[i]);
                        break;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return dangerousPermissionsList;
    }

    private ArrayList<String> getPermissionGroups(ArrayList<String> dangerousPermissionsList) {

        ArrayList<String> permissionGroups = new ArrayList<>();
        for (int i = 0; i < dangerousPermissionsList.size(); i++) {
            PermissionGroupInfo permissionGroupInfo = null;
            try {
                PermissionInfo permissionInfo = getPackageManager().getPermissionInfo(dangerousPermissionsList.get(i), PackageManager.GET_META_DATA);
                permissionGroupInfo = getPackageManager().getPermissionGroupInfo(permissionInfo.group, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (permissionGroupInfo != null) {
                permissionGroups.add((String) permissionGroupInfo.loadLabel(getPackageManager()));
            }
        }
        Set<String> tempSet = new HashSet<>(permissionGroups);
        permissionGroups.clear();
        permissionGroups.addAll(tempSet);

        return permissionGroups;

    }

    private String getAppSource(String appSourceType, PackageInfo p) {
        boolean isSystemApp = isSystemPackage(p);
        if (isSystemApp) {
            return getString(R.string.system_app);
        } else {
            Log.i(TAG, getString(R.string.source_type) + appSourceType);
            if (TextUtils.isEmpty(appSourceType)) {
                return getString(R.string.unknown_source);
            } else if (appSourceType.equals(getString(R.string.vending_package))) {
                return getString(R.string.google_play);
            } else if (appSourceType.equals(getString(R.string.venezia_package))) {
                return getString(R.string.amazon_store);
            } else return "Undefined Market";
        }
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private String currentMilliSecondsToDate(long time) {

        return new SimpleDateFormat(getString(R.string.date_format), Locale.GERMAN)
                .format(new Date(time));

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    private void loadSourceFromPreferences(SharedPreferences sharedPreferences) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
                new ComponentName(getApplicationContext(), ApplicationsWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lvAppWidget);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onItemClick(int position) {
        appList = installedApps.get(position);
        Intent intent = new Intent(this, AppDetailActivity.class);
        Log.i(TAG, "onItemClick: intent:" + intent);
        intent.putExtra(getString(R.string.item_position), position);
        intent.putExtra(getString(R.string.item_logo), drawableToByte(appList.getIcon()));
        startActivity(intent);
        Log.i(TAG, "onItemClick: startActivity(intent)");
       /* if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intent, bundle);
        } else {
            startActivity(intent);
        }*/
    }

    private class getInstalledApplicationsTask extends AsyncTask<Void, Integer, List<AppList>> {

        int progressStatus = 0;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected List<AppList> doInBackground(Void... voids) {


            /** @param time = current time
             *  @param delta = initial period
             *  @param interval = time - delta (it is the time interval which is used by UsageStats
             *                  for extracting the data)
             *
             */

            List<AppList> res = new ArrayList<>();
            List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
            Calendar c = Calendar.getInstance();
            c.add(Calendar.YEAR, -1);
            long begin = c.getTimeInMillis();
            long end = System.currentTimeMillis();
            UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            mListUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                    begin, end);


            for (int i = 0; i < packs.size(); i++) {
                progressStatus = progressStatus + packs.size() / 100;
                progressDialog.setMax(packs.size());
                publishProgress(progressStatus);

                PackageInfo p = packs.get(i);
                if ((!isSystemPackage(p))) {

                    AppList appList = new AppList();
                    String packageName = p.packageName;
                    appList.setPackageName(packageName);
                    // Log.w(MainActivity.TAG, "packageName: " + packageName);
                    appList.setName(p.applicationInfo.loadLabel(getPackageManager()).toString());
                    // Log.w(MainActivity.TAG, "app name: " + p.applicationInfo.loadLabel(getPackageManager()).toString());
                    appList.setVersion(p.versionName);
                    long lastUpdatedTimeLong = p.lastUpdateTime;
                    String lastUpdateTime = currentMilliSecondsToDate(lastUpdatedTimeLong);
                    appList.setLastUpdateTime(lastUpdateTime);
                    appList.setLastUpdateTimeInMilliseconds(lastUpdatedTimeLong);

                    String appSourceType = getPackageManager().getInstallerPackageName(packageName);
                    String appSource = getAppSource(appSourceType, p);
                    Log.w(TAG, "source: " + appSource);
                    appList.setAppSource(appSource);

                    String[] appRequestedPermissionsArray = getRequestedPermissions(packageName);
                    if (appRequestedPermissionsArray != null) {
                        Integer dangerousPermissionsAmount = getDangerousPermissions(appRequestedPermissionsArray).size();
                        String dangerousPermissionsAmountString = Integer.toString(dangerousPermissionsAmount);
                        appList.setDangerousPermissionsAmount(dangerousPermissionsAmountString);

                        ArrayList<String> permissionGroupsList = getPermissionGroups(getDangerousPermissions(appRequestedPermissionsArray));
                        String permissionGroupsString = TextUtils.join("\n", permissionGroupsList);
                        appList.setPermissionGroups(permissionGroupsString);

                        permissionGroupAmount = Integer.toString(permissionGroupsList.size());
                        appList.setPermissionGroupsAmount(permissionGroupAmount);
                    } else {
                        appList.setDangerousPermissionsAmount("0");
                    }


                    //Connect to the Firebase Realtime Database
                    if (isFirstRun) {
                        DatabaseReference permissionsReference = FirebaseDatabase
                                .getInstance()
                                .getReference(getString(R.string.perm_group_amount));
                        Log.w(MainActivity.TAG, "permissionsReference: " + permissionsReference);
                        String id = permissionsReference.push().getKey();

                        permissionsReference.child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                Log.w(MainActivity.TAG, "onDataChange: " + snapshot.getValue());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(MainActivity.TAG, "onCancelled: " + databaseError.getMessage());
                            }
                        });


                        Log.w(MainActivity.TAG, "id: " + id);
                        if (id != null) {
                            permissionsReference.child(id).setValue(permissionGroupAmount);
                            Log.w(MainActivity.TAG, "permissionGroupAmount: " + permissionGroupAmount);
                        }


                    } else {
                        Log.w(MainActivity.TAG, "not first run");
                    }

                    long firstInstallTimeLong = p.firstInstallTime;
                    String firstInstallTime = currentMilliSecondsToDate(firstInstallTimeLong);
                    // String firstInstallTime = String.valueOf(p.firstInstallTime);
                    appList.setFirstInstallTime(firstInstallTime);

                    /* The Last Run Time can be extracted only from UsageStats
                     * Through the following loop we try to find the package name extracted from
                     * packs and match it with UsageStats list
                     * To compare package names from different lists we need to adjust the variables
                     * in terms of their types.
                     * @params packageNameForUsageStats - this String value shows the package name
                     * of UsageStats from the first to the end
                     *
                     */
                    for (int k = 0; k < mListUsageStats.size(); k++) {

                        String packageNameForUsageStats = mListUsageStats.get(k).getPackageName();
                        if (packageNameForUsageStats.equals(packageName)) {
                            lastTimeExecuted = mListUsageStats.get(k).getLastTimeUsed();
                            String lastRunTime = currentMilliSecondsToDate(lastTimeExecuted);
                            appList.setLastRunTime(lastRunTime);
                            appList.setLastRunTimeInMilliseconds(lastTimeExecuted);
                            break;
                        } else {
                            lastTimeExecuted = 0;
                            appList.setLastRunTime(getString(R.string.no_available_data));
                        }
                    }

                    appList.setOfflineTrust(getOfflineTrustLevel(lastUpdatedTimeLong, lastTimeExecuted,
                            appSource, permissionGroupAmount));
                    appList.setIcon(p.applicationInfo.loadIcon(getPackageManager()));
                    res.add(appList);
                }
            }
            return res;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<AppList> appLists) {
            super.onPostExecute(appLists);
            Log.i(TAG, "installedApps: " + appLists);
            installedApps.clear();
            installedApps.addAll(appLists);
            // installedApps = appListотs;
            //  recyclerView.setAdapter(appRecyclerViewAdapter);
         if (appRecyclerViewAdapter != null)  {
            appRecyclerViewAdapter.notifyDataSetChanged();}
            progressDialog.setProgress(100);
            progressDialog.dismiss();

            applicationsWidgetListUnsorted = installedApps;
            Collections.sort(applicationsWidgetListUnsorted, new Comparator<AppList>() {
                @Override
                public int compare(AppList appList1, AppList appList2) {
                    return (int) (appList1.getOfflineTrust() - appList2.getOfflineTrust());
                }
            });

        }

    }
}
