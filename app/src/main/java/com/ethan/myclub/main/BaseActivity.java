package com.ethan.myclub.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.ethan.myclub.schedule.view.ScheduleActivity;
import com.ethan.myclub.user.login.view.LoginActivity;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import io.reactivex.disposables.Disposable;

public abstract class BaseActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static final int REQUEST_LOGIN = 10204;
    public static final int REQUEST_REGISTER = 10205;
    public static final int REQUEST_LOGOUT = 10206;

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
            if (requestCode == REQUEST_LOGIN || requestCode == REQUEST_REGISTER || requestCode == REQUEST_LOGOUT) {
                MainActivity.needUpdateFlag.userProfile = true;
                MainActivity.needUpdateFlag.clubList = true;
                MainActivity.needUpdateFlag.userUnreadCount = true;

                deleteSchedule();
            }

        }
    }

    private void deleteSchedule() {
        File file = new File(getFilesDir(), ScheduleActivity.FILE_NAME_SCHEDULE);
        if (file.isFile() && file.exists())
            file.delete();
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

    }

    public ToolbarWrapper getToolbarWrapper() {
        return mToolbarWrapper;
    }

    public void setToolbarWrapper(ToolbarWrapper toolbarWrapper) {
        mToolbarWrapper = toolbarWrapper;
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
        if (mProgressDialog != null)
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

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
