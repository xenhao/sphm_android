package com.pa.promo;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.pa.common.Config;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.landing.ActivityLanding;
import com.coolfindservices.androidconsumer.R;

public class FragmentInputPromoCode extends MyFragment implements
		OnClickListener, Config {
	OnFragmentChangeListener listener;

	public FragmentInputPromoCode(){
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_refer_to_friend, null);
		v.findViewById(R.id.btnMenu).setOnClickListener((OnClickListener) this);
//		v.findViewById(R.id.share_code).setOnClickListener(this);

		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			if (activity instanceof OnFragmentChangeListener) {
				listener = (OnFragmentChangeListener) activity;
			} else {
				throw new ClassCastException(activity.toString()
						+ " must implemenet OnFragmentChangeListener");

			}
		} catch (Exception e) {
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnMenu:
			ActivityLanding parent = (ActivityLanding) getActivity();
			parent.menuClick();

			break;
			
//		case R.id.share_code:
//			simpleToast("Coming Soon");
//			break;
		}
	}

	

}
