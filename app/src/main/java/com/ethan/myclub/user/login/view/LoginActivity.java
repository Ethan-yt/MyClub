package com.ethan.myclub.user.login.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.ethan.myclub.R;
import com.ethan.myclub.databinding.ActivityLoginBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.user.login.viewmodel.LoginViewModel;

public class LoginActivity extends BaseActivity {
    public ActivityLoginBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mBinding.setViewModel(new LoginViewModel(this));
    }

}
