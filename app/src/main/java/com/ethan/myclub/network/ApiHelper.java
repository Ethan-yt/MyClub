package com.ethan.myclub.network;

import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.network.services.ApiService;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ethan on 2017/3/2.
 */

public class ApiHelper {

    private ApiHelper() {

    }

    public static ApiService getInstance() {
        return ApiServiceHolder.mApiService;
    }

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
                            Request request = chain.request()
                                    .newBuilder()
                                    .addHeader("Authorization", "JWT " + Preferences.sToken)
                                    .build();
                            return chain.proceed(request);
                        }
                    })
                    .build();

            final String BASE_URL = "http://zhujian.nghuyong.top/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();

            mApiService = retrofit.create(ApiService.class);
        }
    }
}
