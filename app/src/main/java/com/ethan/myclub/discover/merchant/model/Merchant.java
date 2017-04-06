package com.ethan.myclub.discover.merchant.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ethan on 2017/4/6.
 */

public class Merchant {
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("starLevel")
    @Expose
    public Integer starLevel;
    @SerializedName("contact")
    @Expose
    public String contact;
    @SerializedName("location")
    @Expose
    public String location;
    @SerializedName("logoUrl")
    @Expose
    public String logoUrl;
    @SerializedName("support_type")
    @Expose
    public String supportType;
    @SerializedName("support_activity")
    @Expose
    public String supportActivity;

}
