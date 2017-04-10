package com.ethan.myclub.user.message.viewmodel;

import android.support.v4.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethan.myclub.R;
import com.ethan.myclub.club.member.adapter.MemberAdapter;
import com.ethan.myclub.club.my.view.EmptyView;
import com.ethan.myclub.user.message.view.UserMessageActivity;
import com.ethan.myclub.databinding.ActivityUserMessageBinding;
import com.ethan.myclub.user.model.MessageFeedBack;

import java.util.List;

public class UserMessageViewModel {

    private UserMessageActivity mActivity;
    private ActivityUserMessageBinding mBinding;

    private List<MessageFeedBack> mMessageFeedBacks;

    private final EmptyView mEmptyView;
    private MemberAdapter mAdapter;


    public UserMessageViewModel(UserMessageActivity activity, ActivityUserMessageBinding binding, List<MessageFeedBack> messageFeedBacks) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);
        mMessageFeedBacks = messageFeedBacks;

        mActivity.getToolbarWrapper()
                .setTitle("社团通讯录")
                .showBackIcon()
                .show();
        mEmptyView = new EmptyView(mActivity);

        mBinding.swipeLayout.setColorSchemeResources(R.color.colorAccent);
        mBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //update();
            }
        });

        //mAdapter = new MessageAdapter(R.layout.item_user_message, messageFeedBacks);
       // mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);

    }
}