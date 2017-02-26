package com.ethan.myclub.user.schedule;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ethan.myclub.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ethan on 2017/1/19.
 */

// TODO: 2017/2/17 对每一个课程的增删改

public class ScheduleView extends LinearLayout {

    List<FrameLayout> mDayViews;
    ScheduleModel mScheduleModel;


    public ScheduleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScheduleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public ScheduleView(Context context) {
        super(context);
    }

    private void initScheduleModels() {
        for (final CourseModel courseModel : mScheduleModel.getCourses()) {

            List<CourseTime> courseTimes = courseModel.getTime();

            for (CourseTime courseTime : courseTimes) {

                CardView cv = new CardView(getContext());

                int itemHeight = getResources().getDimensionPixelSize(R.dimen.ScheduleCourseHeight);

                CardView.LayoutParams cvLp = new CardView.LayoutParams(
                        CardView.LayoutParams.MATCH_PARENT, (courseTime.getTimeEnd() - courseTime.getTimeBegin() + 1) * itemHeight);

                cvLp.setMargins(5, 10+(courseTime.getTimeBegin() - 1) * (itemHeight+5), 5, 5);

                TextView tv = new TextView(getContext());
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(12);
                tv.setTextColor(Color.BLACK);
                tv.setText(courseModel.getName() + "\n @" + courseTime.getLocation());

                cv.setLayoutParams(cvLp);
                cv.addView(tv);
                cv.setBackgroundColor(Color.WHITE);
                cv.setContentPadding(5,5,5,5);

                cv.setRadius(getResources().getDimensionPixelSize(R.dimen.ScheduleCourseRadius));
                mDayViews.get(courseTime.getDay() - 1).addView(cv);

                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), courseModel.getName(), Toast.LENGTH_SHORT).show();
                    }
                });

            }


        }

    }


    public void initDayView() {
        removeAllViews();

        mDayViews = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            FrameLayout dayView = new FrameLayout(getContext());
            dayView.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f));
            mDayViews.add(dayView);
            addView(dayView);
        }

    }


    public ScheduleModel getScheduleModel() {
        return mScheduleModel;
    }

    public void setScheduleModel(ScheduleModel scheduleModel) {
        mScheduleModel = scheduleModel;
        initDayView();
        initScheduleModels();
    }
}
