package com.pa.splash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.appsflyer.AppsFlyerLib;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nanigans.android.sdk.NanigansEventManager;
import com.pa.common.Config;
import com.pa.common.Encryptor;
import com.pa.common.GlobalVar;
import com.pa.common.PARestClient;
import com.pa.common.SessionLoginFragment;
import com.pa.common.Tracer;
import com.pa.landing.ActivityLanding;
import com.pa.parser.ParserActivationStatus;
import com.pa.parser.ParserBasicResult;
import com.pa.parser.ParserState;
import com.pa.parser.ParserUser;
import com.pa.pojo.ActivationStatus;
import com.pa.pojo.State;
import com.pa.pojo.User;
import com.coolfindservices.androidconsumer.R;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.identity.Registration;

public class FragmentLogin extends SessionLoginFragment implements
		OnClickListener {
	DisplayMetrics dm;
	View logo;
	View layoutLogin;
	View guestBtn, loginBtn, listServiceBtn, copyRight;
	private static final int GUEST_LOGIN = 1001;
	private static final int GUEST_LOGIN_SUCCESS = 200;
	int logoHeight;

	EditText username, password, eMobileNumber;
	TextView privacy_policy, ePrefix;
	RadioGroup rg;

	//	variables for country selection
	TextView txtCountry2;
	String[] countrySelection;
	ArrayList<State> storedStates;
	//	boolean switch for random unresponsive getStateData
	private Boolean retryGetState = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_login, null);

		logo			= v.findViewById(R.id.logo);
		layoutLogin		= v.findViewById(R.id.layoutLogin);
		guestBtn		= v.findViewById(R.id.btnGuest);
		loginBtn		= v.findViewById(R.id.btnLogin);
//		listServiceBtn	= v.findViewById(R.id.txtListYourService);
		copyRight		= v.findViewById(R.id.copyright);

		v.findViewById(R.id.btnRegister).setOnClickListener(this);
		v.findViewById(R.id.btnLogin).setOnClickListener(this);
		v.findViewById(R.id.forgotPass).setOnClickListener(this);
		v.findViewById(R.id.btnGuest).setOnClickListener(this);
		v.findViewById(R.id.btnIntercom).setOnClickListener(this);
		v.findViewById(R.id.txtListYourService).setOnClickListener(this);
		v.findViewById(R.id.term_of_use).setOnClickListener(this);
		try {
			// View copyright=v.findViewById(R.id.copyright);
			privacy_policy = (TextView) v.findViewById(R.id.privacy_policy);
			privacy_policy.setOnClickListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		username = (EditText) v.findViewById(R.id.username);
		password = (EditText) v.findViewById(R.id.password);
		v.findViewById(R.id.btn_login_logout).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						fb_type = TYPE_LOGIN;
						buttonLoginLogout.performClick();
					}
				});

		rg = (RadioGroup) v.findViewById(R.id.group);
//		rg.check(R.id.g2);

		txtCountry2=(TextView)v.findViewById(R.id.country2);
		txtCountry2.setOnClickListener(this);

		// Preset Singapore For Live only
		if ("2".equals(getActivity().getResources().getString(R.string.server))) {
			txtCountry2.setText("Singapore");
//			countrySelection = country;
		} else {
//			countrySelection = country2;
		}

		return v;
	}

	boolean isActivation = false;

	@SuppressLint("ValidFragment")
	public FragmentLogin(boolean isActivation) {
		this.isActivation = isActivation;
		f_phone = GlobalVar.mobile_number;
		formUsername = GlobalVar.a;
	}

	public FragmentLogin(

	) {
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
//		getStateData();
	}

	@Override
	public void onResume(){
		super.onResume();
		//	additional checking to get state data if initial went unresponsive
//		if(countrySelection == null || countrySelection.length <= 2){
//			getStateData();		Log.i("CALLED FROM ONRESUME", "CALLED FROM ONRESUME" + " " + countrySelection);
//		}
	}

	void getStateData(){
		RequestParams params=new RequestParams();
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
//        params.add("country_only", "true");
		params.add("state_only", "true");

		AsyncHttpResponseHandler responseHandler=new AsyncHttpResponseHandler(){

			@Override
			public void onStart() {
				super.onStart();
				loadingInternetDialog.show();
			}

			@Override
			public void onSuccess(int statusCode, String content) {
				super.onSuccess(statusCode, content);
				Log.d("Country", content);
				ParserState parser = new ParserState(content);
				if (parser.getStatus().equalsIgnoreCase("success")) {
					// Merge states to Malaysia
					ArrayList<String> merged = new ArrayList<String>();
					merged.add("Singapore");

					storedStates = parser.getStates();

					Iterator<State> iterator = storedStates.iterator();
					State state;
					while (iterator.hasNext()) {
						state = iterator.next();
						merged.add(malaysia_prefix + state.state_long);
					}

					countrySelection = merged.toArray(new String[merged.size()]);
				}
			}

			@Override
			public void onFailure(int statusCode, Throwable error,
								  String content) {
				// TODO Auto-generated method stub
				super.onFailure(statusCode, error, content);
				Log.d("Country", content);
				simpleToast(error + "\n" + content);

				loadingInternetDialog.dismiss();

			}

			@Override
			public void onFinish() {
				super.onFinish();
				loadingInternetDialog.dismiss();
				//	show state selection spinner if this was last minute state query
				if(retryGetState){
					showSpinner(getView());
					retryGetState = false;
				}
			}
		};

		PARestClient.post(pref.getPref(Config.SERVER), Config.API_LOCATION, params, responseHandler);
	}

	//	show spinner for state selection
	private void showSpinner(final View v){
		analytic.trackScreen("Select Country for Service");
		showSpinnerSelection(countrySelection

				, "Select country", new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
											int arg2, long arg3) {
						try {
							generalDialog.hide();
							((TextView) v).setText(countrySelection[arg2]);
							v.setTag(arg2);

							State selectedState = storedStates.get(arg2 > 0 ? arg2 - 1 : arg2);
							GlobalVar.state = selectedState.state_long;
							GlobalVar.state_short = selectedState.state_short;


							if (Arrays.equals(country2, state)) {
								v.setTag(R.id.city, state_short[arg2]);
							}

							final TextView tv1 = (TextView) v
									.getTag(R.id.co_state);
							final TextView tv2 = (TextView) v.getTag(R.id.co_city);

							if (tv1 != null && tv2 != null
									&& Arrays.equals(country2, country)) {
								// tv2 = (TextView) tv.getTag(R.id.co_city);
								Tracer.d("debug", "country" + country2[arg2]);
								if ("Singapore".equals(country2[arg2])) {
									tv1.setText("Singapore");
									tv2.setText("Singapore");
									simpleToast("when is this executed5???");
								} else {
									tv1.setText("");
									tv1.setHint("Select your state");

									tv2.setText("");
									tv2.setHint("Select your city");
								}

							}

							if (tv2 != null && Arrays.equals(country2, state)) {
								// tv2 = (TextView) tv.getTag(R.id.co_city);
								if ("Singapore".equals(country2[arg2])) {
									tv1.setText("Singapore");
									tv2.setText("Singapore");
									simpleToast("when is this executed4???");
								} else {
									tv2.setText("");
									tv2.setHint("Select your city");
								}
							}
							simpleToast("when is this executed3???");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Tracer.d("FragmentLogin created");
		super.onActivityCreated(savedInstanceState);
		privacy_policy.setOnClickListener(this);
		if (GlobalVar.animateLogo) {
			animateLogo();
		} else {
			GlobalVar.animateLogo = true;
			logo.setVisibility(View.GONE);
			layoutLogin.setVisibility(View.VISIBLE);
			guestBtn.setVisibility(View.VISIBLE);
			loginBtn.setVisibility(View.VISIBLE);
//			listServiceBtn.setVisibility(View.VISIBLE);
			copyRight.setVisibility(View.VISIBLE);
		}

		if (isActivation) {
			showActivationDialog();
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
//		pref.savePref(Config.SERVER, SERVER + "");
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == R.id.g1) {
					SERVER = 2;
				} else {
					SERVER = 1;
				}
				pref.savePref(Config.SERVER, SERVER + "");

			}
		});

		// Turn on switch for Staging app
		if ("1".equals(getResources().getString(R.string.server))) {
			rg.setVisibility(View.VISIBLE);
			rg.check(R.id.g1);
		}

		if(!TextUtils.isEmpty(pref.getPref(Config.PREF_LAST_USERNAME))){
			username.setText(pref.getPref(Config.PREF_LAST_USERNAME));
		}

		analytic.trackScreen("Login Page");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GUEST_LOGIN) {
			if (resultCode == GUEST_LOGIN_SUCCESS) {
				getActivity().finish();

			}
		}
	}

	int SERVER = Config.DEFAULT_SERVER;

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
		Tracer.d(email + name + fbid + last_name);

		GlobalVar.email = email;
		GlobalVar.first_name = name;
		GlobalVar.last_name = last_name;
		GlobalVar.fbid = fbid;

		if (fb_type == TYPE_REGISTER) {
			// Toast.makeText(getActivity(), email, Toast.LENGTH_SHORT).show();
			// doRegisterViaFB(email, name, fbid, dob);
			// doLoginViaFB(email, name, fbid, dob);
			GlobalVar.isFB = true;
//			startActivity(new Intent(getActivity(), ActivityRegister.class));
			doLoginFB();

		} else if (fb_type == TYPE_LOGIN) {
			Tracer.d("Do login via FB");
			// doLoginViaFB(email, name, fbid, dob);

			formUsername = "".equals(email) ? fbid : email;
			formPassword = fbid;
			GlobalVar.isFB = true;

			doLoginFB();
		}
	}

	//  *** VERY BAD PRACTICE. KILL OFF doLoginFB() ASAP.   ***//
	private void doLoginFB() {		//simpleToast("diLoginFB() Executed");
		// TODO Auto-generated method stub

		pref.savePref(Config.PREF_LAST_USERNAME, formUsername);

		loadingInternetDialog.show();
		RequestParams params = new RequestParams();
		params.add("username", formUsername);
		params.add("password", formPassword);

		if (GlobalVar.isFB) {
			params.add("fbid", GlobalVar.fbid);
			// GlobalVar.isFB = false;
		}

		AsyncHttpClient post = new AsyncHttpClient();
		PARestClient.post(pref.getPref(Config.SERVER), Config.API_LOGIN,
				params, new AsyncHttpResponseHandler() {

					@Override
					public void onStart(){
						super.onStart();
						loadingDialog.show();
					}

					@Override
					public void onSuccess(int statusCode, String result) {
						// TODO Auto-generated method stub
						ParserUser parser = new ParserUser(result);
						if (isStatusSuccess(parser.getStatus())) {
							user = parser.getUser();// getArr().get(0);

							if ("customer".equals(parser.getType())) {
								if ("Y".equals(user.is_active)) {
									pref.savePref(Config.PREF_USERNAME,
											user.username);
									pref.savePref(
											Config.PREF_ACTIVE_SESSION_TOKEN,
											user.active_session_token);
									pref.savePref(Config.PREF_USER, "");
									Log.d("Intercom", "Login as " + pref.getPref(Config.PREF_USERNAME));
									Intercom.client().registerIdentifiedUser(new Registration().withUserId(pref.getPref(Config.PREF_USERNAME)));
									NanigansEventManager.getInstance().setUserId(Encryptor.md5(pref.getPref(Config.PREF_USERNAME)));

									if(GlobalVar.isGuest) {
										GlobalVar.isGuest = false;
										GlobalVar.isResumeGuest = false;
										getActivity().finish();
									}else {
										startActivity(new Intent(getActivity(), ActivityLanding.class));
										GlobalVar.isFB = false;        //simpleToast("doLoginFB success");
									}
								} else {						//simpleToast("doLoginFB show activation");

									f_phone = user.cs_mobile_number;

									showActivationDialog();
								}
							} else {
								simpleToast("This isn't a customer account. Please recheck your account");
							}

						} else {		//simpleToast("doLoginFB return not customer");

							//	edited flow
							if(GlobalVar.isFB) {        //Log.i("check FB", "checked");
								if (parser.getCode() == null) {
									//  not planned yet
								} else {
									String ccode = parser.getCode();
									switch (parser.getCode()) {
										case "101":
										case "103":
											//	open registration
											startActivity(new Intent(getActivity(), ActivityRegister.class));
											//simpleToast("Information insufficient");
											break;
										case "105":
											//	go to verification
											f_phone = user.cs_mobile_number;
											showActivationDialog();
											break;
									}
								}
							}
						}
						loadingInternetDialog.dismiss();
					}

					@Override
					public void onFailure(Throwable error, String content) {		//simpleToast("doLoginFB failed");	Log.i("fb error", String.valueOf(error));
						// TODO Auto-generated method stub
						super.onFailure(error, content);
						loadingInternetDialog.dismiss();

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								getActivity());
						alertDialogBuilder.setTitle("FB Sign In Failed");
						alertDialogBuilder
								.setMessage(error.toString() + content)
								.setCancelable(false)
								.setPositiveButton("OK", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								})
								.setNegativeButton("No", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();

					}

					@Override
					public void onFinish(){
						super.onFinish();
						loadingDialog.dismiss();
					}
				});

	}

	private void animateLogo() {
		// logo.setVisibility(View.VISIBLE);
		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new AccelerateInterpolator()); // add this
		fadeOut.setDuration(2000);
		fadeOut.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				logo.setVisibility(View.GONE);

				animateLogin();
			}
		});
		logo.startAnimation(fadeOut);

	}

	private void animateLogin() {
		layoutLogin.setVisibility(View.INVISIBLE);
		guestBtn.setVisibility(View.INVISIBLE);
		loginBtn.setVisibility(View.INVISIBLE);
//		listServiceBtn.setVisibility(View.INVISIBLE);
		copyRight.setVisibility(View.INVISIBLE);

		Animation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setInterpolator(new DecelerateInterpolator()); // and this
		fadeIn.setDuration(1500);
		fadeIn.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				layoutLogin.setVisibility(View.VISIBLE);
				guestBtn.setVisibility(View.VISIBLE);
				loginBtn.setVisibility(View.VISIBLE);
//				listServiceBtn.setVisibility(View.VISIBLE)
				copyRight.setVisibility(View.VISIBLE);
			}
		});
		layoutLogin.setAnimation(fadeIn);
		guestBtn.setAnimation(fadeIn);
		loginBtn.setAnimation(fadeIn);
//		listServiceBtn.setAnimation(fadeIn)
		copyRight.setAnimation(fadeIn);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.btnRegister:
				GlobalVar.isFB = false;
				startActivity(new Intent(getActivity(), ActivityRegister.class));
				break;
			case R.id.btnLogin:
				Intent intent = new Intent(getActivity(), ActivitySignIn.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				if(GlobalVar.isGuest){
					startActivityForResult(intent, GUEST_LOGIN);
				}else {
					startActivity(intent);
				}
//			startActivity(new Intent(getActivity(),ActivitySignIn.class));
//			getActivity().finish();
//			if (isValidLogin()) {
//				GlobalVar.isFB = false;
//				doLogin();
//			}
				break;
			case R.id.forgotPass:
				// simpleToast("Coming soon");
				showForgotPass();
				break;

			case R.id.btnGuest:
//			Intent intent = new Intent(getActivity(), ActivityLanding.class);
//			intent.putExtra("isGuest", true);
//			startActivity(intent);
				if(GlobalVar.isResumeGuest){
					GlobalVar.isResumeGuest = false;
					//	finish activity here as this ActivityLogin was added in front of existing NewActivityLanding
					getActivity().finish();
				}else {
					GlobalVar.isGuest = true;
					startActivity(new Intent(getActivity(), ActivityLanding.class));
					//	did not finish activity as this was the original flow
//				getActivity().finish();
				}
				break;

			case R.id.btnIntercom:
				Intercom.client().displayConversationsList();

				break;

			case R.id.privacy_policy:
				// openUrl("http://www.pageadvisor.com/home/subscribe-privacy-policy");
				showWebDialog("http://pageadvisor.bounche.com/home/privacypolicy");
				break;

			case R.id.term_of_use:
				showWebDialog("http://pageadvisor.bounche.com/home/terms");
				break;

			case R.id.txtListYourService:
				showWebDialog("http://www.pageadvisor.com/register-your-business/");
				break;

			case R.id.country2:
			case R.id.country:
				//	check if state data is available before displaying state selection spinner
				if(countrySelection != null) {
					showSpinner(v);
				}else{
					retryGetState = true;
					getStateData();
				}
		}
	}

	Dialog dialogForgot;
	TextView ePrefixForgot, ePhone;
	EditText eMobileNumberForgot, eToken, ePass, eRepass;
	String f_phone_reset;
	ViewSwitcher switcherForgot;

	void showForgotPass() {

		View v = inflater.inflate(R.layout.fragment_forgot_pass, null);
		ePrefixForgot = (TextView) v.findViewById(R.id.prefix);
		eMobileNumberForgot = (EditText) v.findViewById(R.id.phone);

		ePhone = (TextView) v.findViewById(R.id.phone2);
		eToken = (EditText) v.findViewById(R.id.token);
		ePass = (EditText) v.findViewById(R.id.pass);
		eRepass = (EditText) v.findViewById(R.id.repass);
		switcherForgot = (ViewSwitcher) v.findViewById(R.id.switcher_forgot);
		// eMobileNumber.setText(f_phone.substring(3));
		// String str_prefix = f_phone.substring(0, 3);
		// ePrefix.setText(str_prefix);

		ePrefixForgot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showSpinnerSelection(country_prefix, (TextView) v,
						"Select prefix");
			}
		});

		v.findViewById(R.id.btnSubmit).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (isValidReset()) {
							doReset();
						}
					}
				});

		v.findViewById(R.id.btnBack).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialogForgot.hide();
			}
		});
		v.findViewById(R.id.btnReset).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (isValidGetToken()) {
					doGetToken();
					ePhone.setText(f_phone_reset);
				}
			}
		});

		dialogForgot = new Dialog(getActivity(), R.style.PauseDialog);
		dialogForgot.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialogForgot.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogForgot.setContentView(v);
		dialogForgot.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogForgot.show();

	}

	/**
	 *
	 */
	protected void doReset() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.add("mobile_number", f_phone_reset);
		params.add("forgot_password_token", f_token);
		params.add("password", f_pass);
		params.add("password_confirmation", f_repass);

		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
			/*
			 * (non-Javadoc)
			 *
			 * @see com.loopj.android.http.AsyncHttpResponseHandler#onStart()
			 */
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				loadingInternetDialog.show();
			}

			/*
			 * (non-Javadoc)
			 *
			 * @see com.loopj.android.http.AsyncHttpResponseHandler#onFinish()
			 */
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				loadingInternetDialog.dismiss();
			}

			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				ParserBasicResult parser=new ParserBasicResult(new String(arg2));

				if("success".equals(parser.getStatus())){
					dialogForgot.dismiss();
					simpleToast("Try using your new password to login");
				}else{
					simpleToast("Invalid reset code");
				}
			};
		};

		PARestClient.post(pref.getPref(Config.SERVER),
				Config.API_FORGOT_PASS_UPDATE, params, responseHandler);
	}

	String f_token;
	String f_pass;
	String f_repass;

	/**
	 * @return
	 */
	protected boolean isValidReset() {
		// TODO Auto-generated method stub
		f_token = eToken.getText().toString();
		f_pass = ePass.getText().toString();
		f_repass = eRepass.getText().toString();

		if (!formCheckString(f_token, "reset code")) {

		} else if (!formCheckString(f_pass, "new password")) {

		} else if (!formCheckString(f_repass, "new password confirmation")) {

		} else if (!f_pass.equals(f_repass)) {
			simpleToast("Password confirmation is not match");
		} else {
			return true;
		}

		return false;
	}

	/**
	 *
	 */
	protected void doGetToken() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.add("mobile_number", f_phone_reset);

		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
			/*
			 * (non-Javadoc)
			 *
			 * @see com.loopj.android.http.AsyncHttpResponseHandler#onStart()
			 */
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				loadingInternetDialog.show();
			}

			/*
			 * (non-Javadoc)
			 *
			 * @see com.loopj.android.http.AsyncHttpResponseHandler#onFinish()
			 */
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				loadingInternetDialog.dismiss();
			}

			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				switcherForgot.setDisplayedChild(1);
			};
		};

		PARestClient.post(pref.getPref(Config.SERVER),
				Config.API_FORGOT_PASS_TOKEN, params, responseHandler);
	}

	/**
	 * @return
	 */
	protected boolean isValidGetToken() {
		// TODO Auto-generated method stub
		String prefix, mobilenumber;

		prefix = ePrefixForgot.getText().toString();
		mobilenumber = eMobileNumberForgot.getText().toString();

		if (!formCheckString(prefix, "prefix")) {

		} else if (!formCheckString(prefix, "mobile number")) {

		} else {
			f_phone_reset = prefix + mobilenumber;
			return true;
		}

		return false;
	}

	User user;

	private void doLogin() {
		// TODO Auto-generated method stub

		pref.savePref(Config.PREF_LAST_USERNAME, formUsername);

		loadingInternetDialog.show();
		RequestParams params = new RequestParams();
		params.add("username", formUsername);
		params.add("password", formPassword);

		if (GlobalVar.isFB) {
			params.add("fbid", GlobalVar.fbid);
			// GlobalVar.isFB = false;
		}

		AsyncHttpClient post = new AsyncHttpClient();
		PARestClient.post(pref.getPref(Config.SERVER), Config.API_LOGIN,
				params, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, String result) {
						// TODO Auto-generated method stub
						ParserUser parser = new ParserUser(result);
						if (isStatusSuccess(parser.getStatus())) {
							user = parser.getUser();// getArr().get(0);

							if ("customer".equals(parser.getType())) {
								if ("Y".equals(user.is_active)) {
									pref.savePref(Config.PREF_USERNAME,
											user.username);
									pref.savePref(
											Config.PREF_ACTIVE_SESSION_TOKEN,
											user.active_session_token);
									pref.savePref(Config.PREF_USER, "");
									Log.d("Intercom", "Login as " + pref.getPref(Config.PREF_USERNAME));
									Intercom.client().registerIdentifiedUser(new Registration().withUserId(pref.getPref(Config.PREF_USERNAME)));
									NanigansEventManager.getInstance().setUserId(Encryptor.md5(pref.getPref(Config.PREF_USERNAME)));

									if(GlobalVar.isGuest) {
										GlobalVar.isGuest = false;
										GlobalVar.isResumeGuest = false;
										getActivity().finish();
									}else {
										startActivity(new Intent(getActivity(), ActivityLanding.class));
										GlobalVar.isFB = false;
									}
								} else {

									f_phone = user.cs_mobile_number;

									showActivationDialog();
								}
							} else {
								simpleToast("This isn't a customer account. Please recheck your account");
							}

						} else {
							ParserBasicResult pbr = new ParserBasicResult(
									result);
							AlertDialog.Builder builder = new AlertDialog.Builder(
									getActivity());
							builder.setMessage(pbr.getReason());
							builder.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog,
															int which) {
											dialog.dismiss();
										}
									});

							builder.show();
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

	String formUsername, formPassword;

	boolean isValidLogin() {
		boolean flag = false;

		formUsername = username.getText().toString();
		formPassword = password.getText().toString();

		if (!formCheckStringWithInlineError(username, "username")) {
			flag = false;
		} else if (!formCheckStringWithInlineError(password, "password")) {
			flag = false;
		} else {
			flag = true;
		}

		return flag;
	}

	Dialog dialogActivation;
	EditText txtActivate;

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
							analytic.trackScreen("Resend OTP");

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

		PARestClient.post(pref.getPref(Config.SERVER),
				"user/resend-activation-code", params,
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
						Log.d("Activation", new String(arg2));
						ParserActivationStatus parser = new ParserActivationStatus(
								new String(arg2));
						ActivationStatus status = parser.getData();

						if ("success".equals(status.status)) {
							simpleToast("Success. Activation code has been resent");
						} else {
							simpleToast((String) status.reason);
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

	protected void doActivate() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.add("mobile_number", f_phone);
		params.add("mobile_token", f_activation);
		PARestClient.post(pref.getPref(Config.SERVER), "user/mobile-token",
				params, new AsyncHttpResponseHandler() {
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

									Intercom.client().registerIdentifiedUser(new Registration().withUserId(pref.getPref(Config.PREF_USERNAME)));

//                                    AppsFlyerLib.sendTrackingWithEvent(getActivity().getApplicationContext(), "Account Creation", "");
									Log.d("Register", "OTP Success 1");
									NanigansEventManager.getInstance().setUserId(Encryptor.md5(pref.getPref(Config.PREF_USERNAME)));

									simpleToast("Success. Account has been activated");
									analytic.trackScreen("OTP Success");

									GlobalVar.isGuest = false;
									startActivity(new Intent(getActivity(), ActivityLanding.class));
									dialogActivation.dismiss();

								} else {
									formUsername = GlobalVar.a;
									formPassword = GlobalVar.b;
									Log.d("Register", "OTP Success 2 " + formUsername);
									NanigansEventManager.getInstance().trackUserRegistration(Encryptor.md5(formUsername));
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

	String f_activation, f_phone;

	protected boolean isValidActivate() {
		// TODO Auto-generated method stub
		f_activation = txtActivate.getText().toString();
		if (formCheckString(f_activation, "activation code")) {
			return true;
		}

		return false;
	}
}
