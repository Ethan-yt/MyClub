package com.ethan.myclub.user.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ethan on 2017/4/9.
 */

public class MessageFeedBack implements Serializable {

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
