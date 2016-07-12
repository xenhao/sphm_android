package com.pa.job;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.merchantlist.FragmentMerchantList;
import com.coolfindservices.androidconsumer.R;

public class FragmentUserPostAJob extends MyFragment implements OnClickListener {
	OnFragmentChangeListener listener;

	public FragmentUserPostAJob(){
		
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_post_a_job, null);
		v.findViewById(R.id.openBid).setOnClickListener(this);
		v.findViewById(R.id.privateBid).setOnClickListener(this);
		v.findViewById(R.id.btnBack).setOnClickListener(this);

		return v;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.openBid:
			listener.doFragmentChange(new FragmentPostOpenBid(), true, "Open Bid");
			break;
		case R.id.privateBid:
			//simpleToast("soon");
			listener.doFragmentChange(new FragmentMerchantList(" "," "), true, "Browse Merchant");
			break;
		case R.id.btnBack:
			getActivity().getSupportFragmentManager().popBackStackImmediate();
			break;
		}
	}
}
