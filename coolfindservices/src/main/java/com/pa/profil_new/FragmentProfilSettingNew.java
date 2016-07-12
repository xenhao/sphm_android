package com.pa.profil_new;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.Config;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.common.Tracer;
import com.pa.parser.ParserUserNew;
import com.pa.pojo.User;
import com.coolfindservices.androidconsumer.R;

public class FragmentProfilSettingNew extends MyFragment {
	OnFragmentChangeListener listener;
	User user;

	TextView txtAddress, txtCountry, txtState, txtCity, txtPostalCode,
	txtConsumerName,
	txtHomeNumber, txtMobileNumber;

	Handler handler;

	
	public FragmentProfilSettingNew(){
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_new_profil_setting, null);
		txtConsumerName=(TextView)v.findViewById(R.id.consumer_name);
		txtAddress = (TextView) v.findViewById(R.id.address);
		txtCity = (TextView) v.findViewById(R.id.city);
		txtCountry = (TextView) v.findViewById(R.id.country);
		txtPostalCode = (TextView) v.findViewById(R.id.postal_code);
		txtState = (TextView) v.findViewById(R.id.state);
		txtHomeNumber = (TextView) v.findViewById(R.id.home_number);
		txtMobileNumber = (TextView) v.findViewById(R.id.mobile_number);

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

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					showSpinnerSelection(country, eCountry, "Select country");

					break;
				case 1:
					showSpinnerSelection(state, eState, "Select state");
					break;
				case 2:
					showSpinnerSelection(city, eCity, "Select city");

					break;

				}

			}

		};

		getData();
		
		analytic.trackScreen("Settings - Profile Settings");
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
				showEditDialog();
				break;

			case R.id.city:
				// showSpinnerSelection(city, step1_co_city, "Select state");
				getCityWithLongState((String) eState.getText().toString(), handler);
				break;

			case R.id.state:
				// showSpinnerSelection(country, step1_co_state,
				// "Select country");
				getState(eCountry.getText().toString(), handler);

				break;
			case R.id.country:
				// showSpinnerSelection(country, step1_co_state,
				// "Select country");
				getCountry(handler);

				break;
			}
		}
	};

	void getData() {
		loadingInternetDialog.show();
		//AsyncHttpClient client = new AsyncHttpClient();

		RequestParams params = new RequestParams();
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));

		PARestClient.get(pref.getPref(Config.SERVER),Config.API_GET_USER_DATA, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, error, content);
						loadingInternetDialog.dismiss();
						simpleToast("Error:" + content);
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, content);
						loadingInternetDialog.dismiss();
						ParserUserNew parser = new ParserUserNew(content);
						user = parser.getItem();
						drawData();

					}
				});

	}

	void drawData() {
		txtConsumerName.setText(user.cs_name);
		txtAddress.setText(user.cs_address);
		txtCity.setText(user.cs_city);
		txtCountry.setText(user.cs_country);
		txtPostalCode.setText(user.cs_postal_code);
		txtState.setText(user.cs_state);
		txtHomeNumber.setText(user.cs_home_number);
		txtMobileNumber.setText(user.cs_mobile_number);

	};

	EditText eAddress, ePostalCode, eHomeNumber, eMobileNumber;

	TextView eCountry, eState, eCity,eConsumerName;
	TextView ePrefix;
	Dialog dialogEdit;

	void showEditDialog() {
		analytic.trackScreen("Settings - Edit Profile");
		View v = inflater.inflate(R.layout.fragment_new_profil_setting_edit,
				null);
		eConsumerName=(TextView)v.findViewById(R.id.service_name);
		eAddress = (EditText) v.findViewById(R.id.address);
		eCity = (TextView) v.findViewById(R.id.city);
		eCountry = (TextView) v.findViewById(R.id.country);
		ePostalCode = (EditText) v.findViewById(R.id.postal_code);
		eState = (TextView) v.findViewById(R.id.state);
		eHomeNumber = (EditText) v.findViewById(R.id.home_number);
		eMobileNumber = (EditText) v.findViewById(R.id.mobile_number);
		ePrefix=(TextView)v.findViewById(R.id.prefix);
		
		eCity.setOnClickListener(fragmentListener);
		eState.setOnClickListener(fragmentListener);
		eCountry.setOnClickListener(fragmentListener);

		eState.setTag(R.id.co_city, eCity);
		//eState.setTag(R.id.city,user.);
		eCountry.setTag(R.id.co_city, eCity);
		eCountry.setTag(R.id.co_state, eState);

		OnEditorActionListener onDone=new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if(actionId==EditorInfo.IME_ACTION_DONE){
					hideKeyboard((EditText) v);
					return true;
				}
				return false;
			}
		};
		
		eAddress.setOnEditorActionListener(onDone);
		eHomeNumber.setOnEditorActionListener(onDone);
		eMobileNumber.setOnEditorActionListener(onDone);
		ePostalCode.setOnEditorActionListener(onDone);
		
		
		try {
			eAddress.setText(user.cs_address);
			eCity.setText(user.cs_city);
			eCountry.setText(user.cs_country);
			ePostalCode.setText(user.cs_postal_code);
			eState.setText(user.cs_state);
			eHomeNumber.setText(user.cs_home_number);
			
			eConsumerName.setText(user.cs_name);

			eMobileNumber.setText(user.cs_mobile_number.substring(3));
			String str_prefix=user.cs_mobile_number.substring(0,3);
			ePrefix.setText(str_prefix);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		ePrefix.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showSpinnerSelection(country_prefix, (TextView)v, "Select prefix");
			}
		});

		v.findViewById(R.id.btnBack).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogEdit.hide();
			}
		});
		v.findViewById(R.id.btnCancel2).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialogEdit.hide();
					}
				});

		v.findViewById(R.id.btnNext2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isValid()) {
					doEdit();
				}
			}
		});

		dialogEdit = new Dialog(getActivity(), R.style.PauseDialog);
		dialogEdit.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialogEdit.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogEdit.setContentView(v);
		dialogEdit.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		dialogEdit.show();

	}

	void doEdit() {
		//AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		params.add("usertype", "customer");
		params.add("cs_address", fAddress);
		params.add("cs_city", fCity);
		params.add("cs_country", fCountry);
		params.add("cs_postal_code", fPostalCode);
		params.add("cs_state", fState);
		params.add("cs_home_number", fHomeNumber);
		params.add("cs_mobile_number",ePrefix.getText().toString()+ fMobileNumber);

		PARestClient.post(pref.getPref(Config.SERVER),Config.API_EDIT_USER, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, error, content);
						simpleToast(content);

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
								simpleToast("Successfully edit data");
								dialogEdit.hide();
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

	String fAddress, fCity, fCountry, fPostalCode, fState, fMobileNumber,
			fHomeNumber;

	boolean isValid() {
		boolean flag = false;

		fAddress = eAddress.getText().toString();
		fCity = eCity.getText().toString();
		fCountry = eCountry.getText().toString();
		fPostalCode = ePostalCode.getText().toString();
		fState = eState.getText().toString();
		fMobileNumber = eMobileNumber.getText().toString();
		fHomeNumber = eHomeNumber.getText().toString();

		if (!formCheckString(fAddress, "address")) {

		} else if (!formCheckString(fCity, "city")) {

		} else if (!formCheckString(fCountry, "country")) {

		} else if (!formCheckString(fPostalCode, "postal code")) {

		} else if (!formCheckString(fState, "state")) {

		}

		else if (!formCheckString(fHomeNumber, "home number")) {

		} else if (!formCheckString(fMobileNumber, "mobile number")) {

		} else {
			flag = true;
		}

		return flag;
	}
}
