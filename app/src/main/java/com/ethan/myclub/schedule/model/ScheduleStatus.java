package com.ethan.myclub.schedule.model;


import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ethan on 2017/4/13.
 */


public class ScheduleStatus {

    @SerializedName("member_list")
    @Expose
    public List<Integer> memberList = null;
    @SerializedName("week")
    @Expose
    public Integer week;
    @SerializedName("year")
    @Expose
    public String year;
    @SerializedName("term")
    @Expose
    public String term;

}