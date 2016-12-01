package com.pa.deals;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.Config;
import com.pa.common.ImageLoader;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.landing.ActivityLanding;
import com.pa.parser.ParseDealGetPackageListDetail;
import com.pa.pojo.PackageListDetailItem;
import com.coolfindservices.androidconsumer.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.intercom.android.sdk.Intercom;

/**
 * Created by Steven on 02/11/2015.
 */
public class PackagePromoFragment extends MyFragment implements View.OnClickListener {

    private static final String LOG_TAG             = "PackagePromoFragment";
    private static final String PARAMS_TAG_COUNTRY  = "COUNTRY_PARAM";
    private static final String PARAMS_TAG_STATE    = "STATE_PARAM";
    private String country      = "";
    private String state        = "";
    public String imageUrl      = "";
    private String ServiceID    = "";
    private String ServiceName  = " Browse Category";
    private int LIMIT   = 100;
    private int page    = 1;
    private ArrayList<PackageListDetailItem> mItems ;
    private ArrayList<String> arrCategory   = new ArrayList<String>();
    private ArrayList<String> arrCategoryId = new ArrayList<String>();

    private RecyclerView mRecyclerView;
    private TextView mTxtCategory;

    private LinearLayoutManager mLayoutManager;
    private ImageLoader mImageLoader;
    private PackagePromoAdapter mAdapter;
    private OnFragmentChangeListener mListener;

    public static PackagePromoFragment newInstance(String country, String state) {
        PackagePromoFragment fragment = new PackagePromoFragment();

        Bundle args = new Bundle();
        args.putString(PARAMS_TAG_COUNTRY, country);
        args.putString(PARAMS_TAG_STATE, state);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_package_promo, container, false);

        v.findViewById(R.id.btnIntercom).setOnClickListener(this);
        v.findViewById(R.id.btnBack).setOnClickListener(this);
        v.findViewById(R.id.layout_refresh).setOnClickListener(this);

        PackagePromoAdapter.OnItemClickListener mAdapterListener =
                new PackagePromoAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        PackageDetailFragment fragment =
                                PackageDetailFragment.newInstance(mItems.get(position));
                        mListener.doFragmentChange(fragment, true, "");
                    }
                };

        country         = this.getArguments().getString(PARAMS_TAG_COUNTRY);
        state           = this.getArguments().getString(PARAMS_TAG_STATE);
        imageUrl        = PARestClient.getDealAbsoluteUrl(pref.getPref(Config.SERVER),
                Config.PACKAGE_IMAGE_PATH);

        mTxtCategory    = (TextView) v.findViewById(R.id.txt_category);
        mRecyclerView   = (RecyclerView) v.findViewById(R.id.recyclerView);
        mLayoutManager  = new LinearLayoutManager(getActivity());
        mImageLoader    = new ImageLoader(this.getActivity());
        mItems          = new ArrayList<>();
        mAdapter        = new PackagePromoAdapter(mItems, getActivity(), imageUrl);
        mAdapter.setListener(mAdapterListener);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mTxtCategory.setOnClickListener(this);
        mTxtCategory.setText(ServiceName);

        getPromoList();
        getCategoryList();

        //	hide bottom navigation bar
        ((ActivityLanding) getActivity()).showBottomBar(false);

        return v;
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;
            case R.id.btnIntercom:
                Intercom.client().displayConversationsList();
                break;
            case R.id.layout_refresh:
                if (mItems != null)
                    mItems.clear();
                getPromoList();
            case R.id.txt_category:
                showCategorySpinnerSelection(package_category, mTxtCategory, "  Browse Category");
                break;
        }
    }

    public void getCategoryList() {
        arrCategory.clear();
        arrCategoryId.clear();

        RequestParams params = new RequestParams();
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
        params.add("country", country);
        params.add("state", state);
        params.add("is_promotion", "Y");
        loadingInternetDialog.show();

        AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                loadingInternetDialog.show();
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( getActivity());
                alertDialogBuilder
                        .setTitle("Network Error")
                        .setMessage("Click yes to reload!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getCategoryList();
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
            public void onFinish() {
                super.onFinish();
                loadingInternetDialog.dismiss();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    String status   = json.getString("status");
                    if ("success".equals(status)) {
                        String result = json.getString("result");
                        JSONArray arr = new JSONArray(result);

//                        arrCategory.add("VIEW ALL");
//                        arrCategoryId.add("");

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj      = arr.getJSONObject(i);
                            String category     = obj.getString("service_name");
                            String categoryId   = obj.getString("service_id");
                            arrCategory.add(category);
                            arrCategoryId.add(categoryId);
                        }

                        package_category = new String[arrCategory.size()];
                        for (int i = 0; i < package_category.length; i++) {
                            package_category[i] = arrCategory.get(i);
                        }
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
        };

        PARestClient.dealGet(pref.getPref(Config.SERVER), Config.DEAL_API_GET_PACKAGE_CATEGORY,
                params, mHandler);
    }

    private void getPromoList() {
        RequestParams params = new RequestParams();
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
        params.add("limit", LIMIT + "");
        params.add("page", page + "");
        params.add("country", country);
        params.add("state", state);
        params.add("is_promotion", "Y");
        params.add("service_id", ServiceID);

        AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                loadingInternetDialog.show();
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( getActivity());
                alertDialogBuilder
                        .setTitle("Network Error")
                        .setMessage("Click yes to reload!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getPromoList();
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
            public void onFinish() {
                super.onFinish();
                loadingInternetDialog.dismiss();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.i("Promotion", new String(responseBody));
                    ParseDealGetPackageListDetail parser =
                            new ParseDealGetPackageListDetail(new String(responseBody));

                    if ("success".equalsIgnoreCase(parser.getStatus())) {
                        mItems.clear();
                        mItems.addAll(parser.getResult());
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        Log.i("Package Fragment", params.toString());
        Log.i("Package Fragment", pref.getPref(Config.SERVER) + Config.DEAL_API_GET_PACKAGE);

        PARestClient.dealGet(pref.getPref(Config.SERVER), Config.DEAL_API_GET_PACKAGE_DETAIL_LIST,
                params, mHandler);
    }

    public static class PackagePromoAdapter extends RecyclerView.Adapter<PackagePromoAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView mTitle;
            private TextView mOriginalPrice;
            private TextView mDiscountedPrice;
            private TextView mPackageListDesc;
            private ImageView mCoverImage;
            private View mView;

            public ViewHolder(View v) {
                super(v);
                mTitle              = (TextView) v.findViewById(R.id.title);
                mOriginalPrice      = (TextView) v.findViewById(R.id.originalPrice);
                mDiscountedPrice    = (TextView) v.findViewById(R.id.discountedPrice);
                mPackageListDesc    = (TextView) v.findViewById(R.id.packageListDesc);
                mCoverImage         = (ImageView) v.findViewById(R.id.coverImage);
                mView               = v;
            }
        }

        private ArrayList<PackageListDetailItem> mItems = new ArrayList<>();
        private String mImageUrl = "";

        private OnItemClickListener mListener = null;
        private ImageLoader mImageLoader;
        private Context mContext;

        public PackagePromoAdapter(ArrayList<PackageListDetailItem> mItems,
                                   Context mContext, String mImageUrl) {
            this.mItems         = mItems;
            this.mImageLoader   = new ImageLoader(mContext);
            this.mImageUrl      = mImageUrl;
            this.mContext       = mContext;
            this.mImageLoader.setDefaultImage(R.drawable.promo_placeholder);
        }

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        private void setListener(OnItemClickListener mListener) {
            this.mListener = mListener;
        }

        @Override
        public PackagePromoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_packagelistpromo, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            PackageListDetailItem item = mItems.get(position);

            holder.mTitle.setText(item.title);
            holder.mOriginalPrice.setText(item.currency + " " + item.ori_price);
            holder.mOriginalPrice.setPaintFlags(holder.mOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mDiscountedPrice.setText(item.currency + " " + item.price);
            holder.mPackageListDesc.setText(item.short_description);
            if(item.cover_photo == null ){
                holder.mCoverImage.setImageResource(R.drawable.promo_placeholder);
            }
            else{
                String coverUrl = mImageUrl + "/cover/" + item.cover_photo;
                holder.mCoverImage.setTag(coverUrl);
                mImageLoader.DisplayImage(coverUrl, (Activity)mContext, holder.mCoverImage, false);
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
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

    protected void showCategorySpinnerSelection(final String[] strSelection, final TextView tv,
                                                String title) {
        showCategorySpinnerSelection(strSelection, tv, title, null);
    }

    protected void showCategorySpinnerSelection(final String[] strSelection, final TextView tv,
                                                String title, final Handler h) {
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
                    View v      = inflater.inflate(R.layout.simple_spinner_item_white_text, null);
                    TextView tv = (TextView) v.findViewById(R.id.text1);
                    tv.setText(str[position]);
                    return v;
                }
            }

            ListView list           = (ListView) inflater.inflate(R.layout.listview, null);
            MyAdapter dataAdapter   = new MyAdapter(strSelection);
            list.setAdapter(dataAdapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    try {
                        generalDialog.hide();
                        tv.setText(strSelection[arg2]);
                        tv.setTag(arg2);

                        ServiceID   = arrCategoryId.get(arg2);
                        ServiceName = arrCategory.get(arg2);

                        getPromoList();
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
