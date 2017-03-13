package com.ethan.myclub.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ethan.myclub.R;
import com.ethan.myclub.views.user.login.LoginActivity;
import com.ethan.myclub.views.user.login.RegisterActivity;

/**
 * Created by ethan on 2017/3/5.
 */

public class Utils {
    static public void hideKeyboard(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            View view = activity.getCurrentFocus();
            if (view != null) {
                ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).
                        hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static int Str2Int(String s) throws Exception {
        java.util.Map<String, Integer> numMap = new java.util.HashMap<String, Integer>();
        numMap.put("零", 0);
        numMap.put("一", 1);
        numMap.put("二", 2);
        numMap.put("三", 3);
        numMap.put("四", 4);
        numMap.put("五", 5);
        numMap.put("六", 6);
        numMap.put("七", 7);
        numMap.put("八", 8);
        numMap.put("九", 9);
        Integer ret = numMap.get(s);
        if (ret != null)
            return ret;
        throw new Exception("数字转换失败");
    }

    public static void showLoginAlert(View rootLayout) {
        final Context context = rootLayout.getContext();
        Snackbar.make(rootLayout, "您还没有登录", Snackbar.LENGTH_LONG)
                .setAction("登录", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(context, LoginActivity.class);
                        context.startActivity(intent);
                    }
                })
                .show();

    }
}
