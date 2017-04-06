package com.ethan.myclub.club.info.edit.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.club.info.model.Club;
import com.ethan.myclub.databinding.ActivityClubInfoEditBinding;
import com.ethan.myclub.club.info.edit.viewmodel.ClubInfoEditViewModel;
import com.ethan.myclub.main.ImageSelectActivity;
import com.ethan.myclub.user.profile.view.ProfileEditActivity;

public class ClubInfoEditActivity extends ImageSelectActivity {

    private ClubInfoEditViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityClubInfoEditBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_club_info_edit);
        mViewModel = new ClubInfoEditViewModel(this, binding);
        mViewModel.setClub((Club) getIntent().getSerializableExtra("MyClub"));
    }

    @Override
    public void onBackPressed() {
        mViewModel.onBackPressed();
    }

    public static void startForResult(Activity activity, Club club, int requestCode) {
        if(club == null)
            return;
        Intent intent = new Intent(activity, ClubInfoEditActivity.class);
        intent.putExtra("MyClub", club);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);
    }

}
