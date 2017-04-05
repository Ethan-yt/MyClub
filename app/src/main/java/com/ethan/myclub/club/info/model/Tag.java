package com.ethan.myclub.club.info.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ethan on 2017/4/5.
 */

class Tag {
    @SerializedName("tag_name")
    @Expose
    public String tagName;
}
