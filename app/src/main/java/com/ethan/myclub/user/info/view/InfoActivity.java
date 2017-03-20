package com.ethan.myclub.user.info.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import com.ethan.myclub.R;
import com.ethan.myclub.databinding.ActivityInfoBinding;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.main.SnackbarActivity;
import com.ethan.myclub.user.info.viewmodel.InfoViewModel;
import com.ethan.myclub.user.main.view.AvatarImageView;

import java.io.FileNotFoundException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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
