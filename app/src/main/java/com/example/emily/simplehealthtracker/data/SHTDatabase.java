package com.example.emily.simplehealthtracker.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

@Database(entities = {Entry.class, RepeatingEntry.class}, version = 2, exportSchema = true)
public abstract class SHTDatabase extends RoomDatabase{
    public abstract EntryDao entryDao();
    public abstract RepeatingEntryDao repeatingEntryDao();
    private final static Object LOCK = new Object();
    private final static String DATABASE_NAME = "records";
    private static SHTDatabase sInstance;

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE repeat_entries (firstEntryId INTEGER NOT NULL," +
                    " time_stamp INTEGER NOT NULL," +
                    " repeat_interval INTEGER NOT NULL," +
                    " last_entry_time INTEGER NOT NULL," +
                    " PRIMARY KEY(firstEntryId), " +
                    " FOREIGN KEY(firstEntryId) REFERENCES Entry (entryId)" +
                    " ON DELETE CASCADE)");
        }
    };

    public static SHTDatabase getsInstance(Context context){
        if (sInstance == null){
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        SHTDatabase.class,
                        SHTDatabase.DATABASE_NAME)
                        .addMigrations(MIGRATION_1_2)
                        .build();
            }
            Log.d(SHTDatabase.class.getSimpleName(), "making new db");
        } else {
            Log.d(SHTDatabase.class.getSimpleName(), "db already exists");
        }
        return sInstance;
    }


}
