package com.ethan.myclub.network.model;

/**
 * Created by ethan on 2017/3/4.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 标准数据格式
 * @param <T>
 */
public class ApiResponse<T> {
    @SerializedName("code")
    @Expose
    public int code;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public T data;
    @SerializedName("errors")
    @Expose
    public List<Error> errors;
}

