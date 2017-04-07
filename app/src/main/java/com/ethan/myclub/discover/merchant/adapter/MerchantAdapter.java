package com.ethan.myclub.discover.merchant.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ethan.myclub.R;
import com.ethan.myclub.discover.merchant.MerchantFragment;
import com.ethan.myclub.discover.merchant.model.MerchantResult;

import java.util.List;

/**
 * Created by ethan on 2017/4/6.
 */

public class MerchantAdapter extends BaseQuickAdapter<MerchantResult, BaseViewHolder> {

    private MerchantFragment mMerchantFragment;

    public MerchantAdapter(MerchantFragment merchantFragment, List<MerchantResult> data) {
        super(R.layout.item_discover_merchant, data);
        mMerchantFragment = merchantFragment;
    }

    @Override
    protected void convert(BaseViewHolder helper, MerchantResult item) {
        helper.setText(R.id.tv_merchant_name, item.name);
        helper.setText(R.id.tv_support_activity, "赞助活动：" + item.supportActivity);
        helper.setText(R.id.tv_support_type, "赞助方式：" + item.supportType);
        String imageUrl = item.logoUrl;
        Object target;
        if (imageUrl == null) {
            target = R.drawable.img_default_avatar;
        } else {
            target = imageUrl + "?imageView2/0/w/300/h/300";
        }
        Glide.with(mMerchantFragment)
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
                .into((ImageView) helper.getView(R.id.iv_merchant_logo));
    }

}