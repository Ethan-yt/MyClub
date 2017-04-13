package com.ethan.myclub.user.edit.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.databinding.ActivityUserProfileEditBinding;
import com.ethan.myclub.main.ImageSelectActivity;
import com.ethan.myclub.user.edit.viewmodel.ProfileEditViewModel;
import com.ethan.myclub.user.model.Profile;

public class ProfileEditActivity extends ImageSelectActivity {

    private ProfileEditViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUserProfileEditBinding dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile_edit);
        mViewModel = new ProfileEditViewModel(this, dataBinding);
    }


    @Override
    public void onBackPressed() {
        mViewModel.onBackPressed();
    }

    public static void start(Activity activity, Bundle bundle) {
        Intent intent = new Intent(activity, ProfileEditActivity.class);
        ActivityCompat.startActivity(activity, intent, bundle);
    }
}
