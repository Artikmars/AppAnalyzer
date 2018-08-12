package com.artamonov.appanalyzer;

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
import java.text.ParseException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.artamonov.appanalyzer.MainActivity.appList;

public class GooglePlayTabFragment extends Fragment {

    public static Document document;
    public static Element content;
    public static Element content2;
    static ArrayList<AppList> gpList = new ArrayList<>();
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


    public static GooglePlayTabFragment newInstance() {
        GooglePlayTabFragment fragment = new GooglePlayTabFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        /*AppDetailActivity.appDetailViewModel.getAllApps().observe(this, new Observer<List<AppList>>() {
            @Override
            public void onChanged(@Nullable final List<AppList> words) {
                // Update the cached copy of the words in the adapter.
                tvRating.setText(words.get(0).getGpRating());
                tvInstalls.setText(words.get(0).getGpInstalls());
                tvPeople.setText(words.get(0).getGpPeople());
                tvUpdated.setText(words.get(0).getGpUpdated());
                try {
                    tvScore.setText(AppDetailActivity.getSimplifiedTrustLevel(MainActivity.appList.getAppSource(),
                            words.get(0).getGpInstalls(),
                            words.get(0).getGpPeople(),words.get(0).getGpRating()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });*/


        if (isVisibleToUser) {

            if (!isNetworkAvailable(getActivity()) && appList.getAppSource().equals("Google Play")) {
                populateViewsFromDB();
                return;
            }

            if (isNetworkAvailable(getActivity()) && appList.getAppSource().equals("Google Play")) {
                ParseTask parseTask = new ParseTask();
                parseTask.execute();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.google_play_tab, container, false);
        ButterKnife.bind(view);
        tvRating = view.findViewById(R.id.gp_app_rating);
        tvInstalls = view.findViewById(R.id.gp_downloads);
        tvPeople = view.findViewById(R.id.gp_reviewers);
        tvUpdated = view.findViewById(R.id.gp_update_time);
        tvScore = view.findViewById(R.id.gpScore);

        return view;
    }

    public void populateViews() throws ParseException {
        Log.w(MainActivity.TAG, " in populateViews ");
        tvRating.setText(gpList.get(0).getGpRating());
        tvInstalls.setText(gpList.get(0).getGpInstalls());
        tvPeople.setText(gpList.get(0).getGpPeople());
        tvUpdated.setText(gpList.get(0).getGpUpdated());
        tvScore.setText(AppDetailActivity.getSimplifiedTrustLevel(appList.getAppSource(),
                gpList.get(0).getGpInstalls(),
                gpList.get(0).getGpPeople(), gpList.get(0).getGpRating()));
       /*TextView trustLevel = getActivity().findViewById(R.id.trust_level);
        trustLevel.setText(AppDetailActivity.getSimplifiedTrustLevel(MainActivity.appList.getAppSource(),
                MainActivity.appList.getGpInstalls(),
                MainActivity.appList.getGpPeople(),MainActivity.appList.getGpRating()));*/
    }

    public void populateViewsFromDB() {

        if (AppDetailActivity.appGPApp != null) {
            Log.w(MainActivity.TAG, " in populateViewsFromDB: AppDetailActivity.appGPApp.getGpRating() " +
                    AppDetailActivity.appGPApp.getGpRating());
            tvRating.setText(AppDetailActivity.appGPApp.getGpRating());
            tvInstalls.setText(AppDetailActivity.appGPApp.getGpInstalls());
            tvPeople.setText(AppDetailActivity.appGPApp.getGpPeople());
            tvUpdated.setText(AppDetailActivity.appGPApp.getGpUpdated());
        } else {
            Log.w(MainActivity.TAG, " in populateViewsFromDB: AppDetailActivity.appGPApp is Empty ");
        }

//            tvScore.setText(AppDetailActivity.getSimplifiedTrustLevel(AppDetailActivity.appGPApp.getAppSource(),
        //          AppDetailActivity.appGPApp.getGpInstalls(),
        //         AppDetailActivity.appGPApp.getGpPeople(), AppDetailActivity.appGPApp.getGpRating()));



/*
        AppDetailViewModel viewModel = ViewModelProviders.of(this).get(AppDetailViewModel.class);
        boolean observer = viewModel.getGpData(MainActivity.appList.getPackageName()).hasActiveObservers();
        Log.i(MainActivity.TAG, "observer: " + observer);
       // LiveData<AppList> gp = AppDetailActivity.appDetailViewModel.getGpData(MainActivity.appList.getPackageName());
        viewModel.getGpData(MainActivity.appList.getPackageName()).observe(this, new Observer<AppList>() {
            @Override
            public void onChanged(@Nullable AppList appList) {
                Log.i(MainActivity.TAG, "populateViewsFromDB: getGpData: gp " + appList);
                Log.i(MainActivity.TAG, "populateViewsFromDB: getGpData: gp.rating(): " + appList.getGpRating());
                tvRating.setText(appList.getGpRating());
                tvInstalls.setText(appList.getGpInstalls());
                tvPeople.setText(appList.getGpPeople());
                tvUpdated.setText(appList.getGpUpdated());
                try {
                    tvScore.setText(AppDetailActivity.getSimplifiedTrustLevel(MainActivity.appList.getAppSource(),
                            appList.getGpInstalls(),
                            appList.getGpPeople(), appList.getGpRating()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
*/


     /*   LiveData<List<AppList>> logList = AppDetailActivity.appDetailViewModel.getAllApps();
        logList.observe(this, new Observer<List<AppList>>() {
            @Override
            public void onChanged(@Nullable List<AppList> logList){
                Log.i(MainActivity.TAG, "   LiveData<List<AppList>> from DB:  " + logList);
                Log.i(MainActivity.TAG, "   LiveData<List<AppList>> from DB: .size() "
                        + logList.size());
                Log.i(MainActivity.TAG, "   LiveData<List<AppList>> from DB:  " +
                        ".get(0).getPackageName()" + logList.get(0).getPackageName());
                for (int i = 0; i < logList.size(); i++){
                    Log.i(MainActivity.TAG, "   LiveData<List<AppList>> from DB:  " + logList.get(i).getGpRating());
                }
            }
        });*/

      /*  tvRating.setText(gp.getGpRating());
        tvInstalls.setText(gp.getGpInstalls());
        tvPeople.setText(gp.getGpPeople());
        tvUpdated.setText(gp.getGpUpdated());
        tvScore.setText(AppDetailActivity.getSimplifiedTrustLevel(MainActivity.appList.getAppSource(),
                gp.getGpInstalls(),
                gp.getGpPeople(), gp.getGpRating()));*/


       /*TextView trustLevel = getActivity().findViewById(R.id.trust_level);
        trustLevel.setText(AppDetailActivity.getSimplifiedTrustLevel(MainActivity.appList.getAppSource(),
                MainActivity.appList.getGpInstalls(),
                MainActivity.appList.getGpPeople(),MainActivity.appList.getGpRating()));*/
    }

    public class ParseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            try {
                populateViews();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... strings) {
            Log.i(MainActivity.TAG, "doInBackground ");
            gpList = new ArrayList<>();
            String gpUrl = "https://play.google.com/store/apps/details?id=" + appList.getPackageName() + "&hl=en";
            try {
                // Log.i(MainActivity.TAG, "gpUrl: " + gpUrl);
                document = Jsoup.connect(gpUrl).get();
                //  Log.i(MainActivity.TAG, "document: " + document);
                content = document.select("div:contains(Additional Information)").get(1);

                // Log.i(MainActivity.TAG, "content: " + content);

                String gpParsedString = content.text();
                String[] ratingArray = gpParsedString.split("Policy ");
                // Log.i(MainActivity.TAG, "GP content: " + ratingArray[1]);
                String[] gpRatingArray = ratingArray[1].split(" ", 2);
                String gpRating = gpRatingArray[0];

                String ratingPeopleAmount = gpRatingArray[1];
                String[] gpRatingPeopleAmountArray = ratingPeopleAmount.split(" ", 2);
                String gpRatingPeopleAmount = gpRatingPeopleAmountArray[0];
                //  Log.i(MainActivity.TAG, "GP content: " + content.text());
                //  Log.i(MainActivity.TAG, "GP content: gpRating " + gpRating);
                //  Log.i(MainActivity.TAG, "GP content: gpRating2 " + gpRatingPeopleAmount);
                String[] updatedTime = gpParsedString.split("Updated ");
                String[] updatedTimeArray = updatedTime[1].split(" ", 5);
                //  Log.i(MainActivity.TAG, "GP content: gpRating2 " + updatedTime[1]);
                String gpUpdatedTime = updatedTimeArray[0] + " " + updatedTimeArray[1] + " " + updatedTimeArray[2];
                // Log.i(MainActivity.TAG, "GP content: gpRating2 " + gpUpdatedTime);

                String[] installsArray = gpParsedString.split("Installs ");
                //  Log.i(MainActivity.TAG, "GP content: " + installsArray[1]);
                String[] gpInstallsArray = installsArray[1].split(" ", 2);
                String gpInstalls = gpInstallsArray[0];
                //  Log.i(MainActivity.TAG, "GP content: gpInstalls " + gpInstalls);

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

                AppList application = new AppList(MainActivity.appList.getPackageName(), MainActivity.appList.getVersion(),
                        gpInstalls, gpRatingPeopleAmount, gpRating, gpUpdatedTime);
                Log.i(MainActivity.TAG, "   AppList application :  " + application);
                Log.i(MainActivity.TAG, "   AppList application :  " + application.getPackageName());
                AppDetailActivity.appDetailViewModel.insert(application);

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
