package com.artamonov.appanalyzer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppDetailActivity extends AppCompatActivity {

    public static Document document;
    public static Element content;
    public static Element content2;
    private static String packageName;

    @BindView(R.id.gp_label)
    TextView tvGPLabel;
    @BindView(R.id.gp_app_rating_label)
    TextView tvRatingLabel;
    @BindView(R.id.gp_app_rating)
    TextView tvRating;
    @BindView(R.id.gp_downloads_label)
    TextView tvInstallsLabel;
    @BindView(R.id.gp_downloads)
    TextView tvInstalls;
    @BindView(R.id.gp_reviewers_label)
    TextView tvPeopleLabel;
    @BindView(R.id.gp_reviewers)
    TextView tvPeople;
    @BindView(R.id.gp_update_time_label)
    TextView tvUpdatedLabel;
    @BindView(R.id.gp_update_time)
    TextView tvUpdated;
    @BindView(R.id.detailed_app_name)
    TextView tvAppName;
    @BindView(R.id.detailed_app_version)
    TextView tvAppVersion;
    @BindView(R.id.first_install_time)
    TextView tvFirstInstallTime;
    @BindView(R.id.detailed_app_last_run_time)
    TextView tvLastRunTime;
    @BindView(R.id.detailed_app_last_update_time)
    TextView tvLastUpdatedTime;
    @BindView(R.id.detailed_app_source)
    TextView tvAppSource;
    @BindView(R.id.trust_level)
    TextView tvTrustLevel;

    ArrayList<AppList> gpList = new ArrayList<>();
   /* private ArrayList<String> appRequestedPermissions;
    private ArrayList<String> appGrantedPermissions;
    private ArrayList<String> requestedPermissionsProtectionLevel;
    private ArrayList<String> grantedPermissionsProtectionLevel;
    private TextView tvAppGrantedPermissions;
    private TextView tvAppRequestedPermissions;
    private TextView tvAppRequestedPermissionsLabel;
    private TextView tvAppGrantedPermissionsLabel;*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_app_analyzer);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String appName = intent.getStringExtra("name");
        packageName = intent.getStringExtra("package name");
        String appVersion = intent.getStringExtra("version");
        String appFirstInstallTime = intent.getStringExtra("install time");
        String appLastRunTime = intent.getStringExtra("run time");
        String appLastUpdatedTime = intent.getStringExtra("updated time");
        String appSource = intent.getStringExtra("app source");

        if (appSource.equals("Google Play")) {
            Log.i(MainActivity.TAG, "is google play: ");
            // GooglePlayParser.getGPData(packageName);
            ParseTask parseTask = new ParseTask();
            parseTask.execute();
        }
        String trustLevel = intent.getStringExtra("trust level");

        // Bundle bundle = getIntent().getExtras();
        /*appRequestedPermissions = intent.getStringArrayListExtra("requested permissions");
        appGrantedPermissions = intent.getStringArrayListExtra("granted permissions");
        requestedPermissionsProtectionLevel = intent.getStringArrayListExtra("requested permissions protection levels");
        grantedPermissionsProtectionLevel = intent.getStringArrayListExtra("granted permissions protection levels");*/


        // appRequestedPermissions = intent.getStringExtra("requested permissions");
        // appGrantedPermissions = intent.getStringExtra("granted permissions");
        Log.i(MainActivity.TAG, "AppDetailActivity: appSource: " + appSource);
        Log.i(MainActivity.TAG, "AppDetailActivity: appFirstInstallTime: " + appFirstInstallTime);
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
        tvAppName.setText(appName);
        tvAppVersion.setText(appVersion);
        tvFirstInstallTime.setText(appFirstInstallTime);
        tvLastRunTime.setText(appLastRunTime);
        tvLastUpdatedTime.setText(appLastUpdatedTime);
        ivLogo.setImageBitmap(appLogo);
        tvAppSource.setText(appSource);
        tvTrustLevel.setText(trustLevel);


    }


    public class ParseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            Log.i(MainActivity.TAG, "onPostExecute " + result);
            Log.i(MainActivity.TAG, "gpPopulateData: " + gpList.get(0).getGpRating() + " " + gpList.get(0).getGpInstalls() +
                    " " + gpList.get(0).getGpPeople() + " " + gpList.get(0).getGpUpdated());

            tvRating.setText(gpList.get(0).getGpRating());
            tvInstalls.setText(gpList.get(0).getGpInstalls());
            tvPeople.setText(gpList.get(0).getGpPeople());
            tvUpdated.setText(gpList.get(0).getGpUpdated());
            // AppDetailActivity.gpPopulateData(result);
        }

        @Override
        protected Void doInBackground(Void... strings) {
            Log.i(MainActivity.TAG, "doInBackground ");
            gpList = new ArrayList<>();
            String gpUrl = "https://play.google.com/store/apps/details?id=" + packageName + "&hl=en";
            try {
                Log.i(MainActivity.TAG, "gpUrl: " + gpUrl);
                document = Jsoup.connect(gpUrl).get();
                Log.i(MainActivity.TAG, "document: " + document);
                content = document.select("div:contains(Additional Information)").get(1);

                Log.i(MainActivity.TAG, "content: " + content);

                String gpParsedString = content.text();
                String[] ratingArray = gpParsedString.split("Policy ");
                Log.i(MainActivity.TAG, "GP content: " + ratingArray[1]);
                String[] gpRatingArray = ratingArray[1].split(" ", 2);
                String gpRating = gpRatingArray[0];

                String ratingPeopleAmount = gpRatingArray[1];
                String[] gpRatingPeopleAmountArray = ratingPeopleAmount.split(" ", 2);
                String gpRatingPeopleAmount = gpRatingPeopleAmountArray[0];
                Log.i(MainActivity.TAG, "GP content: " + content.text());
                Log.i(MainActivity.TAG, "GP content: gpRating " + gpRating);
                Log.i(MainActivity.TAG, "GP content: gpRating2 " + gpRatingPeopleAmount);
                String[] updatedTime = gpParsedString.split("Updated ");
                String[] updatedTimeArray = updatedTime[1].split(" ", 5);
                Log.i(MainActivity.TAG, "GP content: gpRating2 " + updatedTime[1]);
                String gpUpdatedTime = updatedTimeArray[0] + " " + updatedTimeArray[1] + " " + updatedTimeArray[2];
                Log.i(MainActivity.TAG, "GP content: gpRating2 " + gpUpdatedTime);

                String[] installsArray = gpParsedString.split("Installs ");
                Log.i(MainActivity.TAG, "GP content: " + installsArray[1]);
                String[] gpInstallsArray = installsArray[1].split(" ", 2);
                String gpInstalls = gpInstallsArray[0];
                Log.i(MainActivity.TAG, "GP content: gpInstalls " + gpInstalls);

                if (content2 != null) {
                    Log.i(MainActivity.TAG, "GP content2: " + content2.text());
                    Log.i(MainActivity.TAG, "GP content3: " + content2.toString());
                }

                AppList appList = new AppList();
                appList.setGpRating(gpRating);
                appList.setGpPeople(gpRatingPeopleAmount);
                appList.setGpInstalls(gpInstalls);
                appList.setGpUpdated(gpUpdatedTime);

                Log.i(MainActivity.TAG, "parser:  " + gpRating + " " + gpRatingPeopleAmount + " " + gpInstalls + " " + gpUpdatedTime);
                gpList.add(appList);

                /*  Elements elements = document.body().select("*");
                Log.i(MainActivity.TAG, "GP Loop ");
                for (Element element : elements) {
                    Log.i(MainActivity.TAG, element.ownText());
                }*/

            } catch (IOException e1) {
                e1.printStackTrace();
                Log.i(MainActivity.TAG, "IO Exception");
            }

             /*   content3 = content2.nextElementSibling();
                if (content3 != null) {
                    Log.i(MainActivity.TAG, "GP Updated Time: " + content3.text());
                    Log.i(MainActivity.TAG, "GP Updated Time: " + content3.toString());
                }*/

            return null;
        }


    }


}
