package com.ethan.myclub.user.info.model;

import android.databinding.BaseObservable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ethan on 2017/3/22.
 */

public class Profile implements Serializable {
    @SerializedName("user_id")
    @Expose
    public Integer userId;
    @SerializedName("student_number")
    @Expose
    public String studentNumber;
    @SerializedName("id_card")
    @Expose
    public String idCard;
    @SerializedName("sex")
    @Expose
    public String sex;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("birthday")
    @Expose
    public String birthday;
    @SerializedName("hometown")
    @Expose
    public String hometown;
    @SerializedName("college")
    @Expose
    public String college;
    @SerializedName("school")
    @Expose
    public String school;
    @SerializedName("major")
    @Expose
    public String major;
    @SerializedName("class_name")
    @Expose
    public String className;
    @SerializedName("enrolled_year")
    @Expose
    public Integer enrolledYear;
    @SerializedName("degree")
    @Expose
    public String degree;
    @SerializedName("brief_introduction")
    @Expose
    public String briefIntroduction;
    @SerializedName("avatar_url")
    @Expose
    public String avatarUrl;
    @SerializedName("avatar_thumbnail_url")
    @Expose
    public String avatarThumbnailUrl;

}
