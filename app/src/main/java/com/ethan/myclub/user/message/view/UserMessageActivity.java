package com.ethan.myclub.user.message.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.databinding.ActivityUserMessageBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.user.message.viewmodel.UserMessageViewModel;
import com.ethan.myclub.user.model.MessageFeedBack;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class UserMessageActivity extends BaseActivity {

    private UserMessageViewModel mViewModel;

    public static void start(Activity from, List<MessageFeedBack> messageFeedBacks) {
        Intent intent = new Intent(from, UserMessageActivity.class);
        intent.putExtra("messages", messageFeedBacks.toArray());
        ActivityCompat.startActivity(from, intent, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUserMessageBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_user_message);
        MessageFeedBack[] messageList = (MessageFeedBack[]) getIntent().getSerializableExtra("messages");

        mViewModel = new UserMessageViewModel(this, binding, Arrays.asList(messageList));

    }

}
