package com.ethan.myclub.network;

import android.text.TextUtils;
import android.util.Log;

import com.ethan.myclub.main.MyApplication;
import com.ethan.myclub.network.exception.ApiException;
import com.ethan.myclub.network.exception.ExceptionEngine;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.user.model.Token;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ethan on 2017/3/14.
 */

class ProxyHandler implements InvocationHandler {

    private static final String TAG = "ProxyHandler";
    private Object mProxyObject;

    private BaseActivity mActivity;
    private boolean mIsNeedToken;

    public ProxyHandler(Object proxyObject, BaseActivity activity, boolean isNeedToken) {
        mProxyObject = proxyObject;
        mActivity = activity;
        mIsNeedToken = isNeedToken;

    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        //先检查需要Token但是没有登录的情况
        if (mIsNeedToken && !MyApplication.isLogin()) {
            //弹出登录提示
            mActivity.showLoginSnackbar("您还没有登录哦");
            mActivity = null;
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
                                                    .refreshToken("refresh_token", MyApplication.getToken().mRefreshToken)
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
                                            mActivity.showLoginSnackbar("您的登录状态失效，需要重新登录");
                                            MyApplication.setToken(mActivity, null);
                                            mActivity = null;
                                            return false;//多次获取Token失败
                                        }
                                        if (o instanceof Token) {
                                            Token token = (Token) o;
                                            if (TextUtils.isEmpty(token.mAccessToken)) {
                                                return true;//TOKEN refresh也失败了！再试一次！
                                            } else {
                                                MyApplication.setToken(mActivity, token);
                                                return true;//设置新Token 并重试
                                            }
                                        }
                                        return false;
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.io());
    }


}
