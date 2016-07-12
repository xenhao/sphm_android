package com.pa.profil_new;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.Config;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.common.Tracer;
import com.coolfindservices.androidconsumer.R;

public class FragmentEditPasswordNew extends MyFragment implements Config {
	EditText currentPass, newPass, newPassConf;

	String f_old_password, f_password, f_password_confirmation;

	OnFragmentChangeListener listener;

	
	public FragmentEditPasswordNew(){
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_new_change_password, null);

		currentPass = (EditText) v.findViewById(R.id.currentPass);
		newPass = (EditText) v.findViewById(R.id.newPass);
		newPassConf = (EditText) v.findViewById(R.id.newPassConf);

		v.findViewById(R.id.btnBack).setOnClickListener(fragmentListener);
		v.findViewById(R.id.btnCancel2).setOnClickListener(fragmentListener);
		v.findViewById(R.id.btnNext2).setOnClickListener(fragmentListener);

		return v;

	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		if (activity instanceof OnFragmentChangeListener) {
			listener = (OnFragmentChangeListener) activity;
		} else {
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		analytic.trackScreen("Settings - Change Password");
	}

	OnClickListener fragmentListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnCancel2:
			case R.id.btnBack:
				getActivity().getSupportFragmentManager().popBackStack();
				break;

			case R.id.btnNext2:
				if (isValid()) {
					doSubmit();
				}
				break;
			}
		}
	};

	private void doSubmit() {
		// TODO Auto-generated method stub
		//AsyncHttpClient PAPassClient = new AsyncHttpClient();

		RequestParams params = new RequestParams();
		params.add("session_username", pref.getPref(PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(PREF_ACTIVE_SESSION_TOKEN));
		params.add("old_password", f_old_password);
		params.add("password", f_password);
		params.add("password_confirmation", f_password_confirmation);

		loadingInternetDialog.show();
		PARestClient.post(pref.getPref(Config.SERVER),API_CHANGE_USER_PASSWORD, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, error, content);
						loadingInternetDialog.dismiss();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, content);
						Tracer.d(content);

						loadingInternetDialog.dismiss();
						String status, reason;
						try {
							JSONObject response = new JSONObject(content);

							status = response.getString("status");
							if ("success".equals(status)) {
								simpleToast("Successfully change password");
								getActivity().getSupportFragmentManager()
										.popBackStackImmediate();

							} else {
								reason = response.getString("reason");

								simpleToast(reason.replace("[", "").replace(
										"]", ""));
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

	}

	private boolean isValid() {
		boolean flag = false;
		f_old_password = currentPass.getText().toString();
		f_password = newPass.getText().toString();
		f_password_confirmation = newPassConf.getText().toString();
		if (!formCheckString(f_old_password, "current password")) {

		} else if (!formCheckString(f_password, "new password")) {

		} else if (!formCheckString(f_password_confirmation,
				"new password confirmation")) {

		} else if (!f_password.equals(f_password_confirmation)) {
			simpleToast("Password and password confirmation is different");
		} else {
			flag = true;
		}

		return flag;
	}
}
