package com.ethan.myclub.user.login.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.ethan.myclub.R;
import com.ethan.myclub.databinding.ActivityLoginBinding;
import com.ethan.myclub.main.SnackbarActivity;
import com.ethan.myclub.user.login.viewmodel.LoginViewModel;

public class LoginActivity extends SnackbarActivity {
    public ActivityLoginBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mBinding.setViewModel(new LoginViewModel(this));
    }

    @Override
    protected void setRootLayout() {
        mRootLayout = mBinding.container;
    }
}
