package com.ethan.myclub.activity.detail.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ethan.myclub.R;
import com.ethan.myclub.activity.detail.model.ActivityContent;

import java.util.List;

/**
 * Created by ethan on 2017/4/7.
 */

public class ActivityContentAdapter extends BaseQuickAdapter<ActivityContent, BaseViewHolder> {

    private Context mContext;

    public ActivityContentAdapter(Context context , List<ActivityContent> data) {
        super(R.layout.item_activity_content, data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, ActivityContent item) {
        helper.setText(R.id.tv_text,item.text);
        Glide.with(mContext)
                .load(item.url+ "?imageView2/0/w/500/h/500")
                .crossFade()
                .into((ImageView) helper.getView(R.id.iv_image));

    }


}

