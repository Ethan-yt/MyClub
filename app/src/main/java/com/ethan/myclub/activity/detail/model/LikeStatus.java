package com.ethan.myclub.activity.detail.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ethan on 2017/4/8.
 */

public class LikeStatus {

    @SerializedName("like_status")
    @Expose
    public Boolean likeStatus;
    @SerializedName("like_number")
    @Expose
    public Integer likeNumber;

}
