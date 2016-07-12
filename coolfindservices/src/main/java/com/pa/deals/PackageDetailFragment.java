package com.pa.deals;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.pa.common.Config;
import com.pa.common.GlobalVar;
import com.pa.common.ImageLoader;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.common.ProfilUtils;
import com.pa.landing.ActivityLanding;
import com.pa.order.FragmentOrder;
import com.pa.parser.ParseDealPackageDetail;
import com.pa.parser.ParserBasicResult;
import com.pa.parser.ParserDealBraintreeClientToken;
import com.pa.pojo.DealBraintreeClientToken;
import com.pa.pojo.PJobItem;
import com.pa.pojo.PackageJobItem;
import com.pa.pojo.PackageListDetailItem;
import com.pa.pojo.UserORM;
import com.coolfindservices.androidconsumer.R;

import org.apache.http.Header;

import io.intercom.android.sdk.Intercom;

/**
 * Created by Steven on 04/11/2015.
 */
public class PackageDetailFragment extends MyFragment implements View.OnClickListener {

    private static final String LOG_TAG = "PackageDetailFragment";
    protected PackageJobItem mPackageJobItem;
    private float paymentPrice = 0.0f;
    private String MY_URI, AttachmentImageUrl;
    private TextView mPageTitle, mTitle, mPrice, mTnC, mDetail, mCompanyName, mTotalReview, mReviewPoint,
                    mCancellationPolicy;
    private LinearLayout mContentGallery, mLayoutCancellation;

    private PackageListDetailItem mItem = null;
    private OnFragmentChangeListener listener;
    private RatingBar ratingBar;
    private ImageLoader loader;

    private Boolean isDataMapped;

    public static PackageDetailFragment newInstance(PackageListDetailItem param1) {
        PackageDetailFragment fragment = new PackageDetailFragment();
        fragment.setItem(param1);
        return fragment;
    }

    public void setJobItem(PJobItem jobItem) {
        this.mPackageJobItem = new PackageJobItem(jobItem);
    }

    public PackageDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  no load back stack
        AttachmentImageUrl = PARestClient.getDealAbsoluteUrl(pref.getPref(Config.SERVER),
                Config.PACKAGE_IMAGE_PATH);
        loader          = new ImageLoader(getActivity());

        onGetOrderDetail();
        isDataMapped = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_package_detail, container, false);

        v.findViewById(R.id.btnHome).setOnClickListener(this);
        v.findViewById(R.id.btnIntercom).setOnClickListener(this);
        v.findViewById(R.id.btnBack).setOnClickListener(this);
        v.findViewById(R.id.btnNext).setOnClickListener(this);

        mPageTitle          = (TextView) v.findViewById(R.id.page_title);
        mTitle              = (TextView) v.findViewById(R.id.title);
        mPrice              = (TextView) v.findViewById(R.id.price);
        mTnC                = (TextView) v.findViewById(R.id.tnc);
        mDetail             = (TextView) v.findViewById(R.id.detail);
        mCompanyName        = (TextView) v.findViewById(R.id.companyName);
        ratingBar           = (RatingBar) v.findViewById(R.id.ratingBar1);
        mTotalReview        = (TextView) v.findViewById(R.id.totalReviewNumber);
        mReviewPoint        = (TextView) v.findViewById(R.id.ratingPoint);
        mCancellationPolicy = (TextView) v.findViewById(R.id.txt_cancellation_policy);
        mContentGallery     = (LinearLayout) v.findViewById(R.id.gallery_content);
        mLayoutCancellation = (LinearLayout) v.findViewById(R.id.layout_cancellation);

//        AttachmentImageUrl = PARestClient.getDealAbsoluteUrl(pref.getPref(Config.SERVER),
//                Config.PACKAGE_IMAGE_PATH);
//        loader          = new ImageLoader(getActivity());

//        Log.i("pass data", mItem.detail);
//        String photoUrl = imageUrl + "/attachment/";

        if(!isDataMapped)
            mapInitialData();
//        onGetOrderDetail();
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            analytic.trackScreen("Package Details");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
                isDataMapped = false;
                doNext();
                break;
            case R.id.btnHome:
                for(int i = getActivity().getSupportFragmentManager().getBackStackEntryCount(); i > 1; i--)
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;
//            case R.id.btnPromoCode:
//                String promoCode = inputPromoCode.getText().toString();
//                if ("".equals(promoCode)) {
//                    showMessageDialog("The Promo Code field is required");
//                    return;
//                }
//                hideKeyboard(inputPromoCode);
//                validatePromoCode(promoCode, v.getRootView());
//                break;
//            case R.id.btn_merchant_profile:
//                onGetMerchantProfile();
//                break;
        }
    }

    private void doNext()
    {
        Log.i("do next ", "clicked !");
        if ("Y".equalsIgnoreCase(mPackageJobItem.has_service_address) &&
                "Y".equalsIgnoreCase(mPackageJobItem.has_service_time)) {
            PackageScheduleFragment fragment = PackageScheduleFragment.newInstance(mPackageJobItem);
            listener.doFragmentChange(fragment, true, "");
        }else if("Y".equalsIgnoreCase(mPackageJobItem.has_service_time)){

            PackageScheduleFragment fragment = PackageScheduleFragment.newInstance(mPackageJobItem);
            listener.doFragmentChange(fragment, true, "");

        }else if("Y".equalsIgnoreCase(mPackageJobItem.has_service_address)){
            PackageScheduleAddFragment fragment = PackageScheduleAddFragment.newInstance(mPackageJobItem);
            listener.doFragmentChange(fragment, true, "");
        }
        else {
            //  work around
            PackagePreviewFragment fragment = PackagePreviewFragment.newInstance(mPackageJobItem);
            listener.doFragmentChange(fragment, true, "");
            //  original steps
            //doPayWithBraintree();
        }

    }

    private void onGetOrderDetail() {
        loadingInternetDialog.show();

        RequestParams params = new RequestParams();
        params.add("package_option_serial",mItem.serial);
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

        PARestClient.dealGet(pref.getPref(Config.SERVER),
                Config.DEAL_API_GET_PACKAGE_DETAIL, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, String content) {
                        // TODO Auto-generated method stub
//                super.onSuccess(statusCode, content);
                        Log.i(LOG_TAG, "offer content: " + content);

                        ParseDealPackageDetail mParser = new ParseDealPackageDetail(content);

                        if (mParser.status) {
                            mPackageJobItem = mParser.parsePromoJobItem();      Log.i(LOG_TAG,"is promo?: " + mPackageJobItem.is_promotion);
                            mapInitialData();
                        }

                        loadingInternetDialog.dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error, String content) {
                        // TODO Auto-generated method stub
//                super.onFailure(statusCode, error, content);

                        Log.i("failure", "request fail");
                        loadingInternetDialog.dismiss();
                    }
                });

//        try{
//
//            RequestParams params = new RequestParams();
//            params.add("package_option_serial",mItem.serial);
//            params.add("session_username", pref.getPref(Config.PREF_USERNAME));
//            params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
//
//            PARestClient.dealGet(pref.getPref(Config.SERVER),
//                    Config.DEAL_API_GET_PACKAGE_DETAIL, params, mHandler);
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
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

    public void setItem(PackageListDetailItem mItem) {
        this.mItem = mItem;
    }

    public void mapInitialData() {
        Log.i("map data ", mPackageJobItem.rating + "");

        paymentPrice = Float.parseFloat(mPackageJobItem.price);

        //  rename page if is promotion
        if(mPackageJobItem.is_promotion.equalsIgnoreCase("Y"))
        {  mPageTitle.setText("PROMOTION DETAILS");}
        else{
            mPageTitle.setText("PACKAGE DETAILS");
        }

        mTitle.setText(mPackageJobItem.title);
        mPrice.setText(mPackageJobItem.currency.toUpperCase() + " " + String.format("%.2f", paymentPrice));
        mDetail.setText(mPackageJobItem.detail);
        mTnC.setText(mPackageJobItem.tnc);
        mCompanyName.setText(mPackageJobItem.merchant_name);
        ratingBar.setRating(mPackageJobItem.rating);
        mReviewPoint.setText(mPackageJobItem.rating + " of 5");
        mTotalReview.setText("("+mPackageJobItem.no_of_review+" " +"reviews)" );

        if (mPackageJobItem.cancellation_policy.trim().equalsIgnoreCase("") ||
                mPackageJobItem.cancellation_policy == null)
        {
            mLayoutCancellation.setVisibility(View.GONE);
        }
        else
        {
            mLayoutCancellation.setVisibility(View.VISIBLE);
            mCancellationPolicy.setText(mPackageJobItem.cancellation_policy);
        }

        try{

            String photoUrl = AttachmentImageUrl + "/attachment/";
            for (String photoName : mPackageJobItem.attachment_photo) {
                Log.i(LOG_TAG, photoName);
                View photo = inflater.inflate(R.layout.item_upload_photo, null);
                ImageView img = (ImageView) photo.findViewById(R.id.img);

                String url = photoUrl + photoName;
                loader.DisplayImage(url, getActivity(), img, false);
                img.setTag(url);

                mContentGallery.addView(photo);
                photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogPhotoPreview(v);
                    }
                });
            }

            int galleryVisiable = (mPackageJobItem.attachment_photo.size() > 0) ? View.VISIBLE : View.GONE;
            mContentGallery.setVisibility(galleryVisiable);

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println(requestCode + " " + resultCode);
        if (requestCode == BRAINTREE) {
            if (resultCode == BraintreePaymentActivity.RESULT_OK) {
                String paymentMethodNonce = data
                        .getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
                postNonceToServer(paymentMethodNonce);
            }
        }
    }

    /*
        BrainTree Payment
     */
    private static final int BRAINTREE = 100;
    String braintreeClientToken = "";
    DealBraintreeClientToken braintree;

    void doPayWithBraintree() {

        UserORM user    = new ProfilUtils(getActivity()).getUser();
        String country  = (GlobalVar.country == "Malaysia") ? "my" : "sg";

        RequestParams params = new RequestParams();
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token",
                pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
        params.add("country", country);

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler(){

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
                                doNext();
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
        Customization customization = new Customization.CustomizationBuilder()
                .primaryDescription("Page Advisor | Order Id:" + mPackageJobItem.serial)
                .secondaryDescription(mPackageJobItem.title)
                .amount(mPackageJobItem.currency + " " + paymentPrice)
                .submitButtonText("Pay Now").build();
        intent.putExtra(BraintreePaymentActivity.EXTRA_CUSTOMIZATION,
                customization);

        intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN,
                braintreeClientToken);

        startActivityForResult(intent, BRAINTREE);

    }

    void postNonceToServer(String nonce) {
        String serial = mPackageJobItem.serial;

        Log.i("postNonceToServer 1","serial :" + serial + "username :" + pref.getPref(Config.PREF_USERNAME) + "payment_nonce :"+ nonce);
        Log.i("postNonceToServer 2","braintree_id :" + braintree.result.customerId + "active_session_token :" + pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

        RequestParams params = new RequestParams();
        params.add("package_option_serial", serial);
        params.add("username", pref.getPref(Config.PREF_USERNAME));
        params.add("address", "");
        params.add("state", "");
        params.add("city", "");
        params.add("postcode", "");
        params.add("start_time", "");
        params.add("end_time", "");
        params.put("payment_nonce", nonce);
        params.add("braintree_id", braintree.result.customerId);
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token",
                pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

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
                            Log.d("Payment", "Cancel because it is not success");
                            showDialogOrderCancel();
                        }
                        forceLoadingInternetDialog.dismiss();
                    };

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
                listener.doFragmentChange(new FragmentOrder(false, "PA"), true, "");
                dialogOrderSuccess.hide();
            }
        });

        v.findViewById(R.id.btnNext2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.doFragmentChange(new FragmentOrder(false, "PA"), true, "");
                dialogOrderSuccess.hide();
                // Go to job detail
            }
        });

        dialogOrderSuccess = new Dialog(getActivity(), R.style.PauseDialog);
        dialogOrderSuccess.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                listener.doFragmentChange(new FragmentOrder(), false, "");

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
                doNext();
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
