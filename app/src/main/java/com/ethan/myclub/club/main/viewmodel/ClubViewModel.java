package com.ethan.myclub.club.main.viewmodel;

import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethan.myclub.club.main.adapter.ClubListAdapter;
import com.ethan.myclub.club.main.model.Club;
import com.ethan.myclub.club.main.view.ClubListFragment;
import com.ethan.myclub.club.main.view.EmptyView;
import com.ethan.myclub.databinding.FragmentClubBinding;
import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.network.exception.ApiException;
import com.ethan.myclub.util.CacheUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by ethan on 2017/3/25.
 */

public class ClubViewModel {

    private static final String TAG = "ClubViewModel";
    private final EmptyView mEmptyView;

    private ClubListFragment mFragment;
    private FragmentClubBinding mBinding;

    private ClubListAdapter mAdapter;

    public ClubViewModel(ClubListFragment fragment, FragmentClubBinding binding) {
        mFragment = fragment;
        mBinding = binding;
        mBinding.setViewModel(this);

        mAdapter = new ClubListAdapter(null);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
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
        if (clubsObj == null || !(clubsObj instanceof Club[])) {
            Log.i(TAG, "getUserClubListCache: 读取UserClubList缓存失败，强制获取更新");
            updateUserClubList();
        } else {
            notifyClubsObservable((Club[]) clubsObj, GET_CLUBS_RESULT_OK);
            Log.i(TAG, "getUserClubListCache: 读取UserClubList缓存成功");
        }

    }


    private void notifyClubsObservable(Club[] clubsArray, int resultCode) {

        if (resultCode == GET_CLUBS_RESULT_OK) {
            mBinding.recyclerView.setLayoutFrozen(false);
            List<Club> dataArray = Arrays.asList(clubsArray);
            mAdapter.setNewData(dataArray);
        } else {
            if (clubsArray != null && clubsArray.length == 0)
                mEmptyView.showEmptyView("还没有加入社团哦", "快去发现你喜欢的社团吧！");
            else switch (resultCode) {
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
        if (!Preferences.sIsLogin.get()) {
            Log.i(TAG, "updateUserClubList: 无法获取更新，用户没有登录");
            notifyClubsObservable(null, GET_CLUBS_RESULT_NOT_LOGIN);
            return;
        }
        mEmptyView.showLoadingView();
        mAdapter.setNewData(null);
        mBinding.recyclerView.setLayoutFrozen(true);
        mAdapter.setEmptyView(mEmptyView);

        ApiHelper.getProxy(mFragment.mBaseActivity)
                .getMyClubs()
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Club>>() {
                    @Override
                    public void accept(@NonNull List<Club> clubs) throws Exception {
                        Log.i(TAG, "updateUserClubList: 获取UserClubList完成");
                        Club[] clubsArray = clubs.toArray(new Club[0]);
                        notifyClubsObservable(clubsArray, GET_CLUBS_RESULT_OK);
                        CacheUtil.get(mFragment.getActivity())
                                .put(Preferences.CACHE_USER_CLUB_LIST, clubsArray, Preferences.CACHE_TIME_USER_CLUB_LIST);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.i(TAG, "updateUserInfo: 获取UserClubList失败");
                        if (throwable instanceof ApiException && ((ApiException) throwable).getCode() == ApiException.NETWORK_ERROR)
                            notifyClubsObservable(null, GET_CLUBS_RESULT_NO_NETWORK);
                        else
                            notifyClubsObservable(null, GET_CLUBS_RESULT_ERROR);
                    }
                });
    }
}
