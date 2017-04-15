package com.ethan.myclub.club.notification.viewmodel;

import android.content.DialogInterface;
import android.databinding.ObservableField;
import android.support.v7.app.AlertDialog;

import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.club.notification.view.ClubNotificationCreateActivity;
import com.ethan.myclub.databinding.ActivityClubNotificationCreateBinding;
import com.ethan.myclub.main.ToolbarWrapper;
import com.ethan.myclub.message.view.MessageListActivity;
import com.ethan.myclub.network.ApiHelper;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.operators.observable.ObservableCache;

public class ClubNotificationCreateViewModel {

    private ClubNotificationCreateActivity mActivity;
    private ActivityClubNotificationCreateBinding mBinding;

    private MyClub mMyClub;

    public ObservableField<String> mTitle = new ObservableField<>();
    public ObservableField<String> mContent = new ObservableField<>();


    public ClubNotificationCreateViewModel(ClubNotificationCreateActivity activity, ActivityClubNotificationCreateBinding binding, MyClub myClub) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);
        mMyClub = myClub;

        new ToolbarWrapper.Builder(mActivity)
                .setTitle("发布通知")
                .showBackIcon()
                .show();

    }

    public void sendAttempt() {
        new AlertDialog.Builder(mActivity)
                .setTitle("提示")
                .setMessage("发送通知后所有人都可以收到，确定吗？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        send();
                    }
                })
                .setNeutralButton("否", null)
                .show();
    }

    private void send() {
        ApiHelper.getProxy(mActivity)
                .sendClubNotification(String.valueOf(mMyClub.clubId), mTitle.get(), mContent.get())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mActivity.showWaitingDialog("请稍候", "正在推送通知", d);
                    }

                    @Override
                    public void onNext(Object o) {
                        mActivity.showSnackbar("通知推送成功");
                        MessageListActivity.needRefreshFlag = true;
                        Observable.timer(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(@NonNull Long aLong) throws Exception {
                                        mActivity.finish();
                                    }
                                });
                    }

                    @Override
                    public void onError(Throwable e) {
                        mActivity.showSnackbar("错误：" + e.getMessage());
                        mActivity.dismissDialog();
                    }

                    @Override
                    public void onComplete() {
                        mActivity.dismissDialog();
                    }
                });

    }


}