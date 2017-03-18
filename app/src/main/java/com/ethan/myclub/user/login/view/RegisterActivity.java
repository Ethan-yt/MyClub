package com.ethan.myclub.user.login.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.ethan.myclub.R;
import com.ethan.myclub.databinding.ActivityLoginRegisterBinding;
import com.ethan.myclub.main.SnackbarActivity;
import com.ethan.myclub.user.login.viewmodel.RegisterViewModel;

import cn.smssdk.SMSSDK;

public class RegisterActivity extends SnackbarActivity {

    public ActivityLoginRegisterBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login_register);
        mBinding.setViewModel(new RegisterViewModel(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    @Override
    protected void setRootLayout() {
        mRootLayout = findViewById(R.id.container);
    }
}
