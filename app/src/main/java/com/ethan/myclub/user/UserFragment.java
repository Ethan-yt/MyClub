package com.ethan.myclub.user;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ethan.myclub.R;
import com.ethan.myclub.main.BaseFragment;
import com.ethan.myclub.user.Schedule.ScheduleActivity;


public class UserFragment extends BaseFragment {


    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, container, false);
        fragmentContainer = (LinearLayout) view.findViewById(R.id.fragment_container);

        View timeManagement = view.findViewById(R.id.timeManagement);
        timeManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
