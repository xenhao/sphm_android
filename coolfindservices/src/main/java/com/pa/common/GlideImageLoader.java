package com.pa.common;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.SOURCE;

/**
 * Created by lenovo on 30-Dec-16.
 */

public class GlideImageLoader {

    public void displayImageGlide(Context context, String url, int placeHolder, ImageView imageView){
        Glide.with(context).load(url).placeholder(placeHolder).dontAnimate().into(imageView);
    }

    public void displayImageGlide(Activity activity, String url, int placeHolder, ImageView imageView){
        Glide.with(activity).load(url).placeholder(placeHolder).dontAnimate().into(imageView);
    }

    public void displayImageGlide(Fragment fragment, String url, int placeHolder, ImageView imageView){
        Glide.with(fragment).load(url).placeholder(placeHolder).dontAnimate().into(imageView);
    }

    public void displayImageGlide(FragmentActivity fragmentActivity, String url, int placeHolder, ImageView imageView){
        Glide.with(fragmentActivity).load(url).placeholder(placeHolder).dontAnimate().into(imageView);
    }

    public void fullSizeImageGlide(Context context, String url, int placeHolder, ImageView imageView){
        Glide.with(context).load(url)
                .placeholder(placeHolder)
                .dontAnimate()
                .diskCacheStrategy(SOURCE) // NONE if you load from sdcard
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(imageView);
    }

    public void fullSizeImageGlide(Activity activity, String url, int placeHolder, ImageView imageView){
        Glide.with(activity).load(url)
                .placeholder(placeHolder)
                .dontAnimate()
                .diskCacheStrategy(SOURCE) // NONE if you load from sdcard
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(imageView);
    }
    public void fullSizeImageGlide(Fragment fragment, String url, int placeHolder, ImageView imageView){
        Glide.with(fragment).load(url)
                .placeholder(placeHolder)
                .dontAnimate()
                .diskCacheStrategy(SOURCE) // NONE if you load from sdcard
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(imageView);
    }

    public void fullSizeImageGlide(FragmentActivity fragmentActivity, String url, int placeHolder, ImageView imageView){
        Glide.with(fragmentActivity).load(url)
                .placeholder(placeHolder)
                .dontAnimate()
                .diskCacheStrategy(SOURCE) // NONE if you load from sdcard
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(imageView);
    }

    public void glideImageCaching(Context context, String url){
        //  Glide image cache
        Glide
                .with(context)
                .load(url)
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                    .into(photoToLoad.imageView)
        ;
    }

    public void glideImageCaching(Activity activity, String url){
        //  Glide image cache
        Glide
                .with(activity)
                .load(url)
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                    .into(photoToLoad.imageView)
        ;
    }

    public void glideImageCaching(Fragment fragment, String url){
        //  Glide image cache
        Glide
                .with(fragment)
                .load(url)
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                    .into(photoToLoad.imageView)
        ;
    }

    public void glideImageCaching(FragmentActivity fragmentActivity, String url){
        //  Glide image cache
        Glide
                .with(fragmentActivity)
                .load(url)
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                    .into(photoToLoad.imageView)
        ;
    }

    public void preload(FragmentActivity fragmentActivity, String url){
        Glide.with(fragmentActivity).load(url).preload();
    }

    public void preload(Context context, String url){
        Glide.with(context).load(url).preload();
    }

    public void preload(Activity activity, String url){
        Glide.with(activity).load(url).preload();
    }

    public void preload(Fragment fragment, String url){
        Glide.with(fragment).load(url).preload();
    }
}
