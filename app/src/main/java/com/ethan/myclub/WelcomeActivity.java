package com.ethan.myclub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ethan.myclub.main.MainActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //getSupportActionBar().hide();
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        //Thread.sleep(2000);
        startActivity(intent);

        finish();
    }
}
