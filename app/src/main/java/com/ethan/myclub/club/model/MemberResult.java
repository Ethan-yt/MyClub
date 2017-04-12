package com.ethan.myclub.club.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ethan on 2017/4/9.
 */

public class MemberResult implements Serializable{

    @SerializedName("club")
    @Expose
    public Integer club;
    @SerializedName("join_date")
    @Expose
    public String joinDate;
    @SerializedName("userAccount")
    @Expose
    public String userAccount;
    @SerializedName("title")
    @Expose
    public Integer title;
    @SerializedName("avatar")
    @Expose
    public String avatar;
    @SerializedName("nickname")
    @Expose
    public String nickname;
    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("is_creator")
    @Expose
    public Boolean isCreator;

    public boolean selected = false;
}
