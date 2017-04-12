package com.ethan.myclub.schedule.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ethan on 2017/4/13.
 */

public class ScheduleResult {
    @SerializedName("members_id")
    @Expose
    public List<Members> members = null;
    @SerializedName("spare_number")
    @Expose
    public Integer spareNumber;

}
