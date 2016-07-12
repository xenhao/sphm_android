package com.pa.splash;

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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;

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
import com.pa.parser.ParserUser;
import com.pa.pojo.ActivationStatus;
import com.pa.pojo.User;
import com.coolfindservices.androidconsumer.R;

import org.apache.http.Header;

import java.util.ArrayList;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.identity.Registration;

/**
 * Created by VintedgeMac on 01/07/16.
 */

public class FragmentSignIn extends SessionLoginFragment implements
        View.OnClickListener {
    DisplayMetrics dm;
    View logo;
    View layoutLogin;
    int logoHeight;
    User user;
    String formUsername, formPassword;
    Dialog dialogActivation;
    EditText txtActivate;
    String f_activation, f_phone;
    String f_token;
    String f_pass;
    String f_repass;
    Dialog dialogForgot;
    TextView ePrefixForgot, ePhone;
    EditText eMobileNumberForgot, eToken, ePass, eRepass;
    String f_phone_reset;
    ViewSwitcher switcherForgot;
    int SERVER = Config.DEFAULT_SERVER;

    int fb_type;
    final int TYPE_REGISTER = 0;
    final int TYPE_LOGIN = 1;
    boolean isActivation = false;

    EditText username, password, eMobileNumber;
    TextView privacy_policy, ePrefix;
    RadioGroup rg;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.fragment_sign_in, null);
//        logo = v.findViewById(R.id.logo);
//        layoutLogin = v.findViewById(R.id.layoutLogin);
//
        v.findViewById(R.id.registerClick).setOnClickListener(this);
        v.findViewById(R.id.btnLogin).setOnClickListener(this);
        v.findViewById(R.id.passClick).setOnClickListener(this);
//        v.findViewById(R.id.btnGuest).setOnClickListener(this);
//        v.findViewById(R.id.btnIntercom).setOnClickListener(this);
//        v.findViewById(R.id.txtListYourService).setOnClickListener(this);
//        v.findViewById(R.id.term_of_use).setOnClickListener(this);
//        try {
//            privacy_policy = (TextView) v.findViewById(R.id.privacy_policy);
//            privacy_policy.setOnClickListener(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        username = (EditText) v.findViewById(R.id.username);
        password = (EditText) v.findViewById(R.id.password);
        v.findViewById(R.id.btn_login_logout).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        fb_type = TYPE_LOGIN;
                        buttonLoginLogout.performClick();
                    }
                });
//
//        rg = (RadioGroup) v.findViewById(R.id.group);
        return v;
    }

    @SuppressLint("ValidFragment")
    public FragmentSignIn(boolean isActivation) {
        this.isActivation = isActivation;
        f_phone = GlobalVar.mobile_number;
        formUsername = GlobalVar.a;
    }

    public FragmentSignIn() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Tracer.d("FragmentSignIn created");
        super.onActivityCreated(savedInstanceState);

        buttonLoginLogout = new Button(getActivity());// (Button)
        buttonLoginLogout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Tracer.d("LOGIN FB");
                onClickLogin();
            }
        });
//        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                // TODO Auto-generated method stub
//                if (checkedId == R.id.g1) {
//                    SERVER = 2;
//                } else {
//                    SERVER = 1;
//                }
//                pref.savePref(Config.SERVER, SERVER + "");
//
//            }
//        });

        analytic.trackScreen("Sign In Page");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
//                startActivity(new Intent(getActivity(),ActivitySignIn.class));
			if (isValidLogin()) {
				GlobalVar.isFB = false;
				doLogin();
			}
                break;
            case R.id.passClick:
                // simpleToast("Coming soon");
                showForgotPass();
                break;
            case R.id.registerClick:
                GlobalVar.isFB = false;
                startActivity(new Intent(getActivity(), ActivityRegister.class));
                Log.i("sign in page ", "register button pushed");
                break;
        }
    }

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
                                if (!"N".equals(user.is_active)) {
                                    pref.savePref(Config.PREF_USERNAME,
                                            user.username);
                                    pref.savePref(
                                            Config.PREF_ACTIVE_SESSION_TOKEN,
                                            user.active_session_token);
                                    pref.savePref(Config.PREF_USER, "");
                                    Log.d("Intercom", "Login as " + pref.getPref(Config.PREF_USERNAME));
                                    Intercom.client().registerIdentifiedUser(new Registration().withUserId(pref.getPref(Config.PREF_USERNAME)));
                                    NanigansEventManager.getInstance().setUserId(Encryptor.md5(pref.getPref(Config.PREF_USERNAME)));

                                    startActivity(new Intent(getActivity(),
                                            ActivityLanding.class));
                                    GlobalVar.isFB = false;
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

    public void showActivationDialog() {
        analytic.trackScreen("OTP Activation");

        View v = inflater.inflate(R.layout.fragment_activate, null);
        ePrefix = (TextView) v.findViewById(R.id.prefix);
        eMobileNumber = (EditText) v.findViewById(R.id.co_main_business_number);

        eMobileNumber.setText(f_phone.substring(3));
        String str_prefix = f_phone.substring(0, 3);
        ePrefix.setText(str_prefix);

        ePrefix.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showSpinnerSelection(country_prefix, (TextView) v,
                        "Select prefix");
            }
        });

        v.findViewById(R.id.btnResendActivate).setOnClickListener(
                new View.OnClickListener() {

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
        v.findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {

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
                new View.OnClickListener() {

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

    private void showForgotPass() {

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

        ePrefixForgot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showSpinnerSelection(country_prefix, (TextView) v,
                        "Select prefix");
            }
        });

        v.findViewById(R.id.btnSubmit).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (isValidReset()) {
                            doReset();
                        }
                    }
                });

        v.findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialogForgot.hide();
            }
        });
        v.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {

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

    protected boolean isValidActivate() {
        // TODO Auto-generated method stub
        f_activation = txtActivate.getText().toString();
        if (formCheckString(f_activation, "activation code")) {
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

                                    startActivity(new Intent(getActivity(),
                                            ActivityLanding.class));
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
}