package com.artamonov.appanalyzer;

import android.content.Context;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.artamonov.appanalyzer.MainActivity.TAG;

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

    public static GooglePlayTabFragment newInstance() {
        GooglePlayTabFragment fragment = new GooglePlayTabFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && MainActivity.appList.getAppSource().equals("Google Play")) {
            ParseTask parseTask = new ParseTask();
            parseTask.execute();

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

        return view;
    }

    public void populateViews() {

        tvRating.setText(gpList.get(0).getGpRating());
        tvInstalls.setText(gpList.get(0).getGpInstalls());
        tvPeople.setText(gpList.get(0).getGpPeople());
        tvUpdated.setText(gpList.get(0).getGpUpdated());
    }


    public class ParseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            populateViews();
        }

        @Override
        protected Void doInBackground(Void... strings) {
            Log.i(MainActivity.TAG, "doInBackground ");
            gpList = new ArrayList<>();
            String gpUrl = "https://play.google.com/store/apps/details?id=" + MainActivity.appList.getPackageName() + "&hl=en";
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
