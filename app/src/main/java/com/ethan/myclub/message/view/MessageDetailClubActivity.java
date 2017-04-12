package com.ethan.myclub.message.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.databinding.ActivityMessageDetailClubBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.message.model.Message;
import com.ethan.myclub.message.viewmodel.MessageDetailClubViewModel;

public class MessageDetailClubActivity extends BaseActivity {

    private MessageDetailClubViewModel mViewModel;

    public static void start(Activity from, Message message) {
        Intent intent = new Intent(from, MessageDetailClubActivity.class);
        intent.putExtra("message", message);
        ActivityCompat.startActivity(from, intent, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMessageDetailClubBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_message_detail_club);
        Message message = (Message) getIntent().getSerializableExtra("message");
        mViewModel = new MessageDetailClubViewModel(this, binding, message);

    }

}
