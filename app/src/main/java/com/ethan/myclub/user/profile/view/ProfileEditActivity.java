package com.ethan.myclub.user.profile.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.databinding.ActivityProfileEditBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.user.login.view.RegisterActivity;
import com.ethan.myclub.user.profile.viewmodel.ProfileEditViewModel;

public class ProfileEditActivity extends BaseActivity {

    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_PICK = 2;
    public static final int REQUEST_CODE_CROP = 3;


    private ProfileEditViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfileEditBinding dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile_edit);
        mViewModel = new ProfileEditViewModel(this, dataBinding);
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

    public static void startActivity(Activity activity, String imageUrl, Bundle bundle) {
        Intent intent = new Intent(activity, ProfileEditActivity.class);
        intent.putExtra("ImageUrl", imageUrl);
        ActivityCompat.startActivity(activity, intent, bundle);
    }
}
