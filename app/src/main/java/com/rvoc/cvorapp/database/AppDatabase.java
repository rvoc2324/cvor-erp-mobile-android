package com.rvoc.cvorapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.rvoc.cvorapp.database.dao.ShareHistoryDao;
import com.rvoc.cvorapp.models.ShareHistory;
import com.rvoc.cvorapp.utils.DateConverter;

// Annotate the class as a Room database
@Database(entities = {ShareHistory.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    // Define DAOs
    public abstract ShareHistoryDao shareHistoryDao();

    // Singleton instance of the database
    private static volatile AppDatabase INSTANCE;

    // Method to get the singleton instance
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "app_database" // Database name
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
