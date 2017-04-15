package com.ethan.myclub.club.my.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ethan.myclub.R;
import com.ethan.myclub.club.create.view.ClubCreateActivity;
import com.ethan.myclub.club.my.viewmodel.ClubViewModel;
import com.ethan.myclub.databinding.FragmentClubBinding;
import com.ethan.myclub.main.MyApplication;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.main.BaseFragment;
import com.ethan.myclub.main.MainActivity;
import com.ethan.myclub.main.ToolbarWrapper;
import com.ethan.myclub.util.Utils;


public class MyClubFragment extends BaseFragment {

    public ClubViewModel mViewModel;

    public MyClubFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentClubBinding fragmentClubBinding = (FragmentClubBinding) onCreateDataBindingView(inflater, R.layout.fragment_club, container);
        mViewModel = new ClubViewModel(this, fragmentClubBinding);
        mViewModel.updateUserClubListAttempt();
        return fragmentClubBinding.getRoot();
    }

    @Override
    public void refresh() {
        mViewModel.updateUserClubList();
    }

    @Override
    public void willBeDisplayed() {
        super.willBeDisplayed();
        if (mMainActivity != null) {
            new ToolbarWrapper.Builder(mMainActivity)
                    .withAnimate()
                    .setToolbarFitsSystemWindows()
                    .setTitle("我的社团", true)
                    .show();

        }
    }
}
