package com.artamonov.appanalyzer.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
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

    public static LiveData<AppList> getGpData(LiveData<AppList> app) {
        gpDataApp = app;
        return gpDataApp;
    }

    public LiveData<List<AppList>> getAppList() {
        return appList;
    }

    public void insert(AppList app) {
        new insertAsyncTask(appDao).execute(app);
    }

    public void update(String packageName, String gpRating, String gpPeople, String gpInstalls, String gpUpdated) {
        // new updateGPDataAsyncTask(appDao).execute(packageName, gpRating, gpPeople, gpInstalls, gpUpdated);
        appDao.update(packageName, gpRating, gpPeople, gpInstalls, gpUpdated);
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
            if (app[0] != null) {
                taskDao.insert(app[0]);
            }
            return null;
        }
    }
}

  /*  private static class updateGPDataAsyncTask extends AsyncTask<String, Void, LiveData<AppList>> {
        private AppDao taskDao;

        updateGPDataAsyncTask(AppDao dao) {
            taskDao = dao;
        }

        @Override
        protected LiveData<AppList> doInBackground(String... packageName) {
           return taskDao.update(packageName);
        }

        @Override
        protected void onPostExecute(LiveData<AppList> app) {
            super.onPostExecute(app);
            //getGpData(app);
        }
    }
}*/
