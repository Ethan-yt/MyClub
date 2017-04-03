package com.ethan.myclub.discover.club.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ethan on 2017/4/3.
 */

public class Hits {

    @SerializedName("hits")
    @Expose
    public List<Hit> hits = null;
    @SerializedName("total")
    @Expose
    public Integer total;
    @SerializedName("max_score")
    @Expose
    public Double maxScore;

}
