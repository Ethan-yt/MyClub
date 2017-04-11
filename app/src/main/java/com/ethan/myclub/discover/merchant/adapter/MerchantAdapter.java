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
import com.ethan.myclub.util.ImageUtils;

import java.util.List;

/**
 * Created by ethan on 2017/4/6.
 */

public class MerchantAdapter extends BaseQuickAdapter<MerchantResult, BaseViewHolder> {

    public MerchantAdapter(List<MerchantResult> data) {
        super(R.layout.item_discover_merchant, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MerchantResult item) {
        helper.setText(R.id.tv_merchant_name, item.name);
        helper.setText(R.id.tv_support_activity, "赞助活动：" + item.supportActivity);
        helper.setText(R.id.tv_support_type, "赞助方式：" + item.supportType);
        String star = "";
        for (int i = 0; i < item.starLevel; i++)
            star += "\u2b50";
        helper.setText(R.id.tv_star, star);

        ImageUtils.loadImageUrlRect((ImageView) helper.getView(R.id.iv_merchant_logo),item.logoUrl);
    }

}