package com.ethan.myclub.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.ethan.myclub.R;
import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.user.login.view.LoginActivity;
import com.ethan.myclub.util.CacheUtil;

import io.reactivex.disposables.Disposable;

public abstract class BaseActivity extends AppCompatActivity {

    public static final int REQUEST_LOGIN = 10304;
    public static final int REQUEST_REGISTER = 10305;

    protected ViewGroup mRootLayout;
    private ToolbarWrapper mToolbarWrapper;

    public void showSnackbar(String text) {
        showSnackbar(text, null, null);
    }

    public void showSnackbar(String text, String actionText, View.OnClickListener onClickListener) {
        Snackbar snackbar = Snackbar.make(mRootLayout, text, Snackbar.LENGTH_LONG);
        if (onClickListener != null)
            snackbar.setAction(actionText, onClickListener);
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_LOGIN) {
                showSnackbar("登录成功！");
            }
            if (requestCode == REQUEST_REGISTER) {
                showSnackbar("注册成功！已经帮您自动登录！");
            }
            if (requestCode == REQUEST_REGISTER || requestCode == REQUEST_LOGIN)
                CacheUtil.get(this).remove(Preferences.CACHE_USER_INFO);//登录或者注册成功，清除缓存
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void setContentView(int layoutId) {
        View view = View.inflate(this, layoutId, null);
        setRootLayout(view);
        super.setContentView(view);
    }

    @Override
    public void setContentView(View view) {
        setRootLayout(view);
        super.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        setRootLayout(view);
        super.setContentView(view, params);
    }

    private void setRootLayout(View view) {
        if (!(view instanceof ViewGroup))
            throw new RuntimeException("Content view of a BaseActivity must be a ViewGroup!");
        mRootLayout = (ViewGroup) view;
//        View decorView = getWindow().getDecorView();
//        ViewGroup contentView = (ViewGroup) decorView.findViewById(android.R.id.content);
//        final View childView = contentView.getChildAt(0);

    }


    public ToolbarWrapper getToolbarWrapper() {
        if (mToolbarWrapper == null)
            mToolbarWrapper = new ToolbarWrapper();
        return mToolbarWrapper;
    }

    public class ToolbarWrapper {
        private AppBarLayout mAppBarLayout;
        private Toolbar mToolbar;
        @MenuRes
        private int mMenuResId = -1;
        private String mTitle;
        private boolean mIsTitleInCenter = false;
        private Toolbar.OnMenuItemClickListener mOnMenuItemClickListener;
        private OnFinishCreateMenu mOnFinishCreateMenu;
        @DrawableRes
        private int mNavIconId = -1;
        private View.OnClickListener mNavOnClickListener;
        private boolean mIsAnimate = false;


        private ToolbarWrapper() {
            if (mRootLayout == null)
                throw new RuntimeException("You must call setContentView before init Toolbar!");
        }

        public ToolbarWrapper dismiss() {
            if (mAppBarLayout != null) {
                if(mIsAnimate)
                {
                    Animation out = AnimationUtils.loadAnimation(BaseActivity.this, android.R.anim.fade_out);
                    mAppBarLayout.startAnimation(out);
                }
                mRootLayout.removeView(mAppBarLayout);
                mToolbarWrapper = new ToolbarWrapper();
            }
            return mToolbarWrapper;
        }

        public ToolbarWrapper setTitle(String title) {
            setTitle(title, false);
            return this;
        }

        public ToolbarWrapper setTitle(String title, boolean isCenter) {
            mTitle = title;
            mIsTitleInCenter = isCenter;
            return this;
        }
        public ToolbarWrapper withAnimate() {
            mIsAnimate = true;
            return this;
        }
        public ToolbarWrapper setMenu(@MenuRes int resId, Toolbar.OnMenuItemClickListener onMenuItemClickListener) {
            return setMenu(resId, onMenuItemClickListener, null);
        }

        public ToolbarWrapper setMenu(@MenuRes int resId, Toolbar.OnMenuItemClickListener onMenuItemClickListener, OnFinishCreateMenu onFinishCreateMenu) {
            mMenuResId = resId;
            mOnMenuItemClickListener = onMenuItemClickListener;
            mOnFinishCreateMenu = onFinishCreateMenu;
            return this;
        }

        public ToolbarWrapper showBackIcon() {
            final TypedValue typedValueAttr = new TypedValue();
            getTheme().resolveAttribute(R.attr.homeAsUpIndicator, typedValueAttr, true);
            return showNavIcon(typedValueAttr.resourceId, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        public ToolbarWrapper showNavIcon(@DrawableRes int navIconId, View.OnClickListener onClickListener) {
            mNavIconId = navIconId;
            mNavOnClickListener = onClickListener;
            return this;
        }

        public void show() {
            show(-1);
        }

        public void show(@LayoutRes int layoutResId) {
//            if (mIsNeedMoveFirstChildDown) {
//                int childCount = mRootLayout.getChildCount();
//                if (childCount == 0)
//                    throw new RuntimeException("Root view group must have a child to move down!");
//                View child = mRootLayout.getChildAt(0);
//                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
//
//                TypedValue typedValue = new TypedValue();
//                getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true);
//                layoutParams.topMargin = getResources().getDimensionPixelSize(typedValue.resourceId);
//                child.setLayoutParams(layoutParams);
//            }

            if (layoutResId == -1)
                layoutResId = R.layout.view_toolbar;
            ViewGroup v = (ViewGroup) View.inflate(BaseActivity.this, layoutResId, mRootLayout);
            ViewCompat.requestApplyInsets(mRootLayout);
            mToolbar = (Toolbar) v.findViewById(R.id.toolbar);

            mAppBarLayout = (AppBarLayout) v.findViewById(R.id.app_bar_layout);
            if (mIsTitleInCenter) {
                TextView tv = (TextView) v.findViewById(R.id.center_title);
                tv.setText(mTitle);
                mToolbar.setTitle("");
            } else
                mToolbar.setTitle(mTitle);

            setSupportActionBar(mToolbar);


            if (mMenuResId != -1 && mOnMenuItemClickListener != null) {
                mToolbar.setOnMenuItemClickListener(mOnMenuItemClickListener);
            }

            if (mNavIconId != -1) {
                mToolbar.setNavigationIcon(mNavIconId);
                mToolbar.setNavigationOnClickListener(mNavOnClickListener);
            }
            if(mIsAnimate)
            {
                Animation in = AnimationUtils.loadAnimation(BaseActivity.this, android.R.anim.fade_in);
                mAppBarLayout.startAnimation(in);
            }
        }

        public void close() {
            close(true);
        }

        public void close(boolean animate) {
            mAppBarLayout.setExpanded(false, animate);
        }


    }

    public interface OnFinishCreateMenu {
        void onFinish(Menu menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mToolbarWrapper != null && mToolbarWrapper.mMenuResId != -1) {
            getMenuInflater().inflate(mToolbarWrapper.mMenuResId, menu);
            if (mToolbarWrapper.mOnFinishCreateMenu != null)
                mToolbarWrapper.mOnFinishCreateMenu.onFinish(menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    public ProgressDialog mProgressDialog;
    public Disposable mDisposable;

    private void showDialog(String title, String message, Disposable disposable, int style) {

        hideKeyboard();//隐藏
        if(mProgressDialog != null)
            dismissDialog();
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(style);// 设置进度条的形式为圆形转动的进度条
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setTitle(title);
        dialog.setMessage(message);
        if (disposable != null) {
            mDisposable = disposable;
        }

        if (mDisposable != null) {
            dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelCurrentProgress();
                }
            });
            // 监听cancel事件
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    cancelCurrentProgress();
                    //点击back
                }
            });
        } else
            dialog.setCancelable(false);

        mProgressDialog = dialog;
        mProgressDialog.show();
    }


    public void showWaitingDialog(String title, String message, Disposable d) {
        showDialog(title, message, d, ProgressDialog.STYLE_SPINNER);
    }

    public void showWaitingDialog(String title, String message) {
        showWaitingDialog(title, message, null);
    }

    public void showProgressDialog(String title, String message, Disposable d) {
        showDialog(title, message, d, ProgressDialog.STYLE_HORIZONTAL);
    }

    public void showProgressDialog(String title, String message) {
        showProgressDialog(title, message, null);
    }

    public void dismissDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
            mDisposable = null;
        }
    }

    public void cancelCurrentProgress() {
        if (mDisposable != null)
            if (!mDisposable.isDisposed())
                mDisposable.dispose();
    }

    public void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void showLoginSnackbar(String message) {
        showSnackbar(message, "登录", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginActivity();
            }
        });
    }

    public void startLoginActivity() {
        LoginActivity.startActivityForResult(this, BaseActivity.REQUEST_LOGIN);
    }

}
