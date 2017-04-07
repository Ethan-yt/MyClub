package com.ethan.myclub.discover.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethan.myclub.activity.detail.view.ActivityDetailActivity;
import com.ethan.myclub.discover.activity.adapter.ActivityAdapter;
import com.ethan.myclub.discover.activity.model.ActivityResult;
import com.ethan.myclub.discover.main.TabFragment;
import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.network.ApiHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class ActivityFragment extends TabFragment {

    private static final String TAG = "Discover ActivityResult";

    public ActivityFragment(){
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new ActivityAdapter(this, null);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(!Preferences.sIsLogin.get())
                    ((BaseActivity) getActivity()).showLoginSnackbar("先登录才能查看活动哦");
                else
                    ActivityDetailActivity.start(getActivity(), (ActivityResult) adapter.getData().get(position));
            }
        });
    }

    @Override
    public void update(final int page, int items) {
        ApiHelper.getProxyWithoutToken((BaseActivity) getActivity())
                .searchActivity(mKeyWord, page, items)
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ActivityResult>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //下拉刷新动画开始
                        if (page == 1)
                            mSwipeRefreshLayout.setRefreshing(true);
                    }

                    @Override
                    public void onNext(List<ActivityResult> activityList) {
                        Log.i(TAG, "update: 获取Activity完成");
                        //允许读取更多
                        mAdapter.setEnableLoadMore(true);
                        mCurrentPage++;
                        if (page == 1) {
                            if (activityList == null || activityList.size() == 0) {
                                mEmptyView.showEmptyView("还没有这个活动", "请换一个关键字试试！");
                                mAdapter.setNewData(null);
                                mRecyclerView.setLayoutFrozen(true);
                                mAdapter.setEmptyView(mEmptyView);
                            } else {
                                mRecyclerView.setLayoutFrozen(false);
                                mAdapter.setNewData(activityList);
                                formatOrder(activityList);
                            }
                        } else {
                            //允许下拉刷新
                            mSwipeRefreshLayout.setEnabled(true);
                            mAdapter.loadMoreComplete();
                            mAdapter.addData(activityList);
                        }
                        if (activityList.size() < 10) {
                            mIsNoMore = true;
                            mAdapter.loadMoreEnd();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        //允许读取更多
                        mAdapter.setEnableLoadMore(true);
                        e.printStackTrace();
                        Log.i(TAG, "update: 获取ClubList失败");
                        if (page == 1) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mEmptyView.showErrorView(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    update(1, 10);
                                }
                            });
                            mAdapter.setNewData(null);
                            mRecyclerView.setLayoutFrozen(true);
                            mAdapter.setEmptyView(mEmptyView);
                        } else {
                            //允许下拉刷新
                            mSwipeRefreshLayout.setEnabled(true);
                            mAdapter.loadMoreFail();
                        }


                    }

                    @Override
                    public void onComplete() {
                        if (page == 1)
                            mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void formatOrder(List<ActivityResult> activityList) {
        ActivityResult specialActivity = null;
        for (ActivityResult activity : activityList) {
            if(activity.isSpecial)
            {
                specialActivity = activity;
                activityList.remove(activity);
                break;
            }
        }
        if(specialActivity != null)
            activityList.add(0,specialActivity);
    }

    @Override
    public Observable<List<String>> getSuggestionObservable(String query, BaseActivity activity) {
        return ApiHelper.getProxyWithoutToken(activity).getActivitySuggestion(query);
    }
}
