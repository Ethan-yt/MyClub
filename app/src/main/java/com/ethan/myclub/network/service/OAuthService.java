package com.ethan.myclub.network.service;

import com.ethan.myclub.user.login.model.Token;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by ethan on 2017/3/19.
 */

public interface OAuthService {

    @FormUrlEncoded
    @POST("api/o/token/")
    Observable<Token> login(@Field("grant_type") String grantType,
                            @Field("username") String username,
                            @Field("password") String password,
                            @Field("regID") String regID);

    @FormUrlEncoded
    @POST("api/o/token/")
    Observable<Token> refreshToken(@Field("grant_type") String grantType,
                                   @Field("refresh_token") String refreshToken);


    @FormUrlEncoded
    @POST("api/o/revoke-token/")
    Observable<Token> revokeToken(@Field("token") String accessToken);

    @FormUrlEncoded
    @POST("api/user/register/")
    Observable<Token> register(@Field("username") String username,
                               @Field("password") String password,
                               @Field("nickname") String nickname,
                               @Field("regID") String regID);

}
