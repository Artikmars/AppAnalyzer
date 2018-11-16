package com.artamonov.appanalyzer.contract;

import com.artamonov.appanalyzer.data.database.AppList;

public interface AppDetailContract {

    interface AppDetailPresenter {
        void parseGPData();

        void setOverallTrust();

        AppList getGPData();
    }

    interface AppDetailView {
        void showProgressDialog();

        void dismissProgressDialog();

        void populateOverallTrust();
    }
}
