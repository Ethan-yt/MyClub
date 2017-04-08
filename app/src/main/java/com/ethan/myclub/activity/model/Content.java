package com.ethan.myclub.activity.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ethan on 2017/4/8.
 */

public class Content {
    @SerializedName("content")
    @Expose
    public List<String> content;

}
