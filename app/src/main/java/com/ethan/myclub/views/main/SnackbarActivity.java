package com.ethan.myclub.views.main;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ethan.myclub.R;

public abstract class SnackbarActivity extends AppCompatActivity {

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

}
