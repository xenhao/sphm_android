package com.pa.merchant_profile;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.pa.common.AppPreferences;
import com.pa.common.Config;
import com.pa.common.GlideImageLoader;
import com.pa.common.PARestClient;
import com.pa.common.TimeUtils;
import com.pa.pojo.MerchantReview;
import com.coolfindservices.androidconsumer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 27-Dec-16.
 */

public class MerchantDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MerchantProfileData> dataList;
    private Context mContext;
    private AppPreferences pref;

    private GlideImageLoader imageLoader;

    public MerchantDataAdapter(Context context, ArrayList<MerchantProfileData> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        this.pref = new AppPreferences(context);

        imageLoader = new GlideImageLoader();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        switch (viewType){
            case 1:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.merchant_pic, parent , false);
                return new ProfilePic(v);
//                break;

            case 2:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.merchant_about, parent, false);
                return new MerchantAbout(v);
//            break;

            case 3:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.merchant_review, parent, false);
                return new MerchantReviewPreview(v);
//                break;

            case 4:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.merchant_gallery, parent, false);
                return new MerchantGallery(v);
//                break;

            default:
                return null;
//                break;
        }
    }

    @Override
    public int getItemViewType(int position){
        return Integer.parseInt(dataList.get(position).getViewType());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        switch (holder.getItemViewType()) {
            case 1:
                //  setup & display top part of merchant profile
                final ProfilePic profilePic = (ProfilePic) holder;
                profilePic.pic.setColorFilter(Color.argb(150, 50, 50, 50));
                profilePic.ratingBar.setRating(Float.parseFloat(dataList.get(position).overall_rating));
                profilePic.ratingCount.setText(" " + dataList.get(position).overall_rating + " out of 5");
                profilePic.ratingCount1.setText(dataList.get(position).rating_count + " review(s)");
                profilePic.merchantName.setText(dataList.get(position).merchant_name);
                //  merchant profile image
                imageLoader.displayImageGlide(mContext, PARestClient.getAbsoluteUrl(pref.getPref(Config.SERVER), "") + "user/merchant-image?image_name=" + Uri.parse(dataList.get(position).cover_photo), R.drawable.promo_placeholder, profilePic.pic);
                break;

            case 2:
                final MerchantAbout merchantAbout = (MerchantAbout) holder;
                merchantAbout.about.setText(dataList.get(position).about);
                merchantAbout.service.setText(TextUtils.join("\n", dataList.get(position).service_list));
                break;

            case 3:
                final MerchantReviewPreview merchantReviewPreview = (MerchantReviewPreview) holder;

                //  display no review message
                if(dataList.get(position).reviews.isEmpty()) {
                    merchantReviewPreview.noReviews.setVisibility(View.VISIBLE);
                    merchantReviewPreview.btnMore.setVisibility(View.GONE);
                }else{
                    merchantReviewPreview.btnMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showFullReviewList(dataList.get(position).reviews);
                        }
                    });
                }

                ReviewAdapter rAdapter;
                if(dataList.get(position).reviews.size() > 3)
                    rAdapter = new ReviewAdapter(mContext, dataList.get(position).reviews.subList(0, 3));
                else
                    rAdapter = new ReviewAdapter(mContext, dataList.get(position).reviews.subList(0, dataList.get(position).reviews.size()));
                merchantReviewPreview.list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                merchantReviewPreview.list.setAdapter(rAdapter);
                break;

            case 4:
                final MerchantGallery merchantGallery = (MerchantGallery) holder;

                //  display empty gallery message
                if(dataList.get(position).gallery_image.isEmpty()) {
                    merchantGallery.noImages.setVisibility(View.VISIBLE);
                    merchantGallery.btnMore.setVisibility(View.GONE);
                }

                GalleryAdapter gAdapter;
                gAdapter = new GalleryAdapter(mContext, dataList.get(position).gallery_image);
                merchantGallery.grid.setHasFixedSize(true);
                merchantGallery.grid.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
//                merchantGallery.grid.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                merchantGallery.grid.setAdapter(gAdapter);
                break;

            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    protected Dialog fullReviewDialog;
    protected TextView dialogTitle;
    protected RecyclerView reviewList;
    private void showFullReviewList(ArrayList<MerchantReview> reviews) {
        //  dialog styling & setup
        View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_merch_profile_new, null);
        dialogTitle = (TextView) v.findViewById(R.id.dialog_title);
        dialogTitle.setText("Reviews");

        v.findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullReviewDialog.dismiss();
            }
        });

        fullReviewDialog = new Dialog(mContext, R.style.PauseDialog);

        fullReviewDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        fullReviewDialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        fullReviewDialog.setContentView(v);
        fullReviewDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //  setup recycler view
        reviewList = (RecyclerView) fullReviewDialog.findViewById(R.id.merchant_profile_view);

        reviewList.setHasFixedSize(true);

        ReviewAdapter adapter = new ReviewAdapter(mContext, reviews);

        reviewList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        reviewList.setAdapter(adapter);

        fullReviewDialog.show();
    }

    protected Dialog dialogPhotoPreview;

    protected void showDialogPhotoPreview(final View currentView, String uri) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_view_photo, null);
        ImageView currentImg = (ImageView) currentView.findViewById(R.id.img);

        ImageView img = (ImageView) v.findViewById(R.id.img);
//        img.setImageDrawable(currentImg.getDrawable());

        imageLoader.fullSizeImageGlide(mContext, uri, R.drawable.pa_logo, img);

        v.findViewById(R.id.btnCancel2).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        dialogPhotoPreview.dismiss();
                    }
                });

        v.findViewById(R.id.btnNext2).setVisibility(View.GONE);

        // TODO Auto-generated method stub
        dialogPhotoPreview = new Dialog(mContext, R.style.PauseDialog);
        dialogPhotoPreview.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogPhotoPreview.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialogPhotoPreview.setContentView(v);
        dialogPhotoPreview.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        dialogPhotoPreview.show();
    }

    public class ProfilePic extends RecyclerView.ViewHolder{
        protected ImageView pic;
        protected RatingBar ratingBar;
        protected TextView ratingCount, ratingCount1, merchantName;

        public ProfilePic(View view){
            super(view);

            this.pic            = (ImageView) view.findViewById(R.id.img_cover_timeline);
            this.ratingBar      = (RatingBar) view.findViewById(R.id.ratingBar1);
            this.ratingCount    = (TextView) view.findViewById(R.id.ratingCount);
            this.ratingCount1   = (TextView) view.findViewById(R.id.ratingCount1);
            this.merchantName   = (TextView) view.findViewById(R.id.txtName);
        }

    }

    public class MerchantAbout extends RecyclerView.ViewHolder{
        protected TextView about, service;

        public MerchantAbout(View view){
            super(view);

            this.about      = (TextView) view.findViewById(R.id.about);
            this.service    = (TextView) view.findViewById(R.id.service);
        }

    }

    public class MerchantReviewPreview extends RecyclerView.ViewHolder{
        protected TextView btnMore, noReviews;
        protected RecyclerView list;

        public MerchantReviewPreview(View view){
            super(view);

            this.btnMore      = (TextView) view.findViewById(R.id.btnMore);
            this.noReviews    = (TextView) view.findViewById(R.id.no_reviews);
            this.list         = (RecyclerView) view.findViewById(R.id.list);
        }

    }

    public class MerchantGallery extends RecyclerView.ViewHolder{
        protected TextView btnMore, noImages;
        protected RecyclerView grid;

        public MerchantGallery(View view){
            super(view);

            this.btnMore      = (TextView) view.findViewById(R.id.btnMore);
            this.noImages     = (TextView) view.findViewById(R.id.no_images);
            this.grid         = (RecyclerView) view.findViewById(R.id.grid);
        }

    }

    public class ReviewAdapter extends RecyclerView.Adapter {
        private List<MerchantReview> arr_review;

        public ReviewAdapter(Context con, List<MerchantReview> reviews){
            this.arr_review = reviews;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_merchant_review, parent, false);
            return new ReviewItem(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ReviewItem reviewItem = (ReviewItem) holder;

                reviewItem.ratingBar.setRating((int) Float.parseFloat(arr_review.get(position).review_score));
                reviewItem.reviewedBy.setText("Reviewed by "
                        + arr_review.get(position).customer_username
                        + " on "
                        + TimeUtils.timeStampToDDMMYY("" + arr_review.get(position).createdtime
                        * 1000));
                reviewItem.reviewContent.setText(arr_review.get(position).service_description);
        }

        @Override
        public int getItemCount() {
            return arr_review.size();
        }

        public class ReviewItem extends RecyclerView.ViewHolder{
            protected RatingBar ratingBar;
            protected TextView reviewedBy, reviewContent;

            public ReviewItem(View view){
                super(view);

                this.ratingBar      = (RatingBar) view.findViewById(R.id.ratingBar1);
                this.reviewedBy     = (TextView) view.findViewById(R.id.txtName);
                this.reviewContent  = (TextView) view.findViewById(R.id.services);
            }

        }
    }

    public class GalleryAdapter extends RecyclerView.Adapter {
        private List<String> arr_images;

        public GalleryAdapter(Context con, List<String> images){
            this.arr_images = images;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_merchant_gallery, parent, false);
            return new ImageItem(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ImageItem imageItem = (ImageItem) holder;

            final String uri;
                uri = PARestClient.getAbsoluteUrl(pref.getPref(Config.SERVER), "") + "user/merchant-image?image_name=" + Uri.parse(arr_images.get(position));
                imageLoader.displayImageGlide(mContext, uri, R.drawable.default_img, imageItem.img);

                imageItem.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View paramView) {
                        showDialogPhotoPreview(paramView, uri);
                    }
                });
        }

        @Override
        public int getItemCount() {
            return arr_images.size();
        }

        public class ImageItem extends RecyclerView.ViewHolder{
            protected ImageView img;

            public ImageItem(View view){
                super(view);

                this.img = (ImageView) view.findViewById(R.id.img);
            }

        }
    }
}
