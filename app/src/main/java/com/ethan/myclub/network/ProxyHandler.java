package com.ethan.myclub.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.models.network.Token;
import com.ethan.myclub.network.exceptions.ApiException;
import com.ethan.myclub.network.exceptions.ExceptionEngine;
import com.ethan.myclub.network.services.ApiService;
import com.ethan.myclub.views.main.SnackbarActivity;
import com.ethan.myclub.views.user.login.LoginActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ethan on 2017/3/14.
 */

class ProxyHandler implements InvocationHandler {

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
        if (mIsNeedToken && !Preferences.isLogined()) {
            //弹出登录提示
            return getLoginObservable();
        }

        return ((Observable<?>) method.invoke(mProxyObject, args))
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Throwable throwable) throws Exception {

                                ApiException apiException = ExceptionEngine.handleException(throwable);

                                switch (apiException.code) {
                                    case ApiException.HTTP_UNAUTHORIZED://token过期时
                                        return getLoginObservable();
                                }
                                return Observable.error(apiException);
                            }
                        });
                    }
                })
                //.doOnError()  TODO: 2017/3/15 错误的时候自动弹出snackbar 增加doOnError操作符
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Object> getLoginObservable() {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                mActivity.showSnackbar("您还没有登录", "登录", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(mActivity, LoginActivity.class);
                        mActivity.startActivityForResult(intent, SnackbarActivity.REQUEST_LOGIN);
                    }
                });
                e.onComplete();
            }
        });
    }
}
