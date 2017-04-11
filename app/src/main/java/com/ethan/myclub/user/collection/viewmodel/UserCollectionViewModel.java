package com.ethan.myclub.user.collection.viewmodel;

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
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.club.my.view.EmptyView;
import com.ethan.myclub.databinding.ActivityClubActivityListBinding;
import com.ethan.myclub.discover.activity.adapter.ActivityAdapter;
import com.ethan.myclub.discover.activity.model.ActivityResult;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.user.collection.view.UserCollectionActivity;
import com.ethan.myclub.databinding.ActivityUserCollectionBinding;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class UserCollectionViewModel {

    private UserCollectionActivity mActivity;
    private ActivityUserCollectionBinding mBinding;

    private final EmptyView mEmptyView;
    private ActivityAdapter mAdapter;

    public UserCollectionViewModel(UserCollectionActivity activity, ActivityUserCollectionBinding binding) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);
        mActivity.getToolbarWrapper()
                .setTitle("我的收藏")
                .showBackIcon()
                .show();

        mBinding.swipeLayout.setColorSchemeResources(R.color.colorAccent);
        mBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });
        mAdapter = new ActivityAdapter(null);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ActivityDetailActivity.startForResult(mActivity, (ActivityResult) adapter.getData().get(position), UserCollectionActivity.REQUEST_ACTIVITY_DETAIL);
            }
        });


        mBinding.list.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.list.setAdapter(mAdapter);
        mEmptyView = new EmptyView(mActivity);
        update();
    }

    public void update() {
        ApiHelper.getProxyWithoutToken(mActivity)
                .getMyCollection()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ActivityResult>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mBinding.swipeLayout.setRefreshing(true);
                    }

                    @Override
                    public void onNext(List<ActivityResult> activityResults) {

                        if (activityResults == null || activityResults.size() == 0) {
                            mEmptyView.showEmptyView("没有收藏", "快去给你喜欢的活动点赞吧！");
                            mAdapter.setNewData(null);
                            mBinding.list.setLayoutFrozen(true);
                            mAdapter.setEmptyView(mEmptyView);
                        } else {
                            for (ActivityResult activityResult : activityResults) {
                                activityResult.homePageImg += "?imageView2/0/w/500/h/500";
                            }
                            mBinding.list.setLayoutFrozen(false);
                            formatOrder(activityResults);
                            mAdapter.setNewData(activityResults);
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

    private void formatOrder(List<ActivityResult> activityList) {
        ActivityResult specialActivity = null;
        for (ActivityResult activity : activityList) {
            if (activity.isSpecial) {
                specialActivity = activity;
                activityList.remove(activity);
                break;
            }
        }
        if (specialActivity != null)
            activityList.add(0, specialActivity);
    }
}