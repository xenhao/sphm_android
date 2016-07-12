package com.coolfindservices.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		try{
		RegisterActivity register=new RegisterActivity(context);
		register.storeRegistrationId("");
		register.btnRegisOnClick();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
