package com.ethan.myclub.club.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ethan on 2017/4/6.
 */

public class Tag2 {
    @SerializedName("tag_name")
    @Expose
    public List<String> tagName;
}
