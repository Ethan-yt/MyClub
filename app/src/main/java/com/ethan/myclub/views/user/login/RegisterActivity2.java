package com.ethan.myclub.views.user.login;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;

import com.ethan.myclub.R;
import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.models.network.Response;
import com.ethan.myclub.models.network.Token;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.network.Transformers;
import com.ethan.myclub.utils.dialogs.WaitingDialogHelper;
import com.ethan.myclub.views.main.MainActivity;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RegisterActivity2 extends AppCompatActivity {

    private String mUsername;
    private CardView mBtnNext;
    private EditText mEtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        mUsername = this.getIntent().getStringExtra("username");
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mBtnNext = (CardView) findViewById(R.id.btn_next);
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiHelper.getInstance()
                        .register(mUsername, mEtPassword.getText().toString())
                        .compose(new Transformers.SchedulersSwitcher<Response<Token>>())
                        .compose(new Transformers.sTransformer<Token>())
                        .subscribe(
                                new Observer<Token>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        WaitingDialogHelper.show(RegisterActivity2.this, "注册中");
                                    }

                                    @Override
                                    public void onNext(Token token) {
                                        Preferences.token = token.token;
                                        Intent intent = new Intent();
                                        intent.setClass(RegisterActivity2.this, MainActivity.class);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Snackbar.make(findViewById(R.id.container), "注册失败！" + e.getMessage(), Snackbar.LENGTH_LONG).show();
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
}
