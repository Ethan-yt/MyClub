package com.ethan.myclub.network.converter;

import com.ethan.myclub.models.network.ApiResponse;
import com.ethan.myclub.models.network.Error;
import com.ethan.myclub.network.exceptions.ServerException;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by ethan on 2017/3/15.
 * 这个转换器负责将ApiResponse<T>转换为T
 * 并且对error抛出异常
 */

final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final TypeAdapter<T> adapter;

    GsonResponseBodyConverter(TypeAdapter<T> adapter) {
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            ApiResponse apiResponse = (ApiResponse) adapter.fromJson(value.charStream());
            if (apiResponse.code != 0) {
                List<Error> errors = apiResponse.errors;
                if (errors != null && errors.size() != 0) {
                    Error firstError = errors.get(0);
                    //只显示第一个错误
                    if (firstError.field != null && !firstError.field.isEmpty())
                        throw new ServerException(apiResponse.code, firstError.field + " " + firstError.message);
                    else
                        throw new ServerException(apiResponse.code, firstError.message);
                } else
                    throw new ServerException(apiResponse.code, apiResponse.message);
            }
            // 没有错误，返回data
            return (T) apiResponse.data;
        } finally {
            value.close();
        }
    }
}

