package com.ethan.myclub.schedule.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ethan.myclub.R;
import com.ethan.myclub.schedule.model.Course;
import com.ethan.myclub.schedule.model.CourseTime;
import com.ethan.myclub.schedule.model.Schedule;
import com.ethan.myclub.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ethan on 2017/1/19.
 */

// TODO: 2017/2/17 对每一个课程的增删改

public class ScheduleView extends LinearLayout {

    List<FrameLayout> mDayViews;
    Schedule mSchedule;
    private int mCurrentWeek;
    private OnClickListener mOnClickListener;

    public ScheduleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScheduleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public ScheduleView(Context context) {
        super(context);
    }

    private void initScheduleModels() {

        for (final Course course : mSchedule.getCourses()) {

            List<CourseTime> courseTimes = course.getTime();

            for (final CourseTime courseTime : courseTimes) {

                if (courseTime.getWeekBegin() > mCurrentWeek ||
                        courseTime.getWeekEnd() < mCurrentWeek ||
                        (courseTime.getWeekFlag() == 1 && mCurrentWeek % 2 == 0) ||
                        (courseTime.getWeekFlag() == 2 && mCurrentWeek % 2 == 1))
                    continue;

                CardView cv = new CardView(getContext());

                int itemHeight = getResources().getDimensionPixelSize(R.dimen.ScheduleCourseHeight);

                CardView.LayoutParams cvLp = new CardView.LayoutParams(
                        CardView.LayoutParams.MATCH_PARENT, (courseTime.getTimeEnd() - courseTime.getTimeBegin() + 1) * (itemHeight + 10) - 10);

                cvLp.setMargins(5, mHeadHeight + 10 + (courseTime.getTimeBegin() - 1) * (itemHeight + 10), 5, 5);

                TextView tv = new TextView(getContext());
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(12);
                tv.setTextColor(Color.BLACK);
                String location = courseTime.getLocation();
                tv.setText(course.getName() + (TextUtils.isEmpty(location) ? "" : "\n @" + courseTime.getLocation()));

                cv.setLayoutParams(cvLp);
                cv.addView(tv);
                cv.setContentPadding(5, 5, 5, 5);
                cv.setBackgroundColor(course.getColor());
                cv.setRadius(getResources().getDimensionPixelSize(R.dimen.ScheduleCourseRadius));
                mDayViews.get(courseTime.getDay() - 1).addView(cv);

                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnClickListener != null)
                            mOnClickListener.onClick(courseTime, course);
                        else
                            Toast.makeText(getContext(), course.getName(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }

    }


    private int mHeadHeight = Utils.dp2px(getContext(), 30);

    public void initDayView() {
        removeAllViews();

        mDayViews = new ArrayList<>();
        String str[] = new String[]{"一", "二", "三", "四", "五", "六", "日"};
        for (int i = 0; i < 7; i++) {
            FrameLayout dayView = new FrameLayout(getContext());
            dayView.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f));

            CardView cv = new CardView(getContext());
            CardView.LayoutParams cvLp = new CardView.LayoutParams(
                    CardView.LayoutParams.MATCH_PARENT, mHeadHeight);

            cvLp.setMargins(5, 5, 5, 5);

            TextView tv = new TextView(getContext());
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(12);
            tv.setTextColor(Color.BLACK);
            tv.setText("星期" + str[i]);
            cv.setLayoutParams(cvLp);
            cv.addView(tv);
            cv.setBackgroundColor(Color.WHITE);
            cv.setContentPadding(5, 5, 5, 5);

            cv.setRadius(getResources().getDimensionPixelSize(R.dimen.ScheduleCourseRadius));
            dayView.addView(cv);

            mDayViews.add(dayView);
            addView(dayView);
        }

    }


    public Schedule getSchedule() {
        return mSchedule;
    }

    public void setSchedule(Schedule schedule, int currentWeek) {
        mSchedule = schedule;
        mCurrentWeek = currentWeek;
        initDayView();
        initScheduleModels();
    }

    public interface OnClickListener {
        void onClick(CourseTime courseTime, Course course);
    }
}
