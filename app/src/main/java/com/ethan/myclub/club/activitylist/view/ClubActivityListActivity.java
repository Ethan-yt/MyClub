package com.ethan.myclub.club.activitylist.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.databinding.ActivityClubActivityListBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.club.activitylist.viewmodel.ClubActivityListViewModel;

public class ClubActivityListActivity extends BaseActivity {

    public static final int RESULT_ERROR = 0x3222;
    public static final int RESULT_DELETED = 0x3223;
    private ClubActivityListViewModel mViewModel;
    static final public int REQUEST_EDIT_ACTIVITY = 0x2333;

    public static void start(Activity from, MyClub myClub) {
        Intent intent = new Intent(from, ClubActivityListActivity.class);
        intent.putExtra("MyClub", myClub);
        ActivityCompat.startActivity(from, intent, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyClub myClub = (MyClub) getIntent().getSerializableExtra("MyClub");
        ActivityClubActivityListBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_club_activity_list);

        mViewModel = new ClubActivityListViewModel(this, binding, myClub);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                showSnackbar("活动修改成功");
                mViewModel.update();
            } else if (resultCode == RESULT_ERROR) {
                showSnackbar("活动详情获取失败！");
            } else if (resultCode == RESULT_DELETED) {
                showSnackbar("删除活动成功！");
                mViewModel.update();
            }

        }
    }
}
