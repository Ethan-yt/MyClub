package com.ethan.myclub.club.member.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.club.activitylist.view.ClubActivityListActivity;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.databinding.ActivityClubMemberListBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.club.member.viewmodel.ClubMemberListViewModel;
@Keep
public class ClubMemberListActivity extends BaseActivity {

    private ClubMemberListViewModel mViewModel;

    public static void start(Activity from, MyClub myClub) {
        Intent intent = new Intent(from, ClubMemberListActivity.class);
        intent.putExtra("MyClub", myClub);
        ActivityCompat.startActivity(from, intent, null);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityClubMemberListBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_club_member_list);
        MyClub myClub = (MyClub) getIntent().getSerializableExtra("MyClub");
        mViewModel = new ClubMemberListViewModel(this, binding, myClub);

    }

}
