package com.ethan.myclub.network;

import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.network.converter.ApiExceptionConverterFactory;
import com.ethan.myclub.network.services.ApiService;
import com.ethan.myclub.views.main.SnackbarActivity;
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

    private ApiHelper() {

    }

//    public static ApiService getInstance() {
//        return ApiServiceHolder.mApiService;
//    }

    private static class ApiServiceHolder {
        private static ApiService mApiService;

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
                            if (Preferences.isLogined())
                                request.addHeader("Authorization", "JWT " + Preferences.sToken);
                            return chain.proceed(request.build());
                        }
                    })
                    .build();

            final String BASE_URL = "http://zhujian.nghuyong.top/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ApiExceptionConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();

            mApiService = retrofit.create(ApiService.class);
        }
    }


    public static ApiService getProxy(SnackbarActivity activity) {
        return (ApiService) Proxy.newProxyInstance(ApiService.class.getClassLoader(), new Class<?>[]{ApiService.class}, new ProxyHandler(ApiServiceHolder.mApiService, activity, true));
    }

    public static ApiService getProxyWithoutToken(SnackbarActivity activity) {
        return (ApiService) Proxy.newProxyInstance(ApiService.class.getClassLoader(), new Class<?>[]{ApiService.class}, new ProxyHandler(ApiServiceHolder.mApiService, activity, false));
    }
}
