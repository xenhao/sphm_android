package com.coolfindservices.android;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;

import com.google.ads.conversiontracking.AdWordsAutomatedUsageReporter;
import com.nanigans.android.sdk.NanigansEventManager;
import com.pa.common.Config;
import com.pa.common.Encryptor;
import com.pa.common.MyActivity;
import com.pa.common.Tracer;
import com.pa.landing.ActivityLanding;
import com.pa.splash.ActivityLogin;
import com.coolfindservices.androidconsumer.R;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.identity.Registration;

public class SplashActivity extends MyActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//        AppsFlyerLib.sendTracking(getApplicationContext());

        NanigansEventManager.getInstance().setDebug(!"2".equals(pref.getPref(Config.SERVER)));
        NanigansEventManager.getInstance().onApplicationCreate(getApplicationContext(),
                getResources().getString(R.string.facebook_app_id), Config.NANIGANS_APP_ID);
        NanigansEventManager.getInstance().trackAppLaunch();

		String android_id = Secure.getString(this.getContentResolver(),
                Secure.ANDROID_ID); 
		Tracer.d(android_id);
		try {
			if (getIntent().getExtras().getBoolean("exit", false)) {
				this.finish();
				moveTaskToBack(true);
				return;
			}
		} catch (Exception e) {
		}
//		if (pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN).length() > 0) {
//			startActivity(new Intent(this, ActivityLanding.class));
//		} else {
//			startActivity(new Intent(this, ActivityLogin.class));
//
//		}
		
//		RequestParams params=new RequestParams();
//		params.add("type","android");
		
//		PARestClient.get(pref.getPref(Config.SERVER),
//				Config.API_CEK_APP_VERSION, params,
//				new AsyncHttpResponseHandler() {
//					@Override
//					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
//						// TODO Auto-generated method stub
//						super.onSuccess(arg0, arg1, arg2);
//
//						ParserAppVersion parser = new ParserAppVersion(
//								new String(arg2));
//						AppVersion appV = parser.getData();
//						
//						try {
//							int version=getPackageManager()
//									.getPackageInfo(getPackageName(), 0).versionCode;
//							if (Integer.parseInt(appV.customer_version) > version) {
////								AlertDialog.Builder builder=new AlertDialog.Builder(getApplicationContext());
////							
////								builder.setTitle("Warning");
////								builder.setMessage("There's latest version avaiable. Please update to continue using app.");
////								builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
////									
////									@Override
////									public void onClick(DialogInterface dialog, int which) {
////										// TODO Auto-generated method stub
////										if("0".equals(pref.getPref(Config.SERVER))){
////											openUrl("http://bit.ly/pa_merc_staging");
////										}else{
////											openUrl("http://bit.ly/pa_merc");
////										}
////										
////									}
////								});
//								
//								simpleToast("There's a new version available.");
//								
//							}
//							
//						} catch (NameNotFoundException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//					
//					
//					}
//					
//					
//					@Override
//					public void onStart() {
//						// TODO Auto-generated method stub
//						super.onStart();
//					loadingInternetDialog.show();
//					}
//					
//					@Override
//					public void onFinish() {
//						// TODO Auto-generated method stub
//						super.onFinish();
//						loadingInternetDialog.dismiss();
//					}
//
//					
//				});

		{
			if (pref.getPref(
					Config.PREF_ACTIVE_SESSION_TOKEN)
					.length() > 0) {
                Log.d("Intercom", "Login as "+pref.getPref(Config.PREF_USERNAME));
                Intercom.client().registerIdentifiedUser(new Registration().withUserId(pref.getPref(Config.PREF_USERNAME)));
//				Intercom.client().openGCMMessage(getIntent().getData());
				Intercom.client().openGcmMessage();
				NanigansEventManager.getInstance().setUserId(Encryptor.md5(pref.getPref(Config.PREF_USERNAME)));
				startActivity(new Intent(
                        SplashActivity.this,
                        ActivityLanding.class));
			} else {
                Intercom.client().registerUnidentifiedUser();
				startActivity(new Intent(
						SplashActivity.this,
						ActivityLogin.class));

			}
		}


		RegisterActivity register=new RegisterActivity(this);
		register.btnRegisOnClick();
		}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

        AdWordsAutomatedUsageReporter.enableAutomatedUsageReporting(getApplicationContext(), Config.ADWORDS_ID);
		try {
			if (this.getIntent().getBooleanExtra("exit", false)) {
				this.finish();
				moveTaskToBack(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub

		super.onNewIntent(intent);
		try {

			Log.i("sale", "new intent");
			if (intent.getExtras().getBoolean("exit", false)) {
				this.finish();
				moveTaskToBack(true);
			}
		} catch (Exception e) {
		}
		this.setIntent(intent);
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NanigansEventManager.getInstance().onDestroy();
    }
}
