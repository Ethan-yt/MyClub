package com.ethan.myclub.network;

import com.ethan.myclub.BuildConfig;
import com.ethan.myclub.main.Preferences;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.network.converter.ApiExceptionConverterFactory;
import com.ethan.myclub.network.service.OAuthService;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.lang.reflect.Proxy;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;

/**
 * Created by ethan on 2017/3/19.
 */

public class OAuthHelper {

    private OAuthHelper() {

    }

    public static OAuthService getInstance() {
        return OAuthServiceHolder.sOAuthService;
    }

    private static class OAuthServiceHolder {
        private static OAuthService sOAuthService;

        static {
            OkHttpClient.Builder builder = new OkHttpClient
                    .Builder()
                    .followRedirects(false);
            //设置拦截器，添加headers
            if (BuildConfig.DEBUG)
                builder.addNetworkInterceptor(new StethoInterceptor());

            OkHttpClient client = builder.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request.Builder request = chain.request()
                            .newBuilder();
                    request.addHeader("Authorization", Preferences.CLIENT_CREDENTIALS);
                    return chain.proceed(request.build());
                }
            })
                    .build();

            final String BASE_URL = "https://zhujian.nghuyong.top/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ApiExceptionConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();

            sOAuthService = retrofit.create(OAuthService.class);
        }
    }


    public static OAuthService getProxy(BaseActivity activity) {
        return (OAuthService) Proxy.newProxyInstance(OAuthService.class.getClassLoader(), new Class<?>[]{OAuthService.class}, new ProxyHandler(OAuthServiceHolder.sOAuthService, activity, false));
    }
}
