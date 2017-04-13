package com.ethan.myclub.club.operation;

import android.app.Activity;

import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.message.view.MessageListActivity;

/**
 * Created by ethan on 2017/4/13.
 */

public class Enroll {
    static public void start(final Activity baseActivity, final MyClub myClub) {
        MessageListActivity.start(baseActivity, myClub, 2);
    }
}
