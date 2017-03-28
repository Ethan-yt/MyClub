package com.ethan.myclub.user.main.viewmodel;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ethan.myclub.R;
import com.ethan.myclub.databinding.FragmentUserBinding;
import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.main.BaseFragment;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.user.info.model.Profile;
import com.ethan.myclub.user.info.view.InfoActivity;
import com.ethan.myclub.user.login.view.LoginActivity;
import com.ethan.myclub.user.main.view.UserFragment;
import com.ethan.myclub.user.schedule.ScheduleActivity;
import com.ethan.myclub.util.CacheUtil;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by ethan on 2017/3/20.
 */

public class UserViewModel {

    private static final String TAG = "UserViewModel";
    public static final int REQUEST_EDIT_INFO = 20304;

    private UserFragment mFragment;

    private FragmentUserBinding mBinding;

    public UserViewModel(UserFragment fragment, FragmentUserBinding binding) {
        mFragment = fragment;
        mBinding = binding;
        mBinding.setViewModel(this);
        //new BaseFragment.ToolbarWrapper(mFragment,"个人中心").show();
    }

    public void timeManagement() {
        Intent intent = new Intent(mFragment.getActivity(), ScheduleActivity.class);
        mFragment.startActivity(intent);
    }

    public void login() {
        Intent intent = new Intent(mFragment.getActivity(), LoginActivity.class);
        mFragment.getActivity().startActivityForResult(intent, BaseActivity.REQUEST_LOGIN);
    }

    public void info() {
        if (Preferences.sIsLogin.get()) {
            Intent intent = new Intent(mFragment.getActivity(), InfoActivity.class);

            intent.putExtra("ImageUrl", mBinding.getProfile().avatarThumbnailUrl);

            @SuppressWarnings("unchecked")
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(mFragment.getActivity(),
                            Pair.create((View) mBinding.ivAvatar, "trans_iv_avatar"));
            ActivityCompat.startActivityForResult(mFragment.getActivity(), intent, REQUEST_EDIT_INFO, options.toBundle());
        } else
            ((BaseActivity) mFragment.getActivity()).showLoginSnackbar("您还没有登录！");


    }

    public void settings() {
        ApiHelper.getProxy((BaseActivity) mFragment.getActivity())
                .test()
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        Log.e("成功", "accept: " + o);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("失败", "accept: ", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.e("完成", "accept: ");
                    }
                });
    }

    public void collection() {
        Preferences.setToken(mFragment.getActivity(), null);
        getUserInfoCache();
    }

    public void getUserInfoCache() {

        Object infoObj = CacheUtil.get(mFragment.getActivity()).getAsObject(Preferences.CACHE_USER_INFO);
        if (infoObj == null || !(infoObj instanceof Profile)) {
            Log.i(TAG, "getUserInfoCache: 读取UserInfo缓存失败，强制获取更新");
            updateUserInfo();
        } else {
            notifyInfoObservable((Profile) infoObj);
            Log.i(TAG, "getUserInfoCache: 读取UserInfo缓存成功");
        }

    }

    private void notifyInfoObservable(Profile profile) {

        mBinding.setProfile(profile);

    }

    public void updateUserInfo() {
        if (!Preferences.sIsLogin.get()) {
            Log.i(TAG, "updateUserInfo: 无法获取更新，用户没有登录");
            notifyInfoObservable(null);
            return;
        }
        ApiHelper.getProxy((BaseActivity) mFragment.getActivity())
                .getAccountProfile()
                .subscribe(new Observer<Profile>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Profile profile) {
                        Log.i(TAG, "updateUserInfo: 获取UserInfo完成");
                        Log.e(TAG, "onNext: " + profile.avatarThumbnailUrl);
                        Log.e(TAG, "onNext: " + profile.avatarUrl);
                        notifyInfoObservable(profile);
                        CacheUtil.get(mFragment.getActivity())
                                .put(Preferences.CACHE_USER_INFO, profile, Preferences.CACHE_TIME_USER_INFO);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "updateUserInfo: 获取UserInfo失败");
                        ((BaseActivity) mFragment.getActivity()).showSnackbar("获取用户信息失败：" + e.getMessage(),
                                "重试",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        updateUserInfo();
                                    }
                                });
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(final ImageView view, String imageUrl) {
        Object target;
        if (TextUtils.isEmpty(imageUrl)) {
            target = R.drawable.img_default_avatar;
        } else {
            target = ApiHelper.BASE_URL + imageUrl;
        }
        Glide.with(view.getContext())
                .load(target)
                .crossFade()
                .bitmapTransform(new CropCircleTransformation(view.getContext()))
                .into(view);
    }

    public void message()
    {

    }
}