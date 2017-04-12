package com.ethan.myclub.message.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ethan on 2017/4/11.
 */

public class UnreadNumber {

    @SerializedName("unread_number")
    @Expose
    public Integer unreadNumber;
}
