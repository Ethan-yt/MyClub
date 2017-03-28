package com.ethan.myclub.club.main.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ethan.myclub.R;
import com.ethan.myclub.club.main.viewmodel.ClubViewModel;
import com.ethan.myclub.databinding.FragmentClubBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.main.BaseFragment;


public class ClubListFragment extends BaseFragment {

    public ClubViewModel mViewModel;

    public ClubListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentClubBinding fragmentClubBinding = (FragmentClubBinding) onCreateDataBindingView(inflater, R.layout.fragment_club, container);
        mViewModel = new ClubViewModel(this, fragmentClubBinding);
        willBeDisplayed();
        return fragmentClubBinding.getRoot();
    }

    @Override
    public void refresh() {
        mViewModel.updateUserClubList();
    }

    @Override
    public void willBeDisplayed() {
        super.willBeDisplayed();
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity != null) {
            baseActivity.getToolbarWrapper().setTitle("社团").show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.getUserClubListCache();
    }
}
