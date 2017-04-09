package com.ethan.myclub.user.detail.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.club.model.MemberResult;
import com.ethan.myclub.databinding.ActivityUserDetailBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.user.detail.viewmodel.UserDetailViewModel;

public class UserDetailActivity extends BaseActivity {

    private UserDetailViewModel mViewModel;

    public static void start(Activity from, MemberResult memberResult) {
        Intent intent = new Intent(from, UserDetailActivity.class);
        intent.putExtra("memberResult", memberResult);
        ActivityCompat.startActivity(from, intent, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUserDetailBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_user_detail);
        MemberResult memberResult = (MemberResult) getIntent().getSerializableExtra("memberResult");
        mViewModel = new UserDetailViewModel(this, binding, memberResult);

    }

}
