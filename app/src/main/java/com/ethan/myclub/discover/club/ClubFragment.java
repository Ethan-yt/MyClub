package com.ethan.myclub.discover.club;

import android.util.Log;
import android.view.View;

import com.ethan.myclub.discover.club.adapter.ClubAdapter;
import com.ethan.myclub.discover.club.model.ClubResult;
import com.ethan.myclub.discover.main.TabFragment;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.network.ApiHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class ClubFragment extends TabFragment {

    private static final String TAG = "Discover Club";

    public ClubFragment() {
        mAdapter = new ClubAdapter(this, null);
    }

    public void update(final int page, final int items) {
        ApiHelper.getProxyWithoutToken((BaseActivity) getActivity())
                .searchClub(mKeyWord, page, items)
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ClubResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //下拉刷新动画开始
                        if (page == 1)
                            mSwipeRefreshLayout.setRefreshing(true);
                    }

                    @Override
                    public void onNext(ClubResult clubResult) {
                        Log.i(TAG, "update: 获取ClubList完成");
                        //允许读取更多
                        mAdapter.setEnableLoadMore(true);
                        mCurrentPage++;
                        if (page == 1) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            if (clubResult.hits.hits == null || clubResult.hits.hits.size() == 0) {
                                mEmptyView.showEmptyView("还没有这个社团", "你可以创建这个社团哦！");
                                mAdapter.setNewData(null);
                                mRecyclerView.setLayoutFrozen(true);
                                mAdapter.setEmptyView(mEmptyView);
                            } else {
                                mRecyclerView.setLayoutFrozen(false);
                                mAdapter.setNewData(clubResult.hits.hits);
                            }
                        } else {
                            //允许下拉刷新
                            mSwipeRefreshLayout.setEnabled(true);
                            mAdapter.loadMoreComplete();
                            mAdapter.addData(clubResult.hits.hits);
                        }
                        if (clubResult.hits.hits.size() < 10) {
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

                    }
                });
    }

    @Override
    public Observable<List<String>> getSuggestionObservable(String query, BaseActivity activity) {
        return ApiHelper.getProxyWithoutToken(activity).getClubSuggestion(query);
    }
}
