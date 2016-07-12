package com.pa.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Steven on 06/11/2015.
 */
public class PackageScheduleItem implements Parcelable{
    public long unixDate = 0;
    public String date = "";
    public ArrayList<PackageTimeSlot> mTimeSlot = new ArrayList<>();

    protected PackageScheduleItem(Parcel in) {
        unixDate = in.readLong();
        date = in.readString();
    }

    public static final Creator<PackageScheduleItem> CREATOR = new Creator<PackageScheduleItem>() {
        @Override
        public PackageScheduleItem createFromParcel(Parcel in) {
            return new PackageScheduleItem(in);
        }

        @Override
        public PackageScheduleItem[] newArray(int size) {
            return new PackageScheduleItem[size];
        }
    };

    public PackageScheduleItem() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(unixDate);
        dest.writeString(date);
    }
}
