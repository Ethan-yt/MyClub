package com.ethan.myclub.network.exception;

/**
 * Created by ethan on 2017/3/4.
 */
public class ApiException extends Exception {

    public int code;
    public String message;

    /**
     * 未知错误
     */
    public static final int UNKNOWN = 1000;
    /**
     * 解析错误
     */
    public static final int PARSE_ERROR = 1001;
    /**
     * 网络错误
     */
    public static final int NETWORD_ERROR = 1002;
    /**
     * 协议出错
     */
    public static final int HTTP_ERROR = 1003;
    /**
     * 401
     */
    public static final int HTTP_UNAUTHORIZED = 1004;
    /**
     * 403 权限错误
     */
    public static final int HTTP_FORBIDDEN = 1005;

    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}