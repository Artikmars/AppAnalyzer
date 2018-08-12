package com.artamonov.appanalyzer.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppList application);

    //@Update(onConflict = OnConflictStrategy.REPLACE)
    // void update(AppList application);

    //@Query("UPDATE Application SET gpRating = :gpRating, gpPeople = :gpPeople, gpInstalls = :gpInstalls, gpUpdated = :gpUpdated " +
    //        "WHERE packageName =:packageName")
    // void update(String packageName, String gpRating, String gpPeople, String gpInstalls, String gpUpdated);

    @Query("DELETE FROM Application")
    void deleteAll();

    @Query("SELECT * FROM Application ORDER BY packageName")
    LiveData<List<AppList>> getAllAps();

    @Query("SELECT * FROM Application WHERE packageName = :packageName ")
        // List<AppList> getGpData(String packageName);
    LiveData<AppList> getGpData(String packageName);

}
