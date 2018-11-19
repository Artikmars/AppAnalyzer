package com.artamonov.appanalyzer.network;

import android.os.AsyncTask;
import android.util.Log;

import com.artamonov.appanalyzer.AppDetailActivity;
import com.artamonov.appanalyzer.MainActivity;
import com.artamonov.appanalyzer.data.database.AppList;
import com.artamonov.appanalyzer.presenter.AppDetailPresenter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.artamonov.appanalyzer.MainActivity.appList;


public class GPDetailPageParser extends AsyncTask<Void, Void, Void> {
    public static AppList parsedAppList;
    public static Element content;
    static ArrayList<AppList> gpList = new ArrayList<>();
    private static Document document;
    private static Element content2;
    private String appLink;
    // private AppDetailContract.AppDetailPresenter appDetailPresenter;
    private AppDetailPresenter appDetailPresenter;
    private String gpUrl;

    public GPDetailPageParser(AppDetailPresenter appDetailPresenter) {
        this.appDetailPresenter = appDetailPresenter;
    }

    public GPDetailPageParser(AppDetailPresenter appDetailPresenter, String link) {
        this.appDetailPresenter = appDetailPresenter;
        this.appLink = link;
    }

    @Override
    protected void onPostExecute(Void result) {

        double onlineTrust = AppDetailActivity.getOnlineTrustLevel(GPDetailPageParser.parsedAppList.getGpInstalls(), GPDetailPageParser.parsedAppList.getGpPeople(), GPDetailPageParser.parsedAppList.getGpRating(),
                GPDetailPageParser.parsedAppList.getGpUpdated());
        String onlTrust = String.valueOf(onlineTrust);
        parsedAppList.setOnlineTrust(onlTrust);
        Log.i(MainActivity.TAG, "onPost Execute, onlineTrust: " + onlTrust);
        appDetailPresenter.setOverallTrust();

        if (appLink == null) {
            double overallTrust = AppDetailActivity.getOverallTrustLevel(MainActivity.appList.getLastUpdateTimeInMilliseconds(),
                    MainActivity.appList.getLastRunTimeInMilliseconds(), MainActivity.appList.getAppSource(),
                    GPDetailPageParser.parsedAppList.getGpInstalls(), GPDetailPageParser.parsedAppList.getGpPeople(), GPDetailPageParser.parsedAppList.getGpRating(),
                    GPDetailPageParser.parsedAppList.getGpUpdated(), MainActivity.appList.getDangerousPermissionsAmount());
            String overTrust = String.valueOf(overallTrust);
            parsedAppList.setOverallTrust(overTrust);
            appDetailPresenter.setOverallTrust();
        }
    }

    @Override
    protected Void doInBackground(Void... strings) {
        gpList = new ArrayList<>();

        if (appLink != null) {
            gpUrl = appLink + "&hl=en";
        } else {
            gpUrl = "https://play.google.com/store/apps/details?id=" + appList.getPackageName() + "&hl=en";
        }
        Log.i(MainActivity.TAG, "gpUrl: " + gpUrl);
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

            //Validation
            try {
                Float.parseFloat(gpRating);
            } catch (NumberFormatException e) {
                gpRating = "0";
            }

            String gpPeopleWithoutCommas = gpRatingPeopleAmount.replace(",", "");
            try {
                Integer.parseInt(gpPeopleWithoutCommas);
            } catch (NumberFormatException e) {
                gpPeopleWithoutCommas = "0";
            }

            String gpInstallsWithoutCommas = gpInstalls.replace(",", "");
            String gpInstallsWithoutCommasAndPlus = gpInstallsWithoutCommas.replace("+", "");
            try {
                Integer.parseInt(gpInstallsWithoutCommasAndPlus);

            } catch (NumberFormatException e) {
                gpInstallsWithoutCommasAndPlus = "1";
            }

            try {
                new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH).parse(gpUpdatedTime);
            } catch (ParseException e) {
                gpUpdatedTime = null;
            }

            parsedAppList = new AppList();
            parsedAppList.setGpRating(gpRating);
            parsedAppList.setGpPeople(gpPeopleWithoutCommas);
            parsedAppList.setGpInstalls(gpInstallsWithoutCommasAndPlus);
            parsedAppList.setGpUpdated(gpUpdatedTime);

            if (appLink == null) {
                parsedAppList.setPackageName(MainActivity.appList.getPackageName());
            }

            Log.w(MainActivity.TAG, " parsedAppList: " + parsedAppList.getGpRating());
            Log.w(MainActivity.TAG, " parsedAppList: " + parsedAppList.getGpUpdated());

        } catch (IOException e1) {
            e1.printStackTrace();
            Log.i(MainActivity.TAG, "IO Exception");
        }

        return null;
    }


}
