package com.pa.deals;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.braintreepayments.api.dropin.Customization;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.Config;
import com.pa.common.GlobalVar;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.common.Tracer;
import com.pa.order.FragmentOrder;
import com.pa.parser.ParserBasicResult;
import com.pa.parser.ParserDealBraintreeClientToken;
import com.pa.pojo.DealBraintreeClientToken;
import com.pa.pojo.PackageJobItem;
import com.coolfindservices.androidconsumer.R;

import org.apache.http.Header;
import org.json.JSONObject;

import io.intercom.android.sdk.Intercom;

/**
 * Created by Steven on 10/11/2015.
 */
public class PackageScheduleAddFragment extends MyFragment {

    private static final String LOG_TAG = "PackScheduleAddFrag";
    private PackageJobItem mPackageJobItem;

    private LinearLayout mLayoutAddress;
    private TextView mTxtCountry, mTxtState, mTxtCity;
    private EditText mTxtAddress, mTxtPostalCode;
    private Button mBtnNext;

    private OnFragmentChangeListener mListener;
    private Handler handler;

    private String f_rating, f_merchant_address, f_m_country, f_m_state, f_m_city,
            f_m_postal_code;

    private Boolean isAddressMapped;

    public static PackageScheduleAddFragment newInstance(PackageJobItem mPackageJobItem) {
        PackageScheduleAddFragment fragment = new PackageScheduleAddFragment();
        fragment.setItem(mPackageJobItem);

        return fragment;
    }

    public void setItem(PackageJobItem mPackageJobItem) {
        this.mPackageJobItem = mPackageJobItem;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        showSpinnerSelection(country, mTxtAddress, "Select country");

                        break;
                    case 1:
                        showSpinnerSelection(state, mTxtState, "Select state");
                        break;
                    case 2:
                        showSpinnerSelection(city, mTxtCity, "Select city");

                        break;
                }
            }
        };

        getLastAddress();
        isAddressMapped = true;
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        try {
            if (activity instanceof OnFragmentChangeListener) {
                mListener = (OnFragmentChangeListener) activity;
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
        View v = inflater.inflate(R.layout.fragment_package_schedule_address, container, false);

        mLayoutAddress  = (LinearLayout)v.findViewById(R.id.layout_address);

        mTxtCountry     = (TextView)v.findViewById(R.id.txt_country);
        mTxtState       = (TextView)v.findViewById(R.id.txt_state);
        mTxtCity        = (TextView)v.findViewById(R.id.txt_city);
        mTxtAddress     = (EditText)v.findViewById(R.id.txt_address);
        mTxtPostalCode  = (EditText)v.findViewById(R.id.txt_postal_code);

        mBtnNext        = (Button)v.findViewById(R.id.btn_next);

        v.findViewById(R.id.btnIntercom).setOnClickListener(mClickListener);
        v.findViewById(R.id.btnBack).setOnClickListener(mClickListener);

        mTxtCountry.setOnClickListener(mClickListener);
        mTxtState.setOnClickListener(mClickListener);
        mTxtCity.setOnClickListener(mClickListener);
        mBtnNext.setOnClickListener(mClickListener);

        onInitial();
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            analytic.trackScreen("Enter Address");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onInitial()
    {
        try {

            mTxtCountry.setTag(R.id.co_state, mTxtState);
            mTxtCountry.setTag(R.id.co_city, mTxtCity);
            mTxtState.setTag(R.id.co_city, mTxtCity);

            mTxtCountry.setText(GlobalVar.country);

            if ("Singapore".equalsIgnoreCase(mTxtCountry.getText().toString()))
            {
                mTxtState.setVisibility(View.GONE); // Hide State whenever it's Singapore
                mTxtState.setText(mTxtCountry.getText().toString());
                mTxtCity.setVisibility(View.GONE); // Hide City whenever it's Singapore
                mTxtCity.setText(mTxtCountry.getText().toString());
            }
            else
            {
                mTxtState.setTag(R.id.txt_city, GlobalVar.state_short);
                mTxtState.setText(GlobalVar.state);
            }

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                mTxtCountry.setBackground(null);
                mTxtState.setBackground(null);
            } else {
                mTxtCountry.setBackgroundDrawable(null);
                mTxtState.setBackgroundDrawable(null);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        //        getLastAddress();
        if(!isAddressMapped)    displayAddress();

    }

    private void displayAddress() {
//        f_m_country = mTxtCountry.getText().toString();
//        f_m_state = mTxtState.getText().toString();
//        f_m_city = mTxtCity.getText().toString();
//
//        f_merchant_address = mTxtAddress.getText().toString();
//        f_m_postal_code = mTxtPostalCode.getText().toString();

        Log.i("Address results:", f_m_country + "\n" + f_m_state + "\n" + f_m_city + "\n" + f_merchant_address + "\n" + f_m_postal_code);

        mTxtAddress.setText(f_merchant_address);
        mTxtPostalCode.setText(f_m_postal_code);
    }

    private void showAddressData(){
        Log.i("AddressData results:", f_m_country + "\n" + f_m_state + "\n" + f_m_city + "\n" + f_merchant_address + "\n" + f_m_postal_code);
    }

    private void getLastAddress() {

//        f_m_country = mTxtCountry.getText().toString();
//        f_m_state = mTxtState.getText().toString();
//        f_m_city = mTxtCity.getText().toString();
//
//        f_merchant_address = mTxtAddress.getText().toString();
//        f_m_postal_code = mTxtPostalCode.getText().toString();

        loadingInternetDialog.show();
        // AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token",
                pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

        PARestClient.get(pref.getPref(Config.SERVER),
                Config.API_GET_LAST_ADDRESS, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // TODO Auto-generated method stub
                        super.onFailure(statusCode, error, content);
                        loadingInternetDialog.dismiss();
                        // showLocalWebDialog(content);
                    }

                    @Override
                    public void onSuccess(int statusCode, String content) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, content);
                        loadingInternetDialog.dismiss();
                        Tracer.d(content);
                        try {
                            JSONObject obj = new JSONObject(content);
                            if ("success".equals(obj.getString("status"))) {
                                JSONObject result = obj.getJSONObject("result");

                                f_m_country = result
                                        .getString("service_request_country");
                                if (GlobalVar.country.toLowerCase().contains(f_m_country.toLowerCase())) {

                                    f_m_postal_code = result
                                            .getString("service_request_postal_code");

                                    f_merchant_address = result
                                            .getString("service_request_address");

                                    f_m_city = result
                                            .getString("service_request_city");
                                    f_m_state = result
                                            .getString("service_request_state");

                                    displayAddress();
                                    showAddressData();

//									txtCity.setText(f_m_city);
//									txtState.setText(f_m_state);
//									txtCountry.setText(f_m_country);
//                                    displayAddress();
//                                    mTxtAddress.setText(f_merchant_address);
//                                    mTxtPostalCode.setText(f_m_postal_code);

                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    public View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(LOG_TAG, "On Click !");

            switch (v.getId())
            {
                case R.id.btnBack:
                    hideKeyboard();
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                    break;
                case R.id.btnIntercom:
                    Intercom.client().displayConversationsList();
                    break;
                case R.id.txt_city:
                    getCity((String) mTxtState.getTag(R.id.txt_city), handler);
                    break;
                case R.id.btn_next:
                    onNext();
                    isAddressMapped = false;
                    break;
            }
        }
    };

    public void onNext()
    {
        hideKeyboard();
        if (mPackageJobItem.has_service_address != null && mPackageJobItem.has_service_address.equalsIgnoreCase("Y"))
        {
            if (!formCheckString(mTxtAddress.getText().toString(), "Delivery address")) {
                return;
            } else if (!formCheckString(mTxtCountry.getText().toString(), "Delivery country")) {
                return;
            } else if (!formCheckString(mTxtState.getText().toString(), "Delivery state")) {
                return;
            } else if (!formCheckString(mTxtCity.getText().toString(), "Delivery city")) {
                return;
            } else if (!formCheckString(mTxtPostalCode.getText().toString(), "Delivery postal code")) {
                return;
            }

        }
//        doPayWithBraintree();

        mPackageJobItem.selected_address = mTxtAddress.getText().toString();
        mPackageJobItem.selected_state = mTxtState.getText().toString();
        mPackageJobItem.selected_country = mTxtCountry.getText().toString();
        mPackageJobItem.selected_city = mTxtCity.getText().toString();
        mPackageJobItem.selected_postal_code = mTxtPostalCode.getText().toString();

        PackagePreviewFragment fragment = PackagePreviewFragment.newInstance(mPackageJobItem);
        mListener.doFragmentChange(fragment, true, "");
    }

    /*
        BrainTree Payment
     */
    private static final int BRAINTREE = 100;
    String braintreeClientToken = "";
    DealBraintreeClientToken braintree;

    void doPayWithBraintree() {
        RequestParams params = new RequestParams();
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
                forceLoadingInternetDialog.show();
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                forceLoadingInternetDialog.hide();
                loadingInternetDialog.dismiss();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());
                alertDialogBuilder.setTitle("Network Error");
                alertDialogBuilder
                        .setMessage("Click yes to reload!")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                onNext();
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
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                // TODO Auto-generated method stub
                super.onSuccess(arg0, arg1, arg2);
                forceLoadingInternetDialog.hide();

                ParserDealBraintreeClientToken parser = new ParserDealBraintreeClientToken(
                        new String(arg2));

                DealBraintreeClientToken bt = parser.getData();

                if (bt != null && "success".equals(bt.status)) {
                    braintree = bt;
                    braintreeClientToken = bt.result.token;
                    onBraintreeSubmit();
                } else {
                    simpleToast("Failed");
                }
            }

        };

        PARestClient.dealGet(pref.getPref(Config.SERVER), "order/payment-token",
                params, responseHandler);
    }

    public void onBraintreeSubmit() {
        Intent intent = new Intent(getActivity(),
                BraintreePaymentActivity.class);

        float paymentPrice = Float.parseFloat(mPackageJobItem.price);
//        if (mItem.promo_code != null && !mItem.promo_code.equals(""))
//        {
//            paymentPrice = Float.parseFloat(mItem.discount_price);
//        }

        Customization customization = new Customization.CustomizationBuilder()
                .primaryDescription("Cool Find | Order Id:" + mPackageJobItem.serial)
                .secondaryDescription(mPackageJobItem.title)
                .amount(mPackageJobItem.currency + " " + paymentPrice)
                .submitButtonText("Pay Now").build();
        intent.putExtra(BraintreePaymentActivity.EXTRA_CUSTOMIZATION,
                customization);

        intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN,
                braintreeClientToken);

        startActivityForResult(intent, BRAINTREE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println(requestCode + " " + resultCode);
        if (requestCode == BRAINTREE) {
            if (resultCode == BraintreePaymentActivity.RESULT_OK) {
                String paymentMethodNonce = data
                        .getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
                Log.i(LOG_TAG, "On Braintree return");
                postNonceToServer(paymentMethodNonce);
            }
        }
    }

    void postNonceToServer(String nonce) {
        String serial = mPackageJobItem.serial;

        RequestParams params = new RequestParams();
        params.add("package_option_serial", serial);
        params.add("username", pref.getPref(Config.PREF_USERNAME));
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
        params.put("payment_nonce", nonce);
        params.add("braintree_id", braintree.result.customerId);
        params.put("start_time", mPackageJobItem.selected_start_date);
        params.add("end_time", mPackageJobItem.selected_end_date);

        if (mPackageJobItem.has_service_address != null && mPackageJobItem.has_service_address.equalsIgnoreCase("Y"))
        {
            Log.i("have address", mPackageJobItem.has_service_address);

            params.add("address", mTxtAddress.getText().toString());
            params.add("state", mTxtState.getText().toString());
            params.add("city", mTxtCity.getText().toString());
            params.add("postcode", mTxtPostalCode.getText().toString());
        }

        PARestClient.dealPost(pref.getPref(Config.SERVER), Config.DEAL_API_POST_PACKAGE_ORDER,
                params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        forceLoadingInternetDialog.show();
                    }

                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        ParserBasicResult parser = new ParserBasicResult(
                                new String(arg2));
                        Log.d("Payment", "Result " + new String(arg2));
                        if ("success".equals(parser.getStatus())) {
                            showDialogOrderSuccess();
                        } else {
                            showDialogOrderCancel();
                        }
                        forceLoadingInternetDialog.dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error, String content) {
                        // TODO Auto-generated method stub
                        showDialogOrderCancel();
                        forceLoadingInternetDialog.setCancelable(true);
                    }
                });
    }

    Dialog dialogOrderSuccess;
    void showDialogOrderSuccess() {
        View v;
        v = inflater.inflate(R.layout.dialog_order_success, null);
        TextView txtServiceRequestID = (TextView) v
                .findViewById(R.id.txtServiceRequestID);
        txtServiceRequestID.setText(mPackageJobItem.serial);

        v.findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mListener.doFragmentChange(new FragmentOrder(false, "PA"), true, "");
                dialogOrderSuccess.hide();
            }
        });

        v.findViewById(R.id.btnNext2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.doFragmentChange(new FragmentOrder(false, "PA"), true, "");
                dialogOrderSuccess.hide();
            }
        });

        dialogOrderSuccess = new Dialog(getActivity(), R.style.PauseDialog);
        dialogOrderSuccess.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                mListener.doFragmentChange(new FragmentOrder(), false, "");
            }
        });
        dialogOrderSuccess.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogOrderSuccess.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialogOrderSuccess.setContentView(v);
        dialogOrderSuccess.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        dialogOrderSuccess.show();
    }

    private void showDialogOrderCancel() {
        View v = inflater.inflate(R.layout.dialog_order_cancel, null);
        TextView txtServiceRequestID = (TextView) v
                .findViewById(R.id.txtServiceRequestID);

        txtServiceRequestID.setText(mPackageJobItem.serial);

        v.findViewById(R.id.btnReturnToPayment).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogOrderSuccess.hide();
                onNext();
            }
        });

        v.findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogOrderSuccess.hide();
            }
        });

        v.findViewById(R.id.btnNext2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogOrderSuccess.hide();
                // listener.doFragmentChange(new FragmentOrder(), false, "");
            }
        });

        dialogOrderSuccess = new Dialog(getActivity(), R.style.PauseDialog);
        dialogOrderSuccess.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                // dialogOrderSuccess.hide();
                // listener.doFragmentChange(new FragmentOrder(), false, "");

            }
        });
        dialogOrderSuccess.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogOrderSuccess.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialogOrderSuccess.setContentView(v);
        dialogOrderSuccess.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        dialogOrderSuccess.show();
    }

}
