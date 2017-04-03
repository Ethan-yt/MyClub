package com.ethan.myclub.discover.club.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ethan on 2017/4/3.
 */


public class Hit {

    @SerializedName("_score")
    @Expose
    public Double score;
    @SerializedName("_type")
    @Expose
    public String type;
    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("_source")
    @Expose
    public Source source;
    @SerializedName("_index")
    @Expose
    public String index;
    @SerializedName("highlight")
    @Expose
    public Object highlight;
}
