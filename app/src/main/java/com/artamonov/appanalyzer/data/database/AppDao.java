package com.artamonov.appanalyzer.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppList application);

    // @Update(onConflict = OnConflictStrategy.REPLACE)
    // void query(AppList application);

    @Query(
            "UPDATE Application SET gpRating = :gpRating, gpPeople = :gpPeople, gpInstalls = :gpInstalls, gpUpdated = :gpUpdated "
                    + "WHERE packageName =:packageName")
    void update(
            String packageName,
            String gpRating,
            String gpPeople,
            String gpInstalls,
            String gpUpdated);

    @Query("DELETE FROM Application")
    void deleteAll();

    @Query("SELECT * FROM Application ORDER BY packageName")
    LiveData<List<AppList>> getAllAps();

    @Query("SELECT * FROM Application WHERE packageName = :packageName ")
    LiveData<AppList> getGpData(String packageName);
}
