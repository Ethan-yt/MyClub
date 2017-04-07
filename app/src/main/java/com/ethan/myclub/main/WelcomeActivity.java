package com.ethan.myclub.main;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;

import com.ethan.myclub.R;
import com.ethan.myclub.activity.detail.view.ActivityDetailActivity;
import com.ethan.myclub.global.Preferences;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //程序初始化操作

        //读取配置项
        Preferences.initPreferencesEngine(this);
        Observable.timer(0, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {

                        MainActivity.startActivity(WelcomeActivity.this,
                                ActivityOptionsCompat.makeCustomAnimation(WelcomeActivity.this,
                                        android.R.anim.fade_in,
                                        android.R.anim.fade_out).toBundle()
                        );

                        finish();
                    }
                });
    }
}
