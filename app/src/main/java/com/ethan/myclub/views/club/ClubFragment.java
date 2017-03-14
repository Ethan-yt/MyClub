package com.ethan.myclub.views.club;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ethan.myclub.R;
import com.ethan.myclub.views.main.BaseFragment;
import com.ethan.myclub.views.user.AvatarImageView;
import com.ethan.myclub.views.user.info.InfoActivity;
import com.ethan.myclub.views.user.login.LoginActivity;
import com.ethan.myclub.views.user.schedule.ScheduleActivity;


public class ClubFragment extends BaseFragment {

    public ClubFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_club, container, false);


        return view;
    }

    @Override
    protected void setFragmentContainer() {
        View view = getView();
        if(view != null)
            mFragmentContainer = (ViewGroup) view.findViewById(R.id.fragment_container);
    }

}
