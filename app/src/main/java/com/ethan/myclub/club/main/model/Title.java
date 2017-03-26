package com.ethan.myclub.club.main.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ethan on 2017/3/26.
 */

public class Title implements Serializable {
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("title_name")
    @Expose
    public String titleName;
    @SerializedName("permissions_part1")
    @Expose
    public Integer permissionsPart1;
}
