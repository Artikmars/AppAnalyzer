package com.artamonov.appanalyzer.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.artamonov.appanalyzer.data.database.AppDao;
import com.artamonov.appanalyzer.data.database.AppDatabase;
import com.artamonov.appanalyzer.data.database.AppList;

import java.util.List;

public class AppRepository {
    private static LiveData<AppList> gpDataApp;
    private AppDao appDao;
    private LiveData<List<AppList>> appList;

    public AppRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        appDao = db.appDao();
        appList = appDao.getAllAps();
    }

    static LiveData<AppList> getGpData(LiveData<AppList> app) {
        gpDataApp = app;
        return gpDataApp;
    }

    public LiveData<List<AppList>> getAppList() {
        return appList;
    }

    public void insert(AppList app) {
        new insertAsyncTask(appDao).execute(app);
    }

    public LiveData<AppList> query(String packageName) {
        //  new queryGPDataAsyncTask(appDao).execute(packageName);
        return appDao.getGpData(packageName);
    }

    private static class insertAsyncTask extends AsyncTask<AppList, Void, Void> {

        private AppDao taskDao;

        insertAsyncTask(AppDao dao) {
            taskDao = dao;
        }

        @Override
        protected Void doInBackground(AppList... app) {
            taskDao.insert(app[0]);
            return null;
        }
    }

    private static class queryGPDataAsyncTask extends AsyncTask<String, Void, LiveData<AppList>> {

        public QueryAsyncResponse delegate = null;
        private AppDao taskDao;

        queryGPDataAsyncTask(AppDao dao) {
            taskDao = dao;
        }

        @Override
        protected LiveData<AppList> doInBackground(String... packageName) {
            return taskDao.getGpData(packageName[0]);
        }

        @Override
        protected void onPostExecute(LiveData<AppList> app) {
            super.onPostExecute(app);
            //getGpData(app);
            // delegate.processFinish(app);
        }
    }
}
