package com.ethan.myclub.club.notification.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.databinding.ActivityClubNotificationCreateBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.club.notification.viewmodel.ClubNotificationCreateViewModel;
@Keep
public class ClubNotificationCreateActivity extends BaseActivity {

    private ClubNotificationCreateViewModel mViewModel;

    public static void start(Activity from , MyClub myClub) {
        Intent intent = new Intent(from, ClubNotificationCreateActivity.class);
        intent.putExtra("myClub",myClub);
        ActivityCompat.startActivity(from, intent, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityClubNotificationCreateBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_club_notification_create);
        MyClub myClub = (MyClub) getIntent().getSerializableExtra("myClub");
        mViewModel = new ClubNotificationCreateViewModel(this, binding,myClub);
    }

}
