package com.ethan.myclub.club.my.viewmodel;

import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethan.myclub.club.my.adapter.ClubListAdapter;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.club.my.view.MyClubFragment;
import com.ethan.myclub.club.my.view.EmptyView;
import com.ethan.myclub.club.operation.view.ClubOperationActivity;
import com.ethan.myclub.databinding.FragmentClubBinding;
import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.network.exception.ApiException;
import com.ethan.myclub.util.CacheUtil;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by ethan on 2017/3/25.
 */

public class ClubViewModel {

    private static final String TAG = "ClubViewModel";
    private final EmptyView mEmptyView;

    private MyClubFragment mFragment;
    private FragmentClubBinding mBinding;

    private ClubListAdapter mAdapter;

    public ClubViewModel(MyClubFragment fragment, FragmentClubBinding binding) {
        mFragment = fragment;
        mBinding = binding;
        mBinding.setViewModel(this);

        mAdapter = new ClubListAdapter(null);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ClubOperationActivity.start(mFragment.getActivity(), (MyClub) adapter.getItem(position));
            }
        });

        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(mFragment.getContext(), 2));
        mBinding.recyclerView.setAdapter(mAdapter);

        mEmptyView = new EmptyView(mFragment.mBaseActivity);
    }

    private static final int GET_CLUBS_RESULT_OK = -1;
    private static final int GET_CLUBS_RESULT_NOT_LOGIN = 1;
    private static final int GET_CLUBS_RESULT_ERROR = 3;
    private static final int GET_CLUBS_RESULT_NO_NETWORK = 4;

    public void getUserClubListCache() {

        Object clubsObj = CacheUtil.get(mFragment.getActivity()).getAsObject(Preferences.CACHE_USER_CLUB_LIST);
        if (clubsObj == null || !(clubsObj instanceof MyClub[])) {
            Log.i(TAG, "getUserClubListCache: 读取UserClubList缓存失败，强制获取更新");
            updateUserClubList();
        } else {
            notifyClubsObservable((MyClub[]) clubsObj, GET_CLUBS_RESULT_OK);
            Log.i(TAG, "getUserClubListCache: 读取UserClubList缓存成功");
        }

    }


    private void notifyClubsObservable(MyClub[] clubsArray, int resultCode) {

        if (resultCode == GET_CLUBS_RESULT_OK) {
            if (clubsArray == null || clubsArray.length == 0) {
                mEmptyView.showEmptyView("还没有加入社团哦", "快去发现你喜欢的社团吧！");
                mAdapter.setNewData(null);
                mBinding.recyclerView.setLayoutFrozen(true);
                mAdapter.setEmptyView(mEmptyView);
            }
            else
            {
                mBinding.recyclerView.setLayoutFrozen(false);
                List<MyClub> dataList = Arrays.asList(clubsArray);
                mAdapter.setNewData(dataList);
            }
        } else {
            CacheUtil.get(mFragment.getContext()).remove(Preferences.CACHE_USER_CLUB_LIST);//登录或者注册成功，清除缓存
            switch (resultCode) {
                case GET_CLUBS_RESULT_NOT_LOGIN:
                    mEmptyView.showNotLoginView();
                    break;
                case GET_CLUBS_RESULT_ERROR:
                    mEmptyView.showErrorView(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateUserClubList();
                        }
                    });
                    break;
                case GET_CLUBS_RESULT_NO_NETWORK:
                    mEmptyView.showNoNetWorkError();
                    break;
            }
            mAdapter.setNewData(null);
            mBinding.recyclerView.setLayoutFrozen(true);
            mAdapter.setEmptyView(mEmptyView);
        }
    }

    public void updateUserClubList() {
        ApiHelper.getProxy(mFragment.mBaseActivity)
                .getMyClubs()
                //.delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<MyClub>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mEmptyView.showLoadingView();
                        mAdapter.setNewData(null);
                        mBinding.recyclerView.setLayoutFrozen(true);
                        mAdapter.setEmptyView(mEmptyView);
                    }

                    @Override
                    public void onNext(List<MyClub> myClubs) {
                        Log.i(TAG, "updateUserClubList: 获取UserClubList完成");
                        MyClub[] clubsArray = myClubs.toArray(new MyClub[0]);
                        notifyClubsObservable(clubsArray, GET_CLUBS_RESULT_OK);
                        CacheUtil.get(mFragment.getActivity())
                                .put(Preferences.CACHE_USER_CLUB_LIST, clubsArray, Preferences.CACHE_TIME_USER_CLUB_LIST);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "updateUserInfo: 获取UserClubList失败");
                        if (e instanceof ApiException && ((ApiException) e).getCode() == ApiException.NETWORK_ERROR)
                            notifyClubsObservable(null, GET_CLUBS_RESULT_NO_NETWORK);
                        else
                            notifyClubsObservable(null, GET_CLUBS_RESULT_ERROR);
                    }

                    @Override
                    public void onComplete() {
                        if (!Preferences.sIsLogin.get()) {
                            Log.i(TAG, "updateUserClubList: 无法获取更新，用户没有登录");
                            notifyClubsObservable(null, GET_CLUBS_RESULT_NOT_LOGIN);
                            return;
                        }
                    }
                });
    }
}
