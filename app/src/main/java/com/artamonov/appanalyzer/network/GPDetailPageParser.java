package com.artamonov.appanalyzer.network;

import static com.artamonov.appanalyzer.MainActivity.TAG;
import static com.artamonov.appanalyzer.MainActivity.appList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.artamonov.appanalyzer.AppDetailActivity;
import com.artamonov.appanalyzer.MainActivity;
import com.artamonov.appanalyzer.R;
import com.artamonov.appanalyzer.data.database.AppList;
import com.artamonov.appanalyzer.presenter.AppDetailPresenter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    private Context context;

    public GPDetailPageParser(AppDetailPresenter appDetailPresenter, Context context) {
        this.appDetailPresenter = appDetailPresenter;
        this.context = context;
    }

    public GPDetailPageParser(AppDetailPresenter appDetailPresenter, String link, Context context) {
        this.appDetailPresenter = appDetailPresenter;
        this.appLink = link;
        this.context = context;
    }

    @Override
    protected void onPostExecute(Void result) {

        // double onlineTrust =
        // AppDetailActivity.getOnlineTrustLevel(GPDetailPageParser.parsedAppList.getGpInstalls(),
        // GPDetailPageParser.parsedAppList.getGpPeople(),
        // GPDetailPageParser.parsedAppList.getGpRating(),
        //        GPDetailPageParser.parsedAppList.getGpUpdated());
        //  String onlTrust = String.valueOf(onlineTrust);
        // parsedAppList.setOnlineTrust(onlTrust);
        //  Log.i(MainActivity.TAG, "onPost Execute, onlineTrust: " + onlTrust);
        appDetailPresenter.setOverallTrust();

        if (appLink == null) {
            double overallTrust =
                    AppDetailActivity.getOverallTrustLevel(
                            MainActivity.appList.getLastUpdateTimeInMilliseconds(),
                            MainActivity.appList.getLastRunTimeInMilliseconds(),
                            MainActivity.appList.getAppSource(),
                            GPDetailPageParser.parsedAppList.getGpInstalls(),
                            GPDetailPageParser.parsedAppList.getGpPeople(),
                            GPDetailPageParser.parsedAppList.getGpRating(),
                            GPDetailPageParser.parsedAppList.getGpUpdated(),
                            MainActivity.appList.getDangerousPermissionsAmount());
            String overTrust = String.valueOf(overallTrust);
            parsedAppList.setOverallTrust(overTrust);
            appDetailPresenter.setOverallTrust();
        }
    }

    @Override
    protected Void doInBackground(Void... strings) {
        gpList = new ArrayList<>();

        if (appLink != null) {
            //  gpUrl = appLink + "&hl=en";
            gpUrl = appLink + context.getResources().getString(R.string.gp_link_prefix);
        } else {
            // gpUrl = "https://play.google.com/store/apps/details?id=" + appList.getPackageName() +
            // "&hl=en";
            gpUrl =
                    String.format(
                            context.getResources().getString(R.string.gp_link),
                            appList.getPackageName());
        }
        Log.i(MainActivity.TAG, "gpUrl: " + gpUrl);
        try {

            document = Jsoup.connect(gpUrl).get();
            content =
                    document.select(context.getResources().getString(R.string.gp_additional_info))
                            .get(1);

            String gpParsedString = content.text();
            String[] ratingArray = gpParsedString.split("Policy ");
            String[] gpRatingArray = ratingArray[1].split(" ", 2);
            String gpRating = gpRatingArray[0];

            String ratingPeopleAmount = gpRatingArray[1];
            String[] gpRatingPeopleAmountArray = ratingPeopleAmount.split(" ", 2);
            String gpRatingPeopleAmount = gpRatingPeopleAmountArray[0];
            String[] updatedTime = gpParsedString.split("Updated ");
            String[] updatedTimeArray = updatedTime[1].split(" ", 5);
            String gpUpdatedTime =
                    updatedTimeArray[0] + " " + updatedTimeArray[1] + " " + updatedTimeArray[2];

            String[] installsArray = gpParsedString.split("Installs ");
            String[] gpInstallsArray = installsArray[1].split(" ", 2);
            String gpInstalls = gpInstallsArray[0];

            if (content2 != null) {
                Log.i(MainActivity.TAG, "GP content2: " + content2.text());
                Log.i(MainActivity.TAG, "GP content3: " + content2.toString());
            }

            // Validation
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

            // Category Parsing
            // applicationCategory
            document = Jsoup.connect(gpUrl).get();
            //  content = document.select("meta:contains(applicationCategory)").get(1);
            String gpParsedString2 = content.text();
            Log.i(TAG, "meta:contains(applicationCategory) " + gpParsedString2);

            Elements metaTags = document.getElementsByTag("meta");
            String category = null;
            for (Element metaTag : metaTags) {
                String name = metaTag.attr("itemprop");
                if (name.equals("applicationCategory")) {
                    category = metaTag.attr("content");
                }
            }
            Log.i(TAG, "itemprop: " + metaTags);
            Log.i(TAG, "itemprop: " + metaTags.text());
            Log.i(TAG, "itemprop: " + category);

            parsedAppList = new AppList();
            // parsedAppList.setGpCategory(category);
            formatCategory(category);
            parsedAppList.setGpRating(gpRating);
            parsedAppList.setGpPeople(gpPeopleWithoutCommas);
            parsedAppList.setGpInstalls(gpInstallsWithoutCommasAndPlus);
            parsedAppList.setGpUpdated(gpUpdatedTime);

            if (appLink == null) {
                parsedAppList.setPackageName(MainActivity.appList.getPackageName());
            }

            Log.w(MainActivity.TAG, " parsedAppList installs: " + parsedAppList.getGpInstalls());
            Log.w(MainActivity.TAG, " parsedAppList updated: " + parsedAppList.getGpUpdated());

        } catch (IOException e1) {
            e1.printStackTrace();
            Log.i(MainActivity.TAG, "IO Exception");
        }

        return null;
    }

    private void formatCategory(String category) {

        switch (category) {
            case "ART_AND_DESIGN":
                parsedAppList.setGpCategory("Art & Design");
                break;
            case "AUTO_AND_VEHICLES":
                parsedAppList.setGpCategory("Auto & Vehicles");
                break;
            case "BEAUTY":
                parsedAppList.setGpCategory("Beauty");
                break;
            case "BOOKS_AND_REFERENCE":
                parsedAppList.setGpCategory("Books & Reference");
                break;
            case "BUSINESS":
                parsedAppList.setGpCategory("Business");
                break;
            case "COMICS":
                parsedAppList.setGpCategory("Comics");
                break;
            case "COMMUNICATION":
                parsedAppList.setGpCategory("Communication");
                break;
            case "DATING":
                parsedAppList.setGpCategory("Dating");
                break;
            case "EDUCATION":
                parsedAppList.setGpCategory("Education");
                break;
            case "ENTERTAINMENT":
                parsedAppList.setGpCategory("Entertainment");
                break;
            case "EVENTS":
                parsedAppList.setGpCategory("Events");
                break;
            case "FINANCE":
                parsedAppList.setGpCategory("Finance");
                break;
            case "FOOD_AND_DRINK":
                parsedAppList.setGpCategory("Food &Drink");
                break;
            case "HEALTH_AND_FITNESS":
                parsedAppList.setGpCategory(" Health &Fitness");
                break;
            case "HOUSE_AND_HOME":
                parsedAppList.setGpCategory("House &Home");
                break;
            case "LIFESTYLE":
                parsedAppList.setGpCategory("Lifestyle");
                break;
            case "MAPS_AND_NAVIGATION":
                parsedAppList.setGpCategory("Maps & Navigation");
                break;
            case "MEDICAL":
                parsedAppList.setGpCategory("Medical");
                break;
            case "MUSIC_AND_AUDIO":
                parsedAppList.setGpCategory("Music & Audio");
                break;
            case "NEWS_AND_MAGAZINES":
                parsedAppList.setGpCategory("News & Magazines");
                break;
            case "PARENTING":
                parsedAppList.setGpCategory("Parenting");
                break;
            case "PERSONALIZATION":
                parsedAppList.setGpCategory("Personalization");
                break;
            case "PHOTOGRAPHY":
                parsedAppList.setGpCategory("Photography");
                break;
            case "PRODUCTIVITY":
                parsedAppList.setGpCategory("Productivity");
                break;
            case "SHOPPING":
                parsedAppList.setGpCategory("Shopping");
                break;
            case "SOCIAL":
                parsedAppList.setGpCategory("Social");
                break;
            case "SPORTS":
                parsedAppList.setGpCategory("Sports");
                break;
            case "TOOLS":
                parsedAppList.setGpCategory("Tools");
                break;
            case "TRAVEL_AND_LOCAL":
                parsedAppList.setGpCategory("Travel & Local");
                break;
            case "VIDEO_PLAYERS":
                parsedAppList.setGpCategory("Video Players & Editors");
                break;
            case "WEATHER":
                parsedAppList.setGpCategory("Weather");
                break;
            case "LIBRARIES_AND_DEMO":
                parsedAppList.setGpCategory("Libraries & Demo");
                break;
            default:
                parsedAppList.setGpCategory("Undefined");
        }
    }
}
