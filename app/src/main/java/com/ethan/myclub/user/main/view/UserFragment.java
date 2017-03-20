package com.ethan.myclub.user.main.view;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ethan.myclub.R;
import com.ethan.myclub.databinding.FragmentUserBinding;
import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.main.BaseFragment;
import com.ethan.myclub.main.SnackbarActivity;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.user.info.InfoActivity;
import com.ethan.myclub.user.login.view.LoginActivity;
import com.ethan.myclub.user.main.viewmodel.UserViewModel;
import com.ethan.myclub.user.schedule.ScheduleActivity;

import java.security.PublicKey;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class UserFragment extends BaseFragment {

    public FragmentUserBinding mBinding;
    public UserViewModel mViewModel;

    public UserFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false);
        mViewModel = new UserViewModel(this);
        mBinding.setViewModel(mViewModel);
        mViewModel.updateUserInfomation();


        return mBinding.getRoot();
    }

    @Override
    protected void setFragmentContainer() {
        View view = getView();
        if (view != null)
            mFragmentContainer = (ViewGroup) view.findViewById(R.id.fragment_container);
    }

    @Override
    public void refresh() {
        super.refresh();
        mViewModel.updateUserInfomation();
    }
}
