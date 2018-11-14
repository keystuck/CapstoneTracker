package com.example.emily.simplehealthtracker.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface EntryDao {
    @Query("SELECT * FROM entry ORDER BY time_stamp ASC")
    LiveData<List<Entry>> getAll();

    @Query("SELECT * FROM entry WHERE time_stamp > :time ORDER BY time_stamp ASC")
    LiveData<Entry> getEarliestFutureEntry(long time);

    @Query("SELECT * FROM entry WHERE record_type LIKE :type AND "
            +  "time_stamp >= :early AND "
    + " time_stamp <= :late ORDER BY time_stamp ASC")
    LiveData<List<Entry>> findByTypeAndTime(String type, long early, long late);

    @Query("SELECT * FROM entry WHERE description LIKE :desc AND " +
            "time_stamp >= :early AND " +
            "time_stamp <= :late ORDER BY time_stamp ASC")
    LiveData<List<Entry>> findByDescAndTime(String desc, long early, long late);

    @Query("SELECT * FROM entry WHERE record_type LIKE :givenType ORDER BY time_stamp ASC")
    LiveData<List<Entry>> findByType(String givenType);

    @Query("SELECT * FROM entry WHERE record_type LIKE 'MEDS' AND taken = 0 ORDER BY time_stamp ASC")
    LiveData<List<Entry>> findUntakenMeds();

    @Query("UPDATE entry SET taken = 1, time_stamp = :time WHERE entryId LIKE :id")
    void checkOffScheduled(long id, long time);

    @Query("SELECT * FROM entry WHERE entryId LIKE :id ORDER BY time_stamp ASC")
    LiveData<List<Entry>> findById(long id);

    @Query("SELECT * FROM entry WHERE reminder_set = 1 AND " +
            "time_stamp >= :early ORDER BY time_stamp ASC")
    LiveData<List<Entry>> findWithReminder(long early);

    @Insert
    long insertEntry(Entry entry);

    @Delete
    void deleteEntry(Entry entry);

    @Update
    int updateEntry(Entry entry);
}
