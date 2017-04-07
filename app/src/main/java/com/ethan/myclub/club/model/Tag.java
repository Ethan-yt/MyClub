package com.ethan.myclub.club.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ethan on 2017/4/5.
 */

public class Tag implements Serializable {
    @SerializedName("tag_name")
    @Expose
    public String tagName;
}
