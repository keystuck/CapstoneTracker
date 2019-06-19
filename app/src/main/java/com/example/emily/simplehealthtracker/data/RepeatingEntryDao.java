package com.example.emily.simplehealthtracker.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RepeatingEntryDao {

    @Query("SELECT * FROM repeat_entries ORDER BY time_stamp")
    LiveData<List<RepeatingEntry>> getAll();

    @Query("UPDATE repeat_entries SET last_entry_time = :time WHERE firstEntryId LIKE :id")
    int updateLastRepeat(long id, long time);

    @Insert
    void insertRepeatingEntry(RepeatingEntry repeatingEntry);

    @Delete
    int deleteRepeatingEntry(RepeatingEntry repeatingEntry);
}
