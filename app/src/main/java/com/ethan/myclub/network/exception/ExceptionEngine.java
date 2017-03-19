package com.ethan.myclub.network.exception;

import android.net.ParseException;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.UnknownHostException;

/**
 * Created by ethan on 2017/3/4.
 */
public class ExceptionEngine extends Throwable {

    //对应HTTP的状态码
    private static final int BAD_REQUEST = 400;
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

        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            //获取errorBody
            String errorDescription = "";
            String errorBody = "";
            String errorType = "";
            try {
                errorBody = httpException.response().errorBody().string();
                JSONObject object = new JSONObject(errorBody);
                errorDescription = object.optString("error_description");//错误描述
                errorType = object.optString("error");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            Log.e("Network", "handleException: " + errorBody);
            switch (httpException.code()) {
                case BAD_REQUEST:
                    ex = new ApiException(e, ApiException.HTTP_ERROR, "格式出错 " + errorDescription);
                    break;
                case UNAUTHORIZED:
                    if (errorDescription.equals("Invalid credentials given."))
                        ex = new ApiException(e, ApiException.API_ERROR, "用户名或密码错误");
                    else if (errorType.equals("invalid_client"))
                        ex = new ApiException(e, ApiException.API_ERROR, "内部错误：客户端ID失效，请联系管理员");
                    else
                        ex = new ApiException(e, ApiException.HTTP_UNAUTHORIZED, "token过期");
                    break;
                case FORBIDDEN:
                    ex = new ApiException(e, ApiException.HTTP_FORBIDDEN, "权限不足");
                    break;
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    //均视为网络错误
                    ex = new ApiException(e, ApiException.HTTP_ERROR, "网络错误");
                    break;
            }
            return ex;
        } else if (e instanceof ServerException) {
            // 服务器返回的错误
            ServerException resultException = (ServerException) e;
            ex = new ApiException(resultException, ApiException.API_ERROR + resultException.code, resultException.message);
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            ex = new ApiException(e, ApiException.PARSE_ERROR, "解析错误");
            return ex;
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {
            ex = new ApiException(e, ApiException.NETWORK_ERROR, "连接失败");
            return ex;
        } else {
            ex = new ApiException(e, ApiException.UNKNOWN_ERROR, "未知错误");
            e.printStackTrace();
            return ex;
        }
    }
}


