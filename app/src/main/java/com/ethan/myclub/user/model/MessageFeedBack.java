package com.ethan.myclub.user.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ethan on 2017/4/9.
 */

public class MessageFeedBack {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("checked")
    @Expose
    public Boolean checked;
    @SerializedName("member")
    @Expose
    public Integer member;
    @SerializedName("notification")
    @Expose
    public Notification notification;

}
