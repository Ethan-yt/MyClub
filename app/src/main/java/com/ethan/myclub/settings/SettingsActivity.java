package com.ethan.myclub.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.main.ToolbarWrapper;

/**
 * Created by ethan on 2017/4/14.
 */

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        getFragmentManager()
                .beginTransaction()
                .add(R.id.frag_setting, new SettingsFragment())
                .commit();

        new ToolbarWrapper.Builder(this)
                .showBackIcon()
                .setTitle("设置")
                .show();
    }

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        ActivityCompat.startActivity(activity, intent, null);

    }
}
