package com.pa.splash;

import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.Config;
import com.pa.common.GlobalVar;
import com.pa.common.PARestClient;
import com.pa.common.SessionLoginFragment;
import com.pa.common.Tracer;
import com.pa.landing.ActivityLanding;
import com.pa.parser.ParserActivationStatus;
import com.pa.parser.ParserBasicResult;
import com.pa.parser.ParserError;
import com.pa.parser.ParserUser;
import com.pa.pojo.ActivationStatus;
import com.pa.pojo.ParentServiceCategory;
import com.pa.pojo.User;
import com.coolfindservices.androidconsumer.R;

public class FragmentRegister extends SessionLoginFragment implements Config,
		OnClickListener {
	LinearLayout switcher;
	View step0, step1, step3;
	// View step2,step2b, step2c;

	int indexOfDisplayedStep = 0;
	ArrayList<ParentServiceCategory> arrServiceCategory;
	LinearLayout categoryWrapper;

	ArrayList<String> selectedCat;
	ArrayList<String> forNextCat;

	EditText e_username, e_password, e_password_confirmation, e_co_name,
			e_co_last_name;
	EditText e_co_postal_code, e_co_main_business_number, e_co_main_email,
			e_co_address, e_referral_code, e_co_contact_person_number;
	// step1
	LinearLayout branchWrapper;
	TextView step1_co_city, step1_co_state, step1_co_country;
	LinearLayout hidden;

	TextView tnc;
	Handler handler;

	TextView prefix;
	
	
	int fb_type;
	final int TYPE_REGISTER = 0;
	final int TYPE_LOGIN = 1;

	
	@Override
	protected void doneLoginFB(String email, String name, String fbid,
			String last_name) {
		// TODO Auto-generated method stub
		super.doneLoginFB(email, name, fbid, last_name);
		loadingInternetDialog.dismiss();
		pref.savePref(Config.APP_USER_FB_ID, fbid);
		Tracer.d(email+name+fbid+last_name);
		
		GlobalVar.email=email;
		GlobalVar.first_name=name;
		GlobalVar.last_name=last_name;
		GlobalVar.fbid=fbid;
		
		if (fb_type == TYPE_REGISTER) {
			// Toast.makeText(getActivity(), email, Toast.LENGTH_SHORT).show();
			// doRegisterViaFB(email, name, fbid, dob);
			//doLoginViaFB(email, name, fbid, dob);
			startActivity(new Intent(getActivity(), ActivityRegister.class));
			GlobalVar.isFB=true;

		} else if (fb_type == TYPE_LOGIN) {
			Tracer.d("Do login via FB");
			//doLoginViaFB(email, name, fbid, dob);
			
			formUsername=email;
			formPassword=fbid;
			GlobalVar.isFB=true;
			doLogin();
		}
		
	}

	String formUsername,formPassword,f_phone;
	User user;
	void doLogin(){

		// TODO Auto-generated method stub

		loadingInternetDialog.show();
		RequestParams params = new RequestParams();
		params.add("username", formUsername);
		params.add("password", formPassword);
		if (GlobalVar.isFB) {
			params.add("fbid", GlobalVar.fbid);
			GlobalVar.isFB = false;
		}



		AsyncHttpClient post = new AsyncHttpClient();
		PARestClient.post(pref.getPref(Config.SERVER),Config.API_LOGIN, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, String result) {
				// TODO Auto-generated method stub
				ParserUser parser = new ParserUser(result);
				if (isStatusSuccess(parser.getStatus())) {
					user = parser.getUser();// getArr().get(0);

					if ("customer".equals(parser.getType())) {
						if (!"N".equals(user.is_active)) {
							pref.savePref(Config.PREF_USERNAME, user.username);
							pref.savePref(Config.PREF_ACTIVE_SESSION_TOKEN,
									user.active_session_token);
							pref.savePref(Config.PREF_USER, "");
							startActivity(new Intent(getActivity(),
									ActivityLanding.class));
						} else {

							f_phone = user.cs_mobile_number;

							showActivationDialog();
						}
					} else {
						simpleToast("This isn't a customer account. Please recheck your account");
					}

				} else {
//					ParserBasicResult pbr = new ParserBasicResult(result);
//					simpleToast(pbr.getReason());
					GlobalVar.isFB = true;
					startActivity(new Intent(getActivity(), ActivityRegister.class));

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

	
	Dialog dialogActivation;
	EditText txtActivate;
	TextView ePrefix,eMobileNumber;
	public void showActivationDialog() {
		analytic.trackScreen("OTP Activation");

		View v = inflater.inflate(R.layout.fragment_activate, null);
		ePrefix = (TextView) v.findViewById(R.id.prefix);
		eMobileNumber = (EditText) v.findViewById(R.id.co_main_business_number);

		eMobileNumber.setText(f_phone.substring(3));
		String str_prefix = f_phone.substring(0, 3);
		ePrefix.setText(str_prefix);

		ePrefix.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showSpinnerSelection(country_prefix, (TextView) v,
						"Select prefix");
			}
		});

		v.findViewById(R.id.btnResendActivate).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (isValidResend()) {
							doResend();
							analytic.trackScreen("Resend Activation Code");

						}
					}
				});

		txtActivate = (EditText) v.findViewById(R.id.activation);
		v.findViewById(R.id.btnBack).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// dialogActivation.dismiss();

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Confirmation");
				builder.setMessage("You will be logged out from Activation Page. To activate your account, you can re-login with your new account and insert your activation code.");
				builder.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								dialogActivation.dismiss();
							}
						});

				builder.show();
			}
		});
		v.findViewById(R.id.btnActivate).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (isValidActivate()) {
							doActivate();
						}
					}
				});

		dialogActivation = new Dialog(getActivity(), R.style.PauseDialog);
		dialogActivation.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialogActivation.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogActivation.setContentView(v);
		dialogActivation.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogActivation.show();

	}

	protected void doResend() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.add("username", formUsername);
		params.add("cs_mobile_number", f_phone);

		PARestClient.post(pref.getPref(Config.SERVER),"user/resend-activation-code", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						loadingInternetDialog.show();
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
						loadingInternetDialog.hide();
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);

					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, arg1, arg2);
						ParserActivationStatus parser = new ParserActivationStatus(
								new String(arg2));
						ActivationStatus status = parser.getData();

						if ("success".equals(status.status)) {
							simpleToast("Success. Activation code has been resent");
						} else {
							simpleToast(TextUtils.join(",",
									(String[]) status.reason));
						}
					}

				});
	}

	protected boolean isValidResend() {
		// TODO Auto-generated method stub
		String prefix, mobilenumber;

		prefix = ePrefix.getText().toString();
		mobilenumber = eMobileNumber.getText().toString();

		if (!formCheckString(prefix, "prefix")) {

		} else if (!formCheckString(prefix, "mobile number")) {

		} else {
			f_phone = prefix + mobilenumber;
			return true;
		}

		return false;
	}

	
	boolean isActivation=false;
	
	protected void doActivate() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.add("mobile_number", f_phone);
		params.add("mobile_token", f_activation);
		PARestClient.post(pref.getPref(Config.SERVER),"user/mobile-token", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						String result = new String(arg2);
						ParserActivationStatus parser = new ParserActivationStatus(
								result);
						try {
							if (isStatusSuccess(parser.getData().status)) {
								if (!isActivation) {
									ActivationStatus status = parser.getData();// getArr().get(0);
									pref.savePref(Config.PREF_USERNAME,
											user.username);
									pref.savePref(
											Config.PREF_ACTIVE_SESSION_TOKEN,
											user.active_session_token);

									startActivity(new Intent(getActivity(),
											ActivityLanding.class));
									dialogActivation.dismiss();
									simpleToast("Success. Account has been activated");
									analytic.trackScreen("Account Activated");

									
								} else {
									formUsername = GlobalVar.a;
									formPassword = GlobalVar.b;
									doLogin();
								}

							} else {
								// ParserBasicResult pbr = new
								// ParserBasicResult(
								// result);
								simpleToast(TextUtils
										.join("", (ArrayList<String>) parser
												.getData().reason));
								analytic.trackScreen("Wrong OTP Token");

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						loadingInternetDialog.dismiss();
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						loadingInternetDialog.show();
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
						loadingInternetDialog.dismiss();
					}

				});
	}

	String f_activation;

	protected boolean isValidActivate() {
		// TODO Auto-generated method stub
		f_activation = txtActivate.getText().toString();
		if (formCheckString(f_activation, "activation code")) {
			return true;
		}

		return false;
	}

	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// getCountry();

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					showSpinnerSelection(country, step1_co_country,
							"Select country");

					break;
				case 1:
					showSpinnerSelection(state, step1_co_state, "Select state");
					break;
				case 2:
					showSpinnerSelection(city, step1_co_city, "Select city");

					break;

				}

			}

		};

	}

	boolean isFB=false;
	@SuppressLint("ValidFragment")
	public FragmentRegister(){
		isFB=GlobalVar.isFB;
		//GlobalVar.isFB=false;
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_register, null);
		if(isFB)v=inflater.inflate(R.layout.fragment_register_fb, null);
		
		switcher = (LinearLayout) v.findViewById(R.id.switcher);
		v.findViewById(R.id.btnBack).setOnClickListener(fragmentListener);

		hidden = (LinearLayout) v.findViewById(R.id.hidden);
		//step1 = v.findViewById(R.id.step1);
		//step0 = v.findViewById(R.id.step0);

		
		
		// step0
		//v.findViewById(R.id.btnNext).setOnClickListener(step0listener);
		//v.findViewById(R.id.btnCancel).setOnClickListener(step0listener);
		// e_username = (EditText) step0.findViewById(R.id.txtUsername);
		e_password = (EditText) v.findViewById(R.id.txtPassword);
		e_password_confirmation = (EditText) v
				.findViewById(R.id.txtPasswordConfirmation);

		v.findViewById(R.id.btnNext1).setOnClickListener(step1listener);
		v.findViewById(R.id.btnCancel1).setOnClickListener(step1listener);
		e_co_address = (EditText) v.findViewById(R.id.txtAddr);

		step1_co_country = (TextView) v.findViewById(R.id.co_country);
		step1_co_city = (TextView) v.findViewById(R.id.co_city);
		step1_co_state = (TextView) v.findViewById(R.id.co_state);
		step1_co_city.setOnClickListener(step1listener);
		step1_co_state.setOnClickListener(step1listener);
		step1_co_country.setOnClickListener(step1listener);

		e_co_name = (EditText) v.findViewById(R.id.co_first_name);
		e_co_last_name = (EditText) v.findViewById(R.id.co_last_name);
		e_co_postal_code = (EditText) v.findViewById(R.id.co_postal_code);
		e_co_main_business_number = (EditText) v
				.findViewById(R.id.co_main_business_number);
		e_co_main_email = (EditText) v.findViewById(R.id.co_main_email);
		// e_co_about=(EditText)step1.findViewById(R.id.)
		// e_co_contact_person_number = (EditText) step1
		// .findViewById(R.id.co_contact_person_number);
		e_referral_code = (EditText) v.findViewById(R.id.referral_number);

		prefix=(TextView)v.findViewById(R.id.prefix);
		tnc=(TextView) v.findViewById(R.id.tnc);
		
		
		if(!isFB)
		v.findViewById(R.id.btn_login_logout).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fb_type=TYPE_LOGIN;
				buttonLoginLogout.performClick();
			}
		});

		
		return v;
	}

	private void changeStep(int step) {

		Tracer.d("Step:" + step + "," + switcher.getChildCount());
		if (step < 0)
			step = 0;
		else if (step >= switcher.getChildCount())
			step = switcher.getChildCount() - 1;

		indexOfDisplayedStep = step;

		// switcher.invalidate();

	}

	void changeNextStep() {
		indexOfDisplayedStep++;
		changeStep(indexOfDisplayedStep);
	}

	void changePrevStep() {
		indexOfDisplayedStep--;
		changeStep(indexOfDisplayedStep);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		
		hasBeenRegistered = false;
		final String link1="Terms of Use";
		final String link2="Privacy Policy";
		String strTnc=getResources().getText(R.string.tnc).toString();
		
		int start1=strTnc.indexOf(link1);
		int end1=start1+link1.length();
		int start2=strTnc.indexOf(link2);
		int end2=start2+link2.length();
		
		
		Spannable span = Spannable.Factory.getInstance().newSpannable(strTnc);   
		span.setSpan(new ClickableSpan() {  
		    @Override
		    public void onClick(View v) {  
		        //Log.d("main", "link clicked");
		        //simpleToast("Tnc");
		        //showTNC(link1,getResources().getString(R.string.link1));
		        showWebDialog("http://services.cool-find.com/terms-of-use", link1);
		    } }, start1, end1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(new ClickableSpan() {  
		    @Override
		    public void onClick(View v) {  
		       // simpleToast("Privacy Policy");
		        //showTNC(link2,getResources().getString(R.string.link1));
		        showWebDialog("http://services.cool-find.com/privacy-policy", link2);

		    } }, start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		tnc.setText(span);
		tnc.setMovementMethod(LinkMovementMethod.getInstance());

		prefix.setOnClickListener(this);
		
		if(isFB){
            if (!TextUtils.isEmpty(GlobalVar.email)) {
                e_co_main_email.setEnabled(false);
                e_co_main_email.setText(GlobalVar.email);
            }
			
			e_co_name.setText(GlobalVar.first_name);
			e_co_last_name.setText(GlobalVar.last_name);
			e_password.setText(GlobalVar.fbid);
			e_password_confirmation.setText(GlobalVar.fbid);
			analytic.trackScreen("Create New Account - FB");
			
		}else{
			analytic.trackScreen("Create New Account");

		}
		
		buttonLoginLogout = new Button(getActivity());// (Button)
		// v.findViewById(R.id.button1);
		buttonLoginLogout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Tracer.d("LOGIN FB");
				// loadingInternetDialog = new ProgressDialog(
				// getActivity());
				// loadingInternetDialog.setMessage("Please wait...");
				// loadingInternetDialog.setTitle("Loading");
				// loadingInternetDialog.show();
				onClickLogin();
			}
		});

		
		prefix.setText("+"+GetCountryZipCode());
		
	}

	android.view.View.OnClickListener fragmentListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnBack:
				// indexOfDisplayedStep = indexOfDisplayedStep - 1 > 0 ?
				// indexOfDisplayedStep - 1
				// : 0;
				// changeStep(indexOfDisplayedStep);
				// if (!hasBeenRegistered) {
				// changePrevStep();
				// } else {
				// getActivity().finish();
				// }

				closeDialog();
				break;

			default:
				break;
			}
		}
	};
	OnClickListener step1listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnCancel1:
				// changePrevStep();
				closeDialog();

				break;

			case R.id.co_city:
				// showSpinnerSelection(city, step1_co_city, "Select state");
				getCity((String) step1_co_state.getTag(R.id.city), handler);
				break;

			case R.id.co_state:
				// showSpinnerSelection(country, step1_co_state,
				// "Select country");
				getState(step1_co_country.getText().toString(), handler);

				break;
			case R.id.co_country:
				// showSpinnerSelection(country, step1_co_state,
				// "Select country");
				getCountry(handler);

				break;

			case R.id.btnNext1:
				// if (isValidStep0()) {
				// if (postCheckUserName != null)
				// postCheckUserName.cancel(true);
				// checkUserNameExist(e_username.getText().toString());
				//
				// }

				if (isValidStep1()) {
					doRegister();
					// checkUserNameExist(e_username.getText().toString());

				}
				break;

			default:
				break;
			}
		}
	};

	boolean isOpen = true;
	OnClickListener step2listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.catParent:
				if (isOpen) {
					TextView t = (TextView) v;
					t.setCompoundDrawablesWithIntrinsicBounds(0, 0,
							R.drawable.icon_arrow_down, 0);
					isOpen = false;
					categoryWrapper.setVisibility(View.GONE);

				} else {
					TextView t = (TextView) v;
					t.setCompoundDrawablesWithIntrinsicBounds(0, 0,
							R.drawable.icon_arrow_up, 0);

					isOpen = true;
					categoryWrapper.setVisibility(View.VISIBLE);

				}
				break;

			case R.id.btnCancel2:
				changePrevStep();

				break;

			}
		}
	};

	OnClickListener step2blistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {

			case R.id.btnCancel2:
				changePrevStep();

				break;
			case R.id.btnNext2:
				changeNextStep();
				Toast.makeText(getActivity(), selectedCat.toString(),
						Toast.LENGTH_SHORT).show();
				break;

			}
		}
	};

	private OnClickListener step0listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnCancel2:
				getActivity().finish();

				break;
			case R.id.btnNext2:
				if (isValidStep0()) {
					if (postCheckUserName != null)
						postCheckUserName.cancelRequests(getActivity(), true);
					checkUserNameExist(e_username.getText().toString());

				}
				break;
			}
		}
	};
	boolean hasNext = false;
	private String f_cs_country;
	private String f_referral_code;
	private String f_cs_last_name;

	protected void createTimePickerDialog(final TextView v) {
		// TODO Auto-generated method stub
		OnTimeSetListener timePickerListener = new OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				String strHourDay, strMinute;
				strHourDay = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
				strMinute = minute < 10 ? "0" + minute : "" + minute;

				v.setText(strHourDay + ":" + strMinute + ":00");
			}
		};

		TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
				timePickerListener, 8, 0, false);

		timePickerDialog.show();

	}

	protected void tryRegister() {
		// TODO Auto-generated method stub
		// if (isValid()) {
		doRegister();
		// }
	}

	private boolean isValid() {
		// TODO Auto-generated method stub
		f_username = e_username.getText().toString();
		f_password = e_password.getText().toString();
		f_password_confirmation = e_password_confirmation.getText().toString();
		f_cs_name = e_co_name.getText().toString();
		f_cs_last_name = e_co_last_name.getText().toString();

		f_cs_city = step1_co_city.getText().toString();
		f_cs_state = step1_co_state.getText().toString();

		f_cs_home_number = e_co_main_business_number.getText().toString();
		f_cs_email = e_co_main_email.getText().toString();

		f_co_mobile_number = e_co_contact_person_number.getText().toString();
		f_referral_code = e_referral_code.getText().toString();
		boolean flag = true;
		if (!formCheckString(f_username, "user name")) {
			flag = false;
		} else if (!formCheckString(f_password, "password")) {
			flag = false;
		} else if (!formCheckString(f_password_confirmation,
				"password confirmation")) {
			flag = false;
		} else if (!formCheckString(f_cs_name, "fist name")) {
			flag = false;
		} else if (!formCheckString(f_cs_last_name, "last name")) {
			flag = false;
		}

		// else
		// if (!formCheckString(f_cs_state, "state")) {
		// flag = false;
		// } else if (!formCheckString(f_cs_city, "city")) {
		// flag = false;
		// } else if (!formCheckString(f_cs_postal_code, "postal code")) {
		// flag = false;
		// } else if (!formCheckString(f_cs_home_number,
		// "main business number")) {
		// flag = false;
		// } else if (!formCheckString(f_cs_email, "main email")) {
		// flag = false;
		// }

		else if (!formCheckString(f_co_mobile_number, "contact person number")) {
			flag = false;
		}
		// else if (selectedCat.size() == 0) {
		// simpleToast("Please fill  your category and sub category");
		// flag = false;
		// }

		return flag;
	}

	private void checkUserNameExist(String username) {
		loadingInternetDialog.show();
		ArrayList<NameValuePair> entity = new ArrayList<NameValuePair>();
		entity.add(new BasicNameValuePair("username", username));

		RequestParams params = new RequestParams();
		params.add("username", username);

		postCheckUserName = new AsyncHttpClient();
		
		String base="";
		if ("0".equals(pref.getPref(Config.SERVER))) {
			base = "http://" + DOMAIN_DEV + "/";

		} else if ("1".equals(pref.getPref(Config.SERVER))) {

			base = "http://" + DOMAIN_STAGING + "/";

		}
		else if ("2".equals(pref.getPref(Config.SERVER))) {

			base = "http://" + DOMAIN_LIVE + "/";

		}		
		postCheckUserName.post(base, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, String result) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, result);

						// TODO Auto-generated method stub
						Tracer.d(result);
						loadingInternetDialog.dismiss();
						// {"status":"success","reason":["username is safe to use"]}

						ParserBasicResult parser = new ParserBasicResult(result);
						if ("success".equals(parser.getStatus())) {
							// changeNextStep();
							doRegister();
						} else {
							simpleToast("Username already exist");
						}

					}

					@Override
					public void onFailure(Throwable error, String content) {
						// TODO Auto-generated method stub
						super.onFailure(error, content);
						loadingInternetDialog.dismiss();
						simpleToast("Network issue.Please try again");

					}
				});
	}

	private boolean isValidStep0() {
		// TODO Auto-generated method stub

		try {
			f_username = e_username.getText().toString();
			f_password = e_password.getText().toString();
			f_password_confirmation = e_password_confirmation.getText()
					.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		boolean flag = true;
		if (!formCheckString(f_username, "user name")) {
			flag = false;
		} else if (!formCheckString(f_password, "password")) {
			flag = false;
		} else if (!formCheckString(f_password_confirmation,
				"password confirmation")) {
			flag = false;
		} else if (!f_password.equals(f_password_confirmation)) {
			flag = false;
			simpleToast("Password confirmation isn't same");
		}

		return flag;
	}

	private void closeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Warning");
		builder.setMessage("Are you sure want to leave this registration form? Any data has been input will be lost");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				getActivity().finish();
			}
		});

		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.show();
	}

	private boolean isValidStep1() {
		// TODO Auto-generated method stub

		try {
			// f_username = e_username.getText().toString();
			f_password = e_password.getText().toString();
			f_password_confirmation = e_password_confirmation.getText()
					.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		f_cs_name = e_co_name.getText().toString();
		f_cs_postal_code = e_co_postal_code.getText().toString();

		f_cs_city = step1_co_city.getText().toString();
		f_cs_state = step1_co_state.getText().toString();

		f_co_mobile_number = e_co_main_business_number.getText().toString();
		f_cs_email = e_co_main_email.getText().toString();

		// f_co_mobile_number = e_co_contact_person_number.getText().toString();
		f_cs_address = e_co_address.getText().toString();
		f_cs_country = step1_co_country.getText().toString();

		boolean flag = true;
		// if (!formCheckString(f_username, "user name")) {
		// flag = false;
		// }
		if (!formCheckStringWithInlineError(e_co_main_email, "email")) {
			flag = false;
		} else if (!isMailValid(f_cs_email)) {
			flag = false;
			e_co_main_email.setError("Email format is not valid");
		} else if (!formCheckStringWithInlineError(e_password, "password")) {
			flag = false;
		} else if (!formCheckStringWithInlineError(e_password_confirmation,
				"password confirmation")) {
			flag = false;
		} else if (!f_password.equals(f_password_confirmation)) {
			flag = false;
			e_password_confirmation.setError("Password confirmation isn't same");
		} else

		if (!formCheckStringWithInlineError(e_co_name, "customer name")) {
			flag = false;
		}
		// else if (!formCheckString(f_cs_address, "address")) {
		// flag = false;
		// }
		// else if (!formCheckString(f_cs_country, "country")) {
		// flag = false;
		// } else if (!formCheckString(f_cs_state, "state")) {
		// flag = false;
		// } else if (!formCheckString(f_cs_city, "city")) {
		// flag = false;
		// } else if (!formCheckString(f_cs_postal_code, "postal code")) {
		// flag = false;
		// }
		else if (!formCheckStringWithInlineError(e_co_main_business_number, "phone number")) {
			flag = false;
		}

		// else if (!formCheckString(f_co_mobile_number, "mobile number")) {
		// flag = false;
		// }

		return flag;
	}

	ArrayList<String> selectedLanguage;

	ArrayList<String> selectedWorkingDays;
	private String f_username;
	private String f_password;
	private String f_password_confirmation;
	private String f_cs_name;
	private String f_cs_state;
	private String f_cs_city;
	private String f_cs_postal_code;
	private String f_cs_home_number;
	private String f_cs_email;
	private String f_co_mobile_number;
	private String f_cs_address;
	protected boolean hasBeenRegistered = false;
	private AsyncHttpClient postCheckUserName;

	void doRegister() {
		try {
			loadingInternetDialog.show();
			System.out.println("Regis customer");
			RequestParams params = new RequestParams();
			// params.add("username", f_username);
			params.add("password", f_password);
			params.add("password_confirmation", f_password_confirmation);

			params.add("usertype", "customer");
			params.add("cs_name", f_cs_name);
			params.add("cs_last_name", f_cs_last_name);

			//params.add("cs_country","singapore" /*f_cs_country*/);
			// params.add("cs_state", f_cs_state);
			// params.add("cs_city", f_cs_city);
			// params.add("cs_postal_code", f_cs_postal_code);
			//params.add("cs_home_number", f_cs_home_number);
			params.add("cs_email", f_cs_email);
			 params.add("cs_mobile_number", prefix.getText().toString()+f_co_mobile_number);
			// params.add("cs_address", f_cs_address);
			params.add("refcode", f_referral_code);
			// params.add("referral_code",f_referral_code);
			
			if(isFB){
			params.add("fbid", GlobalVar.fbid);
			
			}else{
				params.add("fbid", "");
				
			}
			// System.out.println("Regis customer" + params.toString());
			AsyncHttpClient post = new AsyncHttpClient();
			
			
			PARestClient.post(pref.getPref(Config.SERVER),API_REGISTER_MERCHANT, params,
					new AsyncHttpResponseHandler() {

						@Override
						public void onSuccess(int statusCode, String result) {
							// TODO Auto-generated method stub
							super.onSuccess(statusCode, result);
							loadingInternetDialog.dismiss();

							// TODO Auto-generated method stub
							// System.out.println(result);
							// simpleToast(result);
							// Tracer.d(result);
							ParserBasicResult parser = new ParserBasicResult(
									result);

							if ("success".equals(parser.getStatus())) {
								Tracer.d("Register customer success");
							simpleToast(parser.getStatus() + "\n"
									+ parser.getResult());
								//getActivity().finish();
								GlobalVar.isRegister=true;
								GlobalVar.a=f_cs_email;
								GlobalVar.b=f_password;
								GlobalVar.mobile_number=prefix.getText().toString()+f_co_mobile_number;
								startActivity(new Intent(getActivity(),ActivityLogin.class));
								// switcher.setDisplayedChild(3);
								hasBeenRegistered = true;
							} else {
								com.pa.pojo.ErrorDefault errorReg = new ParserError(
										new String(result)).getData();
								simpleToast(TextUtils.join("", errorReg.reason));

							}

						}

						@Override
						public void onFailure(int statusCode, Throwable error,
								String content) {
							// TODO Auto-generated method stub
							super.onFailure(statusCode, error, content);
							loadingInternetDialog.dismiss();
							// Tracer.d(content);
							// showLocalWebDialog(content);
							try {
								com.pa.pojo.ErrorDefault errorReg = new ParserError(
										new String(content)).getData();
								simpleToast(TextUtils.join("", errorReg.reason));
							} catch (Exception e) {

							}

						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		cancelTask();
	}

	void cancelTask() {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.prefix:
			showSpinnerSelection(country_prefix, prefix, "Choose prefix");
			break;
		}
	}
	
	public String GetCountryZipCode(){
	    String CountryID="";
	    String CountryZipCode="65";

	    TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
	    //getNetworkCountryIso
	    CountryID= manager.getSimCountryIso().toUpperCase();
	    String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
	    for(int i=0;i<rl.length;i++){
	        String[] g=rl[i].split(",");
	        if(g[1].trim().equals(CountryID.trim())){
	            CountryZipCode=g[0];
	            break;  
	        }
	    }
	    return CountryZipCode;
	}
}
