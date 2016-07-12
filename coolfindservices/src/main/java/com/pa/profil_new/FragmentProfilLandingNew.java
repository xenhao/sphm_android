package com.pa.profil_new;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.landing.ActivityLanding;
import com.coolfindservices.androidconsumer.R;

public class FragmentProfilLandingNew extends MyFragment {
	OnFragmentChangeListener listener;
	
	public FragmentProfilLandingNew(){
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_new_profil_landing, null);

		v.findViewById(R.id.accountSeting).setOnClickListener(fragmentListener);
		v.findViewById(R.id.profileSettings).setOnClickListener(
				fragmentListener);
		v.findViewById(R.id.btnMenu).setOnClickListener(fragmentListener);

		return v;

	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		if(activity instanceof OnFragmentChangeListener){
			listener=(OnFragmentChangeListener)activity;
		}else{
		}
		
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		analytic.trackScreen("Settings");
	}

	OnClickListener fragmentListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.accountSeting:
				listener.doFragmentChange(new FragmentAccountSettingNew(), true, "Account Settings");
				break;
			
			case R.id.profileSettings:
				listener.doFragmentChange(new FragmentProfilSettingNew(), true, "Profile Settings");

				break;
			case R.id.btnMenu:
				ActivityLanding parent = (ActivityLanding) getActivity();
				parent.menuClick();

				break;
	
				
			}
		}
	};
}
