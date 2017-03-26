package com.ethan.myclub.club.main.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ethan.myclub.R;
import com.ethan.myclub.club.main.viewmodel.ClubListViewModel;
import com.ethan.myclub.databinding.FragmentClubBinding;
import com.ethan.myclub.main.BaseFragment;


public class ClubListFragment extends BaseFragment {

    public ClubListViewModel mViewModel;

    public ClubListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentClubBinding fragmentClubBinding = (FragmentClubBinding) onCreateDataBindingView(inflater,R.layout.fragment_club,container);
        mViewModel = new ClubListViewModel(this,fragmentClubBinding);
        return fragmentClubBinding.getRoot();
    }

    @Override
    public void refresh() {
        mViewModel.updateUserClubList();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.getUserClubListCache();
    }
}
