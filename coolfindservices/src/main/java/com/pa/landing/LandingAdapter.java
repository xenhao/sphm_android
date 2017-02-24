package com.pa.landing;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

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
import com.pa.common.Analytic;
import com.pa.common.AppPreferences;
import com.pa.common.Config;
import com.pa.common.GlideImageLoader;
import com.pa.common.GlobalVar;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.common.Tracer;
import com.pa.deals.PackageListDetailFragment;
import com.pa.pojo.ServiceCategory;
import com.coolfindservices.androidconsumer.R;

import java.util.ArrayList;
import java.util.List;

import static com.pa.common.Config.DOMAIN_DEV;
import static com.pa.common.Config.DOMAIN_LIVE;
import static com.pa.common.Config.DOMAIN_STAGING;

/**
 * Created by lenovo on 03-Feb-17.
 */

public class LandingAdapter extends RecyclerView.Adapter {

    private GlideImageLoader imageLoader;
    private ArrayList<LandingDataType> dataList;
    private Activity mContext;
    private AppPreferences pref;

    private String imageUrl;
    private Dialog dialogWeb;

    public LandingAdapter(Activity context, ArrayList<LandingDataType> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        this.pref = new AppPreferences(context);

        imageLoader = new GlideImageLoader();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch(viewType){
            case 1:
                Log.i("WTFFFFF", "WHY ARE YOU NOT HERE???");
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promo_banner, parent, false);
                return new PromoBanner(v);
//                break;

            case 2:
                Log.i("WTFFFFF", "OR HERE???");
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_grid, parent, false);
                return new HomeGrid(v);
//                break;

            default:
                Log.i("WTFFFFFFF", "WHY ARE YOU HERE!!!!!!??????");
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()){
            case 1:
                imageUrl = PARestClient.getDealAbsoluteUrl(pref.getPref(Config.SERVER),
                        Config.PACKAGE_IMAGE_PATH);
                final PromoBanner promoBanner = (PromoBanner) holder;
                setupSlider(promoBanner, dataList, dataList.get(0).homeBanner.size() > 0);
                break;

            case 2:
                final HomeGrid homeGrid = (HomeGrid) holder;

                GridAdapter gAdapter;
                gAdapter = new GridAdapter(mContext, dataList.get(position).homeGrid);
                homeGrid.grid.setHasFixedSize(true);
                homeGrid.grid.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
                homeGrid.grid.setAdapter(gAdapter);
                break;

            default:
                //  nothing to place in view
                Log.i("WTFFFFFFF", "nothing to place in RecyclerView");
                break;
        }
    }

    private void setupSlider(final PromoBanner promoBanner, final ArrayList<LandingDataType> items, boolean hasPromo){
        //  clear sliders of previous data before repopulating them
        promoBanner.slider.removeAllSliders();
        for(int i = 0; i < items.get(0).homeBanner.size(); i++){
            DefaultSliderView defaultSliderView = new DefaultSliderView(mContext);
            defaultSliderView.image(imageUrl+"/cover/"+items.get(0).homeBanner.get(i).cover_photo).setScaleType(BaseSliderView.ScaleType.Fit);
            defaultSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    PackageListDetailFragment fragment = PackageListDetailFragment.newInstance(items.get(0).homeBanner.get(promoBanner.slider.getCurrentPosition()));
                    ((OnFragmentChangeListener) mContext).doFragmentChange(fragment, true, "");
                }
            });
            promoBanner.slider.addSlider(defaultSliderView);
        }
        //  custom indicator
        promoBanner.slider.setCustomIndicator(promoBanner.customIndicator);
        promoBanner.customIndicator.redraw();
        //  set duration before banner change
        promoBanner.slider.setDuration(5000);
        //  show or hide banner
//        promoBanner.slider.setVisibility(hasPromo ? View.VISIBLE : View.GONE);
//        sliderContainer.setVisibility(hasPromo ? View.VISIBLE : View.GONE);
//        promoBanner.customIndicator.setVisibility(hasPromo ? View.VISIBLE : View.GONE);

        if(!hasPromo){
            //  no promotions, set 1dp for banner instead of GONE or 0dp so that swipe refresh would work
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) promoBanner.slider.getLayoutParams();
            params.height = 1;
            promoBanner.slider.setLayoutParams(params);
        }else {
            //  set banner height according to device screen density (dpi)
            DisplayMetrics metrics = new DisplayMetrics();
            mContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) promoBanner.slider.getLayoutParams();
            params.height = metrics.densityDpi;
            promoBanner.slider.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    @Override
    public int getItemViewType(int position){
        return Integer.parseInt(dataList.get(position).getViewType());
    }

    public void showWebDialog(String url, String title) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_web, null);
        TextView pageTitle=(TextView)v.findViewById(R.id.title);
        if(null!=title){
            pageTitle.setVisibility(View.VISIBLE);
            pageTitle.setText(title);
        }

        final ViewSwitcher vs = (ViewSwitcher) v.findViewById(R.id.vs);
        v.findViewById(R.id.btnCancel2).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        dialogWeb.dismiss();
                    }
                });

        WebView w = (WebView) v.findViewById(R.id.web);
        w.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);
                vs.setDisplayedChild(1);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                vs.setDisplayedChild(0);
            }

        });
        w.getSettings().setJavaScriptEnabled(true);

        // Enable WebView to play iframe video
        w.setWebChromeClient(new WebChromeClient());

        dialogWeb = new Dialog(mContext, R.style.PauseDialog);
        dialogWeb.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogWeb.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialogWeb.setContentView(v);
        dialogWeb.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        dialogWeb.show();

        w.loadUrl(url);
    }

    public class PromoBanner extends RecyclerView.ViewHolder{
        protected SliderLayout slider;
        protected PagerIndicator customIndicator;

        public PromoBanner(View view){
            super(view);

            this.slider            = (SliderLayout) view.findViewById(R.id.slider);
            this.customIndicator   = (PagerIndicator) view.findViewById(R.id.custom_indicator);
        }

    }

    public class HomeGrid extends RecyclerView.ViewHolder{

        protected RecyclerView grid;

        public HomeGrid(View view){
            super(view);
            this.grid = (RecyclerView) view.findViewById(R.id.gridCategory);
        }

    }

    public class GridAdapter extends RecyclerView.Adapter {
        private List<ServiceCategory> arr_images;

        GridAdapter(Context con, List<ServiceCategory> images){
            this.arr_images = images;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_category, parent, false);
            return new ImageItem(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final ImageItem imageItem = (ImageItem) holder;

            String uri="";
            if ("0".equals(pref.getPref(Config.SERVER))) {
                uri = "http://" + DOMAIN_DEV + "/";

            } else if ("1".equals(pref.getPref(Config.SERVER))) {

                uri = "http://" + DOMAIN_STAGING + "/";

            }
            else if ("2".equals(pref.getPref(Config.SERVER))) {

                uri = "http://" + DOMAIN_LIVE + "/";

            }

            String url=uri+arr_images.get(position).icon_image
                    +"?session_username="+pref.getPref(Config.PREF_USERNAME)
                    +"&active_session_token="+pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN)
                    ;
            Tracer.d(url);
//            uri = PARestClient.getAbsoluteUrl(pref.getPref(Config.SERVER), "") + "user/merchant-image?image_name=" + Uri.parse(arr_images.get(position));
            Glide
                    .with(mContext)
                    .load(url)
                    .placeholder(R.drawable.default_img)
                    .dontAnimate()
                    .thumbnail(Glide // this thumbnail request has to have the same RESULT cache key
                            .with(mContext) // as the outer request, which usually simply means
                            .load(url) // same size/transformation(e.g. centerCrop)/format(e.g. asBitmap)
                            .fitCenter() // have to be explicit here to match outer load exactly
                    )
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            e.printStackTrace();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if (isFirstResource) {
                                return false; // thumbnail was not shown, do as usual
                            }
                            return new DrawableCrossFadeFactory<Drawable>(/* customize animation here */)
                                    .build(false, false) // force crossFade() even if coming from memory cache
                                    .animate(resource, (GlideAnimation.ViewAdapter)target);
                        }
                    })
                    //.fitCenter() // this is implicitly added when .into() is called if there's no scaleType in xml or the value is fitCenter there
                    .into(imageItem.img);

            //****  change to show category instead     ****//
            imageItem.img.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
//                        int pos = (Integer) v.getTag(R.id.action_settings);

                        if(arr_images.get(position).id.equals("765")) {
                            showWebDialog("http://www.pageadvisor.com/myrepublic", null);
                        }else{
                            ServiceCategory sc = arr_images.get(position);
                            ArrayList<ServiceCategory> arr = new ArrayList<ServiceCategory>();
                            arr.add(sc);
                            //  set GlobalVar.country before proceeding
                            TextView tempCountry2 = (TextView) mContext.findViewById(R.id.country2);
                            GlobalVar.country = tempCountry2.getText().toString();
                            ((OnFragmentChangeListener) mContext).doFragmentChange(new FragmentCategoryTab(arr_images.get(position).service_name, arr_images.get(position).id, tempCountry2.getText().toString(), arr),
                                    true, "");

                            new Analytic(mContext).trackCustomDimension("Category", 1, arr_images.get(position).service_name);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

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
