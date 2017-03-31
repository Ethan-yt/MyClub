package com.ethan.myclub.main;

import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.ContextCompat;


import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.ethan.myclub.R;
import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.user.login.view.LoginActivity;
import com.ethan.myclub.user.main.view.UserFragment;

public class MainActivity extends BaseActivity {

    private BaseFragment currentFragment;
    private BaseViewPagerAdapter adapter;
    private AHBottomNavigationAdapter navigationAdapter;
    // UI
    private AHBottomNavigationViewPager viewPager;

    private AHBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            String FRAGMENTS_TAG = "android:support:fragments";
            // remove掉保存的Fragment
            savedInstanceState.remove(FRAGMENTS_TAG);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    private void initUI() {
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        viewPager = (AHBottomNavigationViewPager) findViewById(R.id.view_pager);

        navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu);
        navigationAdapter.setupWithBottomNavigation(bottomNavigation);

        bottomNavigation.setInactiveColor(ContextCompat.getColor(this, R.color.colorBottomNavigationInactive));
        bottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.colorAccent));

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
                //切换到个人页面前
                if (position == 2) {
                    //检查是否已经登录
                    if (!Preferences.sIsLogin.get()) {
                        showLoginSnackbar("您还没有登录哦");
                        return false;
                    }
                }

                if (currentFragment != null) {
                    currentFragment.willBeHidden();
                }

                viewPager.setCurrentItem(position, false);
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

        bottomNavigation.setCurrentItem(1);// TODO: 2017/3/13  删除这行测试语句
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果登录/注册成功，刷新个人信息
        if (resultCode == RESULT_OK) {
            if (viewPager.getCurrentItem() == 1) {
                adapter.getItem(1).willBeDisplayed();
            }
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onActivityResult(intent.getIntExtra("RequestCode", 0), intent.getIntExtra("ResultCode", 0), intent);
    }
}
