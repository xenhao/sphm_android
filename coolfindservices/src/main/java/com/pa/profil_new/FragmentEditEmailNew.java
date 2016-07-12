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

public class FragmentEditEmailNew extends MyFragment implements Config {
	EditText currentEmail;

	String f_email;

	OnFragmentChangeListener listener;

	public FragmentEditEmailNew(){
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_new_change_email, null);

		currentEmail = (EditText) v.findViewById(R.id.currentMail);

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
		params.add("cs_email", f_email);
		params.add("usertype","customer");
		
//		09-18 10:39:55.571: D/System.out.println(22303): {"status":"failed",
//			"reason":["The co name field is required.",
//			          "The co registration number field is required.",
//			          "The co business description field is required.",
//			          "The co state field is required.",
//			          "The co city field is required.",
//			          "The co postal code field is required.",
//			          "The co address 1 field is required.",
//			          "The co main business number field is required.","The co main email field is required.","The co contact person name field is required.","The co contact person number field is required.","The co contact person email field is required.","The co business hour start field is required.","The co business hour end field is required.","The co business day field is required.","The co service id cache field is required."]}
//		
//		params.add("co_name",GlobalVar.USER.co_name);
//		params.add("co_registration_number", GlobalVar.USER.co_registration_number);
//		params.add("co_business_description",GlobalVar.USER.co_business_description);
//		params.add("co_state", GlobalVar.USER.co_state);
//		params.add("co_city",GlobalVar.USER.co_city);
//		params.add("co_postal_code",GlobalVar.USER.co_postal_code);
//		params.add("co_address_1",GlobalVar.USER.co_address_1);

		loadingInternetDialog.show();
		PARestClient.post(pref.getPref(Config.SERVER),API_EDIT_USER, params,new AsyncHttpResponseHandler(){
			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
				// TODO Auto-generated method stub
				super.onFailure(statusCode, error, content);
				loadingInternetDialog.dismiss();
				if(Tracer.isDebugMode)showLocalWebDialog(content);
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
					JSONObject response=new JSONObject(content);

					status = response.getString("status");
					if ("success".equals(status)) {
						simpleToast("Successfully change email");
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
		f_email = currentEmail.getText().toString();
		if (!formCheckString(f_email, "current password")) {

		} else {
			flag = true;
		}

		return flag;
	}
}
