package com.artamonov.appanalyzer.network;

import android.os.AsyncTask;
import android.util.Log;
import com.artamonov.appanalyzer.MainActivity;
import com.artamonov.appanalyzer.data.database.AppList;
import com.artamonov.appanalyzer.presenter.AppDetailPresenter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GPSearchParser extends AsyncTask<Void, Void, Void> {
    public static AppList parsedAppList;
    public static Element content;
    static ArrayList<AppList> gpList = new ArrayList<>();
    private static Document document;
    private static Element content2;
    // private AppDetailContract.AppDetailPresenter appDetailPresenter;
    private AppDetailPresenter appDetailPresenter;
    private String appName;
    private ArrayList<String> arrayAppNames;
    private ArrayList<String> arrayLinks;
    private ArrayList<String> arrayListWithUniqueValues;

    public GPSearchParser(AppDetailPresenter appDetailPresenter, String appName) {
        this.appDetailPresenter = appDetailPresenter;
        this.appName = appName;
    }

    @Override
    protected void onPostExecute(Void result) {

        appDetailPresenter.setSearchAppsAdapter(arrayAppNames, arrayListWithUniqueValues);
    }

    @Override
    protected Void doInBackground(Void... strings) {
        gpList = new ArrayList<>();
        String gpUrl = "https://play.google.com/store/search?q=" + appName + "&c=apps&hl=en";
        Log.w(MainActivity.TAG, "appName: " + appName);
        try {

            document = Jsoup.connect(gpUrl).get();
            // Elements links =
            // document.select("a[href^=https://play.google.com/store/apps/details?id=]");
            //   Elements links2 = document.select("div.card-click-target");
            // Elements links3 = document.select("img[alt]"); - work!
            Elements links3 = document.getElementsByClass("cover-outer-align");
            arrayAppNames = new ArrayList<>();
            for (Element link : links3) {
                Elements links4 = link.getElementsByTag("img");
                String link6 = links4.attr("alt");
                arrayAppNames.add(link6);
                // Log.w(MainActivity.TAG, "link6: " + link6);
            }
            //  Elements links4 = links3.getElementsByTag("img");
            //  Log.w(MainActivity.TAG, "link3: " + links4);
            /*  for (Element link : links4) {
              String link5 = link.attr("alt");
                Log.w(MainActivity.TAG, "link5: " + link5);
            }*/

            // String content = links3.attr("alt");
            //  Elements links4 = links3.select("[alt]");
            // Elements links4 = document.getElementsByTag("img");

            // Elements links = null;
            Elements links = document.select("div.details");
            Elements links2 = links.select("a[href]");

            //  Elements links =
            // document.select("div:contains(https://play.google.com/store/apps/details?id)");
            //  Elements links =
            // document.select("a[href^=https://play.google.com/store/apps/details?id=]");
            // String absHref = links.attr("abs:href");
            //  Elements links =
            // document.select("href^=https://play.google.com/store/apps/details?id=");
            //  content = document.select("div:contains(Additional Information)").get(1);

            arrayLinks = new ArrayList<>();
            for (Element link : links2) {
                //   Log.w(MainActivity.TAG, "a[href]: " + link.attr("abs:href"));
                String hyperLink = link.attr("abs:href");
                String playLink = "https://play.google.com/store/apps/details?id=";
                if (hyperLink.toLowerCase().contains(playLink.toLowerCase())) {
                    arrayLinks.add(hyperLink);
                }
            }

            arrayListWithUniqueValues = new ArrayList<>();
            HashSet<String> set = new HashSet<>();
            for (String item : arrayLinks) {
                if (!set.contains(item)) {
                    arrayListWithUniqueValues.add(item);
                    set.add(item);
                }

                //   Log.w(MainActivity.TAG, "links: " + links);

                // Log.w(MainActivity.TAG, "links.text(): " + links2);
                //  Log.w(MainActivity.TAG, "links.text(): " + links3);
                // Log.w(MainActivity.TAG, "links.text(): " + links.get(0));
                //  Log.w(MainActivity.TAG, "links.text(): " + links.text());
                // Log.w(MainActivity.TAG, "links3: " + links4);
                //   Log.w(MainActivity.TAG, "links3: " + content);

                //  Log.w(MainActivity.TAG, "links.attr(): " + links.attr("abs:href"));
                //  Log.w(MainActivity.TAG, "absHref: " + absHref);
                /*    Log.w(MainActivity.TAG, "links.text(): " + links.get(0));
                Log.w(MainActivity.TAG, "links.text(): " + links.get(1));
                Log.w(MainActivity.TAG, "links.text(): " + links.get(2));
                Log.w(MainActivity.TAG, "links.text(): " + links.get(3));*/
                /*String gpParsedString = content.text();
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
                parsedAppList.setPackageName(MainActivity.appList.getPackageName());

                Log.w(MainActivity.TAG, " parsedAppList: " + parsedAppList.getGpRating());
                Log.w(MainActivity.TAG, " parsedAppList: " + parsedAppList.getGpUpdated());*/

            }
        } catch (IOException e1) {
            e1.printStackTrace();
            Log.i(MainActivity.TAG, "IO Exception");
        }

        return null;
    }
}
