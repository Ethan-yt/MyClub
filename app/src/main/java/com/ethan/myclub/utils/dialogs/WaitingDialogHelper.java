package com.ethan.myclub.utils.dialogs;


import android.app.ProgressDialog;
import android.content.Context;

import com.ethan.myclub.utils.Utils;

/**
 * Created by ethan on 2017/3/4.
 * 没有考虑线程安全
 */

public class WaitingDialogHelper {

    private static ProgressDialog mProgressDialog;
    private WaitingDialogHelper(){

    }

    public static ProgressDialog getInstance() {
        if (mProgressDialog == null)
            throw new RuntimeException("还没有启动ProgressDialog");
        return mProgressDialog;
    }

    public static void show(Context context,String message)
    {
        Utils.hideKeyboard(context);//隐藏
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setTitle("请稍候");
        dialog.setMessage(message);
        dialog.show();
        mProgressDialog = dialog;
    }

    public static void dismiss()
    {
        mProgressDialog.dismiss();
        mProgressDialog = null;
    }

}
