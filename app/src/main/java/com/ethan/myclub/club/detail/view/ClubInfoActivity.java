package com.ethan.myclub.club.detail.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.databinding.ActivityClubInfoBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.club.detail.viewmodel.ClubInfoViewModel;

public class ClubInfoActivity extends BaseActivity {

    private ClubInfoViewModel mViewModel;
    public static final int REQUEST_EDIT_CLUB_INFO = 10086;

    public static void start(Activity from, int clubId, int permission) {
        Intent intent = new Intent(from, ClubInfoActivity.class);
        intent.putExtra("MyClub", clubId);
        intent.putExtra("Permission", permission);
        ActivityCompat.startActivity(from, intent, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityClubInfoBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_club_info);
        int clubId = getIntent().getIntExtra("MyClub", -1);
        int permission = getIntent().getIntExtra("Permission", 0);

        mViewModel = new ClubInfoViewModel(this, binding, clubId, permission);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_EDIT_CLUB_INFO:
                    mViewModel.update(mViewModel.mClub.get().id);
                    showSnackbar("修改社团资料成功");
                    break;
            }

        }

    }

}
