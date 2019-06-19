package com.example.emily.simplehealthtracker.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.List;

public class RepeatingEntryViewModel extends AndroidViewModel {

    private LiveData<List<RepeatingEntry>> repeatList;

    public RepeatingEntryViewModel(Application application){
        super(application);
        getEntries();
    }

    public LiveData<List<RepeatingEntry>> getEntries(){
        if (repeatList == null){
            loadRepeatingEntries();
        }
        return repeatList;
    }

    public void loadRepeatingEntries(){
        SHTDatabase mDb = SHTDatabase.getsInstance(getApplication());
        repeatList = mDb.repeatingEntryDao().getAll();
    }

    public void addRepeatingEntry(int entryId, long startTime, long interval){
        SHTDatabase mDb = SHTDatabase.getsInstance(getApplication());
        RepeatingEntry tempRepeat = new RepeatingEntry(entryId, startTime, interval, startTime);
        Log.d(RepeatingEntryViewModel.class.getSimpleName(), "adding repeating entry " + tempRepeat.toString());
        mDb.repeatingEntryDao().insertRepeatingEntry(tempRepeat);
    }

    public void updateRepeatingEntry(int entryId, long timestamp){
        SHTDatabase mDb = SHTDatabase.getsInstance(getApplication());
        if (mDb.repeatingEntryDao().updateLastRepeat(entryId, timestamp) == 0){
            Log.d(RepeatingEntryViewModel.class.getSimpleName(), "problem with update");
        }
    }

}
