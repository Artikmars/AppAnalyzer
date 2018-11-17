package com.artamonov.appanalyzer.presenter;

import com.artamonov.appanalyzer.contract.AppDetailContract;
import com.artamonov.appanalyzer.data.database.AppList;
import com.artamonov.appanalyzer.network.GooglePlayParser;

public class AppDetailPresenter implements AppDetailContract.AppDetailPresenter {

    private final AppDetailContract.AppDetailView view;
    private AppList parsedAppList;

    public AppDetailPresenter(AppDetailContract.AppDetailView view) {
        this.view = view;
    }

    @Override
    public void parseGPData() {
        GooglePlayParser googlePlayParser = new GooglePlayParser(this);
        googlePlayParser.execute();

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
}
