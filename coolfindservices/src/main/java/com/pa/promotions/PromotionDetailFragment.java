package com.pa.promotions;


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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

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
import com.pa.common.TimeUtils;
import com.pa.order.FragmentOrder;
import com.pa.parser.ParserBasicResult;
import com.pa.parser.ParserDealBraintreeClientToken;
import com.pa.parser.ParserDealValidatePromoCode;
import com.pa.parser.ParserMerchantProfile;
import com.pa.pojo.DealBraintreeClientToken;
import com.pa.pojo.DealPromoCodeResult;
import com.pa.pojo.MerchantProfile;
import com.pa.pojo.MerchantReview;
import com.pa.pojo.PromotionItem;
import com.pa.pojo.UserORM;
import com.coolfindservices.androidconsumer.R;

import org.apache.http.Header;

import java.util.ArrayList;

import io.intercom.android.sdk.Intercom;

public class PromotionDetailFragment extends MyFragment implements View.OnClickListener {

    private static final String LOG_TAG = "PromotionDetailFrag";
    private float paymentPrice = 0.0f;
    private MerchantProfile mMerchantProfile;
    private String MY_URI;
    ArrayList<MerchantReview> arr_review = new ArrayList<>();
    ArrayList<String> arr_image = new ArrayList<>();

    private TextView mTitle, mPrice, mTnC, mDetail;
    private ImageView mCoverImage;
    private ImageLoader loader, mMerchantLoader;
    private LinearLayout mContentGallery;
    private EditText inputPromoCode;
    private Button mBtnMerchantProfile;

    private OnFragmentChangeListener listener;
    private PromotionItem mItem = null;
    private Dialog mMerchantDialog;
    private ViewFlipper mMerchantFlipper;

    public static PromotionDetailFragment newInstance(PromotionItem param1) {
        PromotionDetailFragment fragment = new PromotionDetailFragment();
        fragment.setItem(param1);
        return fragment;
    }

    public PromotionDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MY_URI = PARestClient.getAbsoluteUrl(pref.getPref(Config.SERVER), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_promotion_detail, container, false);

        v.findViewById(R.id.btnIntercom).setOnClickListener(this);
        v.findViewById(R.id.btnBack).setOnClickListener(this);
        v.findViewById(R.id.btnNext).setOnClickListener(this);
        v.findViewById(R.id.btnPromoCode).setOnClickListener(this);

        mTitle              = (TextView) v.findViewById(R.id.title);
        mPrice              = (TextView) v.findViewById(R.id.price);
        mTnC                = (TextView) v.findViewById(R.id.tnc);
        mDetail             = (TextView) v.findViewById(R.id.detail);
        mCoverImage         = (ImageView) v.findViewById(R.id.coverImage);
        mContentGallery     = (LinearLayout) v.findViewById(R.id.gallery_content);
        inputPromoCode      = (EditText) v.findViewById(R.id.inputPromoCode);
        mBtnMerchantProfile = (Button) v.findViewById(R.id.btn_merchant_profile);

        mTitle.setText(mItem.title);
        mPrice.setText(mItem.currency + " " + mItem.price);
        mDetail.setText(mItem.detail);
        mTnC.setText(mItem.tnc);
        mBtnMerchantProfile.setOnClickListener(this);

        String imageUrl = PARestClient.getDealAbsoluteUrl(pref.getPref(Config.SERVER),
                Config.DEAL_IMAGE_PATH);
        loader          = new ImageLoader(getActivity());
        mMerchantLoader = new ImageLoader(getActivity());

        String coverUrl = imageUrl + "/cover/" + mItem.cover_photo;
        mCoverImage.setTag(coverUrl);
        loader.setDefaultImage(R.drawable.promo_placeholder);
        loader.DisplayImage(coverUrl, getActivity(), mCoverImage, false);

        String photoUrl = imageUrl + "/attachment/";
        for (String photoName : mItem.attachment_photo) {
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

        int galleryVisiable = (mItem.attachment_photo.size() > 0) ? View.VISIBLE : View.GONE;
        mContentGallery.setVisibility(galleryVisiable);

        paymentPrice = Float.parseFloat(mItem.price);

        return v;
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
                doNext();
                break;
            case R.id.btnPromoCode:
                String promoCode = inputPromoCode.getText().toString();
                if ("".equals(promoCode)) {
                    showMessageDialog("The Promo Code field is required");
                    return;
                }
                hideKeyboard(inputPromoCode);
                validatePromoCode(promoCode, v.getRootView());
                break;
            case R.id.btn_merchant_profile:
                onGetMerchantProfile();
                break;
        }
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

    private void onGetMerchantProfile() {
        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                // TODO Auto-generated method stub
                super.onSuccess(arg0, arg1, arg2);

                try {
                    Log.i(LOG_TAG, new String(arg2));
                    ParserMerchantProfile parser = new ParserMerchantProfile(
                            new String(arg2));
                    mMerchantProfile = parser.getData();
                    arr_image = mMerchantProfile.gallery_image;
                    arr_review = mMerchantProfile.reviews;
                    onShowMerchantProfileDialog();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                Log.i(LOG_TAG, content);
            }

            @Override
            public void onStart() {
                super.onStart();
                loadingInternetDialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                loadingInternetDialog.dismiss();
            }
        };

        try {
            RequestParams params = new RequestParams();
            params.add("merchant_username", mItem.merchant_username);
            params.add("session_username", pref.getPref(Config.PREF_USERNAME));
            params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

            PARestClient.get(pref.getPref(Config.SERVER), "user/merchant-profile",
                    params, responseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onShowMerchantProfileDialog() {
        analytic.trackScreen("Merchant Profile");

        View v = inflater.inflate(R.layout.dialog_merchant_profil, null);
        v.findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mMerchantDialog.dismiss();
            }
        });
        mMerchantFlipper = (ViewFlipper) v.findViewById(R.id.flipper);

        ListView list = (ListView) v.findViewById(R.id.list);
        GridView grid = (GridView) v.findViewById(R.id.grid);

        list.setAdapter(new ReviewAdapter());
        grid.setAdapter(new GridAdapter());

        View menu1, menu2, menu3, menu1b, menu2b, menu3b;

        menu1 = v.findViewById(R.id.menu1);
        menu2 = v.findViewById(R.id.menu2);
        menu3 = v.findViewById(R.id.menu3);

        menu1b = v.findViewById(R.id.menu1b);
        menu2b = v.findViewById(R.id.menu2b);
        menu3b = v.findViewById(R.id.menu3b);

        mMerchantFlipper.setTag(R.id.menu1b, menu1b);
        mMerchantFlipper.setTag(R.id.menu2b, menu2b);
        mMerchantFlipper.setTag(R.id.menu3b, menu3b);

        setActive(mMerchantFlipper, 0);

        View.OnClickListener menuClick = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.menu1:
                        setActive(mMerchantFlipper, 0);
                        analytic.trackScreen("Merchant Profile - About");
                        break;
                    case R.id.menu2:
                        setActive(mMerchantFlipper, 1);
                        analytic.trackScreen("Merchant Profile - Reviews");
                        break;
                    case R.id.menu3:
                        setActive(mMerchantFlipper, 2);
                        analytic.trackScreen("Merchant Profile - Gallery");
                        break;
                }
            }
        };

        menu1.setOnClickListener(menuClick);
        menu2.setOnClickListener(menuClick);
        menu3.setOnClickListener(menuClick);

        ImageView cover_timeline = (ImageView) v
                .findViewById(R.id.img_cover_timeline);
        TextView txtName = (TextView) v.findViewById(R.id.txtName);
        TextView ratingCount = (TextView) v.findViewById(R.id.ratingCount);
        TextView ratingCount1 = (TextView) v.findViewById(R.id.ratingCount1);

        RatingBar ratingBar = (RatingBar) v.findViewById(R.id.ratingBar1);

        TextView about = (TextView) v.findViewById(R.id.about);
        TextView service = (TextView) v.findViewById(R.id.service);

        cover_timeline.setTag(MY_URI + "user/merchant-image?image_name="
                + mMerchantProfile.cover_photo);
        mMerchantLoader.DisplayImage(MY_URI + "user/merchant-image?image_name="
                + mMerchantProfile.cover_photo, getActivity(), cover_timeline);

        txtName.setText(mMerchantProfile.merchant_name);
        ratingCount.setText(" " + mMerchantProfile.overall_rating + " out of 5");
        ratingCount1.setText(mMerchantProfile.rating_count + " review(s)");

        ratingBar.setRating(Float.parseFloat(mMerchantProfile.overall_rating));
        about.setText(mMerchantProfile.about);
        service.setText(TextUtils.join("\n", mMerchantProfile.service_list));

        mMerchantDialog = new Dialog(getActivity(), R.style.PauseDialog);

        mMerchantDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mMerchantDialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        mMerchantDialog.setContentView(v);
        mMerchantDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mMerchantDialog.show();
    }

    private void setActive(ViewFlipper flipper, int i) {
        View menu1b = (View) flipper.getTag(R.id.menu1b);
        View menu2b = (View) flipper.getTag(R.id.menu2b);
        View menu3b = (View) flipper.getTag(R.id.menu3b);

        menu1b.setVisibility(View.INVISIBLE);
        menu2b.setVisibility(View.INVISIBLE);
        menu3b.setVisibility(View.INVISIBLE);

        switch (i) {
            case 0:
                menu1b.setVisibility(View.VISIBLE);
                break;
            case 1:
                menu2b.setVisibility(View.VISIBLE);
                break;
            case 2:
                menu3b.setVisibility(View.VISIBLE);
                break;
        }
        flipper.setDisplayedChild(i);
    }

    private void doNext()
    {
        if ("Y".equalsIgnoreCase(mItem.has_service_address) ||
                "Y".equalsIgnoreCase(mItem.has_service_time)) {
            PromotionScheduleFragment fragment = PromotionScheduleFragment.newInstance(mItem);
            listener.doFragmentChange(fragment, true, "");
        } else {
            doPayWithBraintree();
        }
    }

    public void setItem(PromotionItem mItem) {
        this.mItem = mItem;
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

        UserORM user = new ProfilUtils(getActivity()).getUser();
        String country  = (GlobalVar.country == "Malaysia") ? "my" : "sg";

        RequestParams params = new RequestParams();
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token",
                pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
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

//            @Override
//            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
//                                  Throwable arg3) {
//                // TODO Auto-generated method stub
//                super.onFailure(arg0, arg1, arg2, arg3);
//                simpleToast("Failed " + new String(arg2));
//
//            }

        };

        PARestClient.dealGet(pref.getPref(Config.SERVER), "order/payment-token",
                params, responseHandler);

    }

    public void onBraintreeSubmit() {
        Intent intent = new Intent(getActivity(),
                BraintreePaymentActivity.class);
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

    void postNonceToServer(String nonce) {
        String serial = mItem.serial;

        RequestParams params = new RequestParams();
        params.add("promotion_serial", serial);
        params.add("username", pref.getPref(Config.PREF_USERNAME));
        params.add("delivery_time", "");
        params.add("promo_code", validatedPromoCode);
        params.add("address", "");
        params.add("state", "");
        params.add("city", "");
        params.add("postcode", "");
        params.put("payment_nonce", nonce);
        params.add("braintree_id", braintree.result.customerId);
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token",
                pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

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
                            Log.d("Payment", "Cancel because it is not success");
                            showDialogOrderCancel(parser.getStatus());
                        }
                        forceLoadingInternetDialog.dismiss();
                    };

                    @Override
                    public void onFailure(int statusCode, Throwable error, String content) {
                        // TODO Auto-generated method stub
                        showDialogOrderCancel(content);
                        forceLoadingInternetDialog.setCancelable(true);
                    }
                });
    }

    private void showDialogOrderCancel(String reason) {
        View v = inflater.inflate(R.layout.dialog_order_cancel, null);
        TextView txtServiceRequestID = (TextView) v
                .findViewById(R.id.txtServiceRequestID);
        TextView failureReason = (TextView) v.findViewById(R.id.failure_reason);

        txtServiceRequestID.setText(mItem.serial);
        failureReason.setText(reason);

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

    Dialog dialogOrderSuccess;
    void showDialogOrderSuccess() {
//        analytic.trackScreen("Job Offer - Thank You");

//        Map eventData = new HashMap<>();
//        eventData.put("job_request_id", item.serial);
//        Intercom.client().logEvent("job-request-payment", eventData);
//
//        String subServices = "";
//        ArrayList<String> skuArrayList = new ArrayList<>();
//        if (arrDetail != null) {
//            for (int i  = 0; i < arrDetail.size(); i++) {
//                if (i > 0)
//                    subServices += ",";
//                subServices += arrDetail.get(i).service_detail_name;
//                skuArrayList.add(arrDetail.get(i).service_detail_name);
//            }
//        } else {
//            subServices = item.service_category_name;
//        }
//
//        Log.d(TAG, "Sub Services: " + subServices);
//        Log.d(TAG, "Price: " + getAmount());
//        Log.d(TAG, "Currency: " + item.currency);
//
//        AppsFlyerLib.setCurrencyCode(item.currency);
//        Map<String,Object> event = new HashMap<>();
//        event.put("Price",getAmount());
//        event.put("Subservices", subServices);
//        AppsFlyerLib.trackEvent(getActivity().getApplicationContext(), "Appointment", event);
//
//        DecimalFormat df = new DecimalFormat("0.00");
//        Log.d(TAG, "Formatted Price: " + df.format(Double.parseDouble(getAmount())).replace(".", ""));
//
//        String[] skuArray = skuArrayList.toArray(new String[skuArrayList.size()]);
//        String[] valueArray = new String[skuArrayList.size()];
//        Log.d(TAG, "valueArray length: " + valueArray.length);
//        for (int k=0; k<valueArray.length; k++) {
//            valueArray[k] = (k == 0 ? df.format(Double.parseDouble(getAmount())).replace(".", "") : "000");
//        }
//
//        NanigansEventManager.getInstance().trackNanigansEvent(NanigansEventManager.TYPE.PURCHASE, "appointment",
//                new NanigansEventParameter("sku", skuArray),
//                new NanigansEventParameter("value", valueArray),
//                new NanigansEventParameter("unique", item.serial),
//                new NanigansEventParameter("currency", item.currency)
//        );

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
                listener.doFragmentChange(new FragmentOrder(false, "PR"), true, "");
                dialogOrderSuccess.hide();
            }
        });

        v.findViewById(R.id.btnNext2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.doFragmentChange(new FragmentOrder(false, "PR"), true, "");
                dialogOrderSuccess.hide();
                // Go to job detail
            }
        });

//        Map eventData2 = new HashMap<>();
//        eventData2.put("job_request_id", item.serial);
//        eventData2.put("job_proposal_id", arrProposal.get(selectedProposal).serial);
//        Intercom.client().logEvent("job-appointment", eventData2);
//
//        analytic.trackECommerce(arrProposal.get(selectedProposal).serial,
//                arrProposal.get(selectedProposal).merchant_company_name,
//                getAmount(),
//                item.currency,
//                getJobTitle(item.title),
//                item.service_category_name
//        );

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

    /*
        Promo Code
     */
    String validatedPromoCode = "";


    private void validatePromoCode(final String promoCode, final View parentView) {
        RequestParams params = new RequestParams();
        params.add("promotion_serial", mItem.serial);
        params.add("username", pref.getPref(Config.PREF_USERNAME));
        params.add("promo_code", promoCode);
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token",
                pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

        Log.d("PromoCode", "validatePromoCode");

        PARestClient.dealPost(pref.getPref(Config.SERVER),
                "promotion/verify-code",
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
                        Log.d("PromoCode", new String(arg2));
                        DealPromoCodeResult pcr = new ParserDealValidatePromoCode(new String(arg2))
                                .getData();
                        if ("success".equals(pcr.status)) {
                            mPrice.setText(mItem.currency + " " + pcr.result.discounted_amount);
                            mPrice.setTextColor(getResources().getColor(R.color.red));

                            ((View)parentView.findViewById(R.id.inputPromoCode).getParent()).setVisibility(View.INVISIBLE);
                            parentView.findViewById(R.id.txtPromoCode).setVisibility(View.VISIBLE);

                            paymentPrice = Float.parseFloat(pcr.result.discounted_amount);

                            validatedPromoCode = promoCode;
                            mItem.promo_code = promoCode;

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

    class ReviewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return arr_review.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        MyHolder holder;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_merchant_review,
                        null);
                holder = new MyHolder();
                holder.ratingBar = (RatingBar) convertView
                        .findViewById(R.id.ratingBar1);
                holder.name = (TextView) convertView.findViewById(R.id.txtName);
                holder.comment = (TextView) convertView
                        .findViewById(R.id.services);
                convertView.setTag(holder);
            } else {
                holder = (MyHolder) convertView.getTag();
            }

            MerchantReview tmp = arr_review.get(position);
            try {
                holder.ratingBar.setRating((int) Float
                        .parseFloat(tmp.review_score));
                holder.name.setText("Reviewed by "
                        + tmp.customer_username
                        + " on"
                        + TimeUtils.timeStampToDDMMYY("" + tmp.createdtime
                        * 1000));
                holder.comment.setText(tmp.service_description);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        class MyHolder {
            RatingBar ratingBar;
            TextView name, comment;
        }
    }

    class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return arr_image.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        MyHolder holder;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_merchant_gallery, null);

                holder = new MyHolder();
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                convertView.setTag(holder);
            } else {
                holder = (MyHolder) convertView.getTag();
            }
            String uri = arr_image.get(position);
            holder.img.setTag(MY_URI + "user/merchant-image?image_name=" + uri);
            mMerchantLoader.DisplayImage(MY_URI + "user/merchant-image?image_name="
                    + uri, getActivity(), holder.img);

            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View paramView) {
                    showDialogPhotoPreview(paramView);
                }
            });
            return convertView;
        }

        class MyHolder {
            public ImageView img;
        }
    }
}
