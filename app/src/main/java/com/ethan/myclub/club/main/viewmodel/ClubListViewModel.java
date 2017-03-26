package com.ethan.myclub.club.main.viewmodel;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ethan.myclub.R;
import com.ethan.myclub.club.main.adapter.ClubListAdapter;
import com.ethan.myclub.club.main.model.Club;
import com.ethan.myclub.club.main.view.ClubListFragment;
import com.ethan.myclub.databinding.FragmentClubBinding;
import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.main.BaseFragment;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.util.CacheUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by ethan on 2017/3/25.
 */

public class ClubListViewModel {

    private static final String TAG = "ClubListViewModel";

    private ClubListFragment mFragment;

    private FragmentClubBinding mBinding;

    public ClubListViewModel(ClubListFragment fragment, FragmentClubBinding binding){
        mFragment = fragment;
        mBinding = binding;
        mBinding.setViewModel(this);
        new BaseFragment.ToolbarWrapper(mFragment,"社团").show();
    }

    private static final int GET_CLUBS_RESULT_OK = -1;
    private static final int GET_CLUBS_RESULT_NOT_LOGIN = 1;
    private static final int GET_CLUBS_RESULT_ERROR = 3;

    public void getUserClubListCache() {

        Object clubsObj = CacheUtil.get(mFragment.getActivity()).getAsObject(Preferences.CACHE_USER_CLUB_LIST);
        if (clubsObj == null || !(clubsObj instanceof Club[])) {
            Log.i(TAG, "getUserClubListCache: 读取UserClubList缓存失败，强制获取更新");
            updateUserClubList();
        } else {
            notifyClubsObservable((Club[]) clubsObj,GET_CLUBS_RESULT_OK);
            Log.i(TAG, "getUserClubListCache: 读取UserClubList缓存成功");
        }

    }


    private void notifyClubsObservable(Club[] clubsArray,int resultCode) {
        switch (resultCode)
        {
            case GET_CLUBS_RESULT_NOT_LOGIN:
                break;
            case GET_CLUBS_RESULT_ERROR:
                break;
            case GET_CLUBS_RESULT_OK:
                ClubListAdapter mAdapter = new ClubListAdapter(R.layout.item_club, Arrays.asList(clubsArray));
                mBinding.recyclerView.setLayoutManager(new GridLayoutManager(mFragment.getContext(),2));
                mBinding.recyclerView.setAdapter(mAdapter);

                break;
        }
    }

    public void updateUserClubList() {
        if (!Preferences.sIsLogin.get()) {
            Log.i(TAG, "updateUserClubList: 无法获取更新，用户没有登录");
            notifyClubsObservable(null,GET_CLUBS_RESULT_NOT_LOGIN);
            return;
        }
        ApiHelper.getProxy((BaseActivity) mFragment.getActivity())
                .getClubs()
                .subscribe(new Observer<List<Club>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Club> clubs) {
                        Log.i(TAG, "updateUserClubList: 获取UserClubList完成");
                        Club[] clubsArray = clubs.toArray(new Club[0]);
                        notifyClubsObservable(clubsArray,GET_CLUBS_RESULT_OK);
                        CacheUtil.get(mFragment.getActivity())
                                .put(Preferences.CACHE_USER_INFO, clubsArray, Preferences.CACHE_TIME_USER_INFO);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "updateUserInfo: 获取UserClubList失败");
                        notifyClubsObservable(null,GET_CLUBS_RESULT_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
