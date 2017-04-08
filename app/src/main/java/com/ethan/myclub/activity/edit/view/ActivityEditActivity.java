package com.ethan.myclub.activity.edit.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.databinding.ActivityActivityEditBinding;
import com.ethan.myclub.discover.activity.model.ActivityResult;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.activity.edit.viewmodel.ActivityEditViewModel;
import com.ethan.myclub.main.ImageSelectActivity;

public class ActivityEditActivity extends ImageSelectActivity {

    private ActivityEditViewModel mViewModel;


    public static void startForResult(Activity from, ActivityResult activityResult, int requestCode) {
        if(activityResult == null)
            return;
        Intent intent = new Intent(from, ActivityEditActivity.class);
        intent.putExtra("ActivityResult", activityResult);
        ActivityCompat.startActivityForResult(from, intent, requestCode, null);
    }

    @Override
    public void onBackPressed() {
        mViewModel.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityActivityEditBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_activity_edit);
        ActivityResult activityResult = (ActivityResult) getIntent().getSerializableExtra("ActivityResult");

        mViewModel = new ActivityEditViewModel(this, binding, activityResult);

    }

}
