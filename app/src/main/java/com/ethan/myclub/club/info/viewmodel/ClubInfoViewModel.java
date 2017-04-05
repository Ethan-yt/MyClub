package com.ethan.myclub.club.info.viewmodel;

import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ethan.myclub.R;
import com.ethan.myclub.club.info.model.Club;
import com.ethan.myclub.club.info.view.ClubInfoActivity;
import com.ethan.myclub.databinding.ActivityClubInfoBinding;
import com.ethan.myclub.network.ApiHelper;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ClubInfoViewModel {

    private ClubInfoActivity mActivity;
    private ActivityClubInfoBinding mBinding;

    public String mClubId;

    public ClubInfoViewModel(ClubInfoActivity activity, ActivityClubInfoBinding binding, String clubId) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);

        mActivity.getToolbarWrapper()
                .setTitle("社团简介", true)
                .setColor(Color.WHITE)
                .transparent()
                .showBackIcon()
                .target(mBinding.constraintLayout)
                .show();

        mClubId = clubId;
        mBinding.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });
        update();
    }

    private void update() {
        ApiHelper.getProxyWithoutToken(mActivity)
                .getClub(mClubId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Club>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mBinding.swipeRefreshLayout.setRefreshing(true);
                    }

                    @Override
                    public void onNext(Club club) {
                        mBinding.setClub(club);

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