package com.example.emily.simplehealthtracker.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class EntryViewModel extends AndroidViewModel {

    private LiveData<List<Entry>> entryList;

    public EntryViewModel(@NonNull Application application) {
        super(application);
        getEntries();
    }

    public LiveData<List<Entry>> getEntries(){
        if (entryList == null){
            loadEntries();
        }
        return entryList;
    }

    public LiveData<List<Entry>> getUndone(){
        SHTDatabase mDb = SHTDatabase.getsInstance(getApplication());
        return mDb.entryDao().findUntakenMeds();
    }

    public LiveData<List<Entry>> getEntriesTypeTime(String type, long startTime, long endTime){
        SHTDatabase mDb = SHTDatabase.getsInstance(getApplication());
        return mDb.entryDao().findByTypeAndTime(type, startTime, endTime);
    }

    public LiveData<List<Entry>> getEntriesOfType(String type){
        SHTDatabase mDb = SHTDatabase.getsInstance(getApplication());
        return mDb.entryDao().findByType(type);
    }

    public void deleteList(List<Entry> entriesToDelete){
        SHTDatabase mDb = SHTDatabase.getsInstance(getApplication());
        for (Entry entry : entriesToDelete){
                mDb.entryDao().deleteEntry(entry);
        }
        }

    public LiveData<List<Entry>> getEntriesWithReminders(long startTime){
        SHTDatabase mDb = SHTDatabase.getsInstance(getApplication());
        LiveData<List<Entry>> entries = mDb.entryDao().findWithReminder(startTime);
        return entries;
    }

    public void loadEntries(){
        SHTDatabase mDb = SHTDatabase.getsInstance(getApplication());
        entryList = mDb.entryDao().getAll();
    }

    public long addEntry(String desc){
        SHTDatabase mDb = SHTDatabase.getsInstance(getApplication());

        Entry tempEntry = new Entry(desc, 0, 0, System.currentTimeMillis(), "MEDS", 0);
        long id = mDb.entryDao().insertEntry(tempEntry);
        if (mDb.entryDao().getAll() == null){
            Log.d(EntryViewModel.class.getSimpleName(), "null");
            return 0;
        }
        else return id;
    }

    public long addEntry(String desc, int amplitude, int taken, long timestamp, String type, int reminderSet){
        SHTDatabase mDb = SHTDatabase.getsInstance(getApplication());

        Entry tempEntry = new Entry(desc, amplitude, taken, timestamp, type, reminderSet);
        long id = mDb.entryDao().insertEntry(tempEntry);
        if (mDb.entryDao().getAll() == null){
            Log.d(EntryViewModel.class.getSimpleName(), "null");
            return 0;
        }
        else return id;
    }

    public long addEntry(Entry entry){
        SHTDatabase mDb = SHTDatabase.getsInstance(getApplication());
        long id = mDb.entryDao().insertEntry(entry);
        return id;
    }


    public void checkOffScheduledItems(List<Integer> inputIds){
        SHTDatabase mDb = SHTDatabase.getsInstance(getApplication());
        for (Integer inputId : inputIds) {
            mDb.entryDao().checkOffScheduled(inputId, System.currentTimeMillis());
        }
    }

    public LiveData<Entry> getNextReminder(){
        SHTDatabase mDb = SHTDatabase.getsInstance(getApplication());
        return mDb.entryDao().getEarliestFutureEntry(System.currentTimeMillis());
    }
}
