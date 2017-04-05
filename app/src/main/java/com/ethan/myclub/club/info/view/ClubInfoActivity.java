package com.ethan.myclub.club.info.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.club.main.model.Club;
import com.ethan.myclub.databinding.ActivityClubInfoBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.club.info.viewmodel.ClubInfoViewModel;

public class ClubInfoActivity extends BaseActivity {

    private ClubInfoViewModel mViewModel;

    public static void start(Activity from, String clubId) {
        Intent intent = new Intent(from, ClubInfoActivity.class);
        intent.putExtra("Club", clubId);
        ActivityCompat.startActivity(from, intent, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityClubInfoBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_club_info);
        String clubId = getIntent().getStringExtra("Club");
        mViewModel = new ClubInfoViewModel(this, binding, clubId);

    }

}
