package com.pa.promotions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.braintreepayments.api.dropin.Customization;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nanigans.android.sdk.NanigansEventManager;
import com.nanigans.android.sdk.NanigansEventParameter;
import com.pa.common.Config;
import com.pa.common.CustomTimePicker;
import com.pa.common.CustomTimePickerDialog;
import com.pa.common.GlobalVar;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.common.TimeUtils;
import com.pa.common.Tracer;
import com.pa.order.FragmentOrder;
import com.pa.parser.ParserBasicResult;
import com.pa.parser.ParserDealBraintreeClientToken;
import com.pa.pojo.DealBraintreeClientToken;
import com.pa.pojo.PromotionItem;
import com.coolfindservices.androidconsumer.R;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.intercom.android.sdk.Intercom;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PromotionScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PromotionScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PromotionScheduleFragment extends MyFragment
{
    private static final String LOG_TAG = "PromoScheduleFrag";
    private PromotionItem mItem;

    private LinearLayout mLayoutAddress, mLayoutTime;
    private TextView mTxtPreferDate, mTxtCountry, mTxtState, mTxtCity;
    private EditText mTxtAddress, mTxtPostalCode;
    private Button mBtnNext;

    private CustomTimePickerDialog mTimePickerDialog;
    private OnFragmentChangeListener mListener;
    private Handler handler;

    public static PromotionScheduleFragment newInstance(PromotionItem mItem) {
        PromotionScheduleFragment fragment = new PromotionScheduleFragment();
        fragment.setItem(mItem);

        return fragment;
    }

    public PromotionScheduleFragment() {
        // Required empty public constructor
    }

    public void setItem(PromotionItem mItem) {
        this.mItem = mItem;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_promotion_schedule, container, false);

        mLayoutAddress  = (LinearLayout)v.findViewById(R.id.layout_address);
        mLayoutTime     = (LinearLayout)v.findViewById(R.id.layout_time);

        mTxtPreferDate  = (TextView)v.findViewById(R.id.txt_prefer_date);
        mTxtCountry     = (TextView)v.findViewById(R.id.txt_country);
        mTxtState       = (TextView)v.findViewById(R.id.txt_state);
        mTxtCity        = (TextView)v.findViewById(R.id.txt_city);
        mTxtAddress     = (EditText)v.findViewById(R.id.txt_address);
        mTxtPostalCode  = (EditText)v.findViewById(R.id.txt_postal_code);

        mBtnNext        = (Button)v.findViewById(R.id.btn_next);

        v.findViewById(R.id.btnIntercom).setOnClickListener(mClickListener);
        v.findViewById(R.id.btnBack).setOnClickListener(mClickListener);

        mTxtPreferDate.setOnClickListener(mClickListener);
        mTxtCountry.setOnClickListener(mClickListener);
        mTxtState.setOnClickListener(mClickListener);
        mTxtCity.setOnClickListener(mClickListener);
        mBtnNext.setOnClickListener(mClickListener);

        onInitial();

        return v;
    }

    public void onInitial()
    {
        mTxtCountry.setTag(R.id.co_state, mTxtState);
        mTxtCountry.setTag(R.id.co_city, mTxtCity);
        mTxtState.setTag(R.id.co_city, mTxtCity);

        if (mItem.has_service_time == null || !mItem.has_service_time.equalsIgnoreCase("Y"))
        {
            mLayoutTime.setVisibility(View.GONE);
        }

        if (mItem.has_service_address == null || !mItem.has_service_address.equalsIgnoreCase("Y"))
        {
            mLayoutAddress.setVisibility(View.GONE);
        }

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

        onGetLastAddress();
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
                case R.id.txt_prefer_date:
                    onDateTimePickerDialog((TextView) v);
                    break;
//                case R.id.txt_country:
//                    getCountry(handler);
//                    break;
//                case R.id.txt_state:
//                    getState(mTxtCountry.getText().toString(), handler);
//                    break;
                case R.id.txt_city:
                    getCity((String) mTxtState.getTag(R.id.txt_city), handler);
                    break;
                case R.id.btn_next:
                    onNext();
                    break;
            }
        }
    };

    public void onNext()
    {
        if (mItem.has_service_time != null && mItem.has_service_time.equalsIgnoreCase("Y"))
        {
            if (!formCheckString(mTxtPreferDate.getText().toString(), "Preferred service date")) {
                return;
            }
        }

        if (mItem.has_service_address != null && mItem.has_service_address.equalsIgnoreCase("Y"))
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
        doPayWithBraintree();
    }

    public void onGetLastAddress() {
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
                                String f_m_country = result.getString("service_request_country");
                                if (GlobalVar.country.toLowerCase().contains(f_m_country.toLowerCase())) {
                                    String f_m_postal_code = result
                                            .getString("service_request_postal_code");
                                    String f_merchant_address = result
                                            .getString("service_request_address");
                                    String f_m_city = result
                                            .getString("service_request_city");
                                    String f_m_state = result
                                            .getString("service_request_state");

//									txtCity.setText(f_m_city);
//									txtState.setText(f_m_state);
//									txtCountry.setText(f_m_country);
                                    mTxtAddress.setText(f_merchant_address);
                                    mTxtPostalCode.setText(f_m_postal_code);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    Dialog dialogCustomDateTimeDialog;
    String tmpDate, tmpTime, tmpTagDate, tmpTagTime;
    public void onDateTimePickerDialog(final TextView tv) {
        View v = inflater.inflate(R.layout.custom_datepicker, null);

        v.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogCustomDateTimeDialog.dismiss();
            }
        });

        final DatePicker datePicker = (DatePicker) v
                .findViewById(R.id.datePicker1);
        final TimePicker timePicker = (TimePicker) v
                .findViewById(R.id.timePicker1);

        Calendar c = Calendar.getInstance();

        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        int monthOfYear = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        datePicker.init(year, monthOfYear, dayOfMonth,
                new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        tmpDate = dateToDDMMMYY(year, monthOfYear, dayOfMonth);
                        tmpTagDate = year + "-" + (monthOfYear + 1) + "-"
                                + dayOfMonth;
                    }
                });
        datePicker.setMinDate(System.currentTimeMillis() - 1000);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // TODO Auto-generated method stub
                String strHourDay, strMinute;
                strHourDay = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                strMinute = minute < 10 ? "0" + minute : "" + minute;
                tmpTime = (strHourDay + ":" + strMinute + ":00");
            }
        });

        v.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int year = datePicker.getYear();
                int monthOfYear = datePicker.getMonth();
                int dayOfMonth = datePicker.getDayOfMonth();
                tmpDate = dateToDDMMMYY(year, monthOfYear, dayOfMonth);
                tmpTagDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

                timePicker.clearFocus();
                String strHourDay, strMinute;
                int hourOfDay = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute()
                        * CustomTimePicker.TIME_PICKER_INTERVAL;
                strHourDay = timePicker.getCurrentHour() + "";
                strMinute = timePicker.getCurrentMinute() + "";
                strHourDay = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                strMinute = minute < 10 ? "0" + minute : "" + minute;

                tmpTime = (strHourDay + ":" + strMinute);
                tmpTagTime = (strHourDay + ":" + strMinute + ":00");

                tv.setText(tmpDate + ", " + tmpTime);
                tv.setTag(tmpTagDate + " " + tmpTagTime);
                dialogCustomDateTimeDialog.dismiss();
            }
        });

        dialogCustomDateTimeDialog = new Dialog(getActivity(),
                R.style.PauseDialog);
        dialogCustomDateTimeDialog.getWindow().requestFeature(
                Window.FEATURE_NO_TITLE);
        dialogCustomDateTimeDialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialogCustomDateTimeDialog.setContentView(v);
        dialogCustomDateTimeDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        dialogCustomDateTimeDialog.show();
    }

    /*
        BrainTree Payment
     */
    private static final int BRAINTREE = 100;
    String braintreeClientToken = "";
    DealBraintreeClientToken braintree;

    void doPayWithBraintree() {
        String country  = (GlobalVar.country == "Malaysia") ? "my" : "sg";

        RequestParams params = new RequestParams();
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
        params.add("country", country);

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

//            @Override
//            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
//                                  Throwable arg3) {
//                // TODO Auto-generated method stub
////                super.onFailure(arg0, arg1, arg2, arg3);
//                forceLoadingInternetDialog.hide();
//                simpleToast("Failed " + new String(arg2));
//            }

        };

        PARestClient.dealGet(pref.getPref(Config.SERVER), "order/payment-token",
                params, responseHandler);

    }

    public void onBraintreeSubmit() {
        Intent intent = new Intent(getActivity(),
                BraintreePaymentActivity.class);

        float paymentPrice = Float.parseFloat(mItem.price);
        if (mItem.promo_code != null && !mItem.promo_code.equals(""))
        {
            paymentPrice = Float.parseFloat(mItem.discount_price);
        }

        Customization customization = new Customization.CustomizationBuilder()
                .primaryDescription("Page Advisor | Order Id:" + mItem.serial)
                .secondaryDescription(mItem.title)
                .amount(mItem.currency + " " + paymentPrice)
                .submitButtonText("Pay Now").build();
        intent.putExtra(BraintreePaymentActivity.EXTRA_CUSTOMIZATION,
                customization);

        intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN,
                braintreeClientToken);

        startActivityForResult(intent, BRAINTREE);
    }

    void postNonceToServer(String nonce) {
        String serial = mItem.serial;

        RequestParams params = new RequestParams();
        params.add("promotion_serial", serial);
        params.add("username", pref.getPref(Config.PREF_USERNAME));
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
        params.add("promo_code", mItem.promo_code);
        params.put("payment_nonce", nonce);
        params.add("braintree_id", braintree.result.customerId);

        if (mItem.has_service_time != null && mItem.has_service_time.equalsIgnoreCase("Y"))
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = TimeUtils.strToDate(mTxtPreferDate.getText().toString(), "dd MMM yyyy, HH:mm");

            String strDate = mTxtPreferDate.getText().toString();
            try {
                Log.i(LOG_TAG, dateFormat.format(date));
                strDate = dateFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            params.add("delivery_time", strDate);
        }

        if (mItem.has_service_address != null && mItem.has_service_address.equalsIgnoreCase("Y"))
        {
            params.add("address", mTxtAddress.getText().toString());
            params.add("state", mTxtState.getText().toString());
            params.add("city", mTxtCity.getText().toString());
            params.add("postcode", mTxtPostalCode.getText().toString());
        }

        PARestClient.dealPost(pref.getPref(Config.SERVER), Config.DEAL_API_POST_PROMO_ORDER,
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

        NanigansEventManager.getInstance().trackNanigansEvent(NanigansEventManager.TYPE.PURCHASE, "appointment",
                new NanigansEventParameter("sku", mItem.title),
                new NanigansEventParameter("value", mItem.price.replace(".", "")),
                new NanigansEventParameter("unique", mItem.serial),
                new NanigansEventParameter("currency", mItem.currency)
        );

        View v;
        v = inflater.inflate(R.layout.dialog_order_success, null);
        TextView txtServiceRequestID = (TextView) v
                .findViewById(R.id.txtServiceRequestID);
        txtServiceRequestID.setText(mItem.serial);

        v.findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mListener.doFragmentChange(new FragmentOrder(false, "PR"), true, "");
                dialogOrderSuccess.hide();
            }
        });

        v.findViewById(R.id.btnNext2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.doFragmentChange(new FragmentOrder(false, "PR"), true, "");
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

        txtServiceRequestID.setText(mItem.serial);

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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            if (activity instanceof OnFragmentChangeListener) {
                mListener = (OnFragmentChangeListener) activity;
            } else {
                throw new ClassCastException(activity.toString()
                        + " must implemenet OnFragmentChangeListener");
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
