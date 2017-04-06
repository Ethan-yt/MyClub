package com.ethan.myclub.club.operation.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;

import com.ethan.myclub.main.BaseActivity;

/**
 * Created by ethan on 2017/4/5.
 */

public class Operation {
    public Class<?> mActivity;
    public String mName;
    @DrawableRes
    public int mIconId;

    public Operation(Class<?> activity, String name, int iconId) {
        mActivity = activity;
        mName = name;
        mIconId = iconId;
    }
}
