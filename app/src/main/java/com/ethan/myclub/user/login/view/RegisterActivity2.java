package com.ethan.myclub.user.login.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.ethan.myclub.R;
import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.network.OAuthHelper;
import com.ethan.myclub.user.login.model.Token;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.main.MainActivity;
import com.ethan.myclub.main.SnackbarActivity;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RegisterActivity2 extends SnackbarActivity {

    private String mUsername;
    private CardView mBtnNext;
    private EditText mEtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    finishAfterTransition();
                else
                    finish();
            }
        });

        mUsername = this.getIntent().getStringExtra("username");
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mBtnNext = (CardView) findViewById(R.id.btn_next);
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OAuthHelper.getProxy(RegisterActivity2.this)
                        .register(mUsername, mEtPassword.getText().toString())
                        .subscribe(
                                new Observer<Token>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        showWaitingDialog("请稍候", "注册中");
                                    }

                                    @Override
                                    public void onNext(Token token) {
                                        Preferences.sToken = token;
                                        Intent intent = new Intent();
                                        intent.setClass(RegisterActivity2.this, MainActivity.class);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        showSnackbar("注册失败！" + e.getMessage());
                                        e.printStackTrace();
                                        dismissDialog();
                                    }

                                    @Override
                                    public void onComplete() {
                                        dismissDialog();
                                    }
                                });

            }
        });
    }

    @Override
    protected void setRootLayout() {
        mRootLayout = findViewById(R.id.container);
    }
}
