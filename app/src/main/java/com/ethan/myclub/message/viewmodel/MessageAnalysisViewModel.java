package com.ethan.myclub.message.viewmodel;

import android.databinding.ObservableField;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.ethan.myclub.R;
import com.ethan.myclub.club.member.adapter.MemberAdapter;
import com.ethan.myclub.club.model.MemberResult;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.club.my.view.EmptyView;
import com.ethan.myclub.main.ToolbarWrapper;
import com.ethan.myclub.message.adapter.UnreadMemberAdapter;
import com.ethan.myclub.message.model.Message;
import com.ethan.myclub.message.view.MessageAnalysisActivity;
import com.ethan.myclub.databinding.ActivityMessageAnalysisBinding;
import com.ethan.myclub.network.ApiHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MessageAnalysisViewModel {

    private MessageAnalysisActivity mActivity;
    private ActivityMessageAnalysisBinding mBinding;

    private MyClub mMyClub;
    private Message mMessage;

    private final EmptyView mEmptyView;
    private UnreadMemberAdapter mAdapter;

    public ObservableField<String> mReadNum = new ObservableField<>();
    public ObservableField<String> mUnreadNum = new ObservableField<>();
    public ObservableField<String> mReadRate = new ObservableField<>();


    public MessageAnalysisViewModel(MessageAnalysisActivity activity, ActivityMessageAnalysisBinding binding, MyClub myClub, Message message) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);
        mMyClub = myClub;
        mMessage = message;
        new ToolbarWrapper.Builder(mActivity)
                .setTitle("消息阅读情况")
                .showBackIcon()
                .show();
        mEmptyView = new EmptyView(mActivity);

        mBinding.swipeLayout.setColorSchemeResources(R.color.colorAccent);
        mBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });
        mAdapter = new UnreadMemberAdapter(R.layout.item_club_member, null);
        mBinding.list.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.list.setAdapter(mAdapter);

        update();
    }

    private void update() {

        ApiHelper.getProxy(mActivity)
                .getClubMessageReadList(String.valueOf(mMyClub.clubId), String.valueOf(mMessage.contentId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Message>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mBinding.swipeLayout.setRefreshing(true);
                    }

                    @Override
                    public void onNext(List<Message> messages) {
                        if (messages == null || messages.size() == 0) {
                            mEmptyView.showErrorView(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    update();
                                }
                            });
                            mAdapter.setNewData(null);
                            mBinding.list.setLayoutFrozen(true);
                            mAdapter.setEmptyView(mEmptyView);
                        } else {
                            for (Message memberResult : messages) {
                                memberResult.image += "?imageView2/0/w/300/h/300";
                            }
                            mBinding.list.setLayoutFrozen(false);

                            List<Message> readList = new ArrayList<Message>();
                            List<Message> unReadList = new ArrayList<Message>();

                            for (Message message : messages) {
                                if (message.isChecked)
                                    readList.add(message);
                                else
                                    unReadList.add(message);
                            }
                            int read = readList.size();
                            int unread = unReadList.size();
                            mReadNum.set(String.valueOf(read));
                            mUnreadNum.set(String.valueOf(unread));
                            int rate = (int) ((read / (float) (read + unread)) * 100);
                            mReadRate.set(rate + "%");
                            if(unread == 0)
                            {
                                mEmptyView.showEmptyView("太棒了！","所有成员都收到了公告！");
                                mAdapter.setNewData(null);
                                mBinding.list.setLayoutFrozen(true);
                                mAdapter.setEmptyView(mEmptyView);
                            }
                            else
                            {
                                mAdapter.setNewData(unReadList);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mBinding.swipeLayout.setRefreshing(false);
                        mEmptyView.showErrorView(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                update();
                            }
                        });
                        mAdapter.setNewData(null);
                        mBinding.list.setLayoutFrozen(true);
                        mAdapter.setEmptyView(mEmptyView);
                        mActivity.showSnackbar("错误：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mBinding.swipeLayout.setRefreshing(false);
                    }
                });
    }


}