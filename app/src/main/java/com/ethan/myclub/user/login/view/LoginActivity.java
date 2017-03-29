package com.ethan.myclub.user.login.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.ethan.myclub.R;
import com.ethan.myclub.databinding.ActivityLoginBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.user.login.viewmodel.LoginViewModel;

public class LoginActivity extends BaseActivity {

    private LoginViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        mViewModel = new LoginViewModel(this, dataBinding);
    }

}
