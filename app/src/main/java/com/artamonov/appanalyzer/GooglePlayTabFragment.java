package com.artamonov.appanalyzer;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.artamonov.appanalyzer.contract.AppDetailContract;
import com.artamonov.appanalyzer.network.GPDetailPageParser;
import com.artamonov.appanalyzer.presenter.AppDetailPresenter;

import org.jsoup.nodes.Element;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.artamonov.appanalyzer.AppDetailActivity.appGPApp;

public class GooglePlayTabFragment extends Fragment implements AppDetailContract.AppDetailView {

    public static Element content;
    static AppDetailViewModel appDetailFragmentViewModel;
    @BindView(R.id.gp_app_rating_label)
    TextView tvRatingLabel;
    @BindView(R.id.gp_downloads_label)
    TextView tvInstallsLabel;
    @BindView(R.id.gp_reviewers_label)
    TextView tvPeopleLabel;
    @BindView(R.id.gp_update_time_label)
    TextView tvUpdatedLabel;

    private TextView tvCategory;
    private TextView tvRating;
    private TextView tvInstalls;
    private TextView tvPeople;
    private TextView tvUpdated;
    private TextView tvOnlineTrust;

    public static GooglePlayTabFragment newInstance() {
        GooglePlayTabFragment fragment = new GooglePlayTabFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        Log.w(MainActivity.TAG, "newInstance");
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        appDetailFragmentViewModel = ViewModelProviders.of(this).get(AppDetailViewModel.class);
        AppDetailPresenter appDetailPresenter = new AppDetailPresenter(this);

      /*  if (isNetworkAvailable(getActivity()) && appList.getAppSource().equals("Google Play")) {
            ParseTask parseTask = new ParseTask();
            parseTask.execute();
        }*/



/*
        if (!isNetworkAvailable(getActivity()) && appGPApp != null && appList.getAppSource().equals("Google Play")) {
            populateViewsFromDB();
        }*/
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

          /*  if (!isNetworkAvailable(getActivity()) && appList.getAppSource().equals("Google Play")) {
                populateViewsFromDB();
                Log.w(MainActivity.TAG, "setUserVisibleHint: populateViewsFromDB");
                return;
            }*/
                populateViews();
            }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.w(MainActivity.TAG, "onCreateView");
        View view = inflater.inflate(R.layout.google_play_tab, container, false);
        ButterKnife.bind(view);
        tvCategory = view.findViewById(R.id.gp_category);
        tvRating = view.findViewById(R.id.gp_app_rating);
        tvInstalls = view.findViewById(R.id.gp_downloads);
        tvPeople = view.findViewById(R.id.gp_reviewers);
        tvUpdated = view.findViewById(R.id.gp_update_time);
        tvOnlineTrust = view.findViewById(R.id.online_trust);

        return view;
    }

    public void populateViews() {

        // AppList parsedAppList = appDetailPresenter.getGPData();
        //  Log.w(MainActivity.TAG, " parsedAppList: " + GPDetailPageParser.parsedAppList.getGpInstalls());
        if (GPDetailPageParser.parsedAppList != null) {

            Log.w(MainActivity.TAG, " in populateViews: " + GPDetailPageParser.parsedAppList.getGpRating());
            tvCategory.setText(GPDetailPageParser.parsedAppList.getGpCategory());
            tvRating.setText(GPDetailPageParser.parsedAppList.getGpRating());
            tvInstalls.setText(GPDetailPageParser.parsedAppList.getGpInstalls());
            tvPeople.setText(GPDetailPageParser.parsedAppList.getGpPeople());
            tvUpdated.setText(GPDetailPageParser.parsedAppList.getGpUpdated());
            tvOnlineTrust.setText(GPDetailPageParser.parsedAppList.getOnlineTrust());
        } else {
            Log.w(MainActivity.TAG, " in populateViews: list is empty ");
        }
    }

    public void populateViewsFromDB() {

        if (appGPApp != null) {
            tvCategory.setText(appGPApp.getGpCategory());
            tvRating.setText(appGPApp.getGpRating());
            tvInstalls.setText(appGPApp.getGpInstalls());
            tvPeople.setText(appGPApp.getGpPeople());
            tvUpdated.setText(appGPApp.getGpUpdated());
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

    }

    @Override
    public void setSearchAppsAdapter(ArrayList<String> arrayAppNames, ArrayList<String> arrayLinks) {

    }

    @Override
    public void populateOnlineTrust() {
        populateViews();
        Log.i(MainActivity.TAG, "in populateOnlineTrust: " + GPDetailPageParser.parsedAppList.getOnlineTrust());
        tvOnlineTrust.setText(GPDetailPageParser.parsedAppList.getOnlineTrust());
    }
}
