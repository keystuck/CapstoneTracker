package com.example.emily.simplehealthtracker.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

@Database(entities = {Entry.class}, version = 1, exportSchema = false)
public abstract class SHTDatabase extends RoomDatabase{
    public abstract EntryDao entryDao();
    private final static Object LOCK = new Object();
    private final static String DATABASE_NAME = "records";
    private static SHTDatabase sInstance;

    public static SHTDatabase getsInstance(Context context){
        if (sInstance == null){
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        SHTDatabase.class,
                        SHTDatabase.DATABASE_NAME)
//                        .allowMainThreadQueries()
                        .build();
            }
            Log.d(SHTDatabase.class.getSimpleName(), "making new db");
        } else {
            Log.d(SHTDatabase.class.getSimpleName(), "db already exists");
        }
        return sInstance;
    }
}
