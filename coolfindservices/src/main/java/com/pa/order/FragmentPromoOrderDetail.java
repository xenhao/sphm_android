package com.pa.order;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.Config;
import com.pa.common.FormUtils;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.parser.ParserPromoJobDetail;
import com.pa.pojo.JobItem;
import com.pa.pojo.PromoJobItem;
import com.coolfindservices.androidconsumer.R;

import org.json.JSONObject;

public class FragmentPromoOrderDetail extends MyFragment {

    private static final String LOG_TAG = "FragPromoOrderDetail";
    protected PromoJobItem mPromoJobItem;
    protected boolean isCompleted = false;

    private TextView mTxtSerial;
    private TextView mTxtTitle;
    private TextView mTxtStatus;
    private TextView mTxtMerchantName;
    private TextView mTxtMerchantContact;
    private TextView mTxtPrice;
    private TextView mTxtDeliveryTime;
    private TextView mTxtDeliveryAddress;
    private TextView mTxtDetail;
    private TextView mTxtTnc;
    private TextView mTxtDetailHeader;
    private TextView mTxtTncHeader;
    private TextView mTxtApptHeader;
    private TextView mTxtCall;
    private LinearLayout mLayoutDetailContent, mLayoutDetail;
    private LinearLayout mLayoutTncContent, mLayoutTnc;
    private LinearLayout mLayoutApptContent;
    private Button mBtnAction;

    private Dialog mDialogCompleteJob;
    private OnFragmentChangeListener mListener;

    public static FragmentPromoOrderDetail newInstance(JobItem jobItem, boolean isCompleted) {
        FragmentPromoOrderDetail fragment = new FragmentPromoOrderDetail();
        fragment.setJobItem(jobItem);
        fragment.setIsCompleted(isCompleted);
        return fragment;
    }

    public void setJobItem(JobItem jobItem) {
        this.mPromoJobItem = new PromoJobItem(jobItem);
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public FragmentPromoOrderDetail() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_promo_order_detail, container, false);

        mTxtSerial              = (TextView) v.findViewById(R.id.txt_serial);
        mTxtTitle               = (TextView) v.findViewById(R.id.txt_title);
        mTxtStatus              = (TextView) v.findViewById(R.id.txt_status);
        mTxtMerchantName        = (TextView) v.findViewById(R.id.txt_merchant_name);
        mTxtMerchantContact     = (TextView) v.findViewById(R.id.txt_merchant_contact);
        mTxtDeliveryTime        = (TextView) v.findViewById(R.id.txt_delivery_time);
        mTxtDeliveryAddress     = (TextView) v.findViewById(R.id.txt_delivery_address);
        mTxtPrice               = (TextView) v.findViewById(R.id.txt_price);
        mTxtDetail              = (TextView) v.findViewById(R.id.txt_detail);
        mTxtTnc                 = (TextView) v.findViewById(R.id.txt_tnc);
        mTxtDetailHeader        = (TextView) v.findViewById(R.id.txt_detail_header);
        mTxtTncHeader           = (TextView) v.findViewById(R.id.txt_tnc_header);
        mTxtApptHeader          = (TextView) v.findViewById(R.id.txt_appointment_header);
        mTxtCall                = (TextView) v.findViewById(R.id.txt_call);
        mBtnAction              = (Button) v.findViewById(R.id.btn_status_action);
        mLayoutDetailContent    = (LinearLayout) v.findViewById(R.id.layout_detail_content);
        mLayoutTncContent       = (LinearLayout) v.findViewById(R.id.layout_tnc_content);
        mLayoutTnc              = (LinearLayout) v.findViewById(R.id.layout_tnc);
        mLayoutDetail           = (LinearLayout) v.findViewById(R.id.layout_detail);
        mLayoutApptContent      = (LinearLayout) v.findViewById(R.id.layout_appointment_detail);

        v.findViewById(R.id.btnBack).setOnClickListener(onClickListener);

        mBtnAction.setOnClickListener(onClickListener);
        mTxtDetailHeader.setOnClickListener(onClickListener);
        mTxtTncHeader.setOnClickListener(onClickListener);
        mTxtApptHeader.setOnClickListener(onClickListener);
        mTxtCall.setOnClickListener(onClickListener);

        mapInitialData();
        onGetOrderDetail();

        return v;
    }

    public void mapInitialData() {
        String strStatus    = mPromoJobItem.service_order_status.equals("completed") ?
                                "Job Completed" : "Job Started";

        String strAddress = "-";
        if (!mPromoJobItem.service_order_address.trim().equalsIgnoreCase("")) {
            strAddress = mPromoJobItem.service_order_address + ", " +
                    mPromoJobItem.service_order_city + ", " +
                    mPromoJobItem.service_order_postal_code + " " +
                    mPromoJobItem.service_order_state;
        }

        String strTime = "-";
        if (!mPromoJobItem.preferred_time1_start.trim().equalsIgnoreCase("0000-00-00 00:00:00")) {
            strTime = mPromoJobItem.preferred_time1_start;
        }

        int statusBgColour  = mPromoJobItem.service_order_status.equals("completed") ?
                                R.color.pa_blue : R.color.red;

        mTxtSerial.setText(mPromoJobItem.serial);
        mTxtTitle.setText(mPromoJobItem.request_title);
        mTxtPrice.setText(mPromoJobItem.currency + " " + mPromoJobItem.total);
        mBtnAction.setVisibility((mPromoJobItem.service_order_status.equals("completed")) ?
                View.GONE : View.VISIBLE);
        mTxtStatus.setText(strStatus);
        mTxtDeliveryTime.setText(strTime);
        mTxtDeliveryAddress.setText(strAddress);

        if (mPromoJobItem.company_name != null &&
                !mPromoJobItem.company_name.trim().equals("")) {
            mTxtMerchantName.setText(mPromoJobItem.company_name);
        }
        if (mPromoJobItem.co_main_contact_number != null &&
                !mPromoJobItem.co_main_contact_number.trim().equals("")) {
            mTxtMerchantContact.setText(mPromoJobItem.co_main_contact_number);
        }
        if (mPromoJobItem.tnc != null &&
                !mPromoJobItem.tnc.trim().equals("")) {
            mTxtTnc.setText(mPromoJobItem.tnc);
            mLayoutTnc.setVisibility(View.VISIBLE);
        }
        if (mPromoJobItem.detail != null &&
                !mPromoJobItem.detail.trim().equals("")) {
            mTxtDetail.setText(mPromoJobItem.detail);
            mLayoutDetail.setVisibility(View.VISIBLE);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnBack:
//                    mListener.doFragmentChange(new FragmentOrder(isCompleted, "PR"), false, "");
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                    break;
                case R.id.btn_status_action:
                    onCompleteJobDialog();
                    break;
                case R.id.txt_tnc_header:
                    onExpandTab(v, mLayoutTncContent);
                    break;
                case R.id.txt_detail_header:
                    onExpandTab(v, mLayoutDetailContent);
                    break;
                case R.id.txt_appointment_header:
                    onExpandTab(v, mLayoutApptContent);
                    break;
                case R.id.txt_call:
                    if (mPromoJobItem.co_main_contact_number != null &&
                            !mPromoJobItem.co_main_contact_number.trim().equalsIgnoreCase(""))
                        FormUtils.dialNumber(getActivity(), mPromoJobItem.co_main_contact_number);
                    break;
            }
        }
    };

    private View.OnClickListener onCompleteDialogClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnCancel2:
                    if (mDialogCompleteJob != null)
                        mDialogCompleteJob.dismiss();
                    break;

                case R.id.btnNext2:
                    onCompleteJob();
                    break;
            }
        }
    };

    private void onExpandTab(View mViewH, View mViewC) {
        TextView t = (TextView) mViewH;

        if (mViewC.getVisibility() == View.VISIBLE) {
            mViewC.setVisibility(View.GONE);
            t.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.icon_arrow_down), null);
        } else {
            mViewC.setVisibility(View.VISIBLE);
            t.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.icon_arrow_up), null);

        }
    }

    private void onCompleteJobDialog() {
        View v              = inflater.inflate(R.layout.dialog_close_job, null);
        mDialogCompleteJob  = new Dialog(getActivity(), R.style.PauseDialog);

        v.findViewById(R.id.btnCancel2).setOnClickListener(onCompleteDialogClickListener);
        v.findViewById(R.id.btnNext2).setOnClickListener(onCompleteDialogClickListener);

        mDialogCompleteJob.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialogCompleteJob.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        mDialogCompleteJob.setContentView(v);
        mDialogCompleteJob.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mDialogCompleteJob.show();
    }

    private void onGetOrderDetail() {
        loadingInternetDialog.show();

        AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                // TODO Auto-generated method stub
                super.onSuccess(statusCode, content);
                Log.i(LOG_TAG, content);
                ParserPromoJobDetail mParser = new ParserPromoJobDetail(content);

                if (mParser.status) {
                    mPromoJobItem = mParser.parsePromoJobItem();
                    mapInitialData();
                }

                loadingInternetDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // TODO Auto-generated method stub
                super.onFailure(statusCode, error, content);
                loadingInternetDialog.dismiss();
            }
        };

        RequestParams params = new RequestParams();
        params.add("order_serial", mPromoJobItem.serial);
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

        PARestClient.dealGet(pref.getPref(Config.SERVER),
                Config.DEAL_API_GET_PROMO_ORDER_DETAIL, params, mHandler);
    }

    private void onCompleteJob() {
        loadingInternetDialog.show();

        AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                // TODO Auto-generated method stub
                super.onSuccess(statusCode, content);
                Log.i(LOG_TAG, content);
                try {
                    JSONObject o = new JSONObject(content);

                    if (o.has("status") && o.getString("status").equalsIgnoreCase("success"))
                    {
                        mPromoJobItem.service_order_status = "completed";
                        isCompleted = true;
                        mapInitialData();
                    }

                    if (o.has("message")) {
                        simpleToast(o.getString("message"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (mDialogCompleteJob != null)
                    mDialogCompleteJob.dismiss();
                loadingInternetDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // TODO Auto-generated method stub
                super.onFailure(statusCode, error, content);
                Log.i(LOG_TAG, content);
                loadingInternetDialog.dismiss();
            }
        };

        RequestParams params = new RequestParams();
        params.add("order_serial", mPromoJobItem.serial);
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

        PARestClient.dealPost(pref.getPref(Config.SERVER),
                Config.DEAL_API_POST_COMPLETE_PROMO_ORDER, params, mHandler);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            if (activity instanceof OnFragmentChangeListener) {
                mListener = (OnFragmentChangeListener) activity;
                return;
            }
        } catch (ClassCastException e) {}

        throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
