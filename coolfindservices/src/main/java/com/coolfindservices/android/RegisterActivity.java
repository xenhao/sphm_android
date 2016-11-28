package com.coolfindservices.android;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.coolfindservices.androidconsumer.R;

import io.intercom.android.sdk.Intercom;

public class RegisterActivity implements com.pa.common.Config{

	Button btnGCMRegister;
	Button btnAppShare;
	GoogleCloudMessaging gcm;
	Context context;
	String regId;

	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";

	static final String TAG = "RegisterActivity";


	public RegisterActivity(Context context){
		this.context=context;
	}
	
	public void btnRegisOnClick(){ 
		if (TextUtils.isEmpty(regId)) {
			regId = registerGCM();
			Log.d("RegisterActivity", "GCM RegId: " + regId);
		} else {
			//FormUtils.simpleToast((Activity) context,
			//		"Already Registered with GCM Server!");
			registeridListener.onReceiveRegiserId(getRegistrationId());
		}   
	}

	public String registerGCM() {

		gcm = GoogleCloudMessaging.getInstance(context);
		regId = getRegistrationId();

		if (TextUtils.isEmpty(regId)) {

			registerInBackground();

			Log.d("RegisterActivity",
					"registerGCM - successfully registered with GCM server - regId: "
							+ regId);
		} else {
			Log.d("RegisterActivity",regId+"");
			//Toast.makeText(context,
			//		"RegId already available. RegId: " + regId,
			//		Toast.LENGTH_LONG).show();
            sendRegistrationIdToBackend(regId);
		}
		
		String android_id = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID); 
		
		if(TextUtils.isEmpty(regId)){
			regId=android_id;
		}
		return regId;
	}
	
	public String getRegistrationId2(){
		String reg=getRegistrationId();
		
		if(TextUtils.isEmpty(reg)){
			return Secure.getString(context.getContentResolver(),
	                Secure.ANDROID_ID);
		}
		
		return reg;
	}

	public String getRegistrationId() {
		final SharedPreferences prefs = context.getSharedPreferences(
				com.pa.common.Config.APP_PREFERENCES, Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		if (registrationId==null ||registrationId.length()==0) {
			Log.i(TAG, "Registration not found.");
//			String android_id = Secure.getString(context.getContentResolver(),
//	                Secure.ANDROID_ID); 

			return "";
		}
		int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.d("RegisterActivity",
					"I never expected this! Going down, going down!" + e);
			throw new RuntimeException(e);
		}
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(Config.GOOGLE_PROJECT_ID);
					Log.d("RegisterActivity", "registerInBackground - regId: "
							+ regId);
					msg = "Device registered, registration ID=" + regId;

                    sendRegistrationIdToBackend(regId);
					storeRegistrationId(regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("RegisterActivity", "Error: " + msg);
					
					
				}
				Log.d("RegisterActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				//Toast.makeText(context,
				//		"Registered with GCM Server." + msg, Toast.LENGTH_LONG)
				//		.show();
				if(null!=registeridListener){
					registeridListener.onReceiveRegiserId(regId);
				}
			}
		}.execute(null, null, null);
	}

	public void storeRegistrationId(String regId) {
		final SharedPreferences prefs = context.getSharedPreferences(
				com.pa.common.Config.APP_PREFERENCES, Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
	}

    private void sendRegistrationIdToBackend(String regId) {
//        Intercom.client().setupGCM(regId, R.drawable.ic_launcher);
    }
	
	
	public interface onReceieveRegisterId{
		public void onReceiveRegiserId(String id);
	}
	
	onReceieveRegisterId registeridListener;
	
	public void setReceiveRegisterIdListener(onReceieveRegisterId listener){
		registeridListener=listener;
	}
}
