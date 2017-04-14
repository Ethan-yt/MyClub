package com.ethan.myclub.club.member.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.ethan.myclub.util.Utils;

/**
 * Created by ethan on 2017/4/14.
 */

public class MySwipeRefreshLayout extends SwipeRefreshLayout {
    public MySwipeRefreshLayout(Context context) {
        super(context);
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean isInCheckBox = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isInCheckBox = (this.getWidth() - ev.getX() < Utils.dp2px(getContext(), 60));
                if (isInCheckBox)
                    return getChildAt(0).dispatchTouchEvent(ev);
                else
                    return super.dispatchTouchEvent(ev);
            default:
                if (isInCheckBox)
                    return getChildAt(0).dispatchTouchEvent(ev);
                else
                    return super.dispatchTouchEvent(ev);
        }


    }
}
