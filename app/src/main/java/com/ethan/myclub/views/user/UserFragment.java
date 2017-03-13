package com.ethan.myclub.views.user;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ethan.myclub.R;
import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.views.main.BaseFragment;
import com.ethan.myclub.views.user.info.InfoActivity;
import com.ethan.myclub.views.user.login.LoginActivity;
import com.ethan.myclub.views.user.schedule.ScheduleActivity;


public class UserFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AvatarImageView mIvAvatar;

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, container, false);
        fragmentContainer = (ViewGroup) view.findViewById(R.id.fragment_container);

        mIvAvatar = (AvatarImageView) view.findViewById(R.id.iv_avatar);

        View btnTimeManagement = view.findViewById(R.id.timeManagement);
        btnTimeManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                startActivity(intent);
            }
        });

        // 登录按钮
        View btnLogin = view.findViewById(R.id.tv_loginBtn);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        // 个人信息按钮
        View btnInfo = view.findViewById(R.id.basicInfo);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InfoActivity.class);

                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(getActivity(),
                                Pair.create((View) mIvAvatar, "trans_iv_avatar"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
            }
        });

        return view;
    }
}
