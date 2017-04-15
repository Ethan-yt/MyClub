package com.ethan.myclub.message.viewmodel;

import android.databinding.ObservableBoolean;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethan.myclub.R;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.club.my.view.EmptyView;
import com.ethan.myclub.club.notification.view.ClubNotificationCreateActivity;
import com.ethan.myclub.databinding.ActivityMessageListBinding;
import com.ethan.myclub.main.MainActivity;
import com.ethan.myclub.main.ToolbarWrapper;
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

    final private int mMode;

    private final EmptyView mEmptyView;
    final private MessageAdapter mAdapter;

    private boolean mAnalysisMode = false;
    private boolean mDeleteMode = false;

    public ObservableBoolean mHasPermission = new ObservableBoolean(false);

    public MessageListViewModel(MessageListActivity activity, ActivityMessageListBinding binding, @Nullable MyClub myClub, int mode) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);
        mMyClub = myClub;
        mMode = mode;
        String title;
        switch (mode) {
            case 1:
                title = "社团通知";
                if (mMyClub != null && mMyClub.checkPermission(2))
                    mHasPermission.set(true);
                break;
            case 2:
                title = "招新管理";
                break;
            default:
                title = "我的消息";
                break;
        }

        new ToolbarWrapper.Builder(mActivity)
                .setTitle(title)
                .showBackIcon()
                .show();

        mBinding.swipeLayout.setColorSchemeResources(R.color.colorAccent);
        mBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });

        mAdapter = new MessageAdapter(null, mMyClub, mActivity, this);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);

        mBinding.list.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.list.setAdapter(mAdapter);
        mEmptyView = new EmptyView(mActivity);
        update();
        MainActivity.needUpdateFlag.userUnreadCount = true;

        mBinding.fabMenu.setClosedOnTouchOutside(true);
    }

    public void update() {
        Observable<List<Message>> observable;
        switch (mMode) {
            case 1:
                observable = ApiHelper.getProxyWithoutToken(mActivity)
                        .getAllClubMessage(String.valueOf(mMyClub.clubId));
                break;
            case 2:
                observable = ApiHelper.getProxyWithoutToken(mActivity)
                        .getEnrollMessage(String.valueOf(mMyClub.clubId));
                break;
            default:
                observable = ApiHelper.getProxyWithoutToken(mActivity)
                        .getMyMessage();
                break;
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
                        mAnalysisMode = false;
                        if (messages == null || messages.size() == 0) {
                            String title;
                            String content;
                            switch (mMode) {
                                case 1:
                                    title = "没有通知";
                                    content = "当前社团还未发布任何通知";
                                    break;
                                case 2:
                                    title = "没有请求";
                                    content = "暂时还没有加入请求";
                                    break;
                                default:
                                    title = "没有消息";
                                    content = "您还没有接受到任何消息";
                                    break;
                            }
                            mEmptyView.showEmptyView(title, content);
                            mAdapter.setNewData(null);
                            mBinding.list.setLayoutFrozen(true);
                            mAdapter.setEmptyView(mEmptyView);
                        } else {
                            for (Message message : messages) {
                                message.image += "?imageView2/0/w/300/h/300";
                                message.standardTime = Utils.getDateCountdown(message.createdDate);
                                message.senderStandardName = "发布者：" + (TextUtils.isEmpty(message.senderName) ? message.senderNickname : message.senderName);
                            }
                            mBinding.list.setLayoutFrozen(false);
                            mAdapter.setNewData(messages);
                        }
                        MessageListActivity.needRefreshFlag = false;
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

    public void create() {
        mBinding.fabMenu.close(false);
        ClubNotificationCreateActivity.start(mActivity, mMyClub);
    }

    public void analysis() {
        mBinding.fabMenu.close(true);
        mDeleteMode = false;
        mAnalysisMode = !mAnalysisMode;

        if (mAnalysisMode) {
            mActivity.showSnackbar("请选择您要分析的通知");
            mActivity.getToolbarWrapper().changeColor(Color.YELLOW);
        } else {
            mActivity.getToolbarWrapper().changeColor(Color.WHITE);
        }
        mAdapter.mAnalysisMode = mAnalysisMode;
        mAdapter.mDeleteMode = mDeleteMode;
    }

    public void deleteMode() {
        mBinding.fabMenu.close(true);
        mAnalysisMode = false;
        mDeleteMode = !mDeleteMode;

        if (mDeleteMode) {
            mActivity.showSnackbar("请选择您要删除的通知");
            mActivity.getToolbarWrapper().changeColor(Color.RED);
        } else {
            mActivity.getToolbarWrapper().changeColor(Color.WHITE);
        }
        mAdapter.mAnalysisMode = mAnalysisMode;
        mAdapter.mDeleteMode = mDeleteMode;
    }
}