package com.ethan.myclub.club.create.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ethan on 2017/4/4.
 */

public class Club {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("creator")
    @Expose
    public Integer creator;
    @SerializedName("club_name")
    @Expose
    public String clubName;
    @SerializedName("college")
    @Expose
    public Integer college;
    @SerializedName("brief_introduce")
    @Expose
    public String briefIntroduce;
    @SerializedName("contact")
    @Expose
    public String contact;
    @SerializedName("created_date")
    @Expose
    public String createdDate;
    @SerializedName("max_population")
    @Expose
    public Integer maxPopulation;
    @SerializedName("current_population")
    @Expose
    public Integer currentPopulation;
    @SerializedName("badge")
    @Expose
    public String badge;
}
