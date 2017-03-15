package com.ethan.myclub.views.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ethan.myclub.R;
import com.ethan.myclub.utils.Utils;

import io.reactivex.disposables.Disposable;

public abstract class SnackbarActivity extends AppCompatActivity {

    public static final int REQUEST_LOGIN = 1;

    protected View mRootLayout;

    public void showSnackbar(String text) {
        showSnackbar(text, null, null);
    }

    public void showSnackbar(String text, String actionText, View.OnClickListener onClickListener) {
        if (mRootLayout == null)
            setRootLayout();
        Snackbar snackbar = Snackbar.make(mRootLayout, text, Snackbar.LENGTH_LONG);
        if (onClickListener != null)
            snackbar.setAction(actionText, onClickListener);
        snackbar.show();
    }

    protected abstract void setRootLayout();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                showSnackbar("登录成功！");
            }
        }
    }

    protected ProgressDialog mProgressDialog;
    protected Disposable mDisposable;

    private void showDialog(String tittle, String message, Disposable disposable, int style) {

        hideKeyboard();//隐藏
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(style);// 设置进度条的形式为圆形转动的进度条
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setTitle(tittle);
        dialog.setMessage(message);
        if (disposable != null) {
            mDisposable = disposable;
        }

        if(mDisposable != null)
        {
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
        }
        else
            dialog.setCancelable(false);

        mProgressDialog = dialog;
        mProgressDialog.show();
    }


    protected void showWaitingDialog(String tittle, String message, Disposable d) {
        showDialog(tittle, message, d, ProgressDialog.STYLE_SPINNER);
    }

    protected void showWaitingDialog(String tittle, String message) {
        showWaitingDialog(tittle, message, null);
    }

    protected void showProgressDialog(String tittle, String message, Disposable d) {
        showDialog(tittle, message, d, ProgressDialog.STYLE_HORIZONTAL);
    }

    protected void showProgressDialog(String tittle, String message) {
        showProgressDialog(tittle, message, null);
    }

    protected void dismissDialog() {
        mProgressDialog.dismiss();
        mProgressDialog = null;
        mDisposable = null;
    }

    protected void cancelCurrentProgress() {
        if (mDisposable != null)
            if (!mDisposable.isDisposed())
                mDisposable.dispose();
    }

    protected void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
