package com.ethan.myclub.message.viewmodel;

import android.app.NotificationManager;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethan.myclub.R;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.club.my.view.EmptyView;
import com.ethan.myclub.club.notification.view.ClubNotificationCreateActivity;
import com.ethan.myclub.databinding.ActivityMessageListBinding;
import com.ethan.myclub.main.MainActivity;
import com.ethan.myclub.message.adapter.MessageAdapter;
import com.ethan.myclub.message.model.Message;
import com.ethan.myclub.message.view.MessageListActivity;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.util.Utils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MessageListViewModel {

    final private MessageListActivity mActivity;
    final private ActivityMessageListBinding mBinding;
    final private MyClub mMyClub;

    private final EmptyView mEmptyView;
    final private MessageAdapter mAdapter;

    public ObservableBoolean mHasPermission = new ObservableBoolean(false);

    public MessageListViewModel(MessageListActivity activity, ActivityMessageListBinding binding,@Nullable MyClub myClub) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);
        mMyClub = myClub;

        mActivity.getToolbarWrapper()
                .setTitle(mMyClub == null ? "我的消息" : "社团通知")
                .showBackIcon()
                .show();

        mBinding.swipeLayout.setColorSchemeResources(R.color.colorAccent);
        mBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });

        mAdapter = new MessageAdapter(null, mMyClub, mActivity,this);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);

        mBinding.list.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.list.setAdapter(mAdapter);
        mEmptyView = new EmptyView(mActivity);
        update();
        MainActivity.needUpdateFlag.userUnreadCount = true;

        NotificationManager nm =(NotificationManager)mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();

        if (mMyClub!= null && mMyClub.checkPermission(6))
            mHasPermission.set(true);
    }

    public void update() {
        Observable<List<Message>> observable;
        if (mMyClub == null) {
            observable = ApiHelper.getProxyWithoutToken(mActivity)
                    .getMyMessage();
        } else {
            observable = ApiHelper.getProxyWithoutToken(mActivity)
                    .getAllClubMessage(String.valueOf(mMyClub.clubId));
        }

        observable
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        mBinding.swipeLayout.setRefreshing(true);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread()) // 指定主线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Message>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Message> messages) {
                        if (messages == null || messages.size() == 0) {
                            mEmptyView.showEmptyView(mMyClub == null ? "没有消息" : "没有通知", mMyClub == null ? "您还没有接受到任何消息" : "当前社团还未发布任何通知");
                            mAdapter.setNewData(null);
                            mBinding.list.setLayoutFrozen(true);
                            mAdapter.setEmptyView(mEmptyView);
                        } else {
                            for (Message message : messages) {
                                message.image += "?imageView2/0/w/300/h/300";
                                message.standardTime = Utils.getStandardDate(message.createdDate);
                                message.senderStandardName = "发布者：" + (TextUtils.isEmpty(message.senderName) ? message.senderNickname : message.senderName);
                            }
                            mBinding.list.setLayoutFrozen(false);
                            mAdapter.setNewData(messages);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        mBinding.swipeLayout.setRefreshing(false);
                        mActivity.showSnackbar("错误：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mBinding.swipeLayout.setRefreshing(false);
                    }
                });
    }

    public void create()
    {
        ClubNotificationCreateActivity.start(mActivity,mMyClub);
    }
}