package com.ethan.myclub.network.exceptions;

import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.UnknownHostException;

/**
 * Created by ethan on 2017/3/4.
 */
public class ExceptionEngine extends Throwable {

    //对应HTTP的状态码
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    public static ApiException handleException(Throwable e) {
        ApiException ex;

        if (e instanceof HttpException) {             //HTTP错误
            HttpException httpException = (HttpException) e;

            switch (httpException.code()) {

                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    ex = new ApiException(e, ApiException.HTTP_ERROR);
                    ex.message = "网络错误";  //均视为网络错误
                    break;
                case UNAUTHORIZED:
                    ex = new ApiException(e, ApiException.HTTP_FORBIDDEN);
                    ex.message = "token过期";  //均视为网络错误
                    break;
            }
            return ex;
        } else if (e instanceof ServerException) {    //服务器返回的错误
            ServerException resultException = (ServerException) e;
            ex = new ApiException(resultException, resultException.code);
            ex.message = resultException.message;
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            ex = new ApiException(e, ApiException.PARSE_ERROR);
            ex.message = "解析错误";            //均视为解析错误
            return ex;
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {
            ex = new ApiException(e, ApiException.NETWORD_ERROR);
            ex.message = "连接失败";  //均视为网络错误
            return ex;
        } else {
            ex = new ApiException(e, ApiException.UNKNOWN);
            ex.message = "未知错误";          //未知错误
            return ex;
        }
    }
}


