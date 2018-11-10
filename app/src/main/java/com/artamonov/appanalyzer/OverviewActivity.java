package com.artamonov.appanalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;


public class OverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tvAppsAmount = findViewById(R.id.applications_installed);
        TextView tvHighOfflineScoreApps = findViewById(R.id.apps_with_high_rating);
        TextView tvMiddleOfflineScoreApps = findViewById(R.id.apps_with_middle_rating);
        TextView tvLowOfflineScoreApps = findViewById(R.id.apps_with_low_rating);

        Intent intent = getIntent();
        String appsAmount = String.valueOf(intent.getIntExtra("applications_amount", 0));
        String highOfflineScoreApps = String.valueOf(intent.getIntExtra("high_offline_score_apps", 0));
        String middleOfflineScoreApps = String.valueOf(intent.getIntExtra("middle_offline_score_apps", 0));
        String lowOfflineScoreApps = String.valueOf(intent.getIntExtra("low_offline_score_apps", 0));

        tvAppsAmount.setText(appsAmount);
        tvHighOfflineScoreApps.setText(highOfflineScoreApps);
        tvMiddleOfflineScoreApps.setText(middleOfflineScoreApps);
        tvLowOfflineScoreApps.setText(lowOfflineScoreApps);


    }
}
