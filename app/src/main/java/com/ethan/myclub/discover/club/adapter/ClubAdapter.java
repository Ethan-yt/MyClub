package com.ethan.myclub.discover.club.adapter;

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

import java.util.List;

public class ClubAdapter extends BaseQuickAdapter<Hit, BaseViewHolder> {

    private ClubFragment mClubFragment;

    public ClubAdapter(ClubFragment clubFragment, List<Hit> data) {
        super(R.layout.item_discover_club, data);
        mClubFragment = clubFragment;
    }

    @Override
    protected void convert(BaseViewHolder helper, Hit item) {
        helper.setText(R.id.tv_club_name, item.source.clubName);
        helper.setText(R.id.tv_club_brief_introduce, item.source.brief);
        helper.setText(R.id.tv_memberNumber, item.source.population + "个成员");
        String imageUrl = item.source.badge;
        Object target;
        if (imageUrl == null) {
            target = R.drawable.img_default_avatar;
        } else {
            target = imageUrl + "?imageView2/0/w/300/h/300";
        }
        Glide.with(mClubFragment)
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
                .into((ImageView) helper.getView(R.id.iv_club_badge));
    }

}