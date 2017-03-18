package com.ethan.myclub.network.service;

import com.ethan.myclub.user.login.model.Token;
import com.ethan.myclub.user.login.model.Valid;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by ethan on 2017/3/1.
 */

public interface ApiService {

    @FormUrlEncoded
    @POST("api/o/token/")
    Observable<Token> login(@Field("grant_type") String grantType,
                            @Field("username") String username,
                            @Field("password") String password,
                            @Header("Authentication") String s);

    @FormUrlEncoded
    @POST("api/user/register/")
    Observable<Token> register(@Field("username") String username,
                               @Field("password") String password);

    @FormUrlEncoded
    @POST("api/user/account-valid/")
    Observable<Valid> accountValid(@Field("username") String username);

    @Multipart
    @PATCH("/api/user/avatar/")
    Observable<Object> uploadAvatar(@Part MultipartBody.Part file);


}
