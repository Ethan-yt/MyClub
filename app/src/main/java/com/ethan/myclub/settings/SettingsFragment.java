package com.ethan.myclub.settings;

/**
 * Created by ethan on 2017/4/14.
 */

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;

import com.ethan.myclub.R;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.main.MainActivity;
import com.ethan.myclub.main.MyApplication;
import com.ethan.myclub.network.OAuthHelper;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.tencent.bugly.beta.Beta;

import io.reactivex.functions.Consumer;

public class SettingsFragment extends PreferenceFragment {
    private static final String[] LIBRARIES = {
            "ahbottomnavigation",
            "gson",
            "glide",
            "Jsoup",
            "OkHttp",
            "Retrofit",
            "rxjava",
            "rxandroid",

    };
    private static final String[] EXCLUDED_LABRARIES = {
            "AndroidIconics",
            "fastadapter",
            "appcompat_v7",
            "design",
            "recyclerview_v7",
            "support_v4"
    };

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.app_preferences);
        findPreference("about").setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        //config About Library
                        //new Libs.Builder().withActivityTitle("About").withFields(R.string.class.getFields()).start(getActivity());
                        new LibsBuilder()
                                //provide a style (optional) (LIGHT, DARK, LIGHT_DARK_TOOLBAR)
                                .withActivityStyle(Libs.ActivityStyle.LIGHT)
                                .withActivityTitle("关于")
                                .withLibraries(LIBRARIES)
                                .withExcludedLibraries(EXCLUDED_LABRARIES)
                                //start the activity
                                .start(getActivity());
                        return false;
                    }
                });
        findPreference("check_update").setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Beta.checkUpgrade();
                        return false;
                    }
                });
        findPreference("logout").setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        new AlertDialog.Builder(SettingsFragment.this.getActivity())
                                .setMessage("确定要退出吗")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        logout();
                                    }
                                })
                                .setNegativeButton("点错了", null)
                                .show();
                        return false;
                    }
                });
    }

    private void logout() {

        OAuthHelper.getProxy((BaseActivity) SettingsFragment.this.getActivity())
                .revokeToken(MyApplication.getToken().mAccessToken)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object object) throws Exception {

                    }
                });
        MyApplication.setToken(SettingsFragment.this.getActivity(), null);
        MainActivity.startActivity(SettingsFragment.this.getActivity(), BaseActivity.REQUEST_LOGOUT, Activity.RESULT_OK);
    }
}