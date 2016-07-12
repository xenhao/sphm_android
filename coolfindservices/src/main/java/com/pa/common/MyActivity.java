package com.pa.common;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Toast;

import com.coolfindservices.android.SplashActivity;
import com.coolfindservices.androidconsumer.R;

import io.intercom.android.sdk.Intercom;

public class MyActivity extends FragmentActivity {
	protected FragmentManager fm;
	protected LayoutInflater inflater;
	protected AppPreferences pref;
	protected ProgressDialog loadingDialog;
	protected ProgressDialog loadingInternetDialog;

	protected Analytic analytic;

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	
		startActivity(new Intent(this,SplashActivity.class));
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Tracer.d("ON RESUME");
	}
	protected void openUrl(String url) {
		// String url = "http://www.example.com";
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}

	protected void displayExitFormDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Warning");
		builder.setMessage("Are you sure want to leave this form? Any data has been input will be lost");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				//finish();
				fm.popBackStackImmediate();
			}
		});

		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.show();
	}

	
	public String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}
	
	private String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}



	protected void setTrack(String page) {
		//Analytic a = new Analytic(this);
		//a.execute(page);
	}

	protected void simpleToast(String s){
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		pref = new AppPreferences(this);
		Tracer.d("MyActivity");
		if (!isTaskRoot()) {
		    final Intent intent = getIntent();
		    final String intentAction = intent.getAction();
		    if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) &&
		            intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
		        finish();
		    }
		}
    	Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				// TODO Auto-generated method stub
				System.out.println(ex.toString());
				System.exit(2);
			}
		});
		fm=getSupportFragmentManager();
		inflater=this.getLayoutInflater();
		
		loadingDialog = new ProgressDialog(this);
		loadingDialog.setMessage("Please wait...");
		loadingDialog.setTitle("Loading");

		loadingInternetDialog = new ProgressDialog(this);
		loadingInternetDialog.setMessage("This feature using data from internet");
		loadingInternetDialog.setTitle("Loading");
		loadingInternetDialog.setCancelable(true);

		// Configure server
		String configServer = getResources().getString(R.string.server);
		if ("".equals(pref.getPref(Config.SERVER)) || !"1".equals(configServer)) {
			pref.savePref(Config.SERVER, configServer);
		}
		
		analytic=new Analytic(this);


	}
	protected String getSN(){
		String s="";
		
		s=Build.SERIAL;
		
		return s;
	}
	
	public void replaceFragment(int id,Fragment f){
		if(fm!=null){
			FragmentTransaction ft 	= fm.beginTransaction();
			ft.replace(id, f);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.addToBackStack(null);
			ft.commit();
			
		}
	}
	public void replaceFragment(int id,Fragment f,boolean history){
		if(fm!=null){
			FragmentTransaction ft 	= fm.beginTransaction();
			ft.replace(id, f);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			//ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
			if(history)ft.addToBackStack(null);
			ft.commit();
			
		}
	}
	public void replaceFragment(Fragment f){
		if(fm!=null){
			FragmentTransaction ft 	= fm.beginTransaction();
			//ft.replace(R.id.FragmentContainer, f);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.addToBackStack(null);
			ft.commit();
			
		}
	}
	public void replaceFragment(Fragment f,boolean history){
		if(fm!=null){
			FragmentTransaction ft 	= fm.beginTransaction();
			//ft.replace(R.id.FragmentContainer, f);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			if(history)ft.addToBackStack(null);
			ft.commit();
			
		}
	}
	protected void addFragment(int id,Fragment f){
		if(fm!=null){
			FragmentTransaction ft 	= fm.beginTransaction();
			ft.add(id, f);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			//ft.addToBackStack(null);
			ft.commit();
			
		}
	}
	
	protected void displayExitDialog(){

		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		//builder.setIcon(getResources().getDrawable(R.drawable.icon));
		
		AlertDialog alertDialog=builder.create();
		alertDialog.setTitle("Exit");
		alertDialog.setMessage("Are you sure?");
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"YES",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent i=new Intent(getApplicationContext(),SplashActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.putExtra("exit", true);
				startActivity(i);
			}
		});
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"NO",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			} 
		});
		alertDialog.show();
		
	
	}
	public void logout(){
		pref.savePref(Config.PREF_USERNAME, "");
		pref.savePref(Config.PREF_ACTIVE_SESSION_TOKEN, "");
        Intercom.client().reset();
	}
	public boolean back(int id){
		try{
			if(fm.findFragmentById(id)!=null && fm.getBackStackEntryCount()>1){
				fm.popBackStackImmediate();
				return true;
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	protected boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ((this.getPackageName()+".WatcherService")
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Tracer.d("ON PAUSE");
		
	}
	


	
	
	
	
	
	
}
