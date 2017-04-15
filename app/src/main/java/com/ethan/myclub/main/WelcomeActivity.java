package com.ethan.myclub.main;

import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;

import com.ethan.myclub.BuildConfig;
import com.ethan.myclub.R;

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

        int delay = 0;
        if (!BuildConfig.DEBUG) {
            delay = 2;
        }
        StatusBarCompat.translucentStatusBar(this, false);
        Observable.timer(delay, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
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
