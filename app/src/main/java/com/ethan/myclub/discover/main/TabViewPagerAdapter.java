package com.ethan.myclub.discover.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ethan.myclub.discover.activity.ActivityFragment;
import com.ethan.myclub.discover.club.ClubFragment;
import com.ethan.myclub.discover.merchant.MerchantFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ethan on 2017/4/2.
 */

public class TabViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public TabViewPagerAdapter(FragmentManager manager) {
        super(manager);

        addFrag(new ActivityFragment(), "活 动");
        addFrag(new ClubFragment(), "社 团");
        addFrag(new MerchantFragment(),"商 家");
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}