package com.ethan.myclub.discover.club.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ethan on 2017/4/3.
 */

public class Shards {

    @SerializedName("successful")
    @Expose
    public Integer successful;
    @SerializedName("failed")
    @Expose
    public Integer failed;
    @SerializedName("total")
    @Expose
    public Integer total;

}
