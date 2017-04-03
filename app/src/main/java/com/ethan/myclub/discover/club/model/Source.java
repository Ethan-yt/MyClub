package com.ethan.myclub.discover.club.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ethan on 2017/4/3.
 */

public class Source {

    @SerializedName("club_suggest")
    @Expose
    public String clubSuggest;
    @SerializedName("badge")
    @Expose
    public String badge;
    @SerializedName("brief")
    @Expose
    public String brief;
    @SerializedName("club_name")
    @Expose
    public String clubName;
    @SerializedName("population")
    @Expose
    public Integer population;
    @SerializedName("tags")
    @Expose
    public List<String> tags = null;

}
