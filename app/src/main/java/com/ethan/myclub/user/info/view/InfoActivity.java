package com.ethan.myclub.user.info.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.ethan.myclub.R;
import com.ethan.myclub.databinding.ActivityInfoBinding;
import com.ethan.myclub.main.SnackbarActivity;
import com.ethan.myclub.user.info.viewmodel.InfoViewModel;

public class InfoActivity extends SnackbarActivity {

    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_PICK = 2;
    public static final int REQUEST_CODE_CROP = 3;

    public ActivityInfoBinding mDataBinding;
    private InfoViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this,R.layout.activity_info);
        mViewModel = new InfoViewModel(this);
        mDataBinding.setViewModel(mViewModel);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mViewModel.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        mViewModel.onBackPressed();
    }



    @Override
    protected void setRootLayout() {
        mRootLayout = mDataBinding.container;
    }
}