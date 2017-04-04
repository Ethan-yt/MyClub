package com.ethan.myclub.user.login.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ethan.myclub.R;
import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.network.OAuthHelper;
import com.ethan.myclub.user.login.model.Token;
import com.ethan.myclub.main.MainActivity;
import com.ethan.myclub.main.BaseActivity;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class RegisterActivity2 extends BaseActivity {

    private String mUsername;
    private Button mBtnNext;
    private EditText mEtPassword;
    private EditText mEtNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register2);

        getToolbarWrapper()
                .setTitle("注册")
                .showBackIcon()
                .show();

        mUsername = this.getIntent().getStringExtra("username");
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mEtNickname = (EditText) findViewById(R.id.et_nickname);
        mBtnNext = (Button) findViewById(R.id.btn_next);


        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OAuthHelper.getProxy(RegisterActivity2.this)
                        .register(mUsername, mEtPassword.getText().toString(), mEtNickname.getText().toString(), Preferences.sPushRegID)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Token>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                showWaitingDialog("请稍候", "注册中", d);
                            }

                            @Override
                            public void onNext(Token token) {
                                Preferences.setToken(RegisterActivity2.this, token);
                                MainActivity.startActivity(RegisterActivity2.this, BaseActivity.REQUEST_REGISTER, BaseActivity.RESULT_OK);
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

    public static void startActivity(Activity activity, String username, Bundle bundle) {
        Intent intent = new Intent(activity, RegisterActivity2.class);
        intent.putExtra("username", username);
        ActivityCompat.startActivity(activity, intent, bundle);
    }

}
