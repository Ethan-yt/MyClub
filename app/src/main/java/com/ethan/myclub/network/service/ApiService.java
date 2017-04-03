package com.ethan.myclub.network.service;

import com.ethan.myclub.club.main.model.Club;
import com.ethan.myclub.user.profile.model.Profile;
import com.ethan.myclub.user.login.model.Valid;
import com.ethan.myclub.discover.club.model.ClubResult;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

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
    Observable<Profile> getMyProfile();

    @GET("api/club/my-club-list/")
    Observable<List<Club>> getMyClubs();

    @GET("api/club/suggestion/")
    Observable<List<String>> getClubSuggestion(@Query("keyword") String keyWord);

    @GET("api/club/search/")
    Observable<ClubResult> searchClub(@Query("keyword")String keyWord,@Query("page") int page ,@Query("items") int items);
}
