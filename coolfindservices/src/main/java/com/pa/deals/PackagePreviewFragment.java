package com.pa.deals;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.braintreepayments.api.dropin.Customization;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nanigans.android.sdk.NanigansEventManager;
import com.nanigans.android.sdk.NanigansEventParameter;
import com.pa.common.AccordionGroup;
import com.pa.common.Codes;
import com.pa.common.Config;
import com.pa.common.GlobalVar;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.common.ProfilUtils;
import com.pa.landing.ActivityLanding;
import com.pa.order.FragmentOrder;
import com.pa.parser.ParserBasicResult;
import com.pa.parser.ParserDealBraintreeClientToken;
import com.pa.parser.ParserDeductCredits;
import com.pa.parser.ParserUserFreeCredits;
import com.pa.parser.ParserValidatePromoCode;
import com.pa.pojo.DealBraintreeClientToken;
import com.pa.pojo.DeductCreditsResult;
import com.pa.pojo.PackageJobItem;
import com.pa.pojo.PromoCodeResult;
import com.pa.pojo.UserFreeCreditsResult;
import com.pa.pojo.UserORM;
import com.coolfindservices.androidconsumer.R;

import org.apache.http.Header;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.intercom.android.sdk.Intercom;

/**
 * Created by Steven on 04/11/2015.
 */
public class PackagePreviewFragment extends MyFragment implements View.OnClickListener {

    private static final String LOG_TAG = "PackagePreviewFragment";
    private static final int BRAINTREE = 100;
    private PackageJobItem mItem;
    private String braintreeClientToken = "";
    private DealBraintreeClientToken braintree;

    private TextView txtDate;
    private TextView txtTime;
    private TextView txtPrice;
    private TextView txtTitle;
    private TextView txtMerchant;
    private TextView txtDetail;
    private TextView txtTnc;
    private TextView txtRating;
    private TextView txtAddress;
    private TextView txtCountry;
    private TextView txtState;
    private TextView txtCity;
    private TextView txtPostalCode;
    private TextView txtCancellation;
    private EditText txtPromoCode;
    private EditText txtCredits;
    private View layoutDate;
    private View layoutAddress;
    private View layoutCancellation;
    private RatingBar mRatingBar;

    private AccordionGroup accordionGroup;
    private View containerFreeCredits, containerPromoCode;
    private TextView freeCredits;
    private Button btnCredits;
    private float discountedAmount = 0;
    private String finalPrice;
    private EditText inputPromoCode;
    private String validatedPromoCode = "", creditSerial = "";
    private String promoCodeForPayment;
    private String creditSerialForPayment;
    private boolean isNoPayment = false;
    private Boolean isSpecial = false;
    private int type;           //  type: 0 = balance after free credits, 1 = after promocode discount
    private Boolean isDiscounted = false;

    private Dialog dialogOrderSuccess;
    private OnFragmentChangeListener mListener;

    public static PackagePreviewFragment newInstance(PackageJobItem mItem) {
        PackagePreviewFragment fragment = new PackagePreviewFragment();
        fragment.setItem(mItem);
        return fragment;
    }

    private void setItem (PackageJobItem mItem) {
        this.mItem = mItem;
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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View.OnClickListener accordionListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                View parent = (View) v.getParent();
                View content = parent.findViewById(R.id.categoryWrapper);
                if (v instanceof TextView) {
                    TextView t = (TextView) v;

                    if (content.getVisibility() == View.VISIBLE) {
                        content.setVisibility(View.GONE);
                        t.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                null,
                                getResources().getDrawable(
                                        R.drawable.icon_arrow_down), null);
                    } else {
                        content.setVisibility(View.VISIBLE);
                        t.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                null,
                                getResources().getDrawable(
                                        R.drawable.icon_arrow_up), null);

                    }
                }

                if (v instanceof LinearLayout) {

                    LinearLayout l = (LinearLayout) v;
                    ImageView arrow = (ImageView) l
                            .findViewById(R.id.arrow);

                    if (content.getVisibility() == View.VISIBLE) {
                        content.setVisibility(View.GONE);
                        arrow.setImageResource(R.drawable.icon_arrow_down);
                    } else {
                        content.setVisibility(View.VISIBLE);
                        arrow.setImageResource(R.drawable.icon_arrow_up);

                    }
                }

            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_package_review, container, false);

        layoutAddress       = v.findViewById(R.id.layout_address);
        layoutDate          = v.findViewById(R.id.layout_date);
        layoutCancellation  = v.findViewById(R.id.layout_cancellation);
        txtDate             = (TextView) v.findViewById(R.id.txt_date);
        txtTime             = (TextView) v.findViewById(R.id.txt_time);
        txtTitle            = (TextView) v.findViewById(R.id.txt_title);
        txtPrice            = (TextView) v.findViewById(R.id.txt_price);
//        txtMerchant         = (TextView) v.findViewById(R.id.txt_merchant);
        txtDetail           = (TextView) v.findViewById(R.id.txt_detail);
        txtTnc              = (TextView) v.findViewById(R.id.txt_tnc);
//        txtRating           = (TextView) v.findViewById(R.id.txt_rating);
        txtAddress          = (TextView) v.findViewById(R.id.txt_address);
        txtCountry          = (TextView) v.findViewById(R.id.txt_country);
        txtState            = (TextView) v.findViewById(R.id.txt_state);
        txtCity             = (TextView) v.findViewById(R.id.txt_city);
        txtPostalCode       = (TextView) v.findViewById(R.id.txt_postal_code);
        txtCancellation     = (TextView) v.findViewById(R.id.txt_cancellation_policy);
//        mRatingBar          = (RatingBar) v.findViewById(R.id.rating_bar);

        v.findViewById(R.id.btnIntercom).setOnClickListener(this);
        v.findViewById(R.id.btnBack).setOnClickListener(this);
        v.findViewById(R.id.btnNext).setOnClickListener(this);

        accordionGroup = new AccordionGroup((LinearLayout) v.findViewById(R.id.accordion));
        accordionGroup.setup();
        //  hide promocode column for packages under promotion
        if(mItem.is_promotion.equalsIgnoreCase("y")) {  Log.i(LOG_TAG, "IS PROMO!!!");
            v.findViewById(R.id.rowPromoCode).setVisibility(View.GONE);
        }else{  Log.i(LOG_TAG, "NOT PROMO!!!");

        }

        containerFreeCredits = v.findViewById(R.id.containerFreeCredits);
        containerPromoCode = v.findViewById(R.id.containerPromoCode);
        freeCredits = (TextView) v.findViewById(R.id.freeCredits);
        btnCredits = (Button) v.findViewById(R.id.btnCredits);
        btnCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deductCredits(v.getRootView());
            }
        });
        getUserFreeCredits();

        inputPromoCode = (EditText) v.findViewById(R.id.inputPromoCode);
        v.findViewById(R.id.btnPromoCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String promoCode = inputPromoCode.getText().toString();

                if ("".equals(promoCode)) {
                    showMessageDialog("The Promo Code field is required");
                    return;
                }

                hideKeyboard(inputPromoCode);

                validatePromoCode(promoCode, v.getRootView());
            }
        });

        onInitialData();
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            analytic.trackScreen("Order Summary");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onInitialData() {
        if (mItem.has_service_address.equalsIgnoreCase("Y")) {
            layoutAddress.setVisibility(View.VISIBLE);
            txtAddress.setText(mItem.selected_address);
            txtState.setText(mItem.selected_state);
            txtCountry.setText(mItem.selected_country);
            txtPostalCode.setText(mItem.selected_postal_code);
            txtCity.setText(mItem.selected_city);
            if (mItem.selected_country.equalsIgnoreCase("singapore")) {
                txtState.setVisibility(View.GONE);
                txtCity.setVisibility(View.GONE);
            } else {
                txtState.setVisibility(View.VISIBLE);
                txtCity.setVisibility(View.VISIBLE);
            }
        }
        else {
            layoutAddress.setVisibility(View.GONE);
        }

        if (mItem.has_service_time.equalsIgnoreCase("Y")) {
            try {
                Date startDateTime  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        .parse(mItem.selected_start_date);
                String startDate    = new SimpleDateFormat("dd MMMM yyyy").format(startDateTime);
                String startTime    = new SimpleDateFormat("HH:mm").format(startDateTime);
                layoutDate.setVisibility(View.VISIBLE);
                txtDate.setText(startDate);
                txtTime.setText(startTime);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else {
            layoutDate.setVisibility(View.GONE);
        }

        if (mItem.cancellation_policy.trim().equalsIgnoreCase("") ||
                mItem.cancellation_policy == null)
        {
            layoutCancellation.setVisibility(View.GONE);
        }
        else
        {
            layoutCancellation.setVisibility(View.GONE);
            txtCancellation.setText(mItem.cancellation_policy);
        }
        float paymentPrice = Float.parseFloat( mItem.price );

        txtDetail.setText(mItem.detail);
        txtTnc.setText(mItem.tnc);
//        txtMerchant.setText(mItem.merchant_name);
        txtPrice.setText(mItem.currency.toUpperCase() + " " + mItem.price /*String.format("%.2f", paymentPrice)*/);
        txtTitle.setText(mItem.title);
//        txtRating.setText(mItem.rating + " of 5 (" + mItem.no_of_review + " reviews)");
//        mRatingBar.setRating(mItem.rating);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                hideKeyboard();
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;
            case R.id.btnIntercom:
                Intercom.client().displayConversationsList();
                break;
            case R.id.btnNext:
                if(isNoPayment){
                    doNoPayment();
                }else{
                    doPayWithBraintree();
                }
                break;
        }
    }

    private void doPayWithBraintree() {
        UserORM user    = new ProfilUtils(getActivity()).getUser();
        String country  = (GlobalVar.country == "Malaysia") ? "my" : "sg";

        Log.i(LOG_TAG, "Country: " + country);

        RequestParams params = new RequestParams();
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
        params.add("country", country);
        Log.i(LOG_TAG, params.toString());

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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());
                alertDialogBuilder.setTitle("Network Error");
                alertDialogBuilder
                        .setMessage("Click yes to reload!")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                doPayWithBraintree();
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
                ParserDealBraintreeClientToken parser   = new ParserDealBraintreeClientToken(
                                                            new String(arg2));
                DealBraintreeClientToken bt             = parser.getData();

                if (bt != null && "success".equals(bt.status)) {
                    braintree               = bt;
                    braintreeClientToken    = bt.result.token;
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
        Intent intent       = new Intent(getActivity(), BraintreePaymentActivity.class);
        float paymentPrice;
        if(isSpecial){
            paymentPrice = discountedAmount /*Float.parseFloat(mItem.price)*/;
        }else{
            paymentPrice = Float.parseFloat(mItem.price);
        }
        Log.i(LOG_TAG, "BrainTreeSubmit paymentPrice: " + paymentPrice);

        Customization customization = new Customization.CustomizationBuilder()
                .primaryDescription("Cool Find | Order Id:" + mItem.serial)
                .secondaryDescription(mItem.title)
                .amount(mItem.currency + " " + paymentPrice)
                .submitButtonText("Pay Now").build();
        intent.putExtra(BraintreePaymentActivity.EXTRA_CUSTOMIZATION,
                customization);
        intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN,
                braintreeClientToken);
        startActivityForResult(intent, BRAINTREE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(LOG_TAG, requestCode + " " + resultCode);

        if (requestCode == BRAINTREE) {
            if (resultCode == BraintreePaymentActivity.RESULT_OK) {
                String paymentMethodNonce = data.getStringExtra(
                        BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
                Log.i(LOG_TAG, "On Braintree return");
                postNonceToServer(paymentMethodNonce);
            }
        }
    }

    private void getUserFreeCredits() {
        RequestParams params = new RequestParams();
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token",
                pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

        if ("sgd".equals(mItem.currency.toLowerCase())) {
            params.add("country", "sg");
        } else if ("myr".equals(mItem.currency.toLowerCase())) {
            params.add("country", "my");
        }

        Log.d("FreeCredits", "getUserFreeCredits");

        PARestClient.get(pref.getPref(Config.SERVER),
                Config.API_GET_USER_FREE_CREDITS,
                params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        loadingInternetDialog.show();
                    }

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        super.onSuccess(arg0, arg1, arg2);
                        Log.d("FreeCredits", new String(arg2));
                        UserFreeCreditsResult result = new ParserUserFreeCredits(new String(arg2))
                                .getData();
                        if ("success".equals(result.status)) {
                            freeCredits.setText(mItem.currency + " " + result.result.cs_credit);
                        }
                        loadingInternetDialog.hide();
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                          Throwable arg3) {
                        super.onFailure(arg0, arg1, arg2, arg3);
                        loadingInternetDialog.dismiss();
                    }
                });
    }

    private void deductCredits(final View parentView) {
        RequestParams params = new RequestParams();
        params.add("package_order_serial", mItem.serial);
        params.add("customer_username", pref.getPref(Config.PREF_USERNAME));
//        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

        Log.d("FreeCredits", "deductCredits");
        Log.d("active session token: ", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

        PARestClient.dealPost(pref.getPref(Config.SERVER), Config.DEAL_API_USE_CREDIT, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        btnCredits.setEnabled(false);
                        loadingInternetDialog.show();
                    }

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        super.onSuccess(arg0, arg1, arg2);
                        Log.d("FreeCredits", new String(arg2));
                        DeductCreditsResult dcr = new ParserDeductCredits(new String(arg2))
                                .getData();
                        if ("success".equals(dcr.status)) {
                            freeCredits.setText(mItem.currency + " " + dcr.result.balance_credit);

                            discountedAmount = Float.parseFloat(dcr.result.final_amount);

                            txtPrice.setText(mItem.currency + " " + dcr.result.final_amount);
                            txtPrice.setTextColor(getResources().getColor(R.color.red));
                            finalPrice = dcr.result.final_amount;

                            parentView.findViewById(R.id.txtCredits).setVisibility(View.VISIBLE);
                            parentView.findViewById(R.id.txtBalanceCredits).setVisibility(View.VISIBLE);
                            parentView.findViewById(R.id.txtAvailableCredits).setVisibility(View.GONE);
                            parentView.findViewById(R.id.btnCredits).setVisibility(View.GONE);

                            creditSerial = dcr.result.credit_serial;
                            Log.i(LOG_TAG, "creditSerial: " + creditSerial);
                            creditSerialForPayment = dcr.result.credit_serial;
                            isSpecial = true;
                            type = 0;

                            accordionGroup.disable();

                            isNoPayment = Codes.NO_PAYMENT.equals(dcr.result.condition);
                        } else if ("failed".equals(dcr.status)) {
                            showMessageDialog(dcr.message);
                        }
                        btnCredits.setEnabled(true);
                        loadingInternetDialog.hide();
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                          Throwable arg3) {
                        super.onFailure(arg0, arg1, arg2, arg3);
                        Log.d("FreeCredits", "Error " + new String(arg2));
                        btnCredits.setEnabled(true);
                        loadingInternetDialog.dismiss();
                    }
                });
    }

    private void doNoPayment(){
        String serial           = mItem.serial;
        RequestParams params    = new RequestParams();
        params.add("package_option_serial", serial);
        params.add("username", pref.getPref(Config.PREF_USERNAME));
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
        params.add("credit_serial", creditSerial);
        if (mItem.has_service_time != null || mItem.has_service_time.equalsIgnoreCase("Y")) {
            params.put("start_time", mItem.selected_start_date);
            params.add("end_time", mItem.selected_end_date);
        }
        if (mItem.has_service_address != null && mItem.has_service_address.equalsIgnoreCase("Y")) {
            params.add("address", mItem.selected_address);
            params.add("state", mItem.selected_state);
            params.add("city", mItem.selected_city);
            params.add("postcode", mItem.selected_postal_code);
        }
        Log.i(LOG_TAG, params.toString());

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                forceLoadingInternetDialog.show();
            }

            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                ParserBasicResult parser = new ParserBasicResult(new String(arg2));
                if ("success".equals(parser.getStatus())) {
                    isDiscounted = true;
                    discountedAmount = 0;
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
                forceLoadingInternetDialog.dismiss();
            }
        };

        PARestClient.dealPost(pref.getPref(Config.SERVER), Config.DEAL_API_POST_PACKAGE_ORDER,
                params, responseHandler);
    }

    private void validatePromoCode(final String promoCode, final View parentView) {
        RequestParams params = new RequestParams();
        params.add("package_option_serial", mItem.serial);
        params.add("username", pref.getPref(Config.PREF_USERNAME));
//        params.add("active_session_token",
//                pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
        params.add("promo_code", promoCode);
//        params.add("app", "PA");

        Log.d("PromoCode", "validatePromoCode");

        PARestClient.dealPost(pref.getPref(Config.SERVER), Config.DEAL_API_CHECK_PROMO, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        loadingInternetDialog.show();
                    }

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        super.onSuccess(arg0, arg1, arg2);
                        Log.d("PromoCode", new String(arg2));
                        PromoCodeResult pcr = new ParserValidatePromoCode(new String(arg2))
                                .getData();
                        if ("success".equals(pcr.status)) {
                            Log.d("PromoCode", pcr.result.new_amt);

                            discountedAmount = Float.parseFloat(pcr.result.new_amt);
                            UserORM user = new ProfilUtils(getActivity()).getUser();
                            String currency = "";
                            if (user != null) {
                                currency = user.result.currency;
                            }

                            if (TextUtils.isEmpty(currency)) {
                                currency = mItem.currency;
                            }

//                            TextView txtPrice = (TextView) parentView.findViewById(R.id.txtPrice);
                            txtPrice.setText(currency + " " + pcr.result.new_amt);
                            txtPrice.setTextColor(getResources().getColor(R.color.red));
                            finalPrice = pcr.result.new_amt;

                            ((View) inputPromoCode.getParent()).setVisibility(View.INVISIBLE);
                            parentView.findViewById(R.id.txtPromoCode).setVisibility(View.VISIBLE);


                            validatedPromoCode = promoCode;
                            promoCodeForPayment = promoCode;
                            isSpecial = true;
                            type = 1;

                            accordionGroup.disable();
                        } else if ("failed".equals(pcr.status)) {
                            showMessageDialog(pcr.message);
                        }
                        loadingInternetDialog.hide();
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                          Throwable arg3) {
                        super.onFailure(arg0, arg1, arg2, arg3);
                        loadingInternetDialog.dismiss();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                getActivity());

                        // set title
                        alertDialogBuilder.setTitle("Network Error");

                        // set dialog message
                        alertDialogBuilder
                                .setMessage("Click yes to retry!")
                                .setCancelable(true)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int id) {
                                                // if this button is clicked,
                                                // close
                                                // current activity
                                                validatePromoCode(promoCode, parentView);
                                            }
                                        })
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int id) {
                                                // if this button is clicked,
                                                // just close
                                                // the dialog box and do nothing
                                                dialog.cancel();
                                            }
                                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });
    }


    private void postNonceToServer(String nonce) {
        String serial           = mItem.serial;
        RequestParams params    = new RequestParams();
        params.add("package_option_serial", serial);
        params.add("username", pref.getPref(Config.PREF_USERNAME));
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
        params.put("payment_nonce", nonce);
        params.add("braintree_id", braintree.result.customerId);
        if (mItem.has_service_time != null || mItem.has_service_time.equalsIgnoreCase("Y")) {
            params.put("start_time", mItem.selected_start_date);
            params.add("end_time", mItem.selected_end_date);
        }
        if (mItem.has_service_address != null && mItem.has_service_address.equalsIgnoreCase("Y")) {
            params.add("address", mItem.selected_address);
            params.add("state", mItem.selected_state);
            params.add("city", mItem.selected_city);
            params.add("postcode", mItem.selected_postal_code);
        }   Log.i(LOG_TAG, "Post to Nanigans");
        if(isSpecial){
            switch (type){      //  type: 0 = balance after free credits, 1 = after promo code discount
                case 0:
                    params.add("credit_serial", creditSerialForPayment);    Log.i(LOG_TAG, "added param for free credits" + creditSerial + " " + creditSerialForPayment);
                    isSpecial = false;
                    isDiscounted = true;
                    break;
                case 1:
                    params.add("promo_code", promoCodeForPayment);          Log.i(LOG_TAG, "added param for promo code " + promoCodeForPayment);
                    isSpecial = false;
                    isDiscounted = true;
                    break;
            }
        }
        Log.i(LOG_TAG, params.toString());

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                forceLoadingInternetDialog.show();
            }

            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                ParserBasicResult parser = new ParserBasicResult(new String(arg2));
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
                forceLoadingInternetDialog.dismiss();
            }
        };

        PARestClient.dealPost(pref.getPref(Config.SERVER), Config.DEAL_API_POST_PACKAGE_ORDER,
                params, responseHandler);
    }

    private void showDialogOrderSuccess() {

        if(isDiscounted) {
            NanigansEventManager.getInstance().trackNanigansEvent(NanigansEventManager.TYPE.PURCHASE, "appointment",
                    new NanigansEventParameter("sku", mItem.title),
                    new NanigansEventParameter("value", String.format("%.2f", discountedAmount).replace(".", "")),
                    new NanigansEventParameter("unique", mItem.serial),
                    new NanigansEventParameter("currency", mItem.currency)
            );
            isDiscounted = false;       Log.i(LOG_TAG, "Nanigans used discounted amount: " + discountedAmount);
        }else{
            NanigansEventManager.getInstance().trackNanigansEvent(NanigansEventManager.TYPE.PURCHASE, "appointment",
                    new NanigansEventParameter("sku", mItem.title),
                    new NanigansEventParameter("value", mItem.price.replace(".", "")),
                    new NanigansEventParameter("unique", mItem.serial),
                    new NanigansEventParameter("currency", mItem.currency)
            );
            isDiscounted = false;       Log.i(LOG_TAG, "Nanigans used original mItem.price: " + mItem.price);
        }


        View v;
        v = inflater.inflate(R.layout.dialog_order_success, null);
        TextView txtServiceRequestID = (TextView) v
                .findViewById(R.id.txtServiceRequestID);
        txtServiceRequestID.setText(mItem.serial);

        final ActivityLanding activityLanding = (ActivityLanding) getActivity();

        v.findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                activityLanding.setBottomBar(2);
//                mListener.doFragmentChange(new FragmentOrder(false, "PA"), true, "");
                ((ActivityLanding) getActivity()).replaceFragment(new FragmentOrder(false, "PA"), true);
                dialogOrderSuccess.hide();
            }
        });

        v.findViewById(R.id.btnNext2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityLanding.setBottomBar(2);
//                mListener.doFragmentChange(new FragmentOrder(false, "PA"), true, "");
                ((ActivityLanding) getActivity()).replaceFragment(new FragmentOrder(false, "PA"), true);
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
                doPayWithBraintree();
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
