package com.pa.job;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.Config;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.parser.ParserBasicResult;
import com.pa.parser.ParserUser;
import com.pa.pojo.User;
import com.pa.splash.ActivityRegister;
import com.coolfindservices.androidconsumer.R;

public class FragmentGuestJob extends MyFragment implements OnClickListener {
	EditText username, password;
	OnFragmentChangeListener listener;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.fragment_guest_post_a_job, null);
		v.findViewById(R.id.btnRegister).setOnClickListener(this);
		v.findViewById(R.id.btnLogin).setOnClickListener(this);
		v.findViewById(R.id.forgotPass).setOnClickListener(this);
		username = (EditText) v.findViewById(R.id.username);
		password = (EditText) v.findViewById(R.id.password);
		return v;
	}
	
	public FragmentGuestJob(){
		
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnRegister:
			startActivity(new Intent(getActivity(), ActivityRegister.class));
			break;
		case R.id.btnLogin:
			// startActivity(new Intent(getActivity(),ActivityLanding.class));
			doLogin();
			break;
		case R.id.forgotPass:
			simpleToast("Coming soon");
			break;
			
		}
	}
	
	
	private void doLogin() {
		// TODO Auto-generated method stub
		if(isValidLogin()){
			loadingInternetDialog.show();
			
			RequestParams params=new RequestParams();
			params.add("username", formUsername);
			params.add("password", formPassword);
			
			
			
			AsyncHttpClient post=new AsyncHttpClient();
			PARestClient.post(pref.getPref(Config.SERVER),Config.API_LOGIN,params,new AsyncHttpResponseHandler(){
				
				@Override
				public void onSuccess(int statusCode, String result) {
					// TODO Auto-generated method stub
					super.onSuccess(statusCode, result);

					// TODO Auto-generated method stub
					ParserUser parser=new ParserUser(result);
					if(isStatusSuccess(parser.getStatus())){
						User user = parser.getArr().get(0);

						if ("customer".equals(parser.getType())) {
							pref.savePref(Config.PREF_USERNAME, user.name);
							pref.savePref(Config.PREF_ACTIVE_SESSION_TOKEN,
									user.activeToken);

							listener.doFragmentChange(new FragmentUserPostAJob(), false, "Post A Job");
							
						} else {
							simpleToast("This isn't a customer account. Please recheck your account");
						}

					}else{
						ParserBasicResult pbr=new ParserBasicResult(result);
						simpleToast(pbr.getReason());
					}
					loadingInternetDialog.dismiss();
				
				}
				
				@Override
				public void onFailure(Throwable error, String content) {
					// TODO Auto-generated method stub
					super.onFailure(error, content);
					loadingInternetDialog.dismiss();
					
				}
			});
			
		}
	}

	String formUsername, formPassword;
	boolean isValidLogin() {
		boolean flag = false;

		formUsername = username.getText().toString();
		formPassword = password.getText().toString();

		if (!formCheckString(formUsername, "username")) {
			flag = false;
		} else if (!formCheckString(formPassword, "password")) {
			flag = false;
		} else {
			flag = true;
		}

		return flag;
	}

}
