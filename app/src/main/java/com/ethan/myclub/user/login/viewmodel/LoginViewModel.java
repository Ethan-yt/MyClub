package com.ethan.myclub.user.login.viewmodel;

import android.content.Intent;
import android.databinding.ObservableField;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.WindowManager;

import com.ethan.myclub.databinding.ActivityLoginBinding;
import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.network.OAuthHelper;
import com.ethan.myclub.user.login.model.Token;
import com.ethan.myclub.user.login.view.LoginActivity;
import com.ethan.myclub.user.login.view.RegisterActivity;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.Credentials;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ethan on 2017/3/18.
 */

public class LoginViewModel {

    private LoginActivity mView;
    private ActivityLoginBinding mBinding;
    public ObservableField<String> userName = new ObservableField<>("admin");
    public ObservableField<String> password = new ObservableField<>("123456");


    public LoginViewModel(LoginActivity loginActivity, ActivityLoginBinding binding) {
        mView = loginActivity;
        mBinding = binding;
        mBinding.setViewModel(this);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            mView.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
    }

    public void register() {
        Intent intent = new Intent();
        intent.setClass(mView, RegisterActivity.class);
        @SuppressWarnings("unchecked")
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(mView,
                        Pair.create((View) mBinding.btnLogin, "trans_btn_next"));
        ActivityCompat.startActivity(mView, intent, options.toBundle());
    }

    public void login() {
        OAuthHelper.getProxy(mView)
                .login("password", userName.get(), password.get(), Preferences.sPushRegID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<Token>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                mView.showWaitingDialog("请稍候", "登录中", d);
                            }

                            @Override
                            public void onNext(Token token) {
                                Preferences.setToken(mView, token);
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
