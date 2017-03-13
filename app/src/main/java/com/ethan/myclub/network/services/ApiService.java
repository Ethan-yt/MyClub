package com.ethan.myclub.network.services;

import com.ethan.myclub.models.network.Response;
import com.ethan.myclub.models.network.Token;
import com.ethan.myclub.models.network.Valid;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by ethan on 2017/3/1.
 */

public interface ApiService {

    @FormUrlEncoded
    @POST("api/user/login/")
    Observable<Response<Token>> login(@Field("username") String username,
                                      @Field("password") String password);

    @FormUrlEncoded
    @POST("api/user/register/")
    Observable<Response<Token>> register(@Field("username") String username,
                                         @Field("password") String password);

    @FormUrlEncoded
    @POST("api/user/account-valid/")
    Observable<Response<Valid>> accountValid(@Field("username") String username);

    @Multipart
    @POST("/api/user/avatar/")
    Observable<Response<Object>> uploadAvatar(@Part MultipartBody.Part file);


}
