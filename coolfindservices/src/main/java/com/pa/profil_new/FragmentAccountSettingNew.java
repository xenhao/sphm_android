package com.pa.profil_new;

import org.apache.http.Header;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.Config;
import com.pa.common.GlobalVar;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.parser.ParserUserNew;
import com.pa.pojo.User;
import com.coolfindservices.androidconsumer.R;

public class FragmentAccountSettingNew extends MyFragment {
	OnFragmentChangeListener listener;
	User user;
	
	TextView userName,password,email;
	
	public FragmentAccountSettingNew(){
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_new_account_setting, null);
		userName=(TextView)v.findViewById(R.id.user_name);
		password=(TextView)v.findViewById(R.id.password);
		email=(TextView)v.findViewById(R.id.email);
		
		//email.setOnClickListener(fragmentListener);
		password.setOnClickListener(fragmentListener);
		
		v.findViewById(R.id.btnBack).setOnClickListener(fragmentListener);
		
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
		getData();
		analytic.trackScreen("Settings - Account Settings");
	}

	OnClickListener fragmentListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnBack:
				getActivity().getSupportFragmentManager().popBackStackImmediate();
				break;
			case R.id.email:
				listener.doFragmentChange(new FragmentEditEmailNew(),true , "Edit Email");
				
				break;
			case R.id.password:
				listener.doFragmentChange(new FragmentEditPasswordNew(),true , "Edit Password");
				break;
			}
		}
	};
	
	void getData(){
		loadingInternetDialog.show();
		//AsyncHttpClient client=new AsyncHttpClient();
		
		RequestParams params=new RequestParams();
		params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		params.add("session_username",pref.getPref(Config.PREF_USERNAME));
		
		
		PARestClient.get(pref.getPref(Config.SERVER),Config.API_GET_USER_DATA, params,new AsyncHttpResponseHandler(){
			
			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
				// TODO Auto-generated method stub
				super.onFailure(statusCode, error, content);
				loadingInternetDialog.dismiss();
				simpleToast("Error:"+content);
			}
		
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String content) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, content);
				loadingInternetDialog.dismiss();
				ParserUserNew parser=new ParserUserNew(content);
				user=parser.getItem();
				GlobalVar.USER=user;
				drawData();
			}
		});
		
		
	}
	
	void drawData(){
		userName.setText(pref.getPref(Config.PREF_USERNAME));
		email.setText(user.cs_email);
		
	}
}
