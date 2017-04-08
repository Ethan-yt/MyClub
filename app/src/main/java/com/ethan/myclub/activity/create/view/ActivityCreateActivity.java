package com.ethan.myclub.activity.create.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.databinding.ActivityActivityCreateBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.activity.create.viewmodel.ActivityCreateViewModel;

public class ActivityCreateActivity extends BaseActivity {

    private ActivityCreateViewModel mViewModel;

    public static void start(Activity from, MyClub myClub) {
        Intent intent = new Intent(from, ActivityCreateActivity.class);
        intent.putExtra("MyClub",myClub);
        ActivityCompat.startActivity(from, intent, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityActivityCreateBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_activity_create);
        MyClub myClub = (MyClub) getIntent().getSerializableExtra("MyClub");
        mViewModel = new ActivityCreateViewModel(this, binding, myClub);

    }

}
