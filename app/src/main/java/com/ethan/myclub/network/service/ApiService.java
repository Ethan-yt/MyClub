package com.ethan.myclub.network.service;

import com.ethan.myclub.user.info.model.Profile;
import com.ethan.myclub.user.login.model.Token;
import com.ethan.myclub.user.login.model.Valid;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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
    @POST("api/user/account-valid/")
    Observable<Valid> accountValid(@Field("username") String username);

    @Multipart
    @PATCH("/api/user/avatar/")
    Observable<Object> uploadAvatar(@Part MultipartBody.Part file);

    @GET("api/activity/classify/")
    Observable<Object> test();

    @GET("api/user/profile/")
    Observable<Profile> getAccountProfile();

}
