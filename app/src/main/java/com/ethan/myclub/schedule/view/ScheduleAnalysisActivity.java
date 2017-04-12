package com.ethan.myclub.schedule.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;

import com.ethan.myclub.R;
import com.ethan.myclub.club.member.view.ClubMemberListActivity;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.databinding.ActivityScheduleAnalysisBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.schedule.viewmodel.ScheduleAnalysisViewModel;

public class ScheduleAnalysisActivity extends BaseActivity {

    private ScheduleAnalysisViewModel mViewModel;


    public static void start(Activity from, MyClub myClub, String[] users) {
        Intent intent = new Intent(from, ScheduleAnalysisActivity.class);
        intent.putExtra("MyClub", myClub);
        intent.putExtra("users", users);
        ActivityCompat.startActivity(from, intent, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityScheduleAnalysisBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_schedule_analysis);
        MyClub myClub = (MyClub) getIntent().getSerializableExtra("MyClub");
        String[] users = (String[]) getIntent().getSerializableExtra("users");
        mViewModel = new ScheduleAnalysisViewModel(this, binding, myClub, users);

    }

}
