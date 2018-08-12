package com.artamonov.appanalyzer;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.artamonov.appanalyzer.data.AppRepository;
import com.artamonov.appanalyzer.data.database.AppList;

import java.util.List;

public class AppDetailViewModel extends AndroidViewModel {
    private AppRepository appRepository;
    private LiveData<List<AppList>> allApps;


    public AppDetailViewModel(@NonNull Application application) {
        super(application);
        appRepository = new AppRepository(application);
        allApps = appRepository.getAppList();

    }

    LiveData<List<AppList>> getAllApps() {
        return allApps;
    }

    public LiveData<AppList> getGpData(String packageName) {
        return appRepository.query(packageName);
    }

    public void insert(AppList application) {
        appRepository.insert(application);
    }

}
