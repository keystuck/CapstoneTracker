package com.example.emily.simplehealthtracker.data;

import android.os.Parcel;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.os.Parcelable;


//taken from opsidao,
//https://stackoverflow.com/questions/11270161/best-way-to-store-sparsebooleanarray-in-bundle
//in order to save info to bundle
public class SparseBooleanArrayParcelable extends SparseBooleanArray implements Parcelable {


    public static Parcelable.Creator<SparseBooleanArrayParcelable> CREATOR = new Parcelable.Creator<SparseBooleanArrayParcelable>(){
        @Override
        public SparseBooleanArrayParcelable createFromParcel(Parcel parcel) {
            SparseBooleanArrayParcelable read = new SparseBooleanArrayParcelable();
            int size = parcel.readInt();

            int[] keys = new int[size];
            boolean[] values = new boolean[size];

            parcel.readIntArray(keys);
            parcel.readBooleanArray(values);

            for (int i = 0; i < size; i++){
                read.put(keys[i], values[i]);
            }
            return read;
        }

        @Override
        public SparseBooleanArrayParcelable[] newArray(int i) {
            return new SparseBooleanArrayParcelable[i];
        }
    };

    public SparseBooleanArrayParcelable(){

    }

    public SparseBooleanArrayParcelable(SparseBooleanArray sparseBooleanArray){
        for (int i = 0; i < sparseBooleanArray.size(); i++){
            this.put(sparseBooleanArray.keyAt(i), sparseBooleanArray.get(i));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        int[] keys = new int[size()];
        boolean[] values = new boolean[size()];

        for (int j = 0; j < size(); j++){
            keys[j] = keyAt(j);
            values[j] = valueAt(j);
        }

        parcel.writeInt(size());
        parcel.writeIntArray(keys);
        parcel.writeBooleanArray(values);
    }
}
