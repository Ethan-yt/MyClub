package com.ethan.myclub.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ethan.myclub.R;
import com.ethan.myclub.global.Preferences;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //程序初始化操作

        //读取配置项
        Preferences.initPreferencesEngine(this);

        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        //Thread.sleep(2000);
        startActivity(intent);
        finish();
    }
}
