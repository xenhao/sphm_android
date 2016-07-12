package com.pa.parser;

import android.util.Log;

import com.pa.pojo.PackageScheduleItem;
import com.pa.pojo.PackageTimeSlot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by Steven on 06/11/2015.
 */
public class ParserDealGetPackageSchedule {

    private static String LOG_TAG = "ParserDealGetPackageSchedule";
    public boolean status;
    public JSONObject mJson;
    public ArrayList<PackageScheduleItem> mSchedule = new ArrayList<>();

    public ParserDealGetPackageSchedule(String content) {
        try {
            mJson = new JSONObject(content);

            if (mJson.has("status") && mJson.getString("status").equalsIgnoreCase("success")) {
                status = true;
            } else {
                status = false;
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void parseSchedule()
    {
        try {
            mSchedule.clear();

            JSONObject mResult  = mJson.getJSONObject("result");
            long mStartDate     = mJson.getLong("start_date");
            long mEndDate       = mJson.getLong("end_date");
            //todo change today date
            long mCurrentTime   = System.currentTimeMillis()/1000;
            mCurrentTime -= 86400;
            //String ts = tsLong.toString();
            Log.i("get current time", mCurrentTime + "");
            long mCurrentDate   = mStartDate;

//            while (mCurrentDate < mEndDate)
            int position = 0;   Log.i(LOG_TAG, "mResult.length(): " + String.valueOf(mResult.length()));
            if(mResult.length() > 0) {
                while (position <= mResult.length()) {   //Log.e(LOG_TAG, "Running in while loop");
                    if (mCurrentTime > mCurrentDate) {
//                    position++;
                        mCurrentDate += 86400;
                        continue;
                    }

                    PackageScheduleItem schedule            = new PackageScheduleItem();
                    ArrayList<PackageTimeSlot> mArraySlot   = new ArrayList<>();

                    schedule.unixDate = mCurrentDate;
                    Date df         = new java.util.Date(mCurrentDate);
                    schedule.date   = new SimpleDateFormat("yyyy-MM-dd").format(df);


                    if (mResult.has(mCurrentDate + "")) {
                        JSONObject mJSchedule = mResult.getJSONObject(mCurrentDate + "");
                        JSONArray mArrayJSlot = mJSchedule.getJSONArray("slot");
                        Log.i("Schedule Get", mArrayJSlot.length() + "");
                        for (int i = 0, ilen = mArrayJSlot.length(); i < ilen; i++) {
                            JSONObject mJSlot        = mArrayJSlot.getJSONObject(i);
                            PackageTimeSlot mSlot    = new PackageTimeSlot();

                            mSlot.start_datetime_unix   = mJSlot.getLong("start_datetime_unix");
                            mSlot.end_datetime_unix     = mJSlot.getLong("end_datetime_unix");
                            mSlot.start_datetime        = mJSlot.getString("start_datetime");
                            mSlot.end_datetime          = mJSlot.getString("end_datetime");
                            mSlot.start_time            = mJSlot.getString("start_time");
                            mSlot.end_time              = mJSlot.getString("end_time");
                            mArraySlot.add(mSlot);
                        }
                        Log.i("Schedule Get", mArraySlot.size() + "");
                        //  increment only occurs when valid timeslot data is inserted
                        position++;
                        //  extra increment to prevent infinity loop at the end of timeslot data
                        if (position == mResult.length()) position++;
                    }

                    if (mArraySlot.size() > 0) {
                        schedule.mTimeSlot = mArraySlot;
                        mSchedule.add(schedule);
                    }
                    mCurrentDate += 86400;
//                position++;
                }
            }




//            Iterator<String> iter = mResult.keys();
//            while (iter.hasNext()) {
//                String key                  = iter.next();
//                JSONObject mJSchedule       = mResult.getJSONObject(key);
//                PackageScheduleItem temp    = new PackageScheduleItem();
//
//                temp.date       = mJSchedule.getString("date");
//                temp.unixDate   = mJSchedule.getLong("unix_date");
//
//                ArrayList<PackageTimeSlot> mArraySlot = new ArrayList<>();
//                JSONArray mArrayJSlot = mJSchedule.getJSONArray("slot");
//                for(int i = 0, ilen = mArrayJSlot.length(); i < ilen; i++)
//                {
//                    JSONObject mJSlot = mArrayJSlot.getJSONObject(i);
//                    PackageTimeSlot mSlot = new PackageTimeSlot();
//                    mSlot.start_datetime_unix = mJSlot.getLong("start_datetime_unix");
//                    mSlot.end_datetime_unix = mJSlot.getLong("end_datetime_unix");
//                    mSlot.start_datetime = mJSlot.getString("start_datetime");
//                    mSlot.end_datetime = mJSlot.getString("end_datetime");
//                    mSlot.start_time = mJSlot.getString("start_time");
//                    mSlot.end_time = mJSlot.getString("end_time");
//                    mArraySlot.add(mSlot);
//                }
//
//                temp.mTimeSlot = mArraySlot;
//                mSchedule.add(temp);
//
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
