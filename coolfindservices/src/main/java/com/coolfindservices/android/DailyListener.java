package com.coolfindservices.android;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.commonsware.cwac.wakeful.WakefulIntentService.AlarmListener;
import com.pa.common.Tracer;

public class DailyListener implements AlarmListener {
    public void scheduleAlarms(AlarmManager mgr, PendingIntent pi, Context context) {
        // register when enabled in preferences
    	Tracer.d("Daily Listener");
        if (false/*new ProfilUtils(context).getUpdateDaily()*/) {
            Log.i("DailyListener", "Schedule update check...");
 
            // every day at 11 am
            Calendar calendar = Calendar.getInstance();
            // if it's after or equal 12 pm schedule for next day
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 11) {
                calendar.add(Calendar.DAY_OF_YEAR, 1); // add, not set!
            }
            calendar.set(Calendar.HOUR_OF_DAY, 11);
            calendar.set(Calendar.MINUTE, 20);
            calendar.set(Calendar.SECOND, 0);
 
            mgr.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pi);
            
            //set for alarm at 18.00
//            calendar.set(Calendar.HOUR_OF_DAY, 18);
//            calendar.set(Calendar.MINUTE, 0);
//            calendar.set(Calendar.SECOND, 0);
// 
//            mgr.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
//                        AlarmManager.INTERVAL_DAY, pi);

        }
    }
 
    public void sendWakefulWork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
 
        // only when connected or while connecting...
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
 
            boolean updateOnlyOnWifi =false;// new ProfilUtils(context).getUpdateOnlyOnWIFI();
 
            // if we have mobile or wifi connectivity...
            if (((netInfo.getType() == ConnectivityManager.TYPE_MOBILE) && updateOnlyOnWifi == false)
                    || (netInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
                Log.d("DailyListener", "We have internet, start update check directly now!");
 
                Intent backgroundIntent = new Intent(context, BackgroundService.class);
                WakefulIntentService.sendWakefulWork(context, backgroundIntent);
            } else {
                Log.d("DailyListener", "We have no internet, enable ConnectivityReceiver!");
 
                // enable receiver to schedule update when internet is available!
                ConnectivityReceiver.enableReceiver(context);
            }
        } else {
            Log.d("DailyListener", "We have no internet, enable ConnectivityReceiver!");
 
            // enable receiver to schedule update when internet is available!
            ConnectivityReceiver.enableReceiver(context);
        }
    }
 
    public long getMaxAge() {
        return (AlarmManager.INTERVAL_DAY + 60 * 1000);
    }
}