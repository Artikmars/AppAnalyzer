package com.artamonov.appanalyzer;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener,
        AppRecyclerViewAdapter.ItemClickListener {


    public static final String TAG = "myLogs";
    public static String url = "https://www.google.com/search?q=";
    public static String urlGoogle = " vulnerabilities android";

    public static List<AppList> installedApps = new ArrayList<>();
    public static String installedAppsString;
    public List<UsageStats> mListUsageStats;
    public long lastTimeExecuted;
    String lastRunTime;
    String appNameString, versionString;
    private ArrayList<String> requestedPermissionsProtectionLevels;
    private ArrayList<String> grantedPermissionsProtectionLevels;

    public byte[] drawableToByte(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.app_list);
        installedApps = getInstalledApps();
        AppRecyclerViewAdapter appRecyclerViewAdapter = new AppRecyclerViewAdapter(MainActivity.this, installedApps, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(appRecyclerViewAdapter);
        appRecyclerViewAdapter.notifyDataSetChanged();
        setupSharedPreferences();
        checkFirstRun();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void checkFirstRun() {

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
            return;

        } else if (savedVersionCode == DOES_NOT_EXIST && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);

            //upgrade case
        } else if (currentVersionCode > savedVersionCode) {
            return;
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();

    }

    private void setupSharedPreferences() {

        SharedPreferences sharedPreferences = android.support.v7.preference.PreferenceManager
                .getDefaultSharedPreferences(this);
        loadSourceFromPreferences(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private String installedAppsListToString(List<AppList> installedApps, int k) {

        appNameString = installedApps.get(k).getName();
        versionString = installedApps.get(k).getVersion();
        //lastRunTimeString = installedApps.get(k).getLastRunTime();
        //lastUpdateTimeString = installedApps.get(k).getLastUpdateTime();

        installedAppsString = appNameString + "," + versionString;
        Log.i(TAG, "installedAppsString " + installedAppsString);

        return installedAppsString;
    }

    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private List<AppList> getInstalledApps() {

        /** @param time = current time
         *  @param delta = initial period
         *  @param interval = time - delta (it is the time interval which is used by UsageStats
         *                  for extracting the data)
         *
         */
        Log.i(TAG, "in getInstalledApps");
        List<AppList> res = new ArrayList<>();
        // public static List<AppList> serverList = new ArrayList<>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        // List<ApplicationInfo> packs = getPackageManager().getInstalledApplications(0);
        Log.i(TAG, ".getInstalledPackages done. Size packs.size(): " + packs.size());

        Calendar c = Calendar.getInstance();
        //c.add(Calendar.HOUR, -1);
        c.add(Calendar.YEAR, -1);
        long begin = c.getTimeInMillis();
        long end = System.currentTimeMillis();
        // long day = 1000 * 60 * 60 * 24;
        //  long delta = day * 365;
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        mListUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                begin, end);
        Log.i(TAG, ".getInstalledPackages done. Size  mListUsageStats.size(): " + mListUsageStats.size());
        // Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        // startActivity(intent);

        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((!isSystemPackage(p))) {

                AppList appList = new AppList();
                appList.setPackageName(p.packageName);
                appList.setName(p.applicationInfo.loadLabel(getPackageManager()).toString());
                appList.setVersion(p.versionName);
                long lastUpdatedTimeLong = p.lastUpdateTime;
                String lastUpdateTime = currentMilliSecondsToDate(lastUpdatedTimeLong);
                appList.setLastUpdateTime(lastUpdateTime);
                String packageName = p.packageName;
                Log.i(TAG, "packageName: " + packageName);
                String appSourceType = getPackageManager().getInstallerPackageName(packageName);
                String appSource = getAppSource(appSourceType, p);

              /*  if (appSource.equals("Google Play")){
                    String url = "https://play.google.com/store/apps/details?id=" + packageName + "&hl=en";
                    Log.i(TAG, "packageName: " + url);
                    GooglePlayParser.getGPUpdateTime(url, packageName);}
             */

                appList.setAppSource(appSource);
                String[] appRequestedPermissionsArray = getRequestedPermissions(packageName);
//                ArrayList<String> appRequestedPermissionsListArray = new ArrayList<>(Arrays.asList(appRequestedPermissionsArray));

                if (appRequestedPermissionsArray != null) {
                   /* StringBuilder buffer = new StringBuilder();
                    for (String each : appRequestedPermissionsArray)
                        buffer.append(", ").append(each);
                    String appRequestedPermissions = buffer.deleteCharAt(0).toString();*/
                    //appList.setAppRequestedPermissions(appRequestedPermissionsListArray);
                } else appList.setAppRequestedPermissions(null);

                ArrayList<String> appGrantedPermissionsArray = new ArrayList<>(getGrantedPermissions(packageName));
               /* String grantedPermissions = null;
                for (int k = 0; i < appGrantedPermissionsArray.size(); i++) {
                    grantedPermissions = grantedPermissions + appGrantedPermissionsArray.get(k) + ", ";
                }*/
                //requestedPermissionsProtectionLevels = getPermissionsBaseTypes(appRequestedPermissionsListArray);
               // grantedPermissionsProtectionLevels = getPermissionsBaseTypes(appGrantedPermissionsArray);
               // appList.setRequestedPermissionsProtectionLevels(requestedPermissionsProtectionLevels);
               // appList.setGrantedPermissionsProtectionLevels(grantedPermissionsProtectionLevels);
            //    appList.setAppGrantedPermissions(appGrantedPermissionsArray);


                long firstInstallTimeLong = p.firstInstallTime;
                String firstInstallTime = currentMilliSecondsToDate(firstInstallTimeLong);
                Log.i(TAG, "firstInstallTimeLong: " + firstInstallTimeLong);
                // String firstInstallTime = String.valueOf(p.firstInstallTime);
                Log.i(TAG, "firstInstallTime" + firstInstallTime);
                appList.setFirstInstallTime(firstInstallTime);

                   /* switch (appSourceType) {
                        case "com.android.vending":
                            appList.setAppSource("Google Play");
                        case "com.amazon.venezia":
                            appList.setAppSource("Amazon Store");*/
                //case "com.sec.android.app.samsungapps":
                //  appList.setAppSource("Samsung Store");
                //    }
                //  }

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
                        break;
                    } else {
                        lastTimeExecuted = 0;
                        appList.setLastRunTime("NO available data");
                    }
                }
                appList.setIcon(p.applicationInfo.loadIcon(getPackageManager()));

                appList.setTrustLevel(getSimplifiedTrustLevel(appSource, lastRunTime, lastUpdateTime));
                res.add(appList);
            }
        }
        return res;
    }


    List<String> getGrantedPermissions(final String appPackage) {
        List<String> grantedPermissions = new ArrayList<>();
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(appPackage, PackageManager.GET_PERMISSIONS);

            for (int i = 0; i < pi.requestedPermissions.length; i++) {
                if ((pi.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                    grantedPermissions.add(pi.requestedPermissions[i]);
                }
            }
        } catch (Exception e) {
        }
        return grantedPermissions;
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


    private ArrayList<String> getPermissionsBaseTypes( ArrayList<String> permissions) {

        ArrayList <String> result = new ArrayList<>();
        for (int i = 0; i < permissions.size(); i++) {
            String protectionLevel;
            Log.i(TAG, "getPermissionsBaseTypes: permission - " + permissions.get(i));
            try {
                PermissionInfo permissionInfo = getPackageManager().getPermissionInfo(permissions.get(i), PackageManager.GET_META_DATA);
                switch (permissionInfo.protectionLevel) {
                    case PermissionInfo.PROTECTION_NORMAL:
                        protectionLevel = "normal";
                        result.add(protectionLevel);
                        break;
                    case PermissionInfo.PROTECTION_DANGEROUS:
                        protectionLevel = "dangerous";
                        result.add(protectionLevel);
                        break;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < result.size(); i++){
            Log.i(TAG, "getPermissionsBaseTypes: permissionsProtectionLevels - " + result.get(i));
        }

        return result;
    }

    private String getAppSource(String appSourceType, PackageInfo p) {
        boolean isSystemApp = isSystemPackage(p);
        if (isSystemApp) {
            return "System Application";
        } else {
            Log.i(TAG, "appSourceType: " + appSourceType);
            if (TextUtils.isEmpty(appSourceType)) {
                return "Unknown Source";
            } else if (appSourceType.equals("com.android.vending")) {
                return "Google Play";
            } else if (appSourceType.equals("com.amazon.venezia")) {
                return "Amazon Store";
            } else return "Undefined Market";
        }
        //
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private String getSimplifiedTrustLevel(String appSource, String lastRunTime, String lastUpdatedTime) {

        if (appSource.equals("Google Play")) {
            return "high";
        } else return "middle";
    }


    private String currentMilliSecondsToDate(long time) {

        return new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss", Locale.GERMAN)
                .format(new Date(time));

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    private void loadSourceFromPreferences(SharedPreferences sharedPreferences) {

    }

    @Override
    public void onItemClick(int position) {
        AppList appList = installedApps.get(position);
        String appName = appList.getName();
        String packageName = appList.getPackageName();
        String appVersion = appList.getVersion();
        Drawable appLogo = appList.getIcon();
        String firstInstallTime = appList.getFirstInstallTime();
        String lastRunTime = appList.getLastRunTime();
        String lastUpdatedTime = appList.getLastUpdateTime();
        String appSource = appList.getAppSource();
        String trustLevel = appList.getTrustLevel();
        ArrayList<String> appRequestedPermissions = appList.getAppRequestedPermissions();
        ArrayList<String> appGrantedPermissions = appList.getAppGrantedPermissions();
        ArrayList<String> requestedPermissionsProtectionLevel = appList.getRequestedPermissionsProtectionLevels();
        ArrayList<String> grantedPermissionsProtectionLevel = appList.getGrantedPermissionsProtectionLevels();

        Intent intent = new Intent(this, AppDetailActivity.class);
        intent.putExtra("name", appName);
        intent.putExtra("package name", packageName);
        intent.putExtra("version", appVersion);
        intent.putExtra("logo", drawableToByte(appLogo));
        intent.putExtra("install time", firstInstallTime);
        intent.putExtra("run time", lastRunTime);
        intent.putExtra("updated time", lastUpdatedTime);
        intent.putExtra("app source", appSource);
        intent.putExtra("trust level", trustLevel);

        //  Bundle bundle = new Bundle();
        //  bundle.putSerializable("requested permissions", (Serializable) appRequestedPermissions);
        // bundle.putSerializable("granted permissions", (Serializable) appGrantedPermissions);
        // intent.putExtras(bundle);
        intent.putStringArrayListExtra("requested permissions", appRequestedPermissions);
        intent.putStringArrayListExtra("granted permissions", appGrantedPermissions);
        intent.putStringArrayListExtra("requested permissions protection levels", requestedPermissionsProtectionLevel);
        intent.putStringArrayListExtra("granted permissions protection levels", grantedPermissionsProtectionLevel);
        startActivity(intent);
        //String popularityScore =
    }
}
