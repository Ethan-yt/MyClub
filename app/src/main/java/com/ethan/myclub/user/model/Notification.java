package com.ethan.myclub.user.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ethan on 2017/4/9.
 */

public class Notification {
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("club")
    @Expose
    public Integer club;
    @SerializedName("notification_owner")
    @Expose
    public Integer notificationOwner;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("content")
    @Expose
    public String content;
    @SerializedName("is_checked")
    @Expose
    public Boolean isChecked;
    @SerializedName("created_date")
    @Expose
    public String createdDate;
}
