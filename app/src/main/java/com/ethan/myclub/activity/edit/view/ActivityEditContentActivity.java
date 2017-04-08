package com.ethan.myclub.activity.edit.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.databinding.ActivityActivityEditContentBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.activity.edit.viewmodel.ActivityEditContentViewModel;
import com.ethan.myclub.main.ImageSelectActivity;

public class ActivityEditContentActivity extends ImageSelectActivity {

    private ActivityEditContentViewModel mViewModel;

    public static void start(Activity from,String activityId) {
        Intent intent = new Intent(from, ActivityEditContentActivity.class);
        intent.putExtra("activityId",activityId);
        ActivityCompat.startActivity(from, intent, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityActivityEditContentBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_activity_edit_content);
        String activityId = getIntent().getStringExtra("activityId");

        mViewModel = new ActivityEditContentViewModel(this, binding,activityId);

    }

}
