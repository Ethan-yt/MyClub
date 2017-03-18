package com.ethan.myclub.user.login;

import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;

import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.user.login.models.Token;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ethan on 2017/3/18.
 */

public class LoginViewModel extends BaseObservable {

    private LoginActivity mView;
    public ObservableField<String> userName = new ObservableField<>();
    public ObservableField<String> password = new ObservableField<>();


    public LoginViewModel(LoginActivity loginActivity) {
        mView = loginActivity;

    }

    public void register() {
        Intent intent = new Intent();
        intent.setClass(mView, RegisterActivity.class);
        @SuppressWarnings("unchecked")
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(mView,
                        Pair.create((View) mView.mBinding.cvInput, "trans_cv_input"),
                        Pair.create((View) mView.mBinding.btnLogin, "trans_btn_next"));
        ActivityCompat.startActivity(mView, intent, options.toBundle());
    }

    public void login() {
        ApiHelper.getProxyWithoutToken(mView)
                .login(userName.get(), password.get())
                .subscribe(
                        new Observer<Token>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                mView.showWaitingDialog("请稍候", "登录中");
                            }

                            @Override
                            public void onNext(Token token) {
                                Preferences.sToken = token.token;
                                mView.setResult(RESULT_OK);
                                mView.finish();
                            }

                            @Override
                            public void onError(Throwable e) {
                                mView.showSnackbar("登录失败！" + e.getMessage());
                                e.printStackTrace();
                                mView.dismissDialog();
                            }

                            @Override
                            public void onComplete() {
                                mView.dismissDialog();
                            }
                        });

    }


}
