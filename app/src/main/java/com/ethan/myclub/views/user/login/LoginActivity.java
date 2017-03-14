package com.ethan.myclub.views.user.login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ethan.myclub.R;
import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.network.Transformers;
import com.ethan.myclub.models.network.Response;
import com.ethan.myclub.models.network.Token;
import com.ethan.myclub.utils.dialogs.WaitingDialogHelper;
import com.ethan.myclub.views.main.SnackbarActivity;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LoginActivity extends SnackbarActivity {

    private TextView mBtnRegister;
    private CardView mCvInput;
    private EditText mEtUsername;
    private EditText mEtPassword;
    private CardView mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


//        mApiService = retrofit.create(ApiService.class);


        mBtnRegister = (TextView) findViewById(R.id.btn_register);
        mCvInput = (CardView) findViewById(R.id.cv_input);
        mEtUsername = (EditText) findViewById(R.id.et_username);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mBtnLogin = (CardView) findViewById(R.id.btn_login);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);

                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(LoginActivity.this,
                                Pair.create((View) mCvInput, "trans_cv_input"),
                                Pair.create((View) mBtnLogin, "trans_btn_next"));
//                Pair.create((View) mEtUsername, "trans_et_1"),
//                Pair.create((View) mEtPassword, "trans_et_2")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ApiHelper.getInstance()
                        .login(mEtUsername.getText().toString(), mEtPassword.getText().toString())
                        .compose(new Transformers.SchedulersSwitcher<Response<Token>>())
                        .compose(new Transformers.sTransformer<Token>())
                        .subscribe(
                                new Observer<Token>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                        WaitingDialogHelper.show(LoginActivity.this, "登录中");

                                    }

                                    @Override
                                    public void onNext(Token token) {
                                        Preferences.sToken = token.token;
                                        finish();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        showSnackbar("登录失败！" + e.getMessage());
                                        e.printStackTrace();
                                        WaitingDialogHelper.dismiss();
                                    }

                                    @Override
                                    public void onComplete() {
                                        WaitingDialogHelper.dismiss();
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
