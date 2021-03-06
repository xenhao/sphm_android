package com.coolfindservices.android;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.coolfindservices.androidconsumer.BuildConfig;
import com.google.firebase.crash.FirebaseCrash;

public class MainActivity extends Activity {

	ShareExternalServer appUtil;
	String regId;
	AsyncTask<Void, Void, String> shareRegidTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		appUtil = new ShareExternalServer();

		regId = getIntent().getStringExtra("regId");
		Log.d("MainActivity", "regId: " + regId);

		final Context context = this;
		shareRegidTask = new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String result = appUtil.shareRegIdWithAppServer(context, regId);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				shareRegidTask = null;
				//FormUtils.simpleToast(MainActivity.this, result);
			}

		};
		shareRegidTask.execute(null, null, null);

		try {
			//	firebase crash reporting
			if (BuildConfig.FLAVOR.equalsIgnoreCase("live")) {
				Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread thread, Throwable ex) {
						FirebaseCrash.report(ex);
					}
				});
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
