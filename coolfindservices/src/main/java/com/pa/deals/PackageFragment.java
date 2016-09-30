package com.pa.deals;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.Config;
import com.pa.common.ImageLoader;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.parser.ParserDealGetPackage;
import com.pa.pojo.PackageListItem;
import com.coolfindservices.androidconsumer.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.intercom.android.sdk.Intercom;

/**
 * Created by Steven on 02/11/2015.
 */
public class PackageFragment extends MyFragment implements View.OnClickListener {

    private static final String PARAMS_TAG_COUNTRY = "country";
    private String country  = "";
    private String state    = "";
    private String ServiceID = "";
    private String ServiceName = "  Browse Category";

    private LinearLayout mLayoutRefresh, mEmptyState;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mTxtCategory;
    private ImageLoader loader;
    private PackageAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Boolean isRefresh = false;
    final ArrayList<String> arrCategory = new ArrayList<String>();
    final ArrayList<String> arrCategoryId = new ArrayList<String>();

    private ArrayList<PackageListItem> mItems;
    private int LIMIT = 100;
    private int page = 1;
    private boolean hasNext = false;
    private OnFragmentChangeListener listener;
    private Handler handler;
    private String serviceIDNum;

    private String imageUrl;

    public static PackageFragment newInstance() {
        return new PackageFragment();
    }

    public static PackageFragment newInstance(String country) {
        PackageFragment fragment = new PackageFragment();

        Bundle args = new Bundle();
        args.putString(PARAMS_TAG_COUNTRY, country);
        fragment.setArguments(args);

        return fragment;
    }

    public static PackageFragment newInstance(String category, String country) {
        PackageFragment fragment = new PackageFragment();

        Bundle args = new Bundle();
        args.putString(PARAMS_TAG_COUNTRY, country);
        args.putString("service category", category);
        fragment.setArguments(args);

        return fragment;
    }

    public PackageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  no load back stack
        imageUrl = PARestClient.getDealAbsoluteUrl(pref.getPref(Config.SERVER),
                Config.PACKAGE_IMAGE_PATH);

        loader      = new ImageLoader(getActivity());
        mItems      = new ArrayList<>();
        mAdapter    = new PackageAdapter(getActivity(), mItems, imageUrl, loader);
//        mRecyclerView.setAdapter(mAdapter);
//        mTxtCategory.setText(ServiceName);
//
//        mAdapter.setListener(new PackageAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                PackageListDetailFragment fragment = PackageListDetailFragment.newInstance(mItems.get(position));
//                listener.doFragmentChange(fragment, true, "");
//            }
//        });

        String paramsCountry = getArguments().getString(PARAMS_TAG_COUNTRY);
        if("singapore".equalsIgnoreCase(paramsCountry)){
            country = "sg";
            state   = "";
        }else{
            country ="my";
            state   = paramsCountry.replace(malaysia_prefix, "");
        }

        ServiceID = getArguments().getString("service category");

//        getList("");
        getCategoryList();
//        getList(arrCategoryId.get(arrCategory.indexOf(ServiceID)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_package, container, false);

        v.findViewById(R.id.btnIntercom).setOnClickListener(this);
        v.findViewById(R.id.btnBack).setOnClickListener(this);
        v.findViewById(R.id.btnHome).setOnClickListener(this);

        mLayoutRefresh = (LinearLayout) v.findViewById(R.id.layout_refresh);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mTxtCategory = (TextView) v.findViewById(R.id.txt_category);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        mEmptyState = (LinearLayout) v.findViewById(R.id.empty_state);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mLayoutRefresh.setOnClickListener(this);
        mTxtCategory.setOnClickListener(this);

//        String imageUrl = PARestClient.getDealAbsoluteUrl(pref.getPref(Config.SERVER),
//                Config.PACKAGE_IMAGE_PATH);

//        loader      = new ImageLoader(getActivity());
//        mItems      = new ArrayList<>();
//        mAdapter    = new PackageAdapter(getActivity(), mItems, imageUrl, loader);
        mRecyclerView.setAdapter(mAdapter);
        mTxtCategory.setText(ServiceName);

        mAdapter.setListener(new PackageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                PackageListDetailFragment fragment = PackageListDetailFragment.newInstance(mItems.get(position));
                listener.doFragmentChange(fragment, true, "");
            }
        });

//        String paramsCountry = getArguments().getString(PARAMS_TAG_COUNTRY);
//        if("singapore".equalsIgnoreCase(paramsCountry)){
//            country = "sg";
//            state   = "";
//        }else{
//            country ="my";
//            state   = paramsCountry.replace(malaysia_prefix, "");
//        }
//
//        getList(ServiceID);
//        getCategoryList();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        //        if(mAdapter.getItemCount() < 0){
//        if(mRecyclerView.getChildCount() < 0){
        if(mRecyclerView.getAdapter().getItemCount() == 0){
            mEmptyState.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
        }else{
            mEmptyState.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            analytic.trackScreen("Packages Listing");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;
            case R.id.btnIntercom:
                Intercom.client().displayConversationsList();
                break;
            case R.id.layout_refresh:
                if (mItems != null) {
                    mItems.clear();
                }
                getList(serviceIDNum);
                break;
            case R.id.txt_category:
                showCategorySpinnerSelection(package_category, mTxtCategory, "  Browse Category");

                break;
            case R.id.btnHome:
                for(int i = getActivity().getSupportFragmentManager().getBackStackEntryCount(); i > 1; i--)
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
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
            e.printStackTrace();
        }
    }

    public void getCategoryList() {
//		Tracer.d(strState);

        // AsyncHttpClient client = new AsyncHttpClient();
        arrCategory.clear();
        arrCategoryId.clear();

        RequestParams params = new RequestParams();
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
        params.add("country", country);
        params.add("state", state);
        loadingInternetDialog.show();

        Log.i("Category", "start result");

        PARestClient.dealGet(pref.getPref(Config.SERVER),
                Config.DEAL_API_GET_PACKAGE_CATEGORY,
                params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String content) {
                        // TODO Auto-generated method stub
                        super.onSuccess(content);
                        try {
                            JSONObject json = new JSONObject(content);
                            String status = json.getString("status");
                            if ("success".equals(status)) {

                                String result = json.getString("result");
                                JSONArray arr = new JSONArray(result);

//                                arrCategory.add("VIEW ALL");
//                                arrCategoryId.add("");

                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject obj = arr.getJSONObject(i);
                                    String category = obj.getString("service_name");
                                    String categoryId = obj.getString("service_id");
                                    Log.i("get service name ", category);
                                    arrCategory.add(category);
                                    arrCategoryId.add(categoryId);
                                }

                                package_category = new String[arrCategory.size()];
                                for (int i = 0; i < package_category.length; i++) {
                                    package_category[i] = arrCategory.get(i);
                                }
//                                if (h != null)
//                                    h.sendEmptyMessage(2);
                            } else {
                                simpleToast("Failed to get category, please try again");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i("Category", "catch error");
                        }
                        Log.i("Category", "success result");
                        loadingInternetDialog.dismiss();

                    }

                    @Override
                    public void onFailure(Throwable error, String content) {
                        // TODO Auto-generated method stub
//                        super.onFailure(error, content);
                        loadingInternetDialog.dismiss();
                        Log.i("Category", error.getMessage());
                    }

                    @Override
                    public void onFinish(){
                        super.onFinish();
                        try {
                            if(arrCategory.indexOf(ServiceID) == -1) {
                                //  show empty state
                                mSwipeRefreshLayout.setVisibility(View.GONE);
                                mEmptyState.setVisibility(View.VISIBLE);
                            }else{
                                serviceIDNum = arrCategoryId.get(arrCategory.indexOf(ServiceID));
                                getList(serviceIDNum);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                });

    }

    void refreshItems() {
        // Load items
        // ...
        if (mItems != null) {
            mItems.clear();
        }
        isRefresh = true;
        getList(serviceIDNum);
    }

    private void getList(String serviceId) {

        RequestParams params = new RequestParams();
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
        params.add("limit", LIMIT + "");
        params.add("page", page + "");
        params.add("country", country);
        params.add("state", state);
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
                        if(!isRefresh) {
                            loadingInternetDialog.show();
                        }
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
                                        getList(serviceIDNum);
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
                        if(!isRefresh) {
                            loadingInternetDialog.dismiss();
                        }else{
                            // Stop refresh animation
                            mSwipeRefreshLayout.setRefreshing(false);
                            isRefresh = false;
                        }

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            Log.i("Promotion", new String(responseBody));
                            ParserDealGetPackage parser = new ParserDealGetPackage(new String(responseBody));
                            Log.i("Promotion", parser.getStatus());

                            if ("success".equalsIgnoreCase(parser.getStatus())) {
                                mItems.clear();
                                mItems.addAll(parser.getResult());
                                Log.i("Promotion", mItems.size() + " ");
                                mAdapter.notifyDataSetChanged();

                                mEmptyState.setVisibility(View.GONE);
                                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
    }

    public static class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView mCoverImage, mImgMgp;
            private TextView mTitle, mCompanyName, mDiscountedPrice;
            RatingBar ratingBar;

            public ViewHolder(View v) {
                super(v);
                mCoverImage         = (ImageView) v.findViewById(R.id.coverImage);
                mTitle              = (TextView) v.findViewById(R.id.title);
                mDiscountedPrice    = (TextView) v.findViewById(R.id.discountedPrice);
                mCompanyName        = (TextView)v.findViewById(R.id.companyName);
                ratingBar           = (RatingBar) v.findViewById(R.id.ratingBar1);
                mImgMgp             = (ImageView) v.findViewById(R.id.img_mpg);
            }
        }

        private Activity mActivity;
        private ArrayList<PackageListItem> mItems;
        private String mImageUrl;
        private ImageLoader mImageLoader;
        private OnItemClickListener mListener = null;


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

        private void setListener(OnItemClickListener mListener) {
            this.mListener = mListener;
        }

        @Override
        public PackageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_packagelist, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            PackageListItem item = mItems.get(position);

            if(item.cover_photo == null ){
                holder.mCoverImage.setImageResource(R.drawable.promo_placeholder);
            }
            else{
                String coverUrl = mImageUrl + "/cover/" + item.cover_photo;
                holder.mCoverImage.setTag(coverUrl);

                mImageLoader.DisplayImage(coverUrl, mActivity, holder.mCoverImage, false);
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

    protected void showCategorySpinnerSelection(final String[] strSelection,
                                        final TextView tv, String title) {
        showCategorySpinnerSelection(strSelection, tv, title, null);
    };

    protected void showCategorySpinnerSelection(final String[] strSelection,
                                        final TextView tv, String title, final Handler h) {
        // TODO Auto-generated method stub
        try {
            class MyAdapter extends BaseAdapter {
                String[] str;

                public MyAdapter(String[] str) {
                    this.str = str;
                }

                @Override
                public int getCount() {
                    // TODO Auto-generated method stub
                    return str.length;
                }

                @Override
                public Object getItem(int position) {
                    // TODO Auto-generated method stub
                    return str[position];
                }

                @Override
                public long getItemId(int position) {
                    // TODO Auto-generated method stub
                    return 0;
                }

                @Override
                public View getView(int position, View convertView,
                                    ViewGroup parent) {
                    // TODO Auto-generated method stub
                    View v = inflater.inflate(
                            R.layout.simple_spinner_item_white_text, null);
                    TextView tv = (TextView) v.findViewById(R.id.text1);
                    tv.setText(str[position]);

                    return v;
                }

            }

            ListView list = (ListView) inflater
                    .inflate(R.layout.listview, null);

            MyAdapter dataAdapter = new MyAdapter(strSelection);
            list.setAdapter(dataAdapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    // TODO Auto-generated method stub


                    // getCity(strSelection[arg2]);

                    try {
                        generalDialog.hide();
                        tv.setText(strSelection[arg2]);
                        tv.setTag(arg2);

                        serviceIDNum   = arrCategoryId.get(arg2);
                        ServiceName = arrCategory.get(arg2);

                        getList(serviceIDNum);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setView(list);
            builder.setTitle(title);

            generalDialog = builder.create();
            generalDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
