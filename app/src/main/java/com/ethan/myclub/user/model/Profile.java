package com.ethan.myclub.user.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ethan on 2017/3/22.
 */

public class Profile extends BaseObservable implements Serializable {
    @SerializedName("user_id")
    @Expose
    public Integer userId;
    @SerializedName("username")
    @Expose
    public String username;
    @SerializedName("nickname")
    @Expose
    public String nickname;
    @SerializedName("student_number")
    @Expose
    public String studentNumber;

    public boolean mIsEdited = false;

    @Bindable
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        if(!nickname.equals(this.nickname))
            mIsEdited = true;
        this.nickname = nickname;
        notifyPropertyChanged(BR.nickname);
    }

    @Bindable
    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        if(!studentNumber.equals(this.studentNumber))
            mIsEdited = true;
        this.studentNumber = studentNumber;
        notifyPropertyChanged(BR.studentNumber);
    }

    @Bindable
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        if(!sex.equals(this.sex))
            mIsEdited = true;
        this.sex = sex;
        notifyPropertyChanged(BR.sex);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(!name.equals(this.name))
            mIsEdited = true;
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        if(!birthday.equals(this.birthday))
            mIsEdited = true;
        this.birthday = birthday;
        notifyPropertyChanged(BR.birthday);
    }

    @Bindable
    public String getBriefIntroduction() {
        return briefIntroduction;
    }

    public void setBriefIntroduction(String briefIntroduction) {
        if(!briefIntroduction.equals(this.briefIntroduction))
            mIsEdited = true;
        this.briefIntroduction = briefIntroduction;
        notifyPropertyChanged(BR.briefIntroduction);
    }

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
    @SerializedName("avatar")
    @Expose
    public String avatar;
    @SerializedName("is_upload_schedule")
    @Expose
    public Boolean isUploadSchedule;



}
