package com.pa.deals;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.coolfindservices.android.SplashActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.Config;
import com.pa.common.GlideImageLoader;
import com.pa.common.GlobalVar;
import com.pa.common.ImageLoader;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.common.TimeUtils;
import com.pa.landing.ActivityLanding;
import com.pa.merchant_profile.MerchantProfileDialog;
import com.pa.parser.ParseDealGetPackageListDetail;
import com.pa.parser.ParserMerchantProfile;
import com.pa.pojo.MerchantProfile;
import com.pa.pojo.MerchantReview;
import com.pa.pojo.PackageListDetailItem;
import com.pa.pojo.PackageListItem;
import com.coolfindservices.androidconsumer.R;

import org.apache.http.Header;

import java.util.ArrayList;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.commons.utilities.StringUtils;
import io.intercom.android.sdk.utilities.ColorUtils;

/**
 * Created by Steven on 02/11/2015.
 */
public class PackageListDetailFragment extends MyFragment implements View.OnClickListener {

    private static final String LOG_TAG = "PackageListDetailFragment";
    private static final String PARAMS_TAG_COUNTRY = "country";
    private String country  = "";
    private String state    = "";

    private float paymentPrice = 0.0f;
    private MerchantProfile mMerchantProfile;
    private String MY_URI;
    private ImageLoader loader, mMerchantLoader;
    private GlideImageLoader glideLoader;
    private PackageListItem mItem = null;
    private PackageListDetailAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button mBtnMerchantProfile;
    private Dialog mMerchantDialog;
    private ViewFlipper mMerchantFlipper;
    private TextView mMerchantTitle, mPackageTitle ;
    private ImageView mCoverImage, mImgMgp;

    ArrayList<MerchantReview> arr_review = new ArrayList<>();
    ArrayList<String> arr_image = new ArrayList<>();
    RatingBar ratingBar;

    private ArrayList<PackageListDetailItem> mItems ;
    private int LIMIT = 30;
    private int page = 1;
    private boolean hasNext = false;
    private OnFragmentChangeListener listener;

    private String imageUrl;

//    public static PackageListDetailFragment newInstance() {
//        return new PackageListDetailFragment();
//    }

    public static PackageListDetailFragment newInstance(PackageListItem param1) {
        PackageListDetailFragment fragment = new PackageListDetailFragment();
        fragment.setItem(param1);
        return fragment;
    }

    public PackageListDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MY_URI = PARestClient.getAbsoluteUrl(pref.getPref(Config.SERVER), "");

        //  no load back stack
        imageUrl = PARestClient.getDealAbsoluteUrl(pref.getPref(Config.SERVER),
                Config.PACKAGE_IMAGE_PATH);

        loader          = new ImageLoader(getActivity());
        mMerchantLoader = new ImageLoader(getActivity());
        glideLoader     = new GlideImageLoader();
        mItems      = new ArrayList<>();
        mAdapter    = new PackageListDetailAdapter(getActivity(), mItems, imageUrl, loader);

        getList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_package_list_detail, container, false);
        v.findViewById(R.id.btnHome).setOnClickListener(this);
        v.findViewById(R.id.btnIntercom).setOnClickListener(this);
        v.findViewById(R.id.btnBack).setOnClickListener(this);
        v.findViewById(R.id.btn_merchant_profile).setOnClickListener(this);

        mMerchantTitle  = (TextView) v.findViewById(R.id.MerchantTitle);
        mRecyclerView   = (RecyclerView) v.findViewById(R.id.recyclerView);
        ratingBar       = (RatingBar) v.findViewById(R.id.ratingBar1);
        mPackageTitle   = (TextView) v.findViewById(R.id.PackageTitle);
        mCoverImage     = (ImageView) v.findViewById(R.id.coverImage);
        mImgMgp         = (ImageView) v.findViewById(R.id.img_mpg);

        mLayoutManager  = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mMerchantTitle.setText(mItem.merchant_co_name);
        ratingBar.setRating(mItem.rating);
        mPackageTitle.setText(mItem.title);

//        String imageUrl = PARestClient.getDealAbsoluteUrl(pref.getPref(Config.SERVER),
//                Config.PACKAGE_IMAGE_PATH);

//        loader          = new ImageLoader(getActivity());
//        mMerchantLoader = new ImageLoader(getActivity());
//        mItems      = new ArrayList<>();
//        mAdapter    = new PackageListDetailAdapter(getActivity(), mItems, imageUrl, loader);
        mRecyclerView.setAdapter(mAdapter);

        String coverUrl = imageUrl + "/cover/" + mItem.cover_photo;
//        mCoverImage.setTag(coverUrl);
//        loader.setDefaultImage(R.drawable.promo_placeholder);
//        loader.DisplayImage(coverUrl, getActivity(), mCoverImage, false);

        //  Glide image loader
        glideLoader.displayImageGlide(getActivity(),coverUrl, R.drawable.promo_placeholder, mCoverImage);
//        if(TextUtils.isEmpty(mItem.cover_photo))
//            Glide.with(getActivity()).load(R.drawable.promo_placeholder).into(mCoverImage);
//        else
//            Glide.with(getActivity()).load(coverUrl).into(mCoverImage);

        mMerchantLoader.setRequiredSize(210);
        if (mItem.mgp != null && mItem.mgp.equalsIgnoreCase("Y")) {
            mImgMgp.setVisibility(View.VISIBLE);
        } else {
            mImgMgp.setVisibility(View.GONE);
        }


        mAdapter.setListener(new PackageListDetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                PackageDetailFragment fragment = PackageDetailFragment.newInstance(mItems.get(position));
                listener.doFragmentChange(fragment, true, "");
            }
        });

//        getList();

        //	show bottom navigation bar
        ((ActivityLanding) getActivity()).showBottomBar(true);

        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            analytic.trackScreen("Package List Details");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setItem(PackageListItem mItem) {
        this.mItem = mItem;
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
            case R.id.btnHome:
                for(int i = getActivity().getSupportFragmentManager().getBackStackEntryCount(); i > 1; i--)
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;
//            case R.id.btnNext:
//                doNext();
//                break;
//            case R.id.btnPromoCode:
//                String promoCode = inputPromoCode.getText().toString();
//                if ("".equals(promoCode)) {
//                    showMessageDialog("The Promo Code field is required");
//                    return;
//                }
//                hideKeyboard(inputPromoCode);
//                validatePromoCode(promoCode, v.getRootView());
//                break;
            case R.id.btn_merchant_profile:
                if(GlobalVar.isGuest) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            getActivity());
                    alertDialogBuilder.setTitle("Sign In");
                    alertDialogBuilder
                            .setMessage("Sign In or Register to Proceed?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(getActivity(), SplashActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    GlobalVar.isResumeGuest = true;
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }else {
                    onGetMerchantProfile();
                }
                break;
        }
    }

    //    public void setItem(PackageListItem mItem) {
//        this.mItem = mItem;
//    }
    private void doNext()
    {

    }

    private void getList() {

//        String paramsCountry = getArguments().getString(PARAMS_TAG_COUNTRY);
//
//        if("singapore".equalsIgnoreCase(paramsCountry)){
//            country = "sg";
//            state   = "";
//        }else{
//            country ="my";
//            state   = paramsCountry.replace(malaysia_prefix, "");
//        }

        RequestParams params = new RequestParams();
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
//        params.add("limit", LIMIT + "");
//        params.add("page", page + "");
//        params.add("country", country);
//        params.add("state", state);
        params.add("package_serial",mItem.serial);

        Log.i("Package Fragment", params.toString());
        Log.i("Package Fragment", pref.getPref(Config.SERVER) + Config.DEAL_API_GET_PACKAGE_DETAIL_LIST);

        PARestClient.dealGet(pref.getPref(Config.SERVER),
                Config.DEAL_API_GET_PACKAGE_DETAIL_LIST,
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
                        try {
                            Log.i("Promotion", new String(responseBody));
                            ParseDealGetPackageListDetail parser = new ParseDealGetPackageListDetail(new String(responseBody));
                            Log.i("Promotion", parser.getStatus());

                            if ("success".equalsIgnoreCase(parser.getStatus())) {
                                mItems.clear();
                                mItems.addAll(parser.getResult());
                                Log.i("Promotion", mItems.size() + " ");
                                mAdapter.notifyDataSetChanged();
                                Log.i("Promotion 2", mAdapter.getItemCount() + " ");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
    }

    public static class PackageListDetailAdapter extends RecyclerView.Adapter<PackageListDetailAdapter.ViewHolder> {


        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView mTitle, mOriginalPrice, mDiscountedPrice, mPackageListDesc;
            private LinearLayout ViewItem;
            public ViewHolder(View v) {
                super(v);

                mTitle = (TextView) v.findViewById(R.id.title);
                mOriginalPrice = (TextView) v.findViewById(R.id.originalPrice);
                mDiscountedPrice = (TextView) v.findViewById(R.id.discountedPrice);
                mPackageListDesc = (TextView) v.findViewById(R.id.packageListDesc);
                ViewItem = (LinearLayout)v.findViewById(R.id.ListViewItem);
            }
        }

        private Activity mActivity;
        private ArrayList<PackageListDetailItem> mItems;
        private String mImageUrl;
        private ImageLoader mImageLoader;
        private OnItemClickListener mListener = null;


        public PackageListDetailAdapter(Activity mActivity, ArrayList<PackageListDetailItem> mItems, String mImageUrl, ImageLoader mImageLoader) {
            this.mActivity = mActivity;
            this.mItems = mItems;
            this.mImageUrl = mImageUrl;
            this.mImageLoader = mImageLoader;
        }

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        private void setListener(OnItemClickListener mListener) {
            this.mListener = mListener;
        }

        @Override
        public PackageListDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_packagelistdetail, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            PackageListDetailItem item = mItems.get(position);

//            String coverUrl = mImageUrl + "/cover/" + item.cover_photo;
//            holder.mCoverImage.setTag(coverUrl);
//            mImageLoader.DisplayImage(coverUrl, mActivity, holder.mCoverImage, false);

            holder.mTitle.setText(item.title);
            holder.mOriginalPrice.setText(item.currency + " " + item.ori_price);
            holder.mOriginalPrice.setPaintFlags(holder.mOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mDiscountedPrice.setText(item.currency + " " + item.price);
            holder.mPackageListDesc.setText(item.short_description);

            holder.ViewItem.setOnClickListener(new View.OnClickListener() {
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

    private void onGetMerchantProfile() {
        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                // TODO Auto-generated method stub
                super.onSuccess(arg0, arg1, arg2);

                try {

                    ParserMerchantProfile parser = new ParserMerchantProfile(
                            new String(arg2));
                    mMerchantProfile = parser.getData();
                    arr_image = mMerchantProfile.gallery_image;
                    arr_review = mMerchantProfile.reviews;
//                    onShowMerchantProfileDialog();
                    //  new merchant profile
                    new MerchantProfileDialog(getContext(), mMerchantProfile, arr_image, arr_review);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                Log.i("onFailure", content);
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
        cover_timeline.setColorFilter(Color.argb(150, 50, 50, 50));
        TextView txtName = (TextView) v.findViewById(R.id.txtName);
        TextView ratingCount = (TextView) v.findViewById(R.id.ratingCount);
        TextView ratingCount1 = (TextView) v.findViewById(R.id.ratingCount1);

        RatingBar ratingBar = (RatingBar) v.findViewById(R.id.ratingBar1);

        TextView about = (TextView) v.findViewById(R.id.about);
        TextView service = (TextView) v.findViewById(R.id.service);

//        cover_timeline.setTag(MY_URI + "user/merchant-image?image_name="
//                + mMerchantProfile.cover_photo);
//        mMerchantLoader.DisplayImage(MY_URI + "user/merchant-image?image_name="
//                + mMerchantProfile.cover_photo, getActivity(), cover_timeline);

        //  new
//        cover_timeline.setTag(Config.MERCHANT_IMAGE_PREFIX
//                + mMerchantProfile.cover_photo);
//        mMerchantLoader.DisplayImage(Config.MERCHANT_IMAGE_PREFIX
//                + mMerchantProfile.cover_photo, getActivity(), cover_timeline);

        glideLoader.displayImageGlide(getActivity() ,MY_URI + "user/merchant-image?image_name=" + mMerchantProfile.cover_photo, R.drawable.default_img, cover_timeline);
//        if(TextUtils.isEmpty(mMerchantProfile.cover_photo))
//            Glide.with(this).load(R.drawable.default_img).into(cover_timeline);
//        else
//            Glide.with(this).load(MY_URI + "user/merchant-image?image_name=" + mMerchantProfile.cover_photo).into(cover_timeline);

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
//            holder.img.setTag(MY_URI + "user/merchant-image?image_name=" + uri);
//            mMerchantLoader.DisplayImage(MY_URI + "user/merchant-image?image_name="
//                    + uri, getActivity(), holder.img);

            //  new
//            holder.img.setTag(Config.MERCHANT_IMAGE_PREFIX + uri);
//            mMerchantLoader.DisplayImage(Config.MERCHANT_IMAGE_PREFIX
//                    + uri, getActivity(), holder.img);

            glideLoader.displayImageGlide(getContext() ,MY_URI + "user/merchant-image?image_name=" + uri, R.drawable.default_img, holder.img);
//            if(TextUtils.isEmpty(mMerchantProfile.cover_photo))
//                Glide.with(getContext()).load(R.drawable.default_img).into(holder.img);
//            else
//                Glide.with(getContext()).load(MY_URI + "user/merchant-image?image_name=" + uri).into(holder.img);

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
