package com.artamonov.appanalyzer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.artamonov.appanalyzer.contract.AppDetailContract;
import com.artamonov.appanalyzer.network.GPDetailPageParser;
import com.artamonov.appanalyzer.presenter.AppDetailPresenter;
import com.artamonov.appanalyzer.utils.NetworkUtils;

import java.util.ArrayList;

public class SearchDetailActivity extends AppCompatActivity implements AppDetailContract.AppDetailView {

    public static final String TAG = "myLogs";
    Activity activity = new Activity();
    private RecyclerView rvSearchApps;
    //  private Results artistItem;
    // private List<Artist> artistItemList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private AppDetailPresenter appDetailPresenter;
    private TextView tvOverallTrust, tvRating, tvAppName, tvVersion, tvPeopleVoted, tvDownloads,
            tvUpdated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(MainActivity.TAG, "IN SEARCH DETAIL: OnCreate: ");
        setContentView(R.layout.search_detail);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appDetailPresenter = new AppDetailPresenter(this);
        //  progressDialog = new ProgressDialog(this);

        tvAppName = findViewById(R.id.search_name);
        tvOverallTrust = findViewById(R.id.trust_level);
        tvRating = findViewById(R.id.search_app_rating);
        tvVersion = findViewById(R.id.search_app_version);
        tvPeopleVoted = findViewById(R.id.search_reviewers);
        tvDownloads = findViewById(R.id.search_downloads);
        tvUpdated = findViewById(R.id.search_update_time);
        // Get data from SearchAppsAdapter
        Intent intent = getIntent();
        String link = intent.getStringExtra("appLink");
        Log.w(MainActivity.TAG, "IN SEARCH DETAIL: link: " + link);
        if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
            appDetailPresenter.parseGPData(link, getApplicationContext());
            Log.w(MainActivity.TAG, "IN SEARCH DETAIL: parse:");
        }

    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void dismissProgressDialog() {

    }

    @Override
    public void populateOverallTrust() {
        // tvAppName.setText(R.id.search_app_name);
        Log.w(MainActivity.TAG, "IN SEARCH DETAIL: populateOverallTrust: " + GPDetailPageParser.parsedAppList.getOnlineTrust());
        tvOverallTrust.setText(GPDetailPageParser.parsedAppList.getOnlineTrust());
        tvRating.setText(GPDetailPageParser.parsedAppList.getGpRating());
        //   tvVersion.setText(R.id.search_app_version);
        tvPeopleVoted.setText(GPDetailPageParser.parsedAppList.getGpPeople());
        tvDownloads.setText(GPDetailPageParser.parsedAppList.getGpInstalls());
        tvUpdated.setText(GPDetailPageParser.parsedAppList.getGpUpdated());
    }

    @Override
    public void setSearchAppsAdapter(ArrayList<String> arrayAppNames, ArrayList<String> arrayLinks) {

    }

    @Override
    public void populateOnlineTrust() {

    }
}

