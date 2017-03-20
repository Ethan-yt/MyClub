package com.ethan.myclub.user.main.view;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ethan.myclub.R;
import com.ethan.myclub.databinding.FragmentUserBinding;
import com.ethan.myclub.main.BaseFragment;
import com.ethan.myclub.user.main.viewmodel.UserViewModel;


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
