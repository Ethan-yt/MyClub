package com.ethan.myclub.user.main.viewmodel;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;

import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.main.SnackbarActivity;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.user.info.InfoActivity;
import com.ethan.myclub.user.login.view.LoginActivity;
import com.ethan.myclub.user.main.view.UserFragment;
import com.ethan.myclub.user.schedule.ScheduleActivity;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by ethan on 2017/3/20.
 */

public class UserViewModel {

    private UserFragment mFragment;

    public UserViewModel(UserFragment fragment) {
        mFragment = fragment;
    }

    public void timeManagement() {
        Intent intent = new Intent(mFragment.getActivity(), ScheduleActivity.class);
        mFragment.startActivity(intent);
    }

    public void login() {
        Intent intent = new Intent(mFragment.getActivity(), LoginActivity.class);
        mFragment.getActivity().startActivityForResult(intent, SnackbarActivity.REQUEST_LOGIN);
    }

    public void info() {
        Intent intent = new Intent(mFragment.getActivity(), InfoActivity.class);

        @SuppressWarnings("unchecked")
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(mFragment.getActivity(),
                        Pair.create((View) mFragment.mBinding.ivAvatar, "trans_iv_avatar"));
        ActivityCompat.startActivity(mFragment.getActivity(), intent, options.toBundle());
    }

    public void settings() {
        ApiHelper.getProxy((SnackbarActivity) mFragment.getActivity())
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
    }

    public void updateUserInfomation()
    {

    }
}

