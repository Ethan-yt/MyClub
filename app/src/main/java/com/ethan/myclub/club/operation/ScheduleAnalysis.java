package com.ethan.myclub.club.operation;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.ethan.myclub.club.member.view.ClubMemberListActivity;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.main.MainActivity;
import com.ethan.myclub.network.ApiHelper;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by ethan on 2017/4/12.
 */

public class ScheduleAnalysis {
    static public void start(final Activity baseActivity, final MyClub myClub) {
        ClubMemberListActivity.startCheckableForResult(baseActivity,myClub);
    }
}