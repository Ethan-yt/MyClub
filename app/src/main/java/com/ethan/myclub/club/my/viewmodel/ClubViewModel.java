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
import com.ethan.myclub.main.MainActivity;
import com.ethan.myclub.main.MyApplication;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.network.exception.ApiException;

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

        mEmptyView = new EmptyView(mFragment.mMainActivity);
    }

    private static final int GET_CLUBS_RESULT_NOT_LOGIN = 1;
    private static final int GET_CLUBS_RESULT_ERROR = 3;
    private static final int GET_CLUBS_RESULT_NO_NETWORK = 4;

    public void updateUserClubListAttempt() {
        if (MainActivity.needUpdateFlag.clubList) {
            Log.i(TAG, "updateUserClubListAttempt: 更新UserClubList");
            updateUserClubList();
        }
    }


    private void showEmptyView(int resultCode) {
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

    public void updateUserClubList() {
        if (!MyApplication.isLogin()) {
            Log.i(TAG, "updateUserClubList: 无法获取更新，用户没有登录");
            showEmptyView(GET_CLUBS_RESULT_NOT_LOGIN);
            return;
        }
        ApiHelper.getProxy(mFragment.mMainActivity)
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
                        Log.i(TAG, "updateUserClubList: 更新UserClubList完成");

                        if (myClubs == null || myClubs.size() == 0) {
                            mEmptyView.showEmptyView("还没有加入社团哦", "快去发现你喜欢的社团吧！");
                            mAdapter.setNewData(null);
                            mBinding.recyclerView.setLayoutFrozen(true);
                            mAdapter.setEmptyView(mEmptyView);
                        } else {
                            for (MyClub myClub : myClubs) {
                                myClub.clubBadge += "?imageView2/0/w/300/h/300";
                            }
                            mBinding.recyclerView.setLayoutFrozen(false);
                            mAdapter.setNewData(myClubs);
                            MainActivity.needUpdateFlag.clubList = false;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "updateUserClubList: 更新UserClubList失败");
                        if (e instanceof ApiException && ((ApiException) e).getCode() == ApiException.NETWORK_ERROR)
                            showEmptyView(GET_CLUBS_RESULT_NO_NETWORK);
                        else
                            showEmptyView(GET_CLUBS_RESULT_ERROR);
                    }

                    @Override
                    public void onComplete() {
                        if (!MyApplication.isLogin()) {
                            Log.i(TAG, "updateUserClubList: 无法获取更新，用户没有登录");
                            showEmptyView(GET_CLUBS_RESULT_NOT_LOGIN);
                            return;
                        }
                    }
                });
    }
}
