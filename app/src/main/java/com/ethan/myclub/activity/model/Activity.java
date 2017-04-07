package com.ethan.myclub.activity.model;

import java.util.List;

import com.ethan.myclub.club.model.Club;
import com.ethan.myclub.club.model.Tag;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Activity {

    @SerializedName("club")
    @Expose
    public Club club;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("is_special")
    @Expose
    public Boolean isSpecial;
    @SerializedName("content_images")
    @Expose
    public List<String> contentImages = null;
    @SerializedName("content_texts")
    @Expose
    public List<String> contentTexts = null;
    @SerializedName("activityLevel")
    @Expose
    public Integer activityLevel;
    @SerializedName("home_page_img")
    @Expose
    public String homePageImg;
    @SerializedName("join_members_max")
    @Expose
    public Integer joinMembersMax;
    @SerializedName("publish_time")
    @Expose
    public String publishTime;
    @SerializedName("activity_time")
    @Expose
    public String activityTime;
    @SerializedName("brief_introduction")
    @Expose
    public String briefIntroduction;
    @SerializedName("location")
    @Expose
    public String location;
    @SerializedName("comment_num")
    @Expose
    public String commentNum;
    @SerializedName("like_num")
    @Expose
    public Integer likeNum;
    @SerializedName("tags")
    @Expose
    public List<Tag> tag = null;
    @SerializedName("like_status")
    @Expose
    public Boolean likeStatus;

}