package com.ethan.myclub.club.detail.viewmodel;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ethan.myclub.R;
import com.ethan.myclub.club.edit.view.ClubInfoEditActivity;
import com.ethan.myclub.club.model.Club;
import com.ethan.myclub.club.model.Tag;
import com.ethan.myclub.club.detail.view.ClubInfoActivity;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.databinding.ActivityClubInfoBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.network.ApiHelper;
import com.google.android.flexbox.FlexboxLayout;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ClubInfoViewModel {


    private ClubInfoActivity mActivity;
    private ActivityClubInfoBinding mBinding;

    public ObservableField<Club> mClub = new ObservableField<>();

    public ClubInfoViewModel(ClubInfoActivity activity, ActivityClubInfoBinding binding, final MyClub myclub) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);

        BaseActivity.ToolbarWrapper toolbar = mActivity.getToolbarWrapper()
                .setTitle("社团简介", true)
                .setColor(Color.WHITE)
                .transparent()
                .showBackIcon()
                .target(mBinding.constraintLayout);
        if (myclub.checkPermission(1))
            toolbar.setMenu(R.menu.toolbar_edit_white, new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    ClubInfoEditActivity.startForResult(mActivity, mClub.get(), ClubInfoActivity.REQUEST_EDIT_CLUB_INFO);
                    return false;
                }
            });
        toolbar.show();

        mBinding.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update(myclub.clubId);
            }
        });
        update(myclub.clubId);
    }

    public void update(int id) {
        ApiHelper.getProxyWithoutToken(mActivity)
                .getClub(String.valueOf(id))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Club>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mBinding.swipeRefreshLayout.setRefreshing(true);
                    }

                    @Override
                    public void onNext(Club club) {
                        mClub .set(club);
                        mBinding.flTags.removeAllViews();
                        for (Tag tag : club.tag) {
                            TextView tv = new TextView(mActivity);
                            tv.setText(tag.tagName);
                            tv.setBackgroundResource(R.drawable.bg_tag);
                            mBinding.flTags.addView(tv);
                            FlexboxLayout.LayoutParams lp = (FlexboxLayout.LayoutParams) tv.getLayoutParams();
                            lp.setMargins(10, 10, 10, 10);
                            lp.order = tag.tagName.length();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mBinding.swipeRefreshLayout.setRefreshing(false);
                        mActivity.showSnackbar("错误：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mBinding.swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    @BindingAdapter({"clubInfoBadge"})
    public static void loadImage(final ImageView view, String imageUrl) {
        Object target;
        if (imageUrl == null) {
            target = R.drawable.img_default_avatar;
        } else {
            target = imageUrl + "?imageView2/0/w/300/h/300";
        }
        Glide.with(view.getContext())
                .load(target)
                .crossFade()
                .bitmapTransform(new CropCircleTransformation(view.getContext()))
                .into(view);
    }
}