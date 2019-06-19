package com.example.emily.simplehealthtracker.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "repeat_entries",
foreignKeys = @ForeignKey(entity = Entry.class,
                            parentColumns = "entryId",
                            childColumns = "firstEntryId",
                            onDelete = CASCADE))
public class RepeatingEntry {
    @PrimaryKey public int firstEntryId;

    @ColumnInfo(name = "time_stamp")
    public long timeStamp;

    @ColumnInfo(name = "repeat_interval")
    public long repeatInterval;

    @ColumnInfo(name = "last_entry_time")
    public long lastEntryMade;

    public RepeatingEntry(int firstEntryId, long timeStamp, long repeatInterval, long lastEntryMade){
        this.firstEntryId = firstEntryId;
        this.timeStamp = timeStamp;
        this.repeatInterval = repeatInterval * 3600 * 1000 * 24;
        this.lastEntryMade = lastEntryMade;
    }

    public void setLastEntryMade(long lastEntryMade) {
        this.lastEntryMade = lastEntryMade;
    }

    @Override
    public String toString() {
        return "Repeating entry with id " + firstEntryId  +
                " first created at " + timeStamp +
                " should repeat every " + repeatInterval +
                " milliseconds, last created at " + lastEntryMade +
                " next should be at " + (lastEntryMade + repeatInterval);
    }
}
