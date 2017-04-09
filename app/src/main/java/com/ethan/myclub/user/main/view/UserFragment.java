package com.ethan.myclub.user.main.view;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ethan.myclub.R;
import com.ethan.myclub.databinding.FragmentUserBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.main.BaseFragment;
import com.ethan.myclub.user.main.viewmodel.UserViewModel;
import com.ethan.myclub.util.Utils;


public class UserFragment extends BaseFragment {

    public UserViewModel mViewModel;

    public UserFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentUserBinding viewDataBinding = (FragmentUserBinding) onCreateDataBindingView(inflater, R.layout.fragment_user, container);
        mViewModel = new UserViewModel(this, viewDataBinding);
        mViewModel.getUserInfoCache();
        return viewDataBinding.getRoot();
    }

    @Override
    public void refresh() {
        super.refresh();
        mViewModel.updateUserInfo();
    }

    @Override
    public void willBeDisplayed() {
        super.willBeDisplayed();
        if (mBaseActivity != null) {
            mBaseActivity.getToolbarWrapper().changeScrollable(true).close();
            Utils.StatusBarLightMode(mBaseActivity, false);
        }
    }
}
