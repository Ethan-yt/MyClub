package com.ethan.myclub.club.create.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.databinding.ActivityClubCreateBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.club.create.viewmodel.ClubCreateViewModel;

public class ClubCreateActivity extends BaseActivity {

    private ClubCreateViewModel mViewModel;

    public static void start(Activity from) {
        Intent intent = new Intent(from, ClubCreateActivity.class);
        ActivityCompat.startActivity(from, intent, null);
    }
    public static void startForResult(Activity from,int requstCode) {
        Intent intent = new Intent(from, ClubCreateActivity.class);
        ActivityCompat.startActivityForResult(from, intent, requstCode, null);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityClubCreateBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_club_create);
        mViewModel = new ClubCreateViewModel(this, binding);
        getToolbarWrapper()
                .setTitle("创建社团")
                .showBackIcon()
                .show();
    }

}
