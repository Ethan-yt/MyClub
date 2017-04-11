package com.ethan.myclub.discover.activity.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ethan.myclub.R;
import com.ethan.myclub.discover.activity.ActivityFragment;
import com.ethan.myclub.discover.activity.model.ActivityResult;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.util.ImageUtils;
import com.ethan.myclub.util.Utils;

import java.util.List;

/**
 * Created by ethan on 2017/4/7.
 */

public class ActivityAdapter extends BaseMultiItemQuickAdapter<ActivityResult, BaseViewHolder> {
    public ActivityAdapter(List<ActivityResult> data) {
        super(data);
        addItemType(1, R.layout.item_discover_activity_special);
        addItemType(0, R.layout.item_discover_activity_normal);

    }

    @Override
    protected void convert(BaseViewHolder helper, ActivityResult item) {
        helper.setText(R.id.tv_activity_name, item.name);
        helper.setText(R.id.tv_detail, item.briefIntroduction);
        helper.setText(R.id.tv_time, Utils.getStandardDate(item.publishTime));
        helper.setText(R.id.tv_like_num, String.valueOf(item.likeNum));
        String imageUrl = item.homePageImg;

        ImageUtils.loadImageUrlRect((ImageView) helper.getView(R.id.iv_activity_logo), imageUrl);
    }
}
