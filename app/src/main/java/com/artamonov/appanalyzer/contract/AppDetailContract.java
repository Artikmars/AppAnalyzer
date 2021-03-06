package com.artamonov.appanalyzer.contract;

import android.content.Context;

import com.artamonov.appanalyzer.data.database.AppList;

import java.util.ArrayList;

public interface AppDetailContract {

    interface AppDetailPresenter {
        void parseGPData(Context context);

        void parseGPSearch(String appName);

        void setSearchAppsAdapter(ArrayList<String> arrayAppNames, ArrayList<String> arrayLinks);
        void setOverallTrust();

        void setOnlineTrust();

        AppList getGPData();
    }

    interface AppDetailView {
        void showProgressDialog();

        void dismissProgressDialog();

        void populateOverallTrust();

        void setSearchAppsAdapter(ArrayList<String> arrayAppNames, ArrayList<String> arrayLinks);
        void populateOnlineTrust();
    }
}
