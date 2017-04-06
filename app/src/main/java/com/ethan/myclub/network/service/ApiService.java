package com.ethan.myclub.network.service;

import com.ethan.myclub.club.info.model.Club;
import com.ethan.myclub.club.info.model.Club;
import com.ethan.myclub.club.info.model.Tag2;
import com.ethan.myclub.club.main.model.MyClub;
import com.ethan.myclub.user.profile.model.Profile;
import com.ethan.myclub.user.login.model.Valid;
import com.ethan.myclub.discover.club.model.ClubResult;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ethan on 2017/3/1.
 */

public interface ApiService {

    //验证账户可用性
    @FormUrlEncoded
    @POST("api/user/account-valid/")
    Observable<Valid> accountValid(@Field("username") String username);

    //上传头像
    @Multipart
    @PATCH("/api/user/avatar/")
    Observable<Object> uploadAvatar(@Part MultipartBody.Part file);

    //获取我的信息
    @GET("api/user/profile/")
    Observable<Profile> getMyProfile();

    //获取我的社团
    @GET("api/club/my-club-list/")
    Observable<List<MyClub>> getMyClubs();

    //获取社团搜索提示
    @GET("api/club/suggestion/")
    Observable<List<String>> getClubSuggestion(@Query("keyword") String keyWord);

    //搜索社团
    @GET("api/club/search/")
    Observable<ClubResult> searchClub(@Query("keyword") String keyWord, @Query("page") int page, @Query("items") int items);

    //创建社团
    @FormUrlEncoded
    @POST("api/club/")
    Observable<Club>
    createClub(@Field("club_name") String clubName,
               @Field("college_id") String collegeId,
               @Field("brief_introduce") String biefIntroduce,
               @Field("contact") String contact);

    //获取指定ID社团信息
    @GET("api/club/{clubId}/")
    Observable<Club> getClub(@Path("clubId") String clubId);

    //修改社团信息
    @FormUrlEncoded
    @PATCH("api/club/{clubId}/")
    Observable<Club>
    modifyClub(@Path("clubId") String clubId,
               @Field("club_name") String clubName,
               @Field("college_id") String collegeId,
               @Field("brief_introduce") String biefIntroduce,
               @Field("contact") String contact);

    //上传社团头像
    @Multipart
    @PATCH("api/club/{clubId}/badge/")
    Observable<Object> uploadClubBadge(@Path("clubId") String clubId,
                                       @Part MultipartBody.Part file);

    //修改tags
    @POST("api/club/{clubId}/tag/")
    Observable<Club> editClubTags(@Path("clubId") String clubId, @Body Tag2 tags);


}
