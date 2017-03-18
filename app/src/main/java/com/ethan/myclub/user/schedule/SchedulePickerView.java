package com.ethan.myclub.user.schedule;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.ethan.myclub.user.schedule.model.Schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SchedulePickerView extends LinearLayout {

    private List<Schedule> mSchedules;
    private String mYear;
    private String mTerm;
    private String[] mYears;
    private String[] mTerms;

    private NumberPicker np1 = new NumberPicker(this.getContext());
    private NumberPicker np2 = new NumberPicker(this.getContext());

    public void setTerm(String term) {
        mTerm = term;

        for (int i = 0; i < mTerms.length; i++) {
            String s = mTerms[i];
            if (s.equals(term)) {
                setTerm(i);
                return ;
            }
        }
    }

    public void setTerm(int termIndex)
    {
        np2.setValue(termIndex);
        mTerm = mTerms[termIndex];
    }

    public void setYear(String year) {
        for (int i = 0; i < mYears.length; i++) {
            String s = mYears[i];
            if (s.equals(year)) {
                setYear(i);
                return;
            }
        }
    }

    public void setYear(int yearIndex) {
        np1.setValue(yearIndex);
        mYear = mYears[yearIndex];
        //二级联动
        mTerms = map.get(mYears[yearIndex]);
        np2.setDisplayedValues(null);
        np2.setMinValue(0);
        np2.setMaxValue(mTerms.length - 1);
        np2.setDisplayedValues(mTerms);
        mYear = mYears[yearIndex];

    }

    public String getYear() {
        return mYear;
    }

    public String getTerm() {
        return mTerm;
    }


    private Map<String, String[]> map = new TreeMap<>();


    public SchedulePickerView(Context context) {
        super(context);
    }

    public SchedulePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SchedulePickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public List<Schedule> getSchedules() {
        return mSchedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        mSchedules = schedules;
        init();
    }

    private void init() {
        //初始化view

        //建立一个map，key为年份，value为学期数组
        Map<String, List<String>> tempMap = new TreeMap<>();

        for (Schedule schedule : mSchedules) {
            List<String> terms = tempMap.get(schedule.getYear());
            if (terms == null) {
                terms = new ArrayList<>();
                terms.add(String.valueOf(schedule.getTerm()));
                tempMap.put(schedule.getYear(), terms);
            } else {
                terms.add(String.valueOf(schedule.getTerm()));
            }
        }
        //将刚刚的list的value转换为String[]

        for (String key : tempMap.keySet()) {
            List<String> list = tempMap.get(key);
            Collections.sort(list);
            map.put(key, list.toArray(new String[0]));
        }

        //初始化NumberPicker


        NumberPicker.LayoutParams lp1 = new NumberPicker.LayoutParams(0, NumberPicker.LayoutParams.MATCH_PARENT, 1.0f);
        lp1.setMargins(10, 10, 10, 10);
        np1.setLayoutParams(lp1);
        np1.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        mYears = map.keySet().toArray(new String[0]);
        np1.setDisplayedValues(mYears);
        np1.setMinValue(0);
        np1.setMaxValue(mYears.length - 1);


        NumberPicker.LayoutParams lp2 = new NumberPicker.LayoutParams(0, NumberPicker.LayoutParams.MATCH_PARENT, 1.0f);
        lp2.setMargins(10, 10, 10, 10);
        np2.setLayoutParams(lp2);
        np2.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);


        setYear(mYears.length - 1);

        setTerm(mTerms.length - 1);

        //年份改变时只显示当年课表有的学期（实现二级联动）
        np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {

                mTerms = map.get(mYears[i1]);
                np2.setDisplayedValues(null);
                np2.setMinValue(0);
                np2.setMaxValue(mTerms.length - 1);
                np2.setDisplayedValues(mTerms);
                mYear = mYears[i1];

                setTerm(mTerms.length - 1);
            }
        });

        np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mTerm = mTerms[newVal];
            }
        });

        addView(np1);
        addView(np2);
    }


}
