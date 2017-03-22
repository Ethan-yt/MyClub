package com.ethan.myclub.network;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.network.exception.ApiException;
import com.ethan.myclub.network.exception.ExceptionEngine;
import com.ethan.myclub.main.SnackbarActivity;
import com.ethan.myclub.network.service.OAuthService;
import com.ethan.myclub.user.login.model.Token;
import com.ethan.myclub.user.login.view.LoginActivity;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ethan on 2017/3/14.
 */

class ProxyHandler implements InvocationHandler {

    private static final String TAG = "ProxyHandler";
    private Object mProxyObject;

    private SnackbarActivity mActivity;
    private boolean mIsNeedToken;

    public ProxyHandler(Object proxyObject, SnackbarActivity activity, boolean isNeedToken) {
        mProxyObject = proxyObject;
        mActivity = activity;
        mIsNeedToken = isNeedToken;

    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        //先检查需要Token但是没有登录的情况
        if (mIsNeedToken && !Preferences.sIsLogin.get()) {
            //弹出登录提示
            showLoginSnackbar("您还没有登录");
            return Observable.empty();
        }

        return ((Observable<?>) method.invoke(mProxyObject, args))
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    int count = 0;

                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                        return throwableObservable
                                .flatMap(new Function<Throwable, ObservableSource<?>>() {
                                    @Override
                                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                                        ApiException apiException = ExceptionEngine.handleException(throwable);
                                        if (apiException.getCode() == ApiException.HTTP_UNAUTHORIZED)//token过期时
                                        {
                                            Log.e(TAG, "apply: TOKEN竟然过期了 赶快refresh一下");
                                            return OAuthHelper.getInstance()
                                                    .refreshToken("refresh_token", Preferences.getToken().mRefreshToken)
                                                    .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Token>>() {
                                                        @Override
                                                        public ObservableSource<? extends Token> apply(Throwable throwable) throws Exception {
                                                            Log.e(TAG, "apply: TOKEN refresh也失败了！再试一次！" + ((HttpException) throwable).response().errorBody().string(), throwable);

                                                            return Observable.just(new Token());
                                                        }
                                                    });
                                        }
                                        return Observable.error(apiException);
                                    }
                                })
                                .takeWhile(new Predicate<Object>() {
                                    @Override
                                    public boolean test(Object o) throws Exception {
                                        if (++count > 2) {
                                            showLoginSnackbar("您的登录状态失效，需要重新登录");
                                            return false;//多次获取Token失败
                                        }
                                        if (o instanceof Token) {
                                            Token token = (Token) o;
                                            if (TextUtils.isEmpty(token.mAccessToken)) {
                                                //showLoginSnackbar("您的登录状态失效，需要重新登录");
                                                //Preferences.setToken(mActivity, null);
                                                return true;//TOKEN refresh也失败了！再试一次！
                                            } else {
                                                Preferences.setToken(mActivity, token);
                                                return true;//设置新Token 并重试
                                            }
                                        }
                                        return false;
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    private void showLoginSnackbar(String message) {
        mActivity.showSnackbar(message, "登录", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mActivity, LoginActivity.class);
                mActivity.startActivityForResult(intent, SnackbarActivity.REQUEST_LOGIN);
                mActivity = null;
            }
        });
    }
}
