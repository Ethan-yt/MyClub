package com.ethan.myclub.network.services;

import com.ethan.myclub.models.network.Response;
import com.ethan.myclub.models.network.Token;
import com.ethan.myclub.models.network.Valid;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

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

}
