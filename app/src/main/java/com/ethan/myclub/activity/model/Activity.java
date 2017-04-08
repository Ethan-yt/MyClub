package com.ethan.myclub.activity.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.List;

import com.android.databinding.library.baseAdapters.BR;
import com.ethan.myclub.club.model.Club;
import com.ethan.myclub.club.model.Tag;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Activity extends BaseObservable {

    public boolean mIsInfoEdited = false;

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!name.equals(this.name))
            mIsInfoEdited = true;
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getJoinMembersMax() {
        return String.valueOf(joinMembersMax);
    }

    public void setJoinMembersMax(String joinMembersMax) {
        if (!joinMembersMax.equals(String.valueOf(this.joinMembersMax)))
            mIsInfoEdited = true;
        try {
            this.joinMembersMax = Integer.valueOf(joinMembersMax);
        } catch (Throwable e) {
            this.joinMembersMax = 0;
        }
        notifyPropertyChanged(BR.joinMembersMax);
    }

    @Bindable
    public String getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(String activityTime) {
        if (!activityTime.equals(this.activityTime))
            mIsInfoEdited = true;
        this.activityTime = activityTime;
        notifyPropertyChanged(BR.activityTime);
    }

    @Bindable
    public String getBriefIntroduction() {
        return briefIntroduction;
    }

    public void setBriefIntroduction(String briefIntroduction) {
        if (!briefIntroduction.equals(this.briefIntroduction))
            mIsInfoEdited = true;
        this.briefIntroduction = briefIntroduction;
        notifyPropertyChanged(BR.briefIntroduction);
    }

    @Bindable
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (!location.equals(this.location))
            mIsInfoEdited = true;
        this.location = location;
        notifyPropertyChanged(BR.location);
    }

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
    @SerializedName("special_index_image")
    @Expose
    public String specialIndexImage;


}