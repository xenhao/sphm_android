package com.pa.promotions;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.Config;
import com.pa.common.ImageLoader;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.parser.ParserDealGetPromotion;
import com.pa.pojo.PromotionItem;
import com.coolfindservices.androidconsumer.R;

import org.apache.http.Header;

import java.util.ArrayList;

import io.intercom.android.sdk.Intercom;

public class PromotionFragment extends MyFragment implements View.OnClickListener {

    private static final String PARAMS_TAG_COUNTRY = "country";
    private String country  = "";
    private String state    = "";

    private LinearLayout mLayoutRefresh;
    private RecyclerView mRecyclerView;
    private PromotionAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageLoader loader;

    private OnFragmentChangeListener listener;

    public static PromotionFragment newInstance() {
        return new PromotionFragment();
    }

    public static PromotionFragment newInstance(String country) {
        PromotionFragment fragment = new PromotionFragment();

        Bundle args = new Bundle();
        args.putString(PARAMS_TAG_COUNTRY, country);
        fragment.setArguments(args);

        return fragment;
    }

    public PromotionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_promotion, container, false);

        v.findViewById(R.id.btnIntercom).setOnClickListener(this);
        v.findViewById(R.id.btnBack).setOnClickListener(this);

        mLayoutRefresh  = (LinearLayout) v.findViewById(R.id.layout_refresh);
        mRecyclerView   = (RecyclerView) v.findViewById(R.id.recyclerView);
        mLayoutManager  = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mLayoutRefresh.setOnClickListener(this);

        String imageUrl = PARestClient.getDealAbsoluteUrl(pref.getPref(Config.SERVER),
                Config.DEAL_IMAGE_PATH);

        loader      = new ImageLoader(getActivity());
        mItems      = new ArrayList<>();
        mAdapter    = new PromotionAdapter(getActivity(), mItems, imageUrl, loader);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setListener(new PromotionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                PromotionDetailFragment fragment = PromotionDetailFragment.newInstance(mItems.get(position));
                listener.doFragmentChange(fragment, true, "");
            }
        });
        getList();

        return v;
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

    private int LIMIT = 30;
    private int page = 1;
    private ArrayList<PromotionItem> mItems;
    private boolean hasNext = false;

    private void getList()
    {
        String paramsCountry = getArguments().getString(PARAMS_TAG_COUNTRY);

        if("singapore".equalsIgnoreCase(paramsCountry)){
            country = "sg";
            state   = "";
        }else{
            country ="my";
            state   = paramsCountry.replace(malaysia_prefix, "");
        }

        RequestParams params = new RequestParams();
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
        params.add("limit", LIMIT + "");
        params.add("page", page + "");
        params.add("country", country);
        params.add("state", state);

        PARestClient.dealGet(pref.getPref(Config.SERVER),
                Config.DEAL_API_GET_PROMOTION,
                params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        loadingInternetDialog.show();
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
                                        getList();
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
                        loadingInternetDialog.dismiss();

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.i("Promotion", new String(responseBody));
                        ParserDealGetPromotion parser = new ParserDealGetPromotion(new String(responseBody));
                        Log.i("Promotion", parser.getStatus());

                        if ("success".equalsIgnoreCase(parser.getStatus())) {
                            mItems.clear();
                            mItems.addAll(parser.getResult());
                            Log.i("Promotion", mItems.size() + " ");
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
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
                getList();
                break;
        }
    }

    public static class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView mCoverImage;
            private TextView mTitle, mOriginalPrice, mDiscountedPrice;
            public ViewHolder(View v) {
                super(v);
                mCoverImage = (ImageView) v.findViewById(R.id.coverImage);
                mTitle = (TextView) v.findViewById(R.id.title);
                mOriginalPrice = (TextView) v.findViewById(R.id.originalPrice);
                mDiscountedPrice = (TextView) v.findViewById(R.id.discountedPrice);
            }
        }

        private Activity mActivity;
        private ArrayList<PromotionItem> mItems;
        private String mImageUrl;
        private ImageLoader mImageLoader;
        private OnItemClickListener mListener = null;

        public PromotionAdapter(Activity mActivity, ArrayList<PromotionItem> mItems, String mImageUrl, ImageLoader mImageLoader) {
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
        public PromotionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_promotion, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            PromotionItem item = mItems.get(position);

            String coverUrl = mImageUrl + "/cover/" + item.cover_photo;
            holder.mCoverImage.setTag(coverUrl);
            mImageLoader.DisplayImage(coverUrl, mActivity, holder.mCoverImage, false);

            holder.mTitle.setText(item.title);
            holder.mOriginalPrice.setText(item.currency + " " + item.price);
            holder.mOriginalPrice.setPaintFlags(holder.mOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mDiscountedPrice.setText(item.currency + " " + item.discount_price);

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
}
