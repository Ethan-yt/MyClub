package com.ethan.myclub.club.activitylist.viewmodel;

import android.databinding.ObservableBoolean;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethan.myclub.R;
import com.ethan.myclub.activity.create.view.ActivityCreateActivity;
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

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class ClubActivityListViewModel {

    private final EmptyView mEmptyView;
    private final BaseActivity.ToolbarWrapper mToolbar;
    private ClubActivityListActivity mActivity;
    private ActivityClubActivityListBinding mBinding;
    private ActivityAdapter mAdapter;
    private boolean mEditMode = false;
    private MyClub mMyClub;

    public ObservableBoolean mHasPermission = new ObservableBoolean(false);

    public ClubActivityListViewModel(final ClubActivityListActivity activity, ActivityClubActivityListBinding binding, MyClub myClub) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);
        mMyClub = myClub;
        mToolbar = mActivity.getToolbarWrapper()
                .setTitle("社团活动列表")
                .showBackIcon();

        mToolbar.show();

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
                if (mEditMode) {
                    ActivityEditActivity.startForResult(mActivity, (ActivityResult) adapter.getData().get(position), ClubActivityListActivity.REQUEST_EDIT_ACTIVITY);
                } else {
                    ActivityDetailActivity.start(mActivity, (ActivityResult) adapter.getData().get(position));
                }
            }
        });


        mBinding.list.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.list.setAdapter(mAdapter);
        mEmptyView = new EmptyView(mActivity);

        mBinding.fabMenu.setClosedOnTouchOutside(true);

        if (mMyClub.checkPermission(6))
            mHasPermission.set(true);

        update();
    }

    public void update() {
        ApiHelper.getProxy(mActivity)
                .getClubActivity(String.valueOf(mMyClub.clubId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ActivityResult>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mBinding.swipeLayout.setRefreshing(true);
                    }

                    @Override
                    public void onNext(List<ActivityResult> activityResults) {

                        if (activityResults == null || activityResults.size() == 0) {
                            mEmptyView.showEmptyView("没有活动", "当前社团还未发布任何活动");
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

    public void create() {
        mBinding.fabMenu.close(true);
        ActivityCreateActivity.startForResult(mActivity, mMyClub);
    }

    public void edit() {
        mBinding.fabMenu.close(true);
        if (mEditMode) {
            mActivity.showSnackbar("退出编辑模式，您可以查看活动详情");
            mToolbar.changeColor(Color.WHITE);
        } else {
            mActivity.showSnackbar("进入编辑模式，请选择你想修改的活动");
            mToolbar.changeColor(Color.YELLOW);
        }
        mEditMode = !mEditMode;

    }
}