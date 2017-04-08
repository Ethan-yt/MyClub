package com.ethan.myclub.activity.detail.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.databinding.ActivityActivityDetailBinding;
import com.ethan.myclub.discover.activity.model.ActivityResult;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.activity.detail.viewmodel.ActivityDetailViewModel;

public class ActivityDetailActivity extends BaseActivity {

    private ActivityDetailViewModel mViewModel;

    public static void start(Activity from, ActivityResult activityResult) {
        Intent intent = new Intent(from, ActivityDetailActivity.class);
        intent.putExtra("activityResult", activityResult);
        ActivityCompat.startActivity(from, intent, null);
    }

    public static void startForResult(Activity from, ActivityResult activityResult, int requestCode) {
        Intent intent = new Intent(from, ActivityDetailActivity.class);
        intent.putExtra("activityResult", activityResult);
        ActivityCompat.startActivityForResult(from, intent, requestCode, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityActivityDetailBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_activity_detail);
        ActivityResult activityResult = (ActivityResult) getIntent().getSerializableExtra("activityResult");

        mViewModel = new ActivityDetailViewModel(this, binding, activityResult);

    }

}
