package com.artamonov.appanalyzer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class AppDetailActivity extends AppCompatActivity {

    private String appName;
    private String appVersion;
    private Bitmap appLogo;
    private String appSource;
    private String trustLevel;
    private String appRequestedPermissions;
    private String appGrantedPermissions;

   private String appFirstInstallTime;
    private String appLastRunTime;
    private String appLastUpdatedTime;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_app_analyzer);

        Intent intent = getIntent();
        appName = intent.getStringExtra("name");
        appVersion = intent.getStringExtra("version");
        appFirstInstallTime = intent.getStringExtra("install time");
        appLastRunTime = intent.getStringExtra("run time");
        appLastUpdatedTime = intent.getStringExtra("updated time");
        appSource = intent.getStringExtra("app source");
        trustLevel = intent.getStringExtra("trust level");
        appRequestedPermissions = intent.getStringExtra("requested permissions");
        appGrantedPermissions = intent.getStringExtra("granted permissions");
        Log.i(MainActivity.TAG, "AppDetailActivity: appSource: " + appSource);
        Log.i(MainActivity.TAG, "AppDetailActivity: appFirstInstallTime: " + appFirstInstallTime);
        Bundle extras = getIntent().getExtras();
        byte[] byteLogo = extras.getByteArray("logo");
        appLogo = BitmapFactory.decodeByteArray(byteLogo, 0, byteLogo.length);


        TextView tvAppName = findViewById(R.id.detailed_app_name);
        TextView tvAppVersion = findViewById(R.id.detailed_app_version);
        TextView tvFirstInstallTime = findViewById(R.id.first_install_time);
        TextView tvLastRunTime = findViewById(R.id.detailed_app_last_run_time);
        TextView tvLastUpdatedTime = findViewById(R.id.detailed_app_last_update_time);
        TextView tvAppSource = findViewById(R.id.detailed_app_source);
        TextView tvTrustLevel = findViewById(R.id.trust_level);
        TextView tvAppRequestedPermissions = findViewById(R.id.requested_app_permissions);
        TextView tvAppGrantedPermissions = findViewById(R.id.granted_app_permissions);

        ImageView ivLogo = findViewById(R.id.detailed_app_icon);

        tvAppName.setText(appName);
        tvAppVersion.setText(appVersion);
        tvFirstInstallTime.setText(appFirstInstallTime);
        tvLastRunTime.setText(appLastRunTime);
        tvLastUpdatedTime.setText(appLastUpdatedTime);
        ivLogo.setImageBitmap(appLogo);
        tvAppSource.setText(appSource);
        tvTrustLevel.setText(trustLevel);
        tvAppRequestedPermissions.setText(appRequestedPermissions);
        tvAppGrantedPermissions.setText(appGrantedPermissions);
    }
}
