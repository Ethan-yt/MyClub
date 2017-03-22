package com.ethan.myclub.main;

import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;


import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.ethan.myclub.R;
import com.ethan.myclub.util.CacheUtil;


public class MainActivity extends SnackbarActivity {

    private BaseFragment currentFragment;
    private BaseViewPagerAdapter adapter;
    private AHBottomNavigationAdapter navigationAdapter;
    // UI
    private AHBottomNavigationViewPager viewPager;

    private AHBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initUI();

    }

    private void initUI() {
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        viewPager = (AHBottomNavigationViewPager) findViewById(R.id.view_pager);

        navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu);
        navigationAdapter.setupWithBottomNavigation(bottomNavigation);

        bottomNavigation.setAccentColor(Color.parseColor("#80be58"));
        bottomNavigation.setInactiveColor(Color.parseColor("#4c7631"));

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (currentFragment == null) {
                    currentFragment = adapter.getCurrentFragment();
                }

                if (wasSelected) {
                    currentFragment.refresh();
                    return true;
                }

                if (currentFragment != null) {
                    currentFragment.willBeHidden();
                }

                viewPager.setCurrentItem(position, false);
                //currentFragment = adapter.getCurrentFragment();
                currentFragment = adapter.getItem(position);
                currentFragment.willBeDisplayed();

//                if (position == 1) {
//                    bottomNavigation.setNotification("", 1);
//
//                }

                return true;
            }
        });
        viewPager.setOffscreenPageLimit(4);

        adapter = new BaseViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        currentFragment = adapter.getCurrentFragment();
        bottomNavigation.setCurrentItem(2);// TODO: 2017/3/13  删除这行测试语句
    }

    @Override
    protected void setRootLayout() {
        mRootLayout = findViewById(R.id.container);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果登录/注册成功，刷新个人信息
        if (resultCode == RESULT_OK) {
            if (viewPager.getCurrentItem() == 2) {
                adapter.getItem(2).willBeDisplayed();
            }
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onActivityResult(intent.getIntExtra("RequestCode", 0), intent.getIntExtra("ResultCode", 0), intent);
    }
}
