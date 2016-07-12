package com.pa.bid;

import android.os.Bundle;
import android.view.KeyEvent;

import com.pa.common.MyActivity;
import com.coolfindservices.androidconsumer.R;

public class ActivityBid extends MyActivity {
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (!back(R.id.container)) {
				//displayExitDialog();
				this.finish();
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register);
		//replaceFragment(R.id.container, new FragmentBidSubmit());
	
	}

}
