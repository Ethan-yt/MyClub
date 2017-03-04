package com.ethan.myclub.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ethan.myclub.R;
import com.ethan.myclub.views.main.MainActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        //Thread.sleep(2000);
        startActivity(intent);
        finish();
    }
}
