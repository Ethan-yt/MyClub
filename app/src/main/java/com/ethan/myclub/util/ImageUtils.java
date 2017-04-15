package com.ethan.myclub.util;

import android.app.Activity;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ethan.myclub.R;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by ethan on 2017/4/11.
 */

public class ImageUtils {
    private static final String TAG = "Image Util";


    @BindingAdapter({"image_url"})
    public static void loadImageUrl(final ImageView view, String imageUrl) {
        if (TextUtils.isEmpty(imageUrl))
            return;
        Log.i(TAG, "loadImageUrl: " + imageUrl);
        loadImage(view, imageUrl, true);
    }

    @BindingAdapter({"image_url_rect"})
    public static void loadImageUrlRect(final ImageView view, String imageUrl) {
        if (TextUtils.isEmpty(imageUrl))
            return;
        Log.i(TAG, "loadImageUrlRect: " + imageUrl);
        loadImage(view, imageUrl, false);
    }

    @BindingAdapter({"image_uri"})
    public static void loadImageUri(final ImageView view, Uri imageUri) {
        if (imageUri == null)
            return;
        loadImage(view, imageUri, true, imageUri.getScheme().equals("http"));
    }

    @BindingAdapter({"image_uri_rect"})
    public static void loadImageUriRect(final ImageView view, Uri imageUri) {
        if (imageUri == null)
            return;
        loadImage(view, imageUri, false, imageUri.getScheme().equals("http"));
    }

    private static void loadImage(ImageView target, Object resource, boolean isCircle) {
        loadImage(target, resource, isCircle, false);
    }

    private static void loadImage(ImageView target, Object resource, boolean isCircle, boolean isSkipCache) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (target.getContext() instanceof Activity) {
                Activity activity = (Activity) target.getContext();
                if (activity != null && activity.isDestroyed()) {
                    return;
                }
            }
        }

        Boolean skipMemoryCache = true;
        DiskCacheStrategy diskCacheStrategy = DiskCacheStrategy.NONE;

        if (isSkipCache) {
            skipMemoryCache = false;
            diskCacheStrategy = DiskCacheStrategy.ALL;
        }

        DrawableRequestBuilder<Object> builder = Glide.with(target.getContext())
                .load(resource)
                .listener(new RequestListener<Object, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Object model, Target<GlideDrawable> target, boolean isFirstResource) {
                        e.printStackTrace();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Object model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .crossFade()
                .skipMemoryCache(skipMemoryCache)
                .diskCacheStrategy(diskCacheStrategy);
        if (isCircle)
            builder.bitmapTransform(new CropCircleTransformation(target.getContext()));
        builder.into(target);
    }
}
