package com.ethan.myclub.discover.club.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ethan on 2017/4/3.
 */

public class ClubResult {
    @SerializedName("hits")
    @Expose
    public Hits hits;
    @SerializedName("_shards")
    @Expose
    public Shards shards;
    @SerializedName("took")
    @Expose
    public Integer took;
    @SerializedName("timed_out")
    @Expose
    public Boolean timedOut;

}
