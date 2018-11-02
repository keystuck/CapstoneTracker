package com.example.emily.simplehealthtracker.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Entry {

    @PrimaryKey(autoGenerate = true)
    private int entryId;

    @ColumnInfo
    private String description;

    @ColumnInfo
    private int amplitude;

    @ColumnInfo
    private int taken;

    @ColumnInfo(name = "time_stamp")
    private long timeStamp;

    @ColumnInfo(name = "record_type")
    private String recordType;

    @ColumnInfo(name = "reminder_set")
    private int reminderSet;

    public Entry(String description, int amplitude, int taken, long timeStamp, String recordType, int reminderSet){
        this.description = description;
        this.amplitude = amplitude;
        this.taken = taken;

        this.timeStamp = timeStamp;
        this.recordType = recordType;
        this.reminderSet = reminderSet;
    }

    @Override
    public String toString() {
        String output = description +
                " (" + amplitude + ") in category "
                + recordType;
        if (taken == 1){
            output += " occurred/was taken at " +
                    convertDate(timeStamp);
        } else {
            output += " was not taken at " +
                    convertDate(timeStamp) + "\n";
        }
        return output;
    }

    public int getTaken() {
        return taken;
    }

    public void setTaken(int taken) {
        this.taken = taken;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(int amplitude) {
        this.amplitude = amplitude;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public int getReminderSet() { return reminderSet; }

    public void setReminderSet(int reminderSet) { this.reminderSet = reminderSet; }

    public String convertDate(long timestamp){
        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd hh:mm");
        String time = simpleDateFormat.format(date);
        return time;
    }

}
