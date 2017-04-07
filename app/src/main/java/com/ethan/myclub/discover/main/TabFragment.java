package com.ethan.myclub.discover.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethan.myclub.R;
import com.ethan.myclub.club.main.view.EmptyView;
import com.ethan.myclub.main.BaseActivity;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ethan on 2017/4/4.
 */

public abstract class TabFragment extends Fragment {


    protected BaseQuickAdapter mAdapter;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView mRecyclerView;
    protected EmptyView mEmptyView;
    public String mKeyWord = "";
    protected int mCurrentPage = 1;
    protected boolean mIsNoMore = false;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover_tab, container, false);
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
                update(1, 10);
            }
        });
        //设置适配器

        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        //读取更多时
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (mIsNoMore)
                    return;
                //读取更多时禁止下拉刷新
                mSwipeRefreshLayout.setEnabled(false);
                update(mCurrentPage, 10);
            }
        }, mRecyclerView);
        //预加载
        mAdapter.setAutoLoadMoreSize(4);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mEmptyView = new EmptyView(getActivity());

        update(1, 10);
        return view;
    }


    abstract public void update(final int page, final int items);

    abstract public Observable<List<String>> getSuggestionObservable(String query, BaseActivity activity);


}
