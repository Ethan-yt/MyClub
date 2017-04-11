package com.ethan.myclub.club.detail.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ethan.myclub.R;
import com.ethan.myclub.discover.activity.model.ActivityResult;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.util.ImageUtils;
import com.ethan.myclub.util.Utils;

import java.util.List;

/**
 * Created by ethan on 2017/4/8.
 */

public class ClubDetailActivityAdapter extends BaseQuickAdapter<ActivityResult, BaseViewHolder> {

    private BaseActivity mBaseActivity;


    public ClubDetailActivityAdapter(BaseActivity activity, List<ActivityResult> data) {
        super(R.layout.item_discover_activity_small, data);
        mBaseActivity = activity;
    }


    @Override
    protected void convert(BaseViewHolder helper, ActivityResult item) {
        helper.setText(R.id.tv_activity_name, item.name);
        ImageUtils.loadImageUrlRect((ImageView) helper.getView(R.id.iv_activity_logo), item.homePageImg);
    }
}
