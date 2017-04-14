package com.ethan.myclub.club.member.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

//import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

public class ScrollCheckBoxRecyclerView extends RecyclerView {
    private static final String LOG_TAG = "CheckBoxRecyclerView";

    public static final int SCROLL_SPEED = 1;
    private int mPreFirstVisiblePosition = 0;
    private Rect mTouchFrame = null;
    private float mCheckBoxX = RecyclerView.NO_POSITION;
    private int mCheckBoxWidth = -1;
    private int mFirstVisiblePosition = -1;
    private int mLastVisiblePosition = -1;
    private int mStartPosition = -1;
    private int mPrePosition = -1;
    private boolean mIsNeedScrollCheck = false;
    private int mPreY = -1;
    private int mCount = 0;
    //Up is true, down is false
    private boolean mUpOrDown = false;
    private boolean mIsFirstMove = false;
    //这个是判断手指方向的正在下滑或者是正在上滑
    private boolean mIsDown = false;

    public boolean mIsSelectable = false;

    public ScrollCheckBoxRecyclerView(Context context) {
        super(context);
    }

    public ScrollCheckBoxRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollCheckBoxRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //这里使用dispatchTouchEvent，因为onTouchEvent容易被OnTouchListener截取
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mIsSelectable) {
            return super.dispatchTouchEvent(ev);
        }

        LayoutManager manager = getLayoutManager();
        //获取第一个和最后一个显示的Item对应的相对Position
        if (manager instanceof LinearLayoutManager) {
            mFirstVisiblePosition = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
            mLastVisiblePosition = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //获取按下时的位置,x,y
                int startX = (int) ev.getX();
                int startY = (int) ev.getY();
                int preX = startX;
                mPreY = startY;
                mPreFirstVisiblePosition = mFirstVisiblePosition;
                mPrePosition = mStartPosition = pointToPosition(startX, startY);

                if (mStartPosition > -1) {
                    //获取当前Item的View
                    View child = getChildAt(mStartPosition);
                    if (null != child) {
                        //获取响应域，一般响应域里面就是一个CheckBox
                        View tmpCheckBoxContainer = child.findViewWithTag("checkbox_layout");
                        if (null != tmpCheckBoxContainer && tmpCheckBoxContainer.getVisibility() == VISIBLE) {
                            mCheckBoxWidth = tmpCheckBoxContainer.getWidth();
                            //获取响应域的范围，一定要用这种获取绝对位置的方式，不然会受到padding或者是margin的影响
                            int[] location = new int[2];
                            tmpCheckBoxContainer.getLocationOnScreen(location);
                            mCheckBoxX = location[0];
                            //判断按下的位置是否是在响应域内
                            if (startX >= mCheckBoxX && startX <= (mCheckBoxX + mCheckBoxWidth)) {
                                //Log.d(LOG_TAG, "dispatchTouchEvent() DOWN mStartPosition: " + mStartPosition);
                                //设置截取事件的标志位
                                mIsNeedScrollCheck = true;
                                //设置为第一次滑动，这是用作判断折返的
                                mIsFirstMove = true;
                                setStartCheckBoxState();
                                //截获Checkbox的点击事件，防止两次选中
                                return true;
                            } else {
                                mIsNeedScrollCheck = false;
                            }
                        } else {
                            mIsNeedScrollCheck = false;
                            //Log.e(LOG_TAG, "dispatchTouchEvent() ", new Throwable("Cannot get CheckBoxContainer!"));
                        }

                    } else {
                        Log.e(LOG_TAG, "dispatchTouchEvent() ", new Throwable("Cannot get item view!"));
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //获取当前位置
                int currentX = (int) ev.getX();
                int currentY = (int) ev.getY();
                //获取当前的item
                int currentPosition = pointToPosition(currentX, currentY);
                //判断是否允许滑动选中
                if (mIsNeedScrollCheck && -1 != mFirstVisiblePosition && -1 != mLastVisiblePosition && -1 != currentPosition) {
                    //判断是否在下一个Item的像英语
                    if ((currentPosition + mFirstVisiblePosition) != (mPrePosition + mPreFirstVisiblePosition) &&
                            currentX >= mCheckBoxX && currentX <= (mCheckBoxX + mCheckBoxWidth)) {

//                        Log.i(LOG_TAG, "********************************** dispatchTouchEvent() ********************************");
//                        Log.d(LOG_TAG, "dispatchTouchEvent() MOVE mCurrentPosition: " + currentPosition);
//                        Log.d(LOG_TAG, "dispatchTouchEvent() MOVE mFirstVisiblePosition: " + mFirstVisiblePosition);
//                        Log.d(LOG_TAG, "dispatchTouchEvent() MOVE mPrePosition: " + mPrePosition);
//                        Log.d(LOG_TAG, "dispatchTouchEvent() MOVE mPreFirstVisiblePosition: " + mPreFirstVisiblePosition);
//                        Log.i(LOG_TAG, "********************************** dispatchTouchEvent() ********************************");

                        //折返回来时要改变前一个的Checkbox的状态

                        if (mIsFirstMove) {
                            mIsFirstMove = false;
                            if (currentY >= mPreY) {
                                mUpOrDown = false;
                            } else {
                                mUpOrDown = true;
                            }
                        } else {
                            if ((currentPosition + mFirstVisiblePosition) > (mPrePosition + mPreFirstVisiblePosition) && mUpOrDown) {
                                changeCheckBoxState(mPrePosition);
                                mUpOrDown = false;
                            } else if ((currentPosition + mFirstVisiblePosition) < (mPrePosition + mPreFirstVisiblePosition) && !mUpOrDown) {
                                changeCheckBoxState(mPrePosition);
                                mUpOrDown = true;
                            }
                        }


                        changeCheckBoxState(currentPosition);

                    }

                    //判断是否是在最后一个item上滑动，如果是则进行自动向下滑动，如果是在第一个上下滑动，则自动向上滑动
                    //Log.d(LOG_TAG, "dispatchTouchEvent() MOVE: " + (mLastVisiblePosition - mCurrentPosition - mFirstVisiblePosition));
                    if ((mLastVisiblePosition - mFirstVisiblePosition - currentPosition) < 1 && currentY > mPreY) {
                        //自动向下滑
                        Log.d(LOG_TAG, "dispatchTouchEvent() MOVE mCount: " + mCount);
                        View child = getChildAt(currentPosition);
                        if (null != child && 0 == mCount % 5) {
                            scrollToPosition(mLastVisiblePosition + 1);
                        }
                        mCount++;
                    } else if (currentPosition < 2 && currentY < mPreY) {
                        //自动向上滑
                        View child = getChildAt(currentPosition);
                        Log.d(LOG_TAG, "dispatchTouchEvent() MOVE mCount: " + mCount);
                        //mCount用于降低滑动的频率，频率太快容易滑动的看不清楚
                        if (null != child && 0 == mCount % 5) {
                            scrollToPosition(mFirstVisiblePosition - 1);
                        }
                        mCount++;
                    }
                    mPreY = currentY;
                    mPrePosition = currentPosition;
                    mPreFirstVisiblePosition = mFirstVisiblePosition;

                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mIsNeedScrollCheck) {
                    mCount = 0;
                    return false;

                }
                break;


        }
        return super.dispatchTouchEvent(ev);
    }


    //改变开始的CheckBox状态
    private void setStartCheckBoxState() {
        View child = getChildAt(mStartPosition);
        if (null != child) {
            ViewGroup checkBoxContainer = (ViewGroup) child.findViewWithTag("checkbox_layout");
            if (null != checkBoxContainer) {
                CheckBox checkBox = (CheckBox) checkBoxContainer.getChildAt(0);
                if (null != checkBox && checkBox.getVisibility() == VISIBLE && checkBox.isEnabled()) {
                    checkBox.toggle();
                }
            }

        }
    }

    //判断当前Item的Position，相对位置
    private int pointToPosition(int x, int y) {
        Rect frame = mTouchFrame;
        if (frame == null) {
            mTouchFrame = new Rect();
            frame = mTouchFrame;
        }

        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) {
                child.getHitRect(frame);
                if (frame.contains(x, y)) {
                    return i;
                }
            }
        }
        return -1;
    }

    //改变Position的选中状态
    public void changeCheckBoxState(int position) {
        if (position < 0 || position >= getChildCount()) {
            return;
        }

        View child = getChildAt(position);
        if (null != child) {
            ViewGroup checkBoxLayout = (ViewGroup) child.findViewWithTag("checkbox_layout");
            if (null != checkBoxLayout && checkBoxLayout.getVisibility() == VISIBLE) {
                CheckBox checkBox = (CheckBox) checkBoxLayout.getChildAt(0);
                if (null != checkBox && checkBox.isEnabled()) {
                    Log.d(LOG_TAG, "changeCheckBoxState() selectCheckBox: " + position);
                    //checkBox.performClick();
                    checkBox.toggle();
                    //checkBox.setClickable(false);
                    //checkBox.callOnClick();
                }
            }

        }
    }
}