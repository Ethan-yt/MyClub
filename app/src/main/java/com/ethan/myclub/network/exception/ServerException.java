package com.ethan.myclub.network.exception;

/**
 * Created by ethan on 2017/3/4.
 */

public class ServerException extends RuntimeException {
    public int code;
    public String message;

    public static final int SUCCESS = 0;
    public static final int DETAIL =1;
    public static final int NON_FIELD_ERRORS =2;
    public static final int USERNAME = 3;
    public static final int PASSWORD = 4;
    public static final int TOKEN = 5;

    public ServerException(int retCode, String detail) {
        code = retCode;
        message = detail;
    }
}