package com.ethan.myclub.club.operation.view.springindicator;

/**
 * Created by ethan on 2017/4/5.
 */

public interface TabClickListener {

    /**
     * Handle click event when tab is clicked.
     *
     * @param position ViewPager item position.
     * @return Call setCurrentItem if return true.
     */
    public boolean onTabClick(int position);

}