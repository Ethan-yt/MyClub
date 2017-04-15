package com.ethan.myclub.main;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.ethan.myclub.R;

/**
 * Created by ethan on 2017/4/15.
 */

public class ToolbarWrapper {
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    @MenuRes
    public int mMenuResId = -1;
    public BaseActivity.OnFinishCreateMenu mOnFinishCreateMenu;
    private boolean mIsScroll = false;
    private boolean mIsAnimate = false;
    private BaseActivity mBaseActivity;

    private ToolbarWrapper(Builder builder) {
        mAppBarLayout = builder.mAppBarLayout;
        mToolbar = builder.mToolbar;
        mMenuResId = builder.mMenuResId;
        mOnFinishCreateMenu = builder.mOnFinishCreateMenu;
        mIsScroll = builder.mIsScroll;
        mIsAnimate = builder.mIsAnimate;
        mBaseActivity = builder.mBaseActivity;
    }

    public void dismiss() {
        if (mAppBarLayout != null) {
            if (mIsAnimate) {
                Animation out = AnimationUtils.loadAnimation(mBaseActivity, android.R.anim.fade_out);
                mAppBarLayout.startAnimation(out);
            }
            mBaseActivity.mRootLayout.removeView(mAppBarLayout);
            mBaseActivity.setToolbarWrapper(null);
        }
    }

    public void changeColor(int color) {
        mAppBarLayout.setBackgroundColor(color);
        mToolbar.setBackgroundColor(color);
    }

    public void changeScrollable(boolean flag) {
        AppBarLayout.LayoutParams params =
                (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        if (flag && !mIsScroll) {
            mIsScroll = true;
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
            return;
        }
        if (!flag && mIsScroll) {
            params.setScrollFlags(0);
        }
    }


    public void close() {
        close(true);
    }

    public void close(boolean animate) {
        mAppBarLayout.setExpanded(false, animate);
    }


    public static final class Builder {
        private AppBarLayout mAppBarLayout;
        private Toolbar mToolbar;
        @MenuRes
        private int mMenuResId = -1;
        private Toolbar.OnMenuItemClickListener mOnMenuItemClickListener;
        private BaseActivity.OnFinishCreateMenu mOnFinishCreateMenu;
        private View.OnClickListener mNavOnClickListener;

        private String mTitle = "";
        private boolean mIsTitleInCenter = false;
        private boolean mIsAnimate = false;
        private boolean mIsScroll = false;
        private boolean mIsTransparent = false;
        private int mTextColor;
        private boolean mIsUseColor = false;
        private boolean mIsToolbarFitsSystemWindows = false;
        private ViewGroup mTarget;
        private Drawable mNavIcon;
        private BaseActivity mBaseActivity;

        public Builder(BaseActivity baseActivity) {
            mBaseActivity = baseActivity;
            if (mBaseActivity.mRootLayout == null)
                throw new RuntimeException("You must call setContentView before init Toolbar!");
        }

        public Builder setTitle(String title) {
            setTitle(title, false);
            return this;
        }

        public Builder transparent() {
            mIsTransparent = true;
            return this;
        }

        public Builder target(ViewGroup target) {
            mTarget = target;
            return this;
        }

        public Builder setTitle(String title, boolean isCenter) {
            mTitle = title;
            mIsTitleInCenter = isCenter;
            return this;
        }

        public Builder withAnimate() {
            mIsAnimate = true;
            return this;
        }

        public Builder setScrollable() {
            mIsScroll = true;
            return this;
        }

        public Builder setToolbarFitsSystemWindows() {
            mIsToolbarFitsSystemWindows = true;
            return this;
        }

        public Builder setMenu(@MenuRes int resId, Toolbar.OnMenuItemClickListener onMenuItemClickListener) {
            return setMenu(resId, onMenuItemClickListener, null);
        }

        public Builder setMenu(@MenuRes int resId, Toolbar.OnMenuItemClickListener onMenuItemClickListener, BaseActivity.OnFinishCreateMenu onFinishCreateMenu) {
            mMenuResId = resId;
            mOnMenuItemClickListener = onMenuItemClickListener;
            mOnFinishCreateMenu = onFinishCreateMenu;
            return this;
        }

        public Builder showBackIcon() {
            final TypedValue typedValueAttr = new TypedValue();
            mBaseActivity.getTheme().resolveAttribute(R.attr.homeAsUpIndicator, typedValueAttr, true);
            return showNavIcon(typedValueAttr.resourceId, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBaseActivity.onBackPressed();
                }
            });
        }

        public Builder showNavIcon(@DrawableRes int navIconId, View.OnClickListener onClickListener) {
            mNavIcon = ContextCompat.getDrawable(mBaseActivity, navIconId);
            mNavOnClickListener = onClickListener;
            return this;
        }

        public Builder setTextColor(int color) {
            mIsUseColor = true;
            mTextColor = color;
            return this;
        }

        public void show() {
            show(-1);
        }

        public void show(@LayoutRes int layoutResId) {
            if (mBaseActivity.getToolbarWrapper() != null)
                mBaseActivity.getToolbarWrapper().dismiss();
            if (layoutResId == -1)
                layoutResId = R.layout.view_toolbar;
            if (mTarget == null)
                mTarget = mBaseActivity.mRootLayout;

            ViewGroup v = (ViewGroup) View.inflate(mBaseActivity, layoutResId, mTarget);

            mToolbar = (Toolbar) v.findViewById(R.id.toolbar);
            mAppBarLayout = (AppBarLayout) v.findViewById(R.id.app_bar_layout);

            //标题居中
            if (mIsTitleInCenter) {
                TextView tv = (TextView) v.findViewById(R.id.center_title);
                tv.setText(mTitle);
                mToolbar.setTitle("");
                if (mIsUseColor)
                    tv.setTextColor(mTextColor);
            } else {
                mToolbar.setTitle(mTitle);
                if (mIsUseColor) {
                    mToolbar.setTitleTextColor(mTextColor);
                }
            }
            //背景全透明
            if (mIsTransparent) {
                mAppBarLayout.setBackgroundColor(Color.TRANSPARENT);
                mToolbar.setBackgroundColor(Color.TRANSPARENT);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    StateListAnimator stateListAnimator = new StateListAnimator();
                    stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(mAppBarLayout, "elevation", 0));
                    mAppBarLayout.setStateListAnimator(stateListAnimator);

                }
            }
            //可以被CoordinatorLayout控制滑动
            if (mIsScroll) {
                AppBarLayout.LayoutParams params =
                        (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
            }
            //设定toolbar的padding，留出状态栏的高度
            if (mIsToolbarFitsSystemWindows) {
                mToolbar.setFitsSystemWindows(true);
                mBaseActivity.setSupportActionBar(mToolbar);
                ViewCompat.requestApplyInsets(mBaseActivity.mRootLayout);
            }
            //设置菜单
            if (mMenuResId != -1 && mOnMenuItemClickListener != null) {
                if (mIsToolbarFitsSystemWindows) {
                    mToolbar.setOnMenuItemClickListener(mOnMenuItemClickListener);
                } else {
                    mToolbar.inflateMenu(mMenuResId);
                    mToolbar.setOnMenuItemClickListener(mOnMenuItemClickListener);
                }
            }
            //左上角按钮
            if (mNavIcon != null) {
                if (mIsUseColor)
                    mNavIcon.setColorFilter(mTextColor, PorterDuff.Mode.SRC_ATOP);
                mToolbar.setNavigationIcon(mNavIcon);
                mToolbar.setNavigationOnClickListener(mNavOnClickListener);
            }
            //带动画进入
            if (mIsAnimate) {
                Animation in = AnimationUtils.loadAnimation(mBaseActivity, android.R.anim.fade_in);
                mAppBarLayout.startAnimation(in);
            }

            mBaseActivity.setToolbarWrapper(new ToolbarWrapper(this));
        }
    }
}
