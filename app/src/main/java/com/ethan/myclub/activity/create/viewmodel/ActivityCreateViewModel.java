package com.ethan.myclub.activity.create.viewmodel;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.ethan.myclub.activity.create.view.ActivityCreateActivity;
import com.ethan.myclub.activity.model.Activity;
import com.ethan.myclub.club.model.Club;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.databinding.ActivityActivityCreateBinding;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.util.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ActivityCreateViewModel {

    private ActivityCreateActivity mActivity;
    private ActivityActivityCreateBinding mBinding;

    public Activity mActivityDetail;
    public MyClub mMyClub;
    private Calendar c = Calendar.getInstance();

    public ActivityCreateViewModel(ActivityCreateActivity activity, ActivityActivityCreateBinding binding, MyClub myClub) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);
        mMyClub = myClub;
        mActivityDetail = new Activity();
        mActivity.getToolbarWrapper()
                .setTitle("创建活动")
                .showBackIcon()
                .show();
        c.setTimeInMillis(System.currentTimeMillis());
        mBinding.etDatetime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);

                    new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            c.set(Calendar.YEAR, year);
                            c.set(Calendar.MONTH, month);
                            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            int mHour = c.get(Calendar.HOUR_OF_DAY);
                            int mMinute = c.get(Calendar.MINUTE);
                            new TimePickerDialog(mActivity,
                                    new TimePickerDialog.OnTimeSetListener() {

                                        @Override
                                        public void onTimeSet(TimePicker view,
                                                              int hourOfDay, int minute) {
                                            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            c.set(Calendar.MINUTE, minute);
                                            c.set(Calendar.SECOND, 0); // 设为 0
                                            c.set(Calendar.MILLISECOND, 0); // 设为 0
                                            mActivityDetail.setActivityTime(Utils.Date2StdDate(c.getTime()));
                                        }
                                    }, mHour, mMinute, true).show();
                        }
                    }, mYear, mMonth, mDay).show();

                }

            }
        });

    }

    public void createActivity() {
        ApiHelper.getProxy(mActivity)
                .createActivity(String.valueOf(mMyClub.clubId), mActivityDetail.getName(), mActivityDetail.getJoinMembersMax(), Utils.StdDate2ApiDate(mActivityDetail.getActivityTime()), mActivityDetail.getBriefIntroduction(), mActivityDetail.getLocation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mActivity.showWaitingDialog("请稍候", "正在创建活动", d);
                    }

                    @Override
                    public void onNext(Object o) {
                        mActivity.finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mActivity.dismissDialog();
                        mActivity.showSnackbar("错误：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mActivity.dismissDialog();
                    }
                });
    }
}