package com.ethan.myclub.user.login.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.databinding.ActivityLoginBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.main.MainActivity;
import com.ethan.myclub.user.login.viewmodel.LoginViewModel;

public class LoginActivity extends BaseActivity {

    private LoginViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        mViewModel = new LoginViewModel(this, dataBinding);
    }

    public static void startActivityForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, LoginActivity.class);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);

    }
}
