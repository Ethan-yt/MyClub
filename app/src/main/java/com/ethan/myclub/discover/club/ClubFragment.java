package com.ethan.myclub.discover.club;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethan.myclub.R;
import com.ethan.myclub.club.main.view.EmptyView;
import com.ethan.myclub.discover.club.adapter.ClubAdapter;
import com.ethan.myclub.discover.club.model.ClubResult;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.network.ApiHelper;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class ClubFragment extends Fragment {


    private static final String TAG = "Discover Club";
    private ClubAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private EmptyView mEmptyView;
    public String mKeyWord = "";
    private int mCurrentPage = 1;
    private boolean mIsNoMore = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover_club, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);
        //设置下拉刷新
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新时禁止读取更多
                mAdapter.setEnableLoadMore(false);
                mIsNoMore = false;
                mCurrentPage = 1;
                updateClubList(1, 10);
            }
        });
        //设置适配器
        mAdapter = new ClubAdapter(this, null);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        //读取更多时
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (mIsNoMore)
                    return;
                //读取更多时禁止下拉刷新
                mSwipeRefreshLayout.setEnabled(false);
                updateClubList(mCurrentPage, 10);
            }
        }, mRecyclerView);
        //预加载
        mAdapter.setAutoLoadMoreSize(4);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setAdapter(mAdapter);
        mEmptyView = new EmptyView(getActivity());

        updateClubList(1, 10);
        return view;
    }

    public void updateClubList(final int page, final int items) {
        ApiHelper.getProxyWithoutToken((BaseActivity) getActivity())
                .searchClub(mKeyWord, page, items)
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ClubResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //下拉刷新动画开始
                        mSwipeRefreshLayout.setRefreshing(true);
                    }

                    @Override
                    public void onNext(ClubResult clubResult) {
                        Log.i(TAG, "updateClubList: 获取ClubList完成");
                        //允许读取更多
                        mAdapter.setEnableLoadMore(true);
                        mCurrentPage++;
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (page == 1) {
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
                        mSwipeRefreshLayout.setRefreshing(false);
                        e.printStackTrace();
                        Log.i(TAG, "updateClubList: 获取ClubList失败");

                        if (page == 1) {
                            mEmptyView.showErrorView(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    updateClubList(1, 10);
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
}
