package com.ethan.myclub.user.main.viewmodel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.BindingAdapter;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.bumptech.glide.Glide;
import com.ethan.myclub.R;
import com.ethan.myclub.databinding.FragmentUserBinding;
import com.ethan.myclub.main.Preferences;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.main.MainActivity;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.user.collection.view.UserCollectionActivity;
import com.ethan.myclub.user.model.MessageFeedBack;
import com.ethan.myclub.user.model.Profile;
import com.ethan.myclub.user.edit.view.ProfileEditActivity;
import com.ethan.myclub.user.main.view.UserFragment;
import com.ethan.myclub.user.schedule.ScheduleActivity;
import com.ethan.myclub.util.CacheUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.ethan.myclub.main.Preferences.sPushRegID;

/**
 * Created by ethan on 2017/3/20.
 */

public class UserViewModel {

    private static final String TAG = "UserViewModel";


    private UserFragment mFragment;

    private FragmentUserBinding mBinding;

    public ObservableField<Profile> mProfile = new ObservableField<>();
    public ObservableField<List<MessageFeedBack>> mMsg = new ObservableField<>();
    public ObservableField<String> mUnreadNum = new ObservableField<>();


    public UserViewModel(UserFragment fragment, FragmentUserBinding binding) {
        mFragment = fragment;
        mBinding = binding;
        mBinding.setViewModel(this);
        mProfile.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (mProfile.get() != null && !TextUtils.isEmpty(mProfile.get().getNickname())) {
                    String id = mProfile.get().userId + "_" + mProfile.get().getName() + "_" + mProfile.get().getNickname() + "_" + sPushRegID;
                    MobclickAgent.onProfileSignIn(id);
                    Log.i(TAG, "设置统计账号：" + id);
                }
            }
        });
        mMsg.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                int count = 0;
                for (MessageFeedBack messageFeedBack : mMsg.get()) {
                    if (!messageFeedBack.checked)
                        count++;
                }
                mUnreadNum.set(String.valueOf(count));
                MainActivity mainActivity = (MainActivity) mFragment.getActivity();
                if (count != 0) {
                    mainActivity.bottomNavigation.setNotification(String.valueOf(count), 2);
                } else

                {
                    mainActivity.bottomNavigation.setNotification("", 2);
                }
            }
        });

        //new BaseFragment.ToolbarWrapper(mFragment,"个人中心").show();
    }

    public void timeManagement() {
        if (Preferences.sIsLogin.get()) {
            ScheduleActivity.startActivity(mFragment.getActivity(), null);
        } else
            mFragment.mBaseActivity.showLoginSnackbar("您还没有登录！");
    }

    public void info() {
        if (Preferences.sIsLogin.get()) {
            @SuppressWarnings("unchecked")
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(mFragment.getActivity(),
                            Pair.create((View) mBinding.ivAvatar, "trans_iv_avatar"));
            ProfileEditActivity.startActivityForResult(mFragment.getActivity(), mProfile.get(), options.toBundle(), MainActivity.REQUEST_EDIT_INFO);
        } else
            mFragment.mBaseActivity.showLoginSnackbar("您还没有登录！");


    }

    public void settings() {
        new AlertDialog.Builder(mFragment.mBaseActivity)
                .setMessage("确定要退出吗")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Preferences.setToken(mFragment.mBaseActivity, null);
                        MainActivity.startActivity(mFragment.getActivity(), MainActivity.REQUEST_EXIT, Activity.RESULT_OK);

                    }
                })
                .setNegativeButton("点错了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    public void collection() {
        UserCollectionActivity.start(mFragment.mBaseActivity);
    }

    public void getUserInfoCache() {
        if (!Preferences.sIsLogin.get())
            return;

        //获取用户基本资料
        Object infoObj = CacheUtil.get(mFragment.getActivity()).getAsObject(Preferences.CACHE_USER_INFO);
        if (infoObj == null || !(infoObj instanceof Profile)) {
            Log.i(TAG, "getUserInfoCache: 读取UserInfo缓存失败，强制获取更新");
            updateUserInfo();
        } else {
            if (mProfile.get() == null) {
                mProfile.set((Profile) infoObj);
                Log.i(TAG, "getUserInfoCache: 读取UserInfo缓存成功");
            }
        }

        //获取用户消息
        Object msgsObj = CacheUtil.get(mFragment.getActivity()).getAsObject(Preferences.CACHE_USER_MSG);
        if (msgsObj == null || !(msgsObj instanceof MessageFeedBack[])) {
            Log.i(TAG, "getUserInfoCache: 读取UserMsgList缓存失败，强制获取更新");
            updateUserMsg();
        } else {
            if (mMsg.get() == null) {
                mMsg.set(Arrays.asList((MessageFeedBack[]) msgsObj));
                Log.i(TAG, "getUserInfoCache: 读取UserMsgList缓存成功");
            }
        }
    }

    private void updateUserMsg() {
        ApiHelper.getProxy((BaseActivity) mFragment.getActivity())
                .getMyMessage()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<MessageFeedBack>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<MessageFeedBack> messageFeedBacks) {
                        Log.i(TAG, "updateUserInfo: 更新UserMsgList完成");
                        MessageFeedBack[] msgsArray = messageFeedBacks.toArray(new MessageFeedBack[0]);
                        CacheUtil.get(mFragment.getActivity())
                                .put(Preferences.CACHE_USER_MSG, msgsArray, Preferences.CACHE_TIME_USER_MSG);
                        mMsg.set(messageFeedBacks);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "updateUserInfo: 更新UserMsgList失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void updateUserInfo() {
        ApiHelper.getProxy((BaseActivity) mFragment.getActivity())
                .getMyProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Profile>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Profile profile) {
                        Log.i(TAG, "updateUserInfo: 更新UserInfo完成");
                        profile.avatar += "?imageView2/0/w/300/h/300";
                        if (profile.sex.equals("0"))
                            profile.sex = "男";
                        else
                            profile.sex = "女";
                        mProfile.set(profile);
                        CacheUtil.get(mFragment.getActivity())
                                .put(Preferences.CACHE_USER_INFO, profile, Preferences.CACHE_TIME_USER_INFO);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "updateUserInfo: 更新UserInfo失败");
                        mFragment.mBaseActivity.showSnackbar("获取用户信息失败：" + e.getMessage(),
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
                        if (!Preferences.sIsLogin.get()) {
                            Log.i(TAG, "updateUserInfo: 无法获取更新，用户没有登录");
                            mProfile.set(null);
                            return;
                        }
                    }
                });

    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(final ImageView view, String imageUrl) {
        Object target;
        if (TextUtils.isEmpty(imageUrl)) {
            target = R.drawable.img_default_avatar;
        } else {
            target = imageUrl;
        }
        Glide.with(view.getContext())
                .load(target)
                .crossFade()
                .bitmapTransform(new CropCircleTransformation(view.getContext()))
                .into(view);
    }

    public void message() {

    }
}