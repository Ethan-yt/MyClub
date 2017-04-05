package com.ethan.myclub.club.operation.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.ethan.myclub.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ethan on 2017/4/5.
 */

public class ViewPagerAdapter extends PagerAdapter {
    private List<View> mViewList = new ArrayList<>();

    public ViewPagerAdapter(List<View> views) {
        mViewList = views;
    }

    @Override
    public int getCount() {
        return mViewList != null ? mViewList.size() : 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
