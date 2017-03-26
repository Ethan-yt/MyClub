package com.ethan.myclub.club.main.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ethan on 2017/3/26.
 */

public class Club implements Serializable {

    @SerializedName("club_id")
    @Expose
    public Integer clubId;
    @SerializedName("club_name")
    @Expose
    public String clubName;
    @SerializedName("club_badge")
    @Expose
    public String clubBadge;
    @SerializedName("club_badge_thumbnail")
    @Expose
    public String clubBadgeThumbnail;
    @SerializedName("club_brief_introduce")
    @Expose
    public String clubBriefIntroduce;
    @SerializedName("is_creator")
    @Expose
    public Boolean isCreator;
    @SerializedName("title_table")
    @Expose
    public List<Title> titleTable = null;
    @SerializedName("title_id")
    @Expose
    public Integer titleId;
}
