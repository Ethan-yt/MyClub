package com.ethan.myclub.club.create.viewmodel;

import android.app.Activity;

import com.ethan.myclub.club.create.view.ClubCreateActivity;
import com.ethan.myclub.club.model.Club;
import com.ethan.myclub.databinding.ActivityClubCreateBinding;
import com.ethan.myclub.network.ApiHelper;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


public class ClubCreateViewModel {

    private ClubCreateActivity mActivity;
    private ActivityClubCreateBinding mBinding;

    public Club mClub;

    public ClubCreateViewModel(ClubCreateActivity activity, ActivityClubCreateBinding binding) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);

        mBinding.tilClubName.setCounterEnabled(true);
        mBinding.tilClubName.setCounterMaxLength(30);
        mBinding.tilClubBriefIntroduce.setCounterEnabled(true);
        mBinding.tilClubBriefIntroduce.setCounterMaxLength(140);
        mBinding.tilClubContact.setCounterEnabled(true);
        mBinding.tilClubContact.setCounterMaxLength(140);
        mClub = new Club();
    }

    public void createClub() {
        ApiHelper.getProxy(mActivity)
                .createClub(mClub.getClubName(), "1", mClub.getBriefIntroduce(), mClub.getContact())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Club>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mActivity.showWaitingDialog("请稍候", "正在创建社团", d);
                    }

                    @Override
                    public void onNext(Club club) {
                        mActivity.setResult(Activity.RESULT_OK);
                        mActivity.finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mActivity.dismissDialog();
                        mActivity.showSnackbar("错误：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mActivity.dismissDialog();
                    }
                });
    }
}