package com.ethan.myclub.models.network;

/**
 * Created by ethan on 2017/3/4.
 */

/**
 * 标准数据格式
 * @param <T>
 */
public class Response<T> {
    public int retCode;
    public String detail;
    public T data;
}

