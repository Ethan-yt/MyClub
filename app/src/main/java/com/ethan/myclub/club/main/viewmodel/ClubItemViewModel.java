package com.ethan.myclub.club.main.viewmodel;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ethan.myclub.R;
import com.ethan.myclub.network.ApiHelper;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by ethan on 2017/3/26.
 */

public class ClubItemViewModel {

    @BindingAdapter({"clubItemBadgeUrl"})
    public static void loadImage(final ImageView view, String imageUrl) {
        Object target;
        if (imageUrl == null) {
            target = R.drawable.img_default_avatar;
        } else {
            target = ApiHelper.BASE_URL+ imageUrl;
        }
        Glide.with(view.getContext())
                .load(target)
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
                .bitmapTransform(new CropCircleTransformation(view.getContext()))
                //.skipMemoryCache(true)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view);
    }
}
