package com.ethan.myclub.club.member.viewmodel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethan.myclub.R;
import com.ethan.myclub.activity.detail.view.ActivityDetailActivity;
import com.ethan.myclub.activity.edit.view.ActivityEditActivity;
import com.ethan.myclub.club.activitylist.view.ClubActivityListActivity;
import com.ethan.myclub.club.member.adapter.MemberAdapter;
import com.ethan.myclub.club.member.view.ClubMemberListActivity;
import com.ethan.myclub.club.model.MemberResult;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.club.my.view.EmptyView;
import com.ethan.myclub.databinding.ActivityClubMemberListBinding;
import com.ethan.myclub.discover.activity.adapter.ActivityAdapter;
import com.ethan.myclub.discover.activity.model.ActivityResult;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.user.detail.view.UserDetailActivity;
import com.ethan.myclub.user.detail.viewmodel.UserDetailViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class ClubMemberListViewModel {

    private ClubMemberListActivity mActivity;
    private ActivityClubMemberListBinding mBinding;
    private MyClub mMyClub;
    private final EmptyView mEmptyView;
    private MemberAdapter mAdapter;

    final public boolean mIsChooseMember;

    public ClubMemberListViewModel(ClubMemberListActivity activity, ActivityClubMemberListBinding binding, MyClub myClub, boolean isChooseMember) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);
        mMyClub = myClub;
        mIsChooseMember = isChooseMember;
        mActivity.getToolbarWrapper()
                .setTitle(mIsChooseMember ? "请选择社团成员" : "社团通讯录")
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

        if (mIsChooseMember) {
            mBinding.list.mIsSelectable = true;
            mAdapter = new MemberAdapter(R.layout.item_club_member_checkbox, null, mActivity, myClub, this);
        } else if (myClub.isCreator)
            mAdapter = new MemberAdapter(R.layout.item_club_member_creator, null, mActivity, myClub, this);
        else
            mAdapter = new MemberAdapter(R.layout.item_club_member, null, mActivity, myClub, this);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);

        mBinding.list.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.list.setAdapter(mAdapter);

        update();

    }

    public void update() {
        ApiHelper.getProxy(mActivity)
                .getClubMemberList(String.valueOf(mMyClub.clubId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<MemberResult>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mBinding.swipeLayout.setRefreshing(true);
                    }

                    @Override
                    public void onNext(List<MemberResult> memberResults) {
                        if (memberResults == null || memberResults.size() == 0) {
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
                            for (MemberResult memberResult : memberResults) {
                                memberResult.avatar += "?imageView2/0/w/300/h/300";
                            }
                            mBinding.list.setLayoutFrozen(false);
                            mAdapter.setNewData(memberResults);
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

    public void finishSelect() {
        Intent resultIntent = new Intent();
        List<String> memberList = new ArrayList<>();
        for (MemberResult memberResult : mAdapter.getData()) {
            if (memberResult.selected)
                memberList.add(memberResult.userAccount);
        }
        resultIntent.putExtra("MemberArray", memberList.toArray(new String[0]));
        mActivity.setResult(Activity.RESULT_OK, resultIntent);
        mActivity.finish();
    }
}