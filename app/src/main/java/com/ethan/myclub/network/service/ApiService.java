package com.ethan.myclub.network.service;

import com.ethan.myclub.activity.detail.model.LikeStatus;
import com.ethan.myclub.activity.model.Activity;
import com.ethan.myclub.activity.model.Content;
import com.ethan.myclub.club.model.Club;
import com.ethan.myclub.club.model.Tag2;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.discover.activity.model.ActivityResult;
import com.ethan.myclub.discover.merchant.model.MerchantResult;
import com.ethan.myclub.user.model.Profile;
import com.ethan.myclub.user.login.model.Valid;
import com.ethan.myclub.discover.club.model.ClubResult;
import com.ethan.myclub.user.schedule.model.Schedule;

import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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
    //============================账户============================
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

    //============================社团============================
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

    //获取社团活动
    @GET("api/activity/club/{clubId}/")
    Observable<List<ActivityResult>> getClubActivity(@Path("clubId") String clubId);


    //============================商家============================
    //获取商家搜索提示
    @GET("api/merchant/suggestion/")
    Observable<List<String>> getMerchantSuggestion(@Query("keyword") String keyWord);

    //搜索商家
    @GET("api/merchant/search/")
    Observable<List<MerchantResult>> searchMerchant(@Query("keyword") String keyWord, @Query("page") int page, @Query("items") int items);


    //============================活动============================
    //获取活动搜索提示
    @GET("api/activity/suggestion/")
    Observable<List<String>> getActivitySuggestion(@Query("keyword") String keyWord);

    //搜索活动
    @GET("api/activity/search/")
    Observable<List<ActivityResult>> searchActivity(@Query("keyword") String keyWord, @Query("page") int page, @Query("items") int items);

    //获取指定ID活动信息
    @GET("api/activity/{activityId}/")
    Observable<Activity> getActivity(@Path("activityId") String activityId);

    //创建活动
    @FormUrlEncoded
    @POST("api/activity/club/{clubId}/")
    Observable<Object>
    createActivity(@Path("clubId") String clubId,
                   @Field("name") String name,
                   @Field("join_members_max") String joinMembersMax,
                   @Field("activity_time") String activityTime,
                   @Field("brief_introduction") String briefIntroduction,
                   @Field("location") String location);

    //上传活动图片
    @Multipart
    @PATCH("api/activity/image/{activity}/")
    Observable<Object> uploadActivityImage(@Path("activity") String activityId,
                                           @Part MultipartBody.Part file);

    //修改活动
    @FormUrlEncoded
    @PATCH("api/activity/{activity}/")
    Observable<Object>
    modifyActivity(@Path("activity") String activityId,
                   @Field("name") String name,
                   @Field("join_members_max") String joinMembersMax,
                   @Field("activity_time") String activityTime,
                   @Field("brief_introduction") String briefIntroduction,
                   @Field("location") String location);

    //修改活动tags
    @POST("api/activity/tag/{activity}/")
    Observable<Object> editActivityTags(@Path("activity") String activityId, @Body Tag2 tags);

    //活动内容图片
    @Multipart
    @POST("api/activity/content_image/{activity}/")
    Observable<Object> uploadActivityContentImage(@Path("activity") String activityId,
                                                  @Part List<MultipartBody.Part> file);

    //活动内容文字
    @POST("api/activity/content_text/{activity}/")
    Observable<Object> uploadActivityContentText(@Path("activity") String activityId, @Body Content content);

    //删除活动
    @DELETE("api/activity/{activity}/")
    Observable<Object> deleteActivity(@Path("activity") String activityId);


    //对活动点赞/取消赞
    @GET("api/activity/{activityId}/change-like-status/")
    Observable<LikeStatus> changeLikeStatus(@Path("activityId") String activityId);

    //============================课表============================

    //上传课表
    @POST("api/user/schedule/")
    Observable<Object> updateSchedule(@Body List<Schedule> schedules);

    //============================个人============================

    //修改个人信息
    @FormUrlEncoded
    @PATCH("api/user/profile/")
    Observable<Object>
    modifyUserProfile(
            @Field("nickname") String nickname,
            @Field("student_number") String studentNumber,
            @Field("sex") String sex,
            @Field("name") String name,
            @Field("birthday") String birthday,
            @Field("brief_introduction") String briefIntroduction);

    //个人收藏
    @GET("api/activity/search/?is_like=true")
    Observable<List<ActivityResult>> getMyCollection();

    //============================通知============================

    //创建活动
    @FormUrlEncoded
    @POST("api/club/{clubId}/notification/")
    Observable<Object>
    sendNotification(@Path("clubId") String clubId,
                     @Field("title") String title,
                     @Field("content") String content);


}
