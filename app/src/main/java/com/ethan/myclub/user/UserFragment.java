package com.ethan.myclub.user;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ethan.myclub.R;
import com.ethan.myclub.main.BaseFragment;
import com.ethan.myclub.user.login.LoginActivity;
import com.ethan.myclub.user.schedule.ScheduleActivity;


public class UserFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, container, false);
        fragmentContainer = (ViewGroup) view.findViewById(R.id.fragment_container);

        View timeManagement = view.findViewById(R.id.timeManagement);
        timeManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                startActivity(intent);
            }
        });

        View loginBtn = view.findViewById(R.id.tv_loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
