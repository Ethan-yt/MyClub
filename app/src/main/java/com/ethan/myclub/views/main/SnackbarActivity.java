package com.ethan.myclub.views.main;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ethan.myclub.R;

public abstract class SnackbarActivity extends AppCompatActivity {

    public static final int REQUEST_LOGIN = 1;

    protected View mRootLayout;

    public void showSnackbar(String text) {
        showSnackbar(text, null, null);
    }

    public void showSnackbar(String text, String actionText, View.OnClickListener onClickListener) {
        if(mRootLayout == null)
            setRootLayout();
        Snackbar snackbar = Snackbar.make(mRootLayout, text, Snackbar.LENGTH_LONG);
        if(onClickListener != null)
            snackbar.setAction(actionText, onClickListener);
        snackbar.show();
    }

    protected abstract void setRootLayout();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_LOGIN)
        {
            if(resultCode == RESULT_OK)
            {
                showSnackbar("登录成功！");
            }
        }
    }
}
