package com.pa.splash;

import android.os.Bundle;
import android.view.KeyEvent;

import com.facebook.appevents.AppEventsLogger;
import com.pa.common.GlobalVar;
import com.pa.common.MyActivity;
import com.coolfindservices.androidconsumer.R;

public class ActivityLogin extends MyActivity {
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if(GlobalVar.isGuest){
				this.finish();
			}else if (!back(R.id.container)) {
				displayExitDialog();
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.activity_login);
			if(GlobalVar.isRegister){
				replaceFragment(R.id.container, new FragmentLogin(true));
				GlobalVar.isRegister=false;
			}else{
			replaceFragment(R.id.container, new FragmentLogin());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        AppEventsLogger.activateApp(this);
	}

}
