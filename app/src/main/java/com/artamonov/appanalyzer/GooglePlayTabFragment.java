package com.artamonov.appanalyzer;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.artamonov.appanalyzer.data.database.AppList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.artamonov.appanalyzer.AppDetailActivity.appGPApp;
import static com.artamonov.appanalyzer.MainActivity.appList;

public class GooglePlayTabFragment extends Fragment {

    public static Document document;
    public static Element content;
    public static Element content2;
    static ArrayList<AppList> gpList = new ArrayList<>();
    static AppDetailViewModel appDetailFragmentViewModel;
    @BindView(R.id.gp_app_rating_label)
    TextView tvRatingLabel;
    @BindView(R.id.gp_downloads_label)
    TextView tvInstallsLabel;
    @BindView(R.id.gp_reviewers_label)
    TextView tvPeopleLabel;
    @BindView(R.id.gp_update_time_label)
    TextView tvUpdatedLabel;
    private TextView tvRating;
    private TextView tvInstalls;
    private TextView tvPeople;
    private TextView tvUpdated;
    private TextView tvScore;
    private AppList parsedAppList;

    public static GooglePlayTabFragment newInstance() {
        GooglePlayTabFragment fragment = new GooglePlayTabFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        Log.w(MainActivity.TAG, "newInstance");
        return fragment;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
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

        if (isNetworkAvailable(getActivity()) && appList.getAppSource().equals("Google Play")) {
            ParseTask parseTask = new ParseTask();
            parseTask.execute();
        }

        if (!isNetworkAvailable(getActivity()) && appGPApp != null && appList.getAppSource().equals("Google Play")) {
            populateViewsFromDB();
        }


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            if (!isNetworkAvailable(getActivity()) && appList.getAppSource().equals("Google Play")) {
                populateViewsFromDB();
                return;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.w(MainActivity.TAG, "onCreateView");
        View view = inflater.inflate(R.layout.google_play_tab, container, false);
        ButterKnife.bind(view);
        tvRating = view.findViewById(R.id.gp_app_rating);
        tvInstalls = view.findViewById(R.id.gp_downloads);
        tvPeople = view.findViewById(R.id.gp_reviewers);
        tvUpdated = view.findViewById(R.id.gp_update_time);
        tvScore = view.findViewById(R.id.gpScore);

        return view;
    }

    public void populateViews() {
        if (parsedAppList != null) {

            Log.w(MainActivity.TAG, " in populateViews: " + parsedAppList.getGpRating());
            tvRating.setText(parsedAppList.getGpRating());
            tvInstalls.setText(parsedAppList.getGpInstalls());
            tvPeople.setText(parsedAppList.getGpPeople());
            tvUpdated.setText(parsedAppList.getGpUpdated());

            double overallTrust = AppDetailActivity.getOverallTrustLevel(MainActivity.appList.getLastUpdateTimeInMilliseconds(),
                    MainActivity.appList.getLastRunTimeInMilliseconds(), MainActivity.appList.getAppSource(),
                    parsedAppList.getGpInstalls(), parsedAppList.getGpPeople(), parsedAppList.getGpRating(),
                    parsedAppList.getGpUpdated(), MainActivity.appList.getDangerousPermissionsAmount());
            String overTrust = String.valueOf(overallTrust);
            tvScore.setText(overTrust);


        } else {
            Log.w(MainActivity.TAG, " in populateViews: list is empty ");
        }
    }

    public void populateViewsFromDB() {

        if (appGPApp != null) {

            tvRating.setText(appGPApp.getGpRating());
            tvInstalls.setText(appGPApp.getGpInstalls());
            tvPeople.setText(appGPApp.getGpPeople());
            tvUpdated.setText(appGPApp.getGpUpdated());
        }

    }

    public class ParseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            populateViews();
            appDetailFragmentViewModel.insert(parsedAppList);

        }

        @Override
        protected Void doInBackground(Void... strings) {
            gpList = new ArrayList<>();
            String gpUrl = "https://play.google.com/store/apps/details?id=" + appList.getPackageName() + "&hl=en";
            try {

                document = Jsoup.connect(gpUrl).get();
                content = document.select("div:contains(Additional Information)").get(1);

                String gpParsedString = content.text();
                String[] ratingArray = gpParsedString.split("Policy ");
                String[] gpRatingArray = ratingArray[1].split(" ", 2);
                String gpRating = gpRatingArray[0];

                String ratingPeopleAmount = gpRatingArray[1];
                String[] gpRatingPeopleAmountArray = ratingPeopleAmount.split(" ", 2);
                String gpRatingPeopleAmount = gpRatingPeopleAmountArray[0];
                String[] updatedTime = gpParsedString.split("Updated ");
                String[] updatedTimeArray = updatedTime[1].split(" ", 5);
                String gpUpdatedTime = updatedTimeArray[0] + " " + updatedTimeArray[1] + " " + updatedTimeArray[2];

                String[] installsArray = gpParsedString.split("Installs ");
                String[] gpInstallsArray = installsArray[1].split(" ", 2);
                String gpInstalls = gpInstallsArray[0];

                if (content2 != null) {
                    Log.i(MainActivity.TAG, "GP content2: " + content2.text());
                    Log.i(MainActivity.TAG, "GP content3: " + content2.toString());
                }

                parsedAppList = new AppList();
                parsedAppList.setGpRating(gpRating);
                parsedAppList.setGpPeople(gpRatingPeopleAmount);
                parsedAppList.setGpInstalls(gpInstalls);
                parsedAppList.setGpUpdated(gpUpdatedTime);
                parsedAppList.setPackageName(MainActivity.appList.getPackageName());

            } catch (IOException e1) {
                e1.printStackTrace();
                Log.i(MainActivity.TAG, "IO Exception");
            }

            return null;
        }


    }
}
