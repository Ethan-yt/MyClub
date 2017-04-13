package com.ethan.myclub.discover.club.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ethan.myclub.R;
import com.ethan.myclub.discover.club.ClubFragment;
import com.ethan.myclub.discover.club.model.Hit;
import com.ethan.myclub.util.ImageUtils;

import java.util.List;

public class ClubAdapter extends BaseQuickAdapter<Hit, BaseViewHolder> {

    public ClubAdapter(List<Hit> data) {
        super(R.layout.item_discover_club, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Hit item) {
        helper.setText(R.id.tv_club_name, item.source.clubName);
        helper.setText(R.id.tv_club_brief_introduce, item.source.brief);
        helper.setText(R.id.tv_memberNumber, item.source.population + "个成员");
        if (!TextUtils.isEmpty(item.source.tagStr))
            helper.setText(R.id.tv_tag, "标签：" + item.source.tagStr);
        String imageUrl = item.source.badge;

        ImageUtils.loadImageUrlRect((ImageView) helper.getView(R.id.iv_club_badge), imageUrl);
    }

}