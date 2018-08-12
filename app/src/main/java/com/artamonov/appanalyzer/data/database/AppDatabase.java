package com.artamonov.appanalyzer.data.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {AppList.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "Application")
                            //.addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract AppDao appDao();

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AppDao appDao;

        PopulateDbAsync(AppDatabase db) {
            appDao = db.appDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            appDao.deleteAll();
          /*  AppList application = new AppList("Hello");
            appDao.insert(application);
            application = new AppList("World");
            appDao.insert(application);*/
            return null;
        }
    }
}
