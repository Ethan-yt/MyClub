package com.ethan.myclub.network.exception;

/**
 * Created by ethan on 2017/3/4.
 */
public class ApiException extends Exception {

    private int mCode;
    private String mMessage;

    /**
     * 未知错误
     */
    public static final int UNKNOWN_ERROR = 1000;
    /**
     * 解析错误
     */
    public static final int PARSE_ERROR = 1001;
    /**
     * 网络错误
     */
    public static final int NETWORK_ERROR = 1002;
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

    /**
     * API错误
     */
    public static final int API_ERROR = 2000;


    public ApiException(Throwable throwable, int code, String message) {
        super(throwable);
        mCode = code;
        mMessage = message;
    }

    public ApiException(Throwable throwable, int code) {
        this(throwable, code, throwable.getMessage());
    }

    @Override
    public String getMessage() {
        return mMessage;
    }


    public int getCode() {
        return mCode;
    }
}