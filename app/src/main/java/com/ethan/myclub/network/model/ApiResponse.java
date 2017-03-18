package com.ethan.myclub.network.model;

/**
 * Created by ethan on 2017/3/4.
 */

import java.util.List;

/**
 * 标准数据格式
 * @param <T>
 */
public class ApiResponse<T> {
    public int code;
    public String message;
    public T data;
    public List<Error> errors;
}

