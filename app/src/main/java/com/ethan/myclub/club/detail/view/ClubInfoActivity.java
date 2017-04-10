package com.ethan.myclub.club.detail.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.databinding.ActivityClubInfoBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.club.detail.viewmodel.ClubInfoViewModel;
@Keep
public class ClubInfoActivity extends BaseActivity {

    private ClubInfoViewModel mViewModel;
    public static final int REQUEST_EDIT_CLUB_INFO = 10086;

    public static void start(Activity from, MyClub myClub) {
        Intent intent = new Intent(from, ClubInfoActivity.class);
        intent.putExtra("MyClub", myClub);
        ActivityCompat.startActivity(from, intent, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityClubInfoBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_club_info);
        MyClub myClub = (MyClub) getIntent().getSerializableExtra("MyClub");
        mViewModel = new ClubInfoViewModel(this, binding, myClub);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_EDIT_CLUB_INFO:
                    mViewModel.updateClubDetail();
                    showSnackbar("修改社团资料成功");
                    break;
            }

        }

    }

}
