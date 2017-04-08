package com.ethan.myclub.network;

import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.network.converter.ApiExceptionConverterFactory;
import com.ethan.myclub.network.service.ApiService;
import com.ethan.myclub.main.BaseActivity;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.lang.reflect.Proxy;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;

/**
 * Created by ethan on 2017/3/2.
 */

public class ApiHelper {
    public static final String BASE_URL = "https://zhujian.nghuyong.top/";

    private ApiHelper() {

    }

    public static ApiService getInstance() {
        return ApiServiceHolder.sApiService;
    }

    private static class ApiServiceHolder {
        private static ApiService sApiService;

        static {
            OkHttpClient client = new OkHttpClient
                    .Builder()
                    .followRedirects(false)
                    //设置拦截器，添加headers
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            Request.Builder request = chain.request()
                                    .newBuilder();
                            if (Preferences.sIsLogin.get())
                                request.addHeader("Authorization", Preferences.getToken().mTokenType + " " + Preferences.getToken().mAccessToken);
                            return chain.proceed(request.build());
                        }
                    })
                    .build();


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ApiExceptionConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();

            sApiService = retrofit.create(ApiService.class);
        }
    }


    public static ApiService getProxy(BaseActivity activity) {
        return (ApiService) Proxy.newProxyInstance(ApiService.class.getClassLoader(), new Class<?>[]{ApiService.class}, new ProxyHandler(ApiServiceHolder.sApiService, activity, true));
    }

    public static ApiService getProxyWithoutToken(BaseActivity activity) {
        return (ApiService) Proxy.newProxyInstance(ApiService.class.getClassLoader(), new Class<?>[]{ApiService.class}, new ProxyHandler(ApiServiceHolder.sApiService, activity, false));
    }
}
