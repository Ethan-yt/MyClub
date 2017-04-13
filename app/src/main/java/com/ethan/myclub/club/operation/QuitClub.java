package com.ethan.myclub.club.operation;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.Keep;
import android.support.v7.app.AlertDialog;

import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.main.MainActivity;
import com.ethan.myclub.network.ApiHelper;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by ethan on 2017/4/12.
 */
@Keep
public class QuitClub {
    static public void start(final Activity baseActivity, final MyClub myClub) {
        new AlertDialog.Builder(baseActivity)
                .setTitle("提示")
                .setMessage("真的要退出这个社团吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        quitClub(baseActivity, myClub);
                    }
                })
                .setNeutralButton("点错了", null)
                .show();
    }

    static private void quitClub(final Activity activity, MyClub myClub) {
        final BaseActivity baseActivity = (BaseActivity) activity;
        ApiHelper.getProxy(baseActivity)
                .quitClub(String.valueOf(myClub.clubId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        MainActivity.needUpdateFlag.clubList = true;
                        MainActivity.startActivity(baseActivity, null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        baseActivity.showSnackbar(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
