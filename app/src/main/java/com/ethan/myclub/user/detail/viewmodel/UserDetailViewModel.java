package com.ethan.myclub.user.detail.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.android.databinding.library.baseAdapters.BR;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ethan.myclub.R;
import com.ethan.myclub.club.model.MemberResult;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.user.detail.view.UserDetailActivity;
import com.ethan.myclub.databinding.ActivityUserDetailBinding;
import com.ethan.myclub.user.model.Profile;
import com.ethan.myclub.util.Utils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class UserDetailViewModel {

    private UserDetailActivity mActivity;
    private ActivityUserDetailBinding mBinding;

    public ObservableField<Uri> mImageUri = new ObservableField<>();

    public ObservableField<Profile> mProfile = new ObservableField<>();

    private MemberResult mMemberResult;

    public UserDetailViewModel(UserDetailActivity activity, ActivityUserDetailBinding binding, MemberResult memberResult) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);
        mMemberResult = memberResult;

        if (!TextUtils.isEmpty(memberResult.avatar))
            mImageUri.set(Uri.parse(memberResult.avatar));

        mActivity.getToolbarWrapper()
                .setTitle("查看成员详情")
                .showBackIcon()
                .show();

        mBinding.swipeLayout.setColorSchemeResources(R.color.colorAccent);
        mBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });
        update();
    }

    private void update() {
        ApiHelper.getProxy(mActivity)
                .getUserProfile(String.valueOf(mMemberResult.userAccount))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Profile>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mBinding.swipeLayout.setRefreshing(true);
                    }

                    @Override
                    public void onNext(Profile profile) {
                        profile.sex = profile.sex.equals("0") ? "男" : "女";
                        mProfile.set(profile);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mBinding.swipeLayout.setRefreshing(false);
                        mActivity.showSnackbar("错误：" + e.getMessage());
                        Observable.timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(@NonNull Long aLong) throws Exception {
                                        mActivity.finish();
                                    }
                                });
                    }

                    @Override
                    public void onComplete() {
                        mBinding.swipeLayout.setRefreshing(false);
                    }
                });
    }

}