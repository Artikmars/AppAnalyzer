package com.artamonov.appanalyzer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.artamonov.appanalyzer.adapter.SearchAppsRecyclerViewAdapter;
import com.artamonov.appanalyzer.contract.AppDetailContract;
import com.artamonov.appanalyzer.presenter.AppDetailPresenter;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements AppDetailContract.AppDetailView {

    public static final String TAG = "myLogs";
    Activity activity = new Activity();
    private RecyclerView rvSearchApps;
    //  private Results artistItem;
    // private List<Artist> artistItemList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private AppDetailPresenter appDetailPresenter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_menu);

        SearchManager searchManager = (SearchManager) SearchActivity.this.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(SearchActivity.this.getComponentName()));
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String appName) {
                //   artistsPresenter.hideKeyboard(activity);
                System.out.println("onQueryTextSubmit");
                Log.w(MainActivity.TAG, "text to Submit: " + appName);
                appDetailPresenter.parseGPSearch(appName);
                //  progressDialog.show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appDetailPresenter = new AppDetailPresenter(this);
        //  progressDialog = new ProgressDialog(this);

        rvSearchApps = findViewById(R.id.gp_applications);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvSearchApps.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvSearchApps.addItemDecoration(itemDecoration);
    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void dismissProgressDialog() {

    }

    @Override
    public void populateOverallTrust() {

    }

    @Override
    public void setSearchAppsAdapter(ArrayList<String> arrayAppNames, ArrayList<String> arrayLinks) {
        SearchAppsRecyclerViewAdapter searchAppsRecyclerViewAdapter = new SearchAppsRecyclerViewAdapter
                (arrayAppNames, arrayLinks, SearchActivity.this);
        rvSearchApps.setAdapter(searchAppsRecyclerViewAdapter);
        rvSearchApps.setHasFixedSize(true);
    }

    @Override
    public void populateOnlineTrust() {

    }


   /* @Override
    public void showProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    @Override
    public void showFailureMessage(Throwable t) {
        System.out.println("onFailure");
        System.out.println(t.getMessage());
        System.out.println(t.getStackTrace());
    }

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }*/
}
