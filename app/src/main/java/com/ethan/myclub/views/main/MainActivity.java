package com.ethan.myclub.views.main;

import android.content.Intent;
import android.graphics.Color;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.ethan.myclub.R;
import com.ethan.myclub.views.test;



public class MainActivity extends AppCompatActivity {

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

        navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu_5);
        navigationAdapter.setupWithBottomNavigation(bottomNavigation);

        bottomNavigation.setAccentColor(Color.parseColor("#80be58"));
        bottomNavigation.setInactiveColor(Color.parseColor("#4c7631"));

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (currentFragment == null) {
                    currentFragment = adapter.getCurrentFragment();
                    Intent intent=new Intent();
                    intent.setClass(MainActivity.this, test.class);
                    startActivity(intent);
                }

                if (wasSelected) {
                    currentFragment.refresh();
                    Intent intent=new Intent();
                    intent.setClass(MainActivity.this, test.class);
                    startActivity(intent);
                    return true;
                }

                if (currentFragment != null) {
                    currentFragment.willBeHidden();
                }

                viewPager.setCurrentItem(position, false);
                currentFragment = adapter.getCurrentFragment();
                currentFragment.willBeDisplayed();

                if (position == 1) {
                    bottomNavigation.setNotification("", 1);

                }

                return true;
            }
        });
        viewPager.setOffscreenPageLimit(4);

        adapter = new BaseViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        currentFragment = adapter.getCurrentFragment();

    }

}
