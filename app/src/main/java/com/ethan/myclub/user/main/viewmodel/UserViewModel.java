package com.ethan.myclub.user.main.viewmodel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;

import com.ethan.myclub.databinding.FragmentUserBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.main.MainActivity;
import com.ethan.myclub.main.MyApplication;
import com.ethan.myclub.message.model.UnreadNumber;
import com.ethan.myclub.message.view.MessageListActivity;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.network.OAuthHelper;
import com.ethan.myclub.user.collection.view.UserCollectionActivity;
import com.ethan.myclub.user.edit.view.ProfileEditActivity;
import com.ethan.myclub.user.main.view.UserFragment;
import com.ethan.myclub.user.model.Profile;
import com.ethan.myclub.schedule.view.ScheduleActivity;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.mipush.sdk.MiPushClient;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by ethan on 2017/3/20.
 */

public class UserViewModel {

    private static final String TAG = "UserViewModel";


    private UserFragment mFragment;

    private FragmentUserBinding mBinding;

    public ObservableField<Profile> mProfile = new ObservableField<>();
    public ObservableInt mUnreadNum = new ObservableInt(-1);

    public UserViewModel(UserFragment fragment, FragmentUserBinding binding) {
        mFragment = fragment;
        mBinding = binding;
        mBinding.setViewModel(this);
    }

    public void timeManagement() {
        if (MyApplication.isLogin()) {
            ScheduleActivity.startActivity(mFragment.getActivity(), null);
        } else
            mFragment.mMainActivity.showLoginSnackbar("您还没有登录！");
    }

    public void info() {
        if (MyApplication.isLogin()) {
            @SuppressWarnings("unchecked")
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(mFragment.getActivity(),
                            Pair.create((View) mBinding.ivAvatar, "trans_iv_avatar"));
            ProfileEditActivity.start(mFragment.getActivity(), options.toBundle());
        } else
            mFragment.mMainActivity.showLoginSnackbar("您还没有登录！");


    }

    public void settings() {
        new AlertDialog.Builder(mFragment.mMainActivity)
                .setMessage("确定要退出吗")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                })
                .setNegativeButton("点错了", null)
                .show();

    }

    private void logout() {

        OAuthHelper.getProxy(mFragment.mMainActivity)
                .revokeToken(MyApplication.getToken().mAccessToken)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object object) throws Exception {

                    }
                });
        MyApplication.setToken(mFragment.mMainActivity, null);
        MainActivity.startActivity(mFragment.getActivity(), BaseActivity.REQUEST_LOGOUT, Activity.RESULT_OK);
    }

    public void collection() {
        UserCollectionActivity.start(mFragment.mMainActivity);
    }

    public void updateUserUnreadNumberAttempt() {
        //获取用户消息数量
        if (MainActivity.needUpdateFlag.userUnreadCount || mUnreadNum.get() == -1) {
            Log.i(TAG, "updateUserProfileAttempt: 更新mUnreadNum");
            updateUserUnreadNumber();
        }
    }

    public void updateUserProfileAttempt() {
        //获取用户基本资料
        if (MainActivity.needUpdateFlag.userProfile || MyApplication.sProfile == null) {
            Log.i(TAG, "updateUserProfileAttempt: 更新userProfile");
            updateUserProfile();
        }
    }

    public void updateUserUnreadNumber() {
        ApiHelper.getProxy((BaseActivity) mFragment.getActivity())
                .getUnreadNumber()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UnreadNumber>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UnreadNumber unreadNumber) {
                        Log.i(TAG, "updateUserUnreadNumber: 更新未读消息数完成");
                        mFragment.mMainActivity.bottomNavigation.setNotification(unreadNumber.unreadNumber == 0 ? "" : String.valueOf(unreadNumber.unreadNumber), 2);
                        MainActivity.needUpdateFlag.userUnreadCount = false;
                        mUnreadNum.set(unreadNumber.unreadNumber);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "updateUserUnreadNumber: 更新未读消息数失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void updateUserProfile() {
        ApiHelper.getProxy((BaseActivity) mFragment.getActivity())
                .getMyProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Profile>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Profile profile) {
                        Log.i(TAG, "updateUserProfile: 更新UserInfo完成");
                        profile.avatar += "?imageView2/0/w/300/h/300";
                        if (!String.valueOf(profile.userId).equals(MyApplication.getToken().uid))
                            MiPushClient.setUserAccount(mFragment.mMainActivity, String.valueOf(profile.userId), null);
                        if (profile.sex.equals("0"))
                            profile.sex = "男";
                        else
                            profile.sex = "女";
                        mProfile.set(profile);

                        String id = mProfile.get().userId + "_" + mProfile.get().username;
                        MobclickAgent.onProfileSignIn(id);
                        Log.i(TAG, "设置统计账号：" + id);
                        CrashReport.setUserId(id);
                        MainActivity.needUpdateFlag.userProfile = false;

                        MyApplication.sProfile = profile;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "updateUserProfile: 更新UserInfo失败");
                        mFragment.mMainActivity.showSnackbar("获取用户信息失败：" + e.getMessage(),
                                "重试",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        updateUserProfile();
                                    }
                                });
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void message() {
        MessageListActivity.start(mFragment.getActivity(), null);
    }


}