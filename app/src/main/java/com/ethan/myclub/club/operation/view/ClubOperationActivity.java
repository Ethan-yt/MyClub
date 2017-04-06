package com.ethan.myclub.club.operation.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.club.main.model.MyClub;
import com.ethan.myclub.databinding.ActivityClubOperationBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.club.operation.viewmodel.ClubOperationViewModel;

public class ClubOperationActivity extends BaseActivity {

    private ClubOperationViewModel mViewModel;

    public static void start(Activity from, MyClub club) {
        Intent intent = new Intent(from, ClubOperationActivity.class);
        intent.putExtra("club", club);
        ActivityCompat.startActivity(from, intent, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityClubOperationBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_club_operation);
        MyClub club = (MyClub) getIntent().getSerializableExtra("club");

        mViewModel = new ClubOperationViewModel(this, binding, club);

        getToolbarWrapper().showBackIcon()
                .transparent()
                .show();
    }


}
