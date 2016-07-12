package com.coolfindservices.android;

import android.content.Intent;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.pa.common.Tracer;

public class BackgroundService extends WakefulIntentService {

	public BackgroundService() {
		super("BackgroundService");
	}

	/**
	 * Asynchronous background operations of service, with wakelock
	 */
	@Override
	public void doWakefulWork(Intent intent) {
		// your code here...

		Tracer.d("Wake and have inet");
		doSync();
	}

	private void doSync() {
		// TODO Auto-generated method stub
		try {} catch (Exception e) {
			e.printStackTrace();
		}

	}

	

}
