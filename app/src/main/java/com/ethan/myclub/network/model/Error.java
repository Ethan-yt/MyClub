package com.ethan.myclub.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ethan on 2017/3/11.
 */

public class Error {
    @SerializedName("field")
    @Expose
    public String field;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("code")
    @Expose
    public int code;
}
