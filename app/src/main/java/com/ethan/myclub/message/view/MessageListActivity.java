package com.ethan.myclub.message.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.databinding.ActivityMessageListBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.main.MainActivity;
import com.ethan.myclub.message.receiver.MiPushMessageReceiver;
import com.ethan.myclub.message.viewmodel.MessageListViewModel;
import com.xiaomi.mipush.sdk.MiPushMessage;

public class MessageListActivity extends BaseActivity {

    private MessageListViewModel mViewModel;
    static public boolean needRefreshFlag = true;

    public static void start(Activity from, MyClub myClub) {
        Intent intent = new Intent(from, MessageListActivity.class);
        intent.putExtra("MyClub", myClub);
        ActivityCompat.startActivity(from, intent, null);
    }

    /**
     * @param mode 模式1：查看社团通知
     *             模式2：招新管理
     */
    public static void start(Activity from, MyClub myClub, int mode) {
        Intent intent = new Intent(from, MessageListActivity.class);
        intent.putExtra("MyClub", myClub);
        intent.putExtra("mode", mode);
        ActivityCompat.startActivity(from, intent, null);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMessageListBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_message_list);
        MyClub myClub = (MyClub) getIntent().getSerializableExtra("MyClub");
        int mode = getIntent().getIntExtra("mode", 0);
        mViewModel = new MessageListViewModel(this, binding, myClub, mode);
    }


    private MiPushMessageReceiver receiver = null;

    @Override
    protected void onResume() {
        super.onResume();
        if (mViewModel != null && needRefreshFlag)
            mViewModel.update();
        if (receiver == null) {
            receiver = new MiPushMessageReceiver() {
                @Override
                public void onReceivePassThroughMessage(Context context, MiPushMessage message1) {
                    super.onReceivePassThroughMessage(context, message1);
                    mViewModel.update();
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.xiaomi.mipush.RECEIVE_MESSAGE");
            intentFilter.addAction("com.xiaomi.mipush.MESSAGE_ARRIVED");
            intentFilter.addAction("com.xiaomi.mipush.ERROR");
            registerReceiver(receiver, intentFilter);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
            MainActivity.needUpdateFlag.userUnreadCount = true;
        }
    }
}
