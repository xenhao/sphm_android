package com.pa.deals;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.braintreepayments.api.dropin.Customization;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.Config;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.common.ProfilUtils;
import com.pa.order.FragmentOrder;
import com.pa.parser.ParserBasicResult;
import com.pa.parser.ParserDealBraintreeClientToken;
import com.pa.parser.ParserDealGetPackageSchedule;
import com.pa.pojo.DealBraintreeClientToken;
import com.pa.pojo.PackageJobItem;
import com.pa.pojo.PackageScheduleItem;
import com.pa.pojo.PackageTimeSlot;
import com.pa.pojo.UserORM;
import com.coolfindservices.androidconsumer.R;

import org.apache.http.Header;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.intercom.android.sdk.Intercom;

/**
 * Created by Steven on 06/11/2015.
 */
public class PackageScheduleFragment extends MyFragment implements View.OnClickListener {

    private static final String LOG_TAG = "PackageScheduleFragment";
    private PackageJobItem mItem;
    private RecyclerView mDateRecyclerView, mTimeRecyclerView;
//    private PromotionAdapter mAdapter;
//    private PromotionAdapter mAdapter;
    private LinearLayoutManager mDateLayoutManager,mTimeLayoutManager;
    private ArrayList<PackageScheduleItem> mScheduleItems = new ArrayList<>() ;
    private ArrayList<PackageTimeSlot> mTimeItems ;
    private PackageScheduleDateAdapter mdateAdapter;
    private PackageScheduleTimeAdapter mtimeAdapter;
    private OnFragmentChangeListener listener;
    private String  mStartDateTime = "";
    private String  mEndDateTime = "";
    private float paymentPrice = 0.0f;

    private Boolean ismapSchedule;

    public static PackageScheduleFragment newInstance(PackageJobItem mItem) {
        PackageScheduleFragment fragment = new PackageScheduleFragment();
        fragment.setItem(mItem);

        return fragment;
    }
    public PackageScheduleFragment() {
    }

    public void setItem(PackageJobItem mItem) {
        this.mItem = mItem;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  no load back stack
        onGetPackageSchedule();
        ismapSchedule = true;       Log.i("mScheduleItems: ", String.valueOf(mScheduleItems));
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_package_schedule, container, false);

        v.findViewById(R.id.btnIntercom).setOnClickListener(this);
        v.findViewById(R.id.btnBack).setOnClickListener(this);
        v.findViewById(R.id.btnNext).setOnClickListener(this);

        mDateRecyclerView   = (RecyclerView) v.findViewById(R.id.recyclerViewDate);
        mTimeRecyclerView   = (RecyclerView) v.findViewById(R.id.recyclerViewTime);

//        mScheduleItems  = new ArrayList<>();
        mTimeItems      = new ArrayList<>();

        mdateAdapter = new PackageScheduleDateAdapter(getActivity(), mScheduleItems);
        mtimeAdapter = new PackageScheduleTimeAdapter(getActivity(), mTimeItems);

        mDateLayoutManager  = new LinearLayoutManager(getActivity());
        mTimeLayoutManager  = new LinearLayoutManager(getActivity());

        mDateLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDateRecyclerView.setHasFixedSize(true);
        mDateRecyclerView.setLayoutManager(mDateLayoutManager);
        mDateRecyclerView.setAdapter(mdateAdapter);

        mTimeRecyclerView.setHasFixedSize(true);
        mTimeRecyclerView.setLayoutManager(mTimeLayoutManager);
        mTimeRecyclerView.setAdapter(mtimeAdapter);


        mdateAdapter.setListener(new PackageScheduleDateAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mStartDateTime = "";
                Log.i("item click", mScheduleItems.get(position).mTimeSlot.size() + " clicked!!");
                if (mScheduleItems.get(position).mTimeSlot.size() > 0)
                {
                    Log.i("item click", mScheduleItems.get(position).mTimeSlot.get(0).end_time);
                    mStartDateTime = mScheduleItems.get(position).mTimeSlot.get(0).start_datetime;
                    mEndDateTime = mScheduleItems.get(position).mTimeSlot.get(0).end_datetime;

                }
                mTimeItems.clear();
                mTimeItems.addAll(mScheduleItems.get(position).mTimeSlot);
                mtimeAdapter.notifyDataSetChanged();

            }
        });

        mtimeAdapter.setListener(new PackageScheduleTimeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.i("item mtimeAdapter click", mTimeItems.get(position).end_datetime);
                mStartDateTime = mTimeItems.get(position).start_datetime;
                mEndDateTime = mTimeItems.get(position).end_datetime;
            }
        });

        //  rearrangement for data caching
//        onGetPackageSchedule();
        if(!ismapSchedule) {
            Log.i("backstack: ", "attempted to call ArrayList to " + String.valueOf(mScheduleItems) + "\n[SIZE]: " + mScheduleItems.size());
            mapScheduleData();
        }
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            analytic.trackScreen("Select a Timeslot");
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
                ismapSchedule = false;
                doNext();
                break;
        }
    }

    private void doNext()
    {
        if(mStartDateTime.equalsIgnoreCase("")){

            Toast toast = Toast.makeText(getActivity(), "Please Select an Available Time Slot.", Toast.LENGTH_SHORT);
            toast.show();

        }else{
            try{
                mItem.selected_start_date = mStartDateTime;
                mItem.selected_end_date = mEndDateTime;

                if ("Y".equalsIgnoreCase(mItem.has_service_address)) {
                    PackageScheduleAddFragment fragment = PackageScheduleAddFragment.newInstance(mItem);
                    listener.doFragmentChange(fragment, true, "");
                } else {
//                    doPayWithBraintree();
                    PackagePreviewFragment fragment = PackagePreviewFragment.newInstance(mItem);
                    listener.doFragmentChange(fragment, true, "");
                }
            }catch(Exception ex){
                ex.printStackTrace();
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

        RequestParams params = new RequestParams();
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token",
                pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

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

    public void onGetPackageSchedule() {

        loadingInternetDialog.show();

        RequestParams params = new RequestParams();
        params.add("package_option_serial", mItem.serial);
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token",
                pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

        PARestClient.dealGet(pref.getPref(Config.SERVER),
                Config.DEAL_API_GET_PACKAGE_SCHEDULE, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // TODO Auto-generated method stub
                        super.onFailure(statusCode, error, content);
                        loadingInternetDialog.dismiss();
                        showMessageDialog("No Timeslot Available");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        // TODO Auto-generated method stub
//                        super.onSuccess(statusCode, content);
                        loadingInternetDialog.dismiss();

                        try {
                            Log.i("Package Schedule", new String(responseBody));

                            ParserDealGetPackageSchedule parser = new ParserDealGetPackageSchedule(new String(responseBody));
                            parser.parseSchedule();
                            mScheduleItems.clear();
                            mScheduleItems.addAll(parser.mSchedule);

                            mapScheduleData();

                            loadingInternetDialog.dismiss();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    //  separated for data caching
    private void mapScheduleData(){
        mTimeItems.clear();
        if (mScheduleItems.size() > 0) {
            mTimeItems.addAll(mScheduleItems.get(0).mTimeSlot);
            if (mTimeItems.size() > 0)
            {
                mStartDateTime = mTimeItems.get(0).start_datetime;
                mEndDateTime = mTimeItems.get(0).end_datetime;
            }
        }else{
            showMessageDialog("No Timeslot Available in mapScheduleData()");

        }

        Log.i("Package Schedule", mScheduleItems.size() + " Schedlue in mapScheduleData()");

        mdateAdapter.notifyDataSetChanged();
        mtimeAdapter.notifyDataSetChanged();

        //Log.i("Package Schedule", mItems.size() + " ");
//                            ParseDealPackageDetail mParser = new ParseDealPackageDetail(content);
//
//                            if (mParser.status) {
//                                mPackageJobItem = mParser.parsePromoJobItem();
//                                mapInitialData();
//                            }
    }

    public static class PackageScheduleDateAdapter extends RecyclerView.Adapter<PackageScheduleDateAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {

                private TextView mDate, mMonth, mDays;
                private LinearLayout ViewItem;

            public ViewHolder(View v) {
                    super(v);

                mDays = (TextView) v.findViewById(R.id.datedays);
                mDate = (TextView) v.findViewById(R.id.dateTitle);
                mMonth = (TextView) v.findViewById(R.id.monthTitle);

                ViewItem = (LinearLayout) v.findViewById(R.id.ListViewDateItem);
            }
        }

            private Activity mActivity;
            private ArrayList<PackageScheduleItem> mScheduleItems;
            private OnItemClickListener mListener = null;
            private int selectedDatePos = 0;

            public PackageScheduleDateAdapter(Activity mActivity, ArrayList<PackageScheduleItem> mScheduleItems) {
                this.mActivity = mActivity;
                this.mScheduleItems = mScheduleItems;

            }

            public interface OnItemClickListener {
                void onItemClick(int position);
            }

            private void setListener(OnItemClickListener mListener) {
                this.mListener = mListener;
            }

            @Override
            public PackageScheduleDateAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // create a new view
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_packagedate, parent, false);
                ViewHolder vh = new ViewHolder(v);
                return vh;
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, final int position) {
                PackageScheduleItem item = mScheduleItems.get(position);

//            String coverUrl = mImageUrl + "/cover/" + item.cover_photo;
//            holder.mCoverImage.setTag(coverUrl);
//            mImageLoader.DisplayImage(coverUrl, mActivity, holder.mCoverImage, false);



                Date date = new Date(item.unixDate*1000L); // *1000 is to convert seconds to milliseconds

                Calendar c = Calendar.getInstance();
                c.setTime(date); // yourdate is a object of type Date
                try{


                int dayOfWeek = c.get(Calendar.DAY_OF_MONTH);

                String monthOfYear = new SimpleDateFormat("MMMM").format(date);

                String weekday = new SimpleDateFormat("EE").format(date);

                Log.i("get weekday :", weekday + "day of week :" + dayOfWeek + "get Month :" + monthOfYear + "");

                holder.mDays.setText(String.valueOf(dayOfWeek));
                holder.mDate.setText(weekday);
                holder.mMonth.setText(monthOfYear);

                    if (selectedDatePos == position)
                    {
                        holder.ViewItem.setBackgroundResource(R.color.black_transparent);
                    }else {
                        holder.ViewItem.setBackgroundResource(R.color.white);
                    }

                }
                catch(Exception ex){
                    ex.printStackTrace();
                }

                holder.ViewItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedDatePos = position;
                        if (mListener != null)
                            mListener.onItemClick(position);
                        notifyDataSetChanged();
                    }
                });
            }

            @Override
            public int getItemCount() {
                return mScheduleItems.size();
            }

    }

    public static class PackageScheduleTimeAdapter extends RecyclerView.Adapter<PackageScheduleTimeAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView mTime, mOriginalPrice, mDiscountedPrice, mPackageListDesc;
            private LinearLayout ViewItem;

            public ViewHolder(View v) {
                super(v);

                mTime = (TextView) v.findViewById(R.id.titleTime);
                ViewItem = (LinearLayout) v.findViewById(R.id.ListViewTimeItem);
            }

        }

        private Activity mActivity;
        private ArrayList<PackageTimeSlot> mTimeItems;
        private OnItemClickListener mListener = null;
        private int selectedTimePos = 0;

        public PackageScheduleTimeAdapter(Activity mActivity, ArrayList<PackageTimeSlot> mTimeItems) {
            this.mActivity = mActivity;
            this.mTimeItems = mTimeItems;

        }

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        private void setListener(OnItemClickListener mListener) {
            this.mListener = mListener;
        }

        @Override
        public PackageScheduleTimeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_packagetime, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            PackageTimeSlot item = mTimeItems.get(position);

//            String coverUrl = mImageUrl + "/cover/" + item.cover_photo;
//            holder.mCoverImage.setTag(coverUrl);
//            mImageLoader.DisplayImage(coverUrl, mActivity, holder.mCoverImage, false);

            holder.mTime.setText(item.start_time+"");

            if (selectedTimePos == position)
            {
                holder.ViewItem.setBackgroundResource(R.color.black_transparent);
            }else
            {
                holder.ViewItem.setBackgroundResource(R.color.white);
            }

            holder.ViewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedTimePos = position;
                    if (mListener != null)
                        mListener.onItemClick(position);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mTimeItems.size();
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

    public void onBraintreeSubmit() {

        float paymentPrice = Float.parseFloat(mItem.price);

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
        String serial = mItem.serial;

        RequestParams params = new RequestParams();
        params.add("package_option_serial", serial);
        params.add("username", pref.getPref(Config.PREF_USERNAME));
        params.add("address", "");
        params.add("state", "");
        params.add("city", "");
        params.add("postcode", "");
        params.add("start_time", mStartDateTime);
        params.add("end_time", mEndDateTime);
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



