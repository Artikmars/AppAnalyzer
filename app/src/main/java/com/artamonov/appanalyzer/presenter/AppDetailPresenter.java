package com.artamonov.appanalyzer.presenter;

import com.artamonov.appanalyzer.contract.AppDetailContract;
import com.artamonov.appanalyzer.data.database.AppList;
import com.artamonov.appanalyzer.network.GPDetailPageParser;
import com.artamonov.appanalyzer.network.GPSearchParser;

import java.util.ArrayList;

public class AppDetailPresenter implements AppDetailContract.AppDetailPresenter {

    private final AppDetailContract.AppDetailView view;
    private AppList parsedAppList;

    public AppDetailPresenter(AppDetailContract.AppDetailView view) {
        this.view = view;
    }

    @Override
    public void parseGPData() {
        GPDetailPageParser googlePlayParser = new GPDetailPageParser(this);
        googlePlayParser.execute();

    }

    @Override
    public void parseGPSearch(String appName) {
        GPSearchParser gpSearchParser = new GPSearchParser(this, appName);
        gpSearchParser.execute();

    }

    @Override
    public void setSearchAppsAdapter(ArrayList<String> arrayAppNames, ArrayList<String> arrayLinks) {
        view.setSearchAppsAdapter(arrayAppNames, arrayLinks);
    }

    @Override
    public void setOverallTrust() {
        view.populateOverallTrust();
    }

    @Override
    public void setOnlineTrust() {
        view.populateOnlineTrust();
    }
    @Override
    public AppList getGPData() {
        return parsedAppList;
    }

    public void parseGPData(String link) {
        GPDetailPageParser googlePlayParser = new GPDetailPageParser(this, link);
        googlePlayParser.execute();
    }
}
