package com.ethan.myclub.club.operation;

import android.app.Activity;
import android.support.annotation.Keep;

import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.message.view.MessageListActivity;

/**
 * Created by ethan on 2017/4/13.
 */
@Keep
public class ClubMessage {

    static public void start(final Activity baseActivity, final MyClub myClub) {
        MessageListActivity.start(baseActivity, myClub, 1);
    }
}
