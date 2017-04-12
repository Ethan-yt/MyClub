package com.ethan.myclub.schedule.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ethan on 2017/4/13.
 */

public class Members {

    @SerializedName("nickname")
    @Expose
    public String nickname;
    @SerializedName("id")
    @Expose
    public Integer id;
}
