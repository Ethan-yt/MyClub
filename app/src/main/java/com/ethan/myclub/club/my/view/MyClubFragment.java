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
        BaseActivity.ToolbarWrapper toolbarWrapper = mMainActivity.getToolbarWrapper()
                .dismiss()
                .withAnimate()
                .setTitle("我的社团", true);
        if (MyApplication.isLogin())
            toolbarWrapper.setMenu(R.menu.toolbar_club, new MyMenuItemClickListener());

        toolbarWrapper.show();
        mViewModel.updateUserClubList();
    }

    @Override
    public void willBeDisplayed() {
        super.willBeDisplayed();
        if (mMainActivity != null) {
            BaseActivity.ToolbarWrapper toolbarWrapper = mMainActivity.getToolbarWrapper()
                    .dismiss()
                    .withAnimate()
                    .setTitle("我的社团", true);

            if (MyApplication.isLogin())
                toolbarWrapper.setMenu(R.menu.toolbar_club, new MyMenuItemClickListener());

            toolbarWrapper.show();

            Utils.StatusBarLightMode(mMainActivity, true);
        }
    }

    private class MyMenuItemClickListener implements Toolbar.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_scan:
                    mMainActivity.showSnackbar("愚人节快乐");
                    break;
                case R.id.action_add:
                    mMainActivity.startActivity(mMainActivity, MainActivity.REQUEST_ADD_CLUB, Activity.RESULT_OK);
                    break;
                case R.id.action_create:
                    ClubCreateActivity.startForResult(mMainActivity, MainActivity.REQUEST_CREATE_CLUB);
                    break;
            }

            return false;
        }
    }
}
