package com.ethan.myclub.user.schedule;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by ethan on 2017/1/27.
 */

public interface ScheduleService {

    //@Headers({"key:value","key:value","key:value"})
    @Headers("Referer:http://202.195.144.163")
    @GET("jndx/default5.aspx")
    Observable<ResponseBody> getViewState();

    @Headers("Referer:http://202.195.144.163")
    @FormUrlEncoded
    @POST("jndx/default5.aspx")
    Observable<ResponseBody> login(
            @Field("__VIEWSTATE") String viewState,
            @Field("TextBox1") String username,
            @Field("TextBox2") String password,
            @Field(value = "RadioButtonList1" ,encoded = true) String r,
            @Field("Button1") String b);

    @Headers("Referer:http://202.195.144.163")
    @GET("jndx/xskbcx.aspx")

    Observable<ResponseBody> getCurrentSchedule(@Query("xh") String username);

    @Headers("Referer:http://202.195.144.163")
    @FormUrlEncoded
    @POST("jndx/xskbcx.aspx")
    Observable<ResponseBody> getOtherSchedule(
            @Query("xh") String username,
            @Field("xnd") String year,
            @Field("xqd") String term,
            @Field("__VIEWSTATE") String viewState,
            @Field("__EVENTTARGET") String eventTarget,
            @Field("__EVENTARGUMENT") String eventArgument);


}
