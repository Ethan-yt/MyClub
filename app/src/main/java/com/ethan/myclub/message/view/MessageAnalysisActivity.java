package com.ethan.myclub.message.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.databinding.ActivityMessageAnalysisBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.message.model.Message;
import com.ethan.myclub.message.viewmodel.MessageAnalysisViewModel;

public class MessageAnalysisActivity extends BaseActivity {

    private MessageAnalysisViewModel mViewModel;

    public static void start(Activity from, MyClub myClub, Message item) {
        Intent intent = new Intent(from, MessageAnalysisActivity.class);
        intent.putExtra("club", myClub);
        intent.putExtra("msg", item);
        ActivityCompat.startActivity(from, intent, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMessageAnalysisBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_message_analysis);
        MyClub myClub = (MyClub) getIntent().getSerializableExtra("club");
        Message item = (Message) getIntent().getSerializableExtra("msg");
        mViewModel = new MessageAnalysisViewModel(this, binding, myClub, item);

    }

}
