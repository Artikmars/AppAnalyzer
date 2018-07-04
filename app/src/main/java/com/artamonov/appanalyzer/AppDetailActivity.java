package com.artamonov.appanalyzer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AppDetailActivity extends AppCompatActivity {

    @BindView(R.id.detailed_app_name)
    TextView tvAppName;
    @BindView(R.id.detailed_app_version)
    TextView tvAppVersion;
    @BindView(R.id.trust_level)
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_app_analyzer);
        ButterKnife.bind(this);


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
        tvTrustLevel.setText(MainActivity.appList.getTrustLevel());

        tabLayout.setupWithViewPager(viewPager);
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.setPageTitles(getResources().getString(R.string.first_tab));
        adapter.setPageTitles(getResources().getString(R.string.second_tab));
        adapter.setPageTitles(getResources().getString(R.string.third_tab));
        viewPager.setAdapter(adapter);
    }


}
