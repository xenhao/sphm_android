package com.pa.landing;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.DrawableCrossFadeFactory;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.Target;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.Config;
import com.pa.common.GlobalVar;
import com.pa.common.ImageLoader;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.common.SnappyRecyclerView;
import com.pa.common.Tracer;
import com.pa.deals.PackageListDetailFragment;
import com.pa.parser.ParserDealGetPackage;
import com.pa.parser.ParserParentServiceCategoryNew;
import com.pa.parser.ParserPendingRating;
import com.pa.parser.ParserState;
import com.pa.pojo.JobItem;
import com.pa.pojo.PackageListItem;
import com.pa.pojo.PendingRating;
import com.pa.pojo.ServiceCategory;
import com.pa.pojo.State;
import com.coolfindservices.android.RegisterActivity;
import com.coolfindservices.androidconsumer.R;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import io.intercom.android.sdk.Intercom;

/**
 * Created by VintedgeMac on 22/09/2016.
 */

public class FragmentRevisedLanding extends MyFragment implements View.OnClickListener, Config {

    GridView gridCat;
    GridAdapter gridAdapter;
    ServiceAutoCompleteAdapter adapter;
    static ArrayList<ServiceCategory> arrServiceCategory = new ArrayList<ServiceCategory>();
    OnFragmentChangeListener listener;
    ImageLoader loader;
    AutoCompleteTextView findService;
    ArrayList<String> arrSuggest = new ArrayList<String>();
    private Date _lastTypeTime = null;

    TextView txtCountry2;
    String[] countrySelection;
    ArrayList<State> storedStates;

    private LinearLayout fallBack;
    private String serviceCountry, serviceState, f_country2;

    //	boolean switch for random unresponsive getStateData
    private Boolean retryGetState = false;

    String prevKeyword = "";

    JobItem job=null;
    Dialog dialogRateMerchant;
    EditText txtRatingMsg;
    TextView txtRatingServiceName;
    RatingBar ratingPoint;
    String f_rating, f_rating_message;
    private final int STORAGE_PERMISSION = 999;
    private final int CAMERA_PERMISSION = 998;
    private int LIMIT = 100;    //  limit of promotion package being loaded
    private int page = 1;       //  number of pages of promotion packages
    private ArrayList<PackageListItem> mItems;
    private PackageAdapter mAdapter;
    private String imageUrl;
    private SnappyRecyclerView mSnappyRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private boolean hasPromo = false;
    private float dpi;
    private SliderLayout sliderLayout;
    private PagerIndicator pagerIndicator;

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

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        gridAdapter = new GridAdapter();

        try {
//            getServiceData(0);
            getStateData();
        }catch(Exception e){
            e.printStackTrace();
        }

        //  promotion horizontal scroller
        imageUrl = PARestClient.getDealAbsoluteUrl(pref.getPref(Config.SERVER),
                Config.PACKAGE_IMAGE_PATH);

        loader      = new ImageLoader(getActivity());
        mItems      = new ArrayList<>();
        mAdapter    = new PackageAdapter(getActivity(), mItems, imageUrl, loader);

//        getList("485", false, "onCreate");

        //  get screen density
        DisplayMetrics displaymetrics = new DisplayMetrics();

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        dpi = getResources().getDisplayMetrics().density;
    }

//    private void checkPermission(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            //	storage permission
//            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(
//                            getActivity(), R.style.AlertDialogStyle);
//                    builder.setTitle("Permission Disclosure");
//                    builder.setMessage("This page will not function as intended if Storage Permission is not granted.");
//                    builder.setPositiveButton("OK",
//                            new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface dialog,
//                                                    int which) {
//                                    // TODO Auto-generated method stub
//                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
//                                }
//                            });
//
//                    builder.show();
//                } else {
//                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
//                }
//            }
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case STORAGE_PERMISSION:
//                // If request is cancelled, the result arrays are empty.
//                if (/*grantResults.length > 0
//						&&*/ grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    AlertDialog.Builder builder = new AlertDialog.Builder(
//                            getActivity(), R.style.AlertDialogStyle);
//                    builder.setTitle("Storage Permission Denied");
//                    builder.setMessage("Device storage permission denied, app might not function as intended.");
//                    builder.setPositiveButton("OK",
//                            new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface dialog,
//                                                    int which) {
//                                    // TODO Auto-generated method stub
//                                    dialog.dismiss();
////									dialogActivation.dismiss();
//                                }
//                            });
//
//                    builder.show();
//                }
//                break;
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //  check permissions   *not needed in coolfind as target API is still 22 ;)
        //checkPermission();

        View v = inflater.inflate(R.layout.fragment_revised_landing, null);

        v.findViewById(R.id.btnMenu).setOnClickListener(this);
        v.findViewById(R.id.btnIntercom).setOnClickListener(this);

        fallBack = (LinearLayout) v.findViewById(R.id.landingFallback);
        gridCat = (GridView) v.findViewById(R.id.gridCategory);

        /**
         *      SnappyRecyclerView which is initially used for banner
         */
//        mSnappyRecyclerView = (SnappyRecyclerView) v.findViewById(R.id.recyclerView);
//        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        //  set banner layout_weight depend on device DPI(screen density)
//        LinearLayout.LayoutParams params;
//        if(dpi < 2.0)
//            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 4.0f);
//        else
//            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 3.5f);
//        mSnappyRecyclerView.setLayoutParams(params);
//        mSnappyRecyclerView.setHasFixedSize(true);
//        mSnappyRecyclerView.setLayoutManager(mLayoutManager);
//
//        mSnappyRecyclerView.setAdapter(mAdapter);
//
//        mSnappyRecyclerView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });
//
//        mAdapter.setListener(new PackageAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                PackageListDetailFragment fragment = PackageListDetailFragment.newInstance(mItems.get(position));
//                listener.doFragmentChange(fragment, true, "");
//            }
//        });

        //  SliderLayout
        sliderLayout = (SliderLayout) v.findViewById(R.id.slider);
        sliderLayout.setClickable(true);

        pagerIndicator = (PagerIndicator) v.findViewById(R.id.custom_indicator);

        //  set banner layout_weight & page indicator size depend on device DPI(screen density)
        LinearLayout.LayoutParams params;
        if(dpi < 2.0) {
//            pagerIndicator.setDefaultSelectedIndicatorSize();
//            pagerIndicator.setDefaultUnselectedIndicatorSize();
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2.5f);
        }
        else {
            pagerIndicator.setDefaultSelectedIndicatorSize(8, 8, PagerIndicator.Unit.DP);
            pagerIndicator.setDefaultUnselectedIndicatorSize(4, 4, PagerIndicator.Unit.DP);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 3.5f);
        }
        sliderLayout.setLayoutParams(params);
        //***************************************************************

        txtCountry2 = (TextView) v.findViewById(R.id.country2);
        txtCountry2.setOnClickListener(this);

        // Preset Singapore For Live only
        if ("2".equals(getActivity().getResources().getString(R.string.server))) {
//            txtCountry2.setText("Singapore");
//			countrySelection = country;
        } else {
//			countrySelection = country2;
        }

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        try {
            loader=new ImageLoader(getActivity());

            gridCat.setAdapter(gridAdapter);


            adapter = new ServiceAutoCompleteAdapter(getActivity(),
                    R.layout.list_item_pa, arrSuggest);

            if(!TextUtils.isEmpty(pref.getPref(Config.PREF_LAST_COUNTRY))){
                txtCountry2.setText(pref.getPref(Config.PREF_LAST_COUNTRY));
            }

            if("select country".equalsIgnoreCase(txtCountry2.getText().toString())){
                gridCat.setVisibility(View.GONE);
                fallBack.setVisibility(View.VISIBLE);
            }else if("singapore".equalsIgnoreCase(txtCountry2.getText().toString())){
                serviceCountry  = "sg";
                serviceState    = "";
                if(gridAdapter.isEmpty()){
                    getServiceData(0);
                }else{
//                    gridAdapter.notifyDataSetChanged();
                    fallBack.setVisibility(View.GONE);
                    gridAdapter.notifyDataSetChanged();
                    gridCat.setVisibility(View.VISIBLE);
                    //  show promotions layout
                    if(hasPromo){    //getList("485", true);
//                    mSnappyRecyclerView.setVisibility(View.VISIBLE);
                        sliderLayout.setVisibility(View.VISIBLE);
                        pagerIndicator.setVisibility(View.VISIBLE);
                    }
                }
            }else{
                //  set serviceCountry to malaysia location
                serviceCountry  = "my";
                serviceState    = txtCountry2.getText().toString().replace(malaysia_prefix, "");
                if(gridAdapter.isEmpty()){
                    getServiceData(0);
                }else{
                    fallBack.setVisibility(View.GONE);
                    gridAdapter.notifyDataSetChanged();
                    gridCat.setVisibility(View.VISIBLE);
                    //  show promotions layout
                    if(hasPromo){    //getList("485", true);
//                    mSnappyRecyclerView.setVisibility(View.VISIBLE);
                        sliderLayout.setVisibility(View.VISIBLE);
                        pagerIndicator.setVisibility(View.VISIBLE);
                    }
                }
//                fallBack.setVisibility(View.GONE);
//                gridAdapter.notifyDataSetChanged();
//                gridCat.setVisibility(View.VISIBLE);
            }

            try {
                getPendingRating();
            }catch(Exception e){
                Log.i("FragmentRevisedLanding", "No pending ratings.");
                e.printStackTrace();
            }

            doRegisGCM();

            analytic.trackScreen("Select a Service");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void doRegisGCM(){
        RegisterActivity register=new RegisterActivity(getActivity());

        RequestParams params=new RequestParams();
        params.add("device_os","android");
        params.add("device_token",	register.getRegistrationId());
        params.add("active_session_token",pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
        params.add("session_username",pref.getPref(Config.PREF_USERNAME));


        AsyncHttpResponseHandler responseHandler=new AsyncHttpResponseHandler(){};
        PARestClient.post(pref.getPref(Config.SERVER), Config.API_REGISTER_DEVICE, params, responseHandler);

    }

    void getPendingRating(){
        RequestParams params=new RequestParams();
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

        AsyncHttpResponseHandler responseHandler=new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                // TODO Auto-generated method stub
                super.onSuccess(arg0, arg1, arg2);  //FirebaseCrash.report(new Exception("My first Android non-fatal error"));
                try{
                    ParserPendingRating parser=new ParserPendingRating(new String(arg2));
                    PendingRating pr=parser.getData();
                    if("N".equals(pr.rated)){
                        job=new JobItem();

                        job.request_title=pr.rating_data.request_title;
                        job.serial=pr.rating_data.serial;
                        job.preferred_time1_start=pr.rating_data.preferred_time1_start;
                        job.preferred_time2_start=-1+"";
                        job.company_name=pr.rating_data.company_name;
                        job.merchant_username=pr.rating_data.merchant_username;

                        showRateMerchant();
                    }
                }catch(Exception e){
                    //FirebaseCrash.report(new Exception("Nothing to rate error"));
                }

            }
        };

        PARestClient.get(pref.getPref(Config.SERVER),"user/pending-rating", params, responseHandler);

        GlobalVar.state = pref.getPref(Config.PREF_LAST_STATE);
        GlobalVar.state_short = pref.getPref(Config.PREF_LAST_STATE_SHORT);
    }

    void showRateMerchant() {
        View v = inflater.inflate(R.layout.dialog_rate, null);

        TextView title=(TextView)v.findViewById(R.id.title);
        TextView order_id=(TextView)v.findViewById(R.id.txtOrderId);
        TextView date=(TextView)v.findViewById(R.id.txtDate);

        title.setText(job.request_title);
        order_id.setText(job.serial);
        date.setText(getPreferredTime(job.preferred_time1_start,
                job.preferred_time2_start));


        ratingPoint = (RatingBar) v.findViewById(R.id.ratingBar1);
        txtRatingMsg = (EditText) v.findViewById(R.id.txtRatingMessage);
        txtRatingServiceName = (TextView) v.findViewById(R.id.txtServiceName);
        txtRatingServiceName.setText(job.company_name);


        LayerDrawable stars = (LayerDrawable) ratingPoint.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.pa_orange), PorterDuff.Mode.SRC_ATOP);

        ratingPoint.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float touchPositionX = event.getX();
                    float width = ratingPoint.getWidth();
                    float starsf = (touchPositionX / width) * 5.0f;
                    int stars = (int) starsf + 1;
                    ratingPoint.setRating(stars);

                    // Toast.makeText(MainActivity.this, String.valueOf("test"),
                    // Toast.LENGTH_SHORT).show();
                    v.setPressed(false);
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setPressed(true);
                }

                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setPressed(false);
                }

                return true;
            }
        });

        v.findViewById(R.id.btnCancel2).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        dialogRateMerchant.dismiss();
                    }
                });

        v.findViewById(R.id.btnCancel2).setVisibility(View.GONE);

        v.findViewById(R.id.btnNext2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isValidRate()) {
                    doRate();
                    dialogRateMerchant.dismiss();
                }
            }
        });


        dialogRateMerchant = new Dialog(getActivity(), R.style.PauseDialog);
        //dialogRateMerchant.setCancelable(false);
        dialogRateMerchant.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //do whatever you want the back key to do
                simpleToast("You must give the rating to continue using app");
            }
        });

        dialogRateMerchant.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface paramDialogInterface, int paramInt,
                                 KeyEvent paramKeyEvent) {
                // TODO Auto-generated method stub
                if (paramKeyEvent.getKeyCode()== KeyEvent.KEYCODE_BACK){
                    simpleToast("You must give the rating to continue using app");
                    return true;
                }

                return false;
            }
        });

        try {
            dialogRateMerchant.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialogRateMerchant.getWindow().setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            dialogRateMerchant.setContentView(v);
            dialogRateMerchant.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            dialogRateMerchant.setCancelable(false);
            dialogRateMerchant.show();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    protected boolean isValidRate() {
        boolean flag = true;
        // TODO Auto-generated method stub
        f_rating = ratingPoint.getRating() + "";
        f_rating_message = txtRatingMsg.getText().toString();

        if (ratingPoint.getRating() <= 3) {
            if (!formCheckString(f_rating_message, "Rating message")) {
                flag = false;
            }
        }

        return flag;

    }

    protected void doRate() {
        // TODO Auto-generated method stub
        RequestParams params = new RequestParams();

        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token",
                pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
        params.add("merchant_username", job.merchant_username);
        params.add("review_score", f_rating);
        params.add("review_description", f_rating_message);
        params.add("service_order_serial", job.serial);

        AsyncHttpClient client = new AsyncHttpClient();
        PARestClient.post(pref.getPref(Config.SERVER),Config.API_RATE_MERCHANT, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();
//                        loadingInternetDialog.setMessage(" do rate");
                        loadingInternetDialog.show();
                    }

                    @Override
                    public void onSuccess(int statusCode, String content) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, content);

                        try {
                            JSONObject obj = new JSONObject(content);
                            if ("success".equals(obj.getString("status"))) {
                                dialogRateMerchant.dismiss();
                                //reDraw();
                            } else {
                                simpleToast("Error" + obj.getString("reason"));
                            }
                        } catch (Exception e) {

                        }
                        loadingInternetDialog.dismiss();

                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // TODO Auto-generated method stub
                        super.onFailure(statusCode, error, content);
                        simpleToast("Error");

                        loadingInternetDialog.dismiss();

                    }
                });
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
//                                    simpleToast("when is this executed5???");
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
//                                    simpleToast("when is this executed4???");
                                } else {
                                    tv2.setText("");
                                    tv2.setHint("Select your city");
                                }
                            }
//                            simpleToast("when is this executed3???");
//                            Toast.makeText(getActivity(), "country: " + country + "\ncountry2: " + country2 + "\ncountrySelection: " + countrySelection + "\ntxtCountry2: " + txtCountry2.getText().toString()
//                                    , Toast.LENGTH_LONG).show();
                            //  get service data list
                            getServiceData(0);

                            try {
                                f_country2 = txtCountry2.getText().toString();

                                //  save country settings
                                pref.savePref(Config.PREF_LAST_COUNTRY, f_country2);
                                pref.savePref(Config.PREF_LAST_STATE, GlobalVar.state);
                                pref.savePref(Config.PREF_LAST_STATE_SHORT, GlobalVar.state_short);

                                if (f_country2.toLowerCase().contains("malaysia"))
                                    GlobalVar.country = "Malaysia";
                                else
                                    GlobalVar.country = f_country2;
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onResume(){
        super.onResume();

        //	additional checking to get state data if initial went unresponsive
        if(countrySelection == null || countrySelection.length <= 2){
            getStateData();		Log.i("CALLED FROM ONRESUME", "CALLED FROM ONRESUME" + " " + countrySelection);
        }

        //  check for promo availability
        if(hasPromo)    //getList("485", true);
//        mSnappyRecyclerView.setVisibility(View.VISIBLE);
            setupSlider(mItems, hasPromo);
    }

    void getStateData() {
        RequestParams params = new RequestParams();
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
//        params.add("country_only", "true");
        params.add("state_only", "true");

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
//                loadingInternetDialog.setMessage("getStateData");
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
//                simpleToast(error + "\n" + content);

                loadingInternetDialog.dismiss();

            }

            @Override
            public void onFinish() {
                super.onFinish();
                loadingInternetDialog.dismiss();
                //	show state selection spinner if this was last minute state query
                if (retryGetState) {
                    showSpinner(getView());
                    retryGetState = false;
                }
            }
        };

        PARestClient.post(pref.getPref(Config.SERVER), Config.API_LOCATION, params, responseHandler);
    }

    void getServiceData(int parentId) {
//        loadingInternetDialog.setMessage("getServiceData");
        loadingInternetDialog.show();

        if ("singapore".equalsIgnoreCase(txtCountry2.getText().toString())) {
            serviceCountry = "sg";
            serviceState = "";
        } else if ("select country".equalsIgnoreCase(txtCountry2.getText().toString())) {
            //  clear grid adapter if no country is selected
            arrServiceCategory = new ArrayList<ServiceCategory>();
            gridAdapter.notifyDataSetChanged();
            return;
        } else {
            serviceCountry = "my";
            serviceState = txtCountry2.getText().toString().replace(malaysia_prefix, "");
        }

        Tracer.d(API_SERVICE_CATEGORY_HIERARCHICAL + "?parent_id=[" + parentId
                + "]");
        RequestParams params = new RequestParams();
        params.add("parent_id", "[" + parentId + "]");
        params.add("country", serviceCountry);
        params.add("state", serviceState);
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
        PARestClient.get(pref.getPref(Config.SERVER), API_SERVICE_CATEGORY_HIERARCHICAL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(Throwable error, String content) {
                // TODO Auto-generated method stub
                super.onFailure(error, content);
                loadingInternetDialog.dismiss();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());

                // set title
                alertDialogBuilder.setTitle("Network Error");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Click yes to reload!")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        getServiceData(0);
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // if this button is clicked, just close
                                        // the dialog box and do nothing
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }

            @Override
            public void onSuccess(int statusCode, String result) {
                // TODO Auto-generated method stub
                super.onSuccess(statusCode, result);
                try {
                    System.out.println(result);
                    ParserParentServiceCategoryNew parser = new ParserParentServiceCategoryNew(
                            result);
                    if ("success".equals(parser.status)) {
                        if (parser.arr.get(0).children == null) {
                            arrServiceCategory = new ArrayList<ServiceCategory>();
                        }
                        else {
//                            arrServiceCategory = parser.arr.get(0).children;
                            //  check if contains promotion & extract it
                            arrServiceCategory = containsId(parser.arr.get(0).children, "485");
                        }

                        Tracer.d("Service size:" + arrServiceCategory.size());

                        //  hide fall back page before generate grids
                        fallBack.setVisibility(View.GONE);
                        //  restore visibility to grid
                        gridCat.setVisibility(View.VISIBLE);

                        // drawCategory();
                        gridAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

//				loadingInternetDialog.dismiss();
            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                super.onFinish();
                loadingInternetDialog.dismiss();
            }
        });
    }

    private ArrayList<ServiceCategory> containsId(ArrayList<ServiceCategory> list, String id) {
        for (ServiceCategory object : list) {
            if (object.id.equals(id)) {
                list.remove(object);
                getList(id, true, "containsId");
                hasPromo = true;
                //  show promotional items
//        mSnappyRecyclerView.setVisibility(View.VISIBLE);       //simpleToast("show promo layout");
//                sliderLayout.setVisibility(View.VISIBLE);
//                pagerIndicator.setVisibility(View.VISIBLE);
                return list;
            }
        }

        //  no promotional items, hide layout
//        mSnappyRecyclerView.setVisibility(View.GONE);       //simpleToast("hide promo layout");
        sliderLayout.setVisibility(View.GONE);
        pagerIndicator.setVisibility(View.GONE);
        hasPromo = false;
        return list;
    }

    @Override
    public void onClick(final View v) {
        switch(v.getId()){
            case R.id.btnMenu:
                ActivityLanding parent = (ActivityLanding) getActivity();
                parent.menuClick();
                break;
            case R.id.btnIntercom:
                Intercom.client().displayConversationsList();
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
                break;
        }
    }

    private void getList(final String serviceId, final boolean showLayout, String clue) {    //simpleToast("getList called: " + ++getListTracker); Log.i("getList log: ", "getList called: " + getListTracker + " " + showLayout);
        Log.i("getList called", "getList called\n" + clue);
        RequestParams params = new RequestParams();
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
        params.add("limit", LIMIT + "");
        params.add("page", page + "");
        params.add("country", serviceCountry);
        params.add("state", serviceState);
        params.add("service_id", serviceId);

        Log.i("Package Fragment", params.toString());
        Log.i("Package Fragment", pref.getPref(Config.SERVER) + Config.DEAL_API_GET_PACKAGE);

        PARestClient.dealGet(pref.getPref(Config.SERVER),
                Config.DEAL_API_GET_PACKAGE,
                params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        //  no loading dialog
//                        if(!isRefresh) {
//                            loadingInternetDialog.setMessage("getList");
//                            loadingInternetDialog.show();
//                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error, String content) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                getActivity());
                        alertDialogBuilder.setTitle("Network Error");
                        alertDialogBuilder
                                .setMessage("Click yes to reload!")
                                .setCancelable(false)
                                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        getList(serviceId, showLayout, "recursive");     //  promotions category ID
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick( DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        //   no loading dialog
//                        if(!isRefresh) {
                        loadingInternetDialog.dismiss();
//                        }else{
//                            // Stop refresh animation
//                            mSwipeRefreshLayout.setRefreshing(false);
//                            isRefresh = false;
//                        }

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            Log.i("Promotion", new String(responseBody));
                            ParserDealGetPackage parser = new ParserDealGetPackage(new String(responseBody));
                            Log.i("Promotion", parser.getStatus());

                            if ("success".equalsIgnoreCase(parser.getStatus())) {       //simpleToast("success loading promotions");
                                mItems.clear();
                                mItems.addAll(parser.getResult());
                                Log.i("Promotion", mItems.size() + " ");
                                mAdapter.notifyDataSetChanged();
//                                mSnappyRecyclerView.setVisibility(showLayout ? View.VISIBLE : View.GONE);

                                //  setup auto slider
                                setupSlider(mItems, showLayout);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
    }

    private void setupSlider(final ArrayList<PackageListItem> items, boolean hasPromo){
        //  clear sliders of previous data before repopulating them
        sliderLayout.removeAllSliders();
        for(int i = 0; i < items.size(); i++){
            DefaultSliderView defaultSliderView = new DefaultSliderView(getContext());
            defaultSliderView.image(imageUrl+"/cover/"+items.get(i).cover_photo);
            defaultSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    PackageListDetailFragment fragment = PackageListDetailFragment.newInstance(items.get(sliderLayout.getCurrentPosition()));
                    listener.doFragmentChange(fragment, true, "");
//                    simpleToast(/*defaultSliderView.getUrl() + */"\ncurrent position: " + sliderLayout.getCurrentPosition());
//                    Log.i("SLIDERLAYOUT", "click registered");
                }
            });
            sliderLayout.addSlider(defaultSliderView);
        }
        //  custom indicator
        sliderLayout.setCustomIndicator(pagerIndicator);
        pagerIndicator.redraw();
        //  set duration before banner change
        sliderLayout.setDuration(5000);
        sliderLayout.setVisibility(hasPromo ? View.VISIBLE : View.GONE);
        pagerIndicator.setVisibility(hasPromo ? View.VISIBLE : View.GONE);
    }

    /**     SnappyRecyclerView Adapter
     *      *not used for auto scroll banner*
     */
    public static class PackageAdapter extends RecyclerView.Adapter<com.pa.landing.FragmentRevisedLanding.PackageAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView mCoverImage, mImgMgp;
            private TextView mTitle, mCompanyName, mDiscountedPrice;
            private LinearLayout mLinearLayout;
            RatingBar ratingBar;

            public ViewHolder(View v) {
                super(v);
                mCoverImage         = (ImageView) v.findViewById(R.id.coverImage);
                mTitle              = (TextView) v.findViewById(R.id.title);
                mDiscountedPrice    = (TextView) v.findViewById(R.id.discountedPrice);
                mCompanyName        = (TextView)v.findViewById(R.id.companyName);
                ratingBar           = (RatingBar) v.findViewById(R.id.ratingBar1);
                mImgMgp             = (ImageView) v.findViewById(R.id.img_mpg);
                mLinearLayout       = (LinearLayout) v.findViewById(R.id.item_details);
                //  hide details for promo items, used only here on the home page
                mLinearLayout.setVisibility(View.GONE);
            }
        }

        private Activity mActivity;
        private ArrayList<PackageListItem> mItems;
        private String mImageUrl;
        private ImageLoader mImageLoader;
        private com.pa.landing.FragmentRevisedLanding.PackageAdapter.OnItemClickListener mListener = null;


        public PackageAdapter(Activity mActivity, ArrayList<PackageListItem> mItems, String mImageUrl, ImageLoader mImageLoader) {
            this.mActivity = mActivity;
            this.mItems = mItems;
            this.mImageUrl = mImageUrl;
            this.mImageLoader = mImageLoader;
            this.mImageLoader.setDefaultImage(R.drawable.promo_placeholder);
        }

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        private void setListener(com.pa.landing.FragmentRevisedLanding.PackageAdapter.OnItemClickListener mListener) {
            this.mListener = mListener;
        }

        @Override
        public com.pa.landing.FragmentRevisedLanding.PackageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_packagelist, parent, false);
            com.pa.landing.FragmentRevisedLanding.PackageAdapter.ViewHolder vh = new com.pa.landing.FragmentRevisedLanding.PackageAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(com.pa.landing.FragmentRevisedLanding.PackageAdapter.ViewHolder holder, final int position) {
            PackageListItem item = mItems.get(position);

            if(item.cover_photo == null ){
                holder.mCoverImage.setImageResource(R.drawable.promo_placeholder);
            }
            else{
                String coverUrl = mImageUrl + "/cover/" + item.cover_photo;
//                holder.mCoverImage.setTag(coverUrl);

//                mImageLoader.DisplayImage(coverUrl, mActivity, holder.mCoverImage, false);
                //  Glide image loader
                try {
                    if (TextUtils.isEmpty(coverUrl))
                        Glide.with(mActivity).load(R.drawable.promo_placeholder).into(holder.mCoverImage);
                    else
                        Glide.with(mActivity).load(coverUrl).into(holder.mCoverImage);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            holder.mTitle.setText(item.title);
            holder.mCompanyName.setText(item.merchant_co_name);
            holder.mDiscountedPrice.setText(item.currency + " " + item.min_price);
            holder.ratingBar.setRating(item.rating);

            if (item.mgp != null && item.mgp.equalsIgnoreCase("Y")) {
                holder.mImgMgp.setVisibility(View.VISIBLE);
            } else {
                holder.mImgMgp.setVisibility(View.GONE);
            }

            holder.mCoverImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.onItemClick(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

    }

    private class ServiceAutoCompleteAdapter extends ArrayAdapter<String>
            implements Filterable {
        private ArrayList<String> resultList;

        public ServiceAutoCompleteAdapter(Context context,
                                          int textViewResourceId, ArrayList<String> arr) {
            super(context, textViewResourceId);
            resultList = new ArrayList<String>(arr);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        // resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint,
                                              FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    class GridAdapter extends BaseAdapter {
        GridAdapter.GridHolder holder;

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return arrServiceCategory.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return arrServiceCategory.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            try{
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_grid_category,
                            null);
                    holder = new GridHolder();
                    holder.t = (TextView) convertView.findViewById(R.id.txtCat);
                    holder.img = (ImageView) convertView.findViewById(R.id.img);
                    convertView.setTag(holder);

                } else {
                    holder = (GridHolder) convertView.getTag();
                }

                String uri="";
                if ("0".equals(pref.getPref(Config.SERVER))) {
                    uri = "http://" + DOMAIN_DEV + "/";

                } else if ("1".equals(pref.getPref(Config.SERVER))) {

                    uri = "http://" + DOMAIN_STAGING + "/";

                }
                else if ("2".equals(pref.getPref(Config.SERVER))) {

                    uri = "http://" + DOMAIN_LIVE + "/";

                }

                String url=uri+arrServiceCategory.get(position).icon_image
                        +"?session_username="+pref.getPref(Config.PREF_USERNAME)
                        +"&active_session_token="+pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN)
                        ;
                Tracer.d(url);
//                holder.img.setTag(url);
//                loader.DisplayImage(url, getActivity(), holder.img,false);
                //  Glide image loader
                try {
                    if (TextUtils.isEmpty(url))
                        Glide.with(getActivity()).load(R.drawable.default_img).into(holder.img);
                    else {
//                        Glide.with(getActivity()).load(url).into(holder.img);
                        //  image loading flickering fix
                        String oldImage = url;
                        Glide
                                .with(getActivity())
                                .load(url)
                                .thumbnail(Glide // this thumbnail request has to have the same RESULT cache key
                                        .with(getActivity()) // as the outer request, which usually simply means
                                        .load(oldImage) // same size/transformation(e.g. centerCrop)/format(e.g. asBitmap)
                                        .fitCenter() // have to be explicit here to match outer load exactly
                                )
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        e.printStackTrace();
                                        return false;
                                    }

                                    @Override public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        if (isFirstResource) {
                                            return false; // thumbnail was not shown, do as usual
                                        }
                                        return new DrawableCrossFadeFactory<Drawable>(/* customize animation here */)
                                                .build(false, false) // force crossFade() even if coming from memory cache
                                                .animate(resource, (GlideAnimation.ViewAdapter)target);
                                    }
                                })
                                //.fitCenter() // this is implicitly added when .into() is called if there's no scaleType in xml or the value is fitCenter there
                                .into(holder.img);

                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                holder.t.setText(arrServiceCategory.get(position).service_name);

                convertView.setTag(R.id.action_settings, position);
                convertView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        try {
                            int pos = (Integer) v.getTag(R.id.action_settings);
                            ServiceCategory sc = arrServiceCategory.get(pos);
                            ArrayList<ServiceCategory> arr = new ArrayList<ServiceCategory>();
                            arr.add(sc);
//                            listener.doFragmentChange(new FragmentPostOpenBid(arr, serviceCountry),
//                                    true, "");
//                            Toast.makeText(getActivity(), "arr: " + sc.id + "\nserviceCountry: " + txtCountry2.getText().toString() + "\narrServiceCategory.get(pos).service_name: " + arrServiceCategory.get(pos).service_name + "\narrServiceCategory.get(pos).id: " + arrServiceCategory.get(pos).id, Toast.LENGTH_LONG).show();
                            //  set GlobalVar.country before proceeding
                            GlobalVar.country = txtCountry2.getText().toString();
                            listener.doFragmentChange(new FragmentCategoryTab(arrServiceCategory.get(pos).service_name, arrServiceCategory.get(pos).id, txtCountry2.getText().toString(), arr),
                                    true, "");

                            analytic.trackCustomDimension("Category", 1, arrServiceCategory.get(pos).service_name);
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                    }
                });

                if(url.contains("default")){
                    holder.img.setVisibility(View.GONE);
                    holder.t.setVisibility(View.VISIBLE);
                }else{
                    holder.img.setVisibility(View.VISIBLE);
                    holder.t.setVisibility(View.GONE);

                }



            }catch(Exception e){
                e.printStackTrace();
            }
            return convertView;
        }

        class GridHolder {
            TextView t;
            ImageView img;
        }

    }
}
