package com.ethan.myclub.network;

import android.util.Log;

import com.ethan.myclub.models.network.Error;
import com.ethan.myclub.network.exceptions.ApiException;
import com.ethan.myclub.network.exceptions.ExceptionEngine;
import com.ethan.myclub.network.exceptions.ServerException;
import com.ethan.myclub.models.network.Response;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ethan on 2017/3/4.
 */

public class Transformers {

    public static class sTransformer<T> implements ObservableTransformer<Response<T>, T> {

        private static final String TAG = "Transformers";

        @Override
        public ObservableSource<T> apply(Observable<Response<T>> upstream) {
            return upstream.map(new Function<Response<T>, T>() {
                @Override
                public T apply(Response<T> tResponse) throws Exception {
                    if (tResponse.code != 0)
                    {
                        List<Error> errors = tResponse.errors;
                        if(errors != null && errors.size() != 0)
                            throw new ServerException(tResponse.code, errors.get(0).message);
                        else
                            throw new ServerException(tResponse.code, tResponse.message);
                    }

                    return tResponse.data;
                }
            }).onErrorResumeNext(new HttpResponseHandler<T>());
        }
    }


    private static class HttpResponseHandler<T> implements Function<Throwable, Observable<T>> {
        @Override
        public Observable<T> apply(Throwable throwable) throws ApiException {
            //将一切错误移交错误处理引擎
            return Observable.error(ExceptionEngine.handleException(throwable));
        }
    }

    public static class SchedulersSwitcher<T> implements ObservableTransformer<T, T> {

        @Override
        public ObservableSource<T> apply(Observable<T> upstream) {
            return upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    }

}
