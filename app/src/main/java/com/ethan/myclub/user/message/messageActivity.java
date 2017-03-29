package com.ethan.myclub.user.message;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ethan.myclub.R;

/**
 * Created by Administrator on 2017/3/10.
 */

public class messageActivity extends AppCompatActivity {
    private Button message,message1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unused_activity_message_message);

        message=(Button)this.findViewById(R.id.button0);
        message1=(Button)this.findViewById(R.id.button1);
        message.setTextColor(Color.parseColor("#80be58"));
        message.setBackgroundResource(R.drawable.btn_commen_selected);

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message.setTextColor(Color.parseColor("#80be58"));
                message.setBackgroundResource(R.drawable.btn_commen_selected);
                message1.setTextColor(Color.parseColor("#ffffff"));
                message1.setBackgroundResource(R.drawable.btn_commen1);
            }
        });

        message1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message1.setTextColor(Color.parseColor("#80be58"));
                message1.setBackgroundResource(R.drawable.btn_commen1_selected);
                message.setTextColor(Color.parseColor("#ffffff"));
                message.setBackgroundResource(R.drawable.btn_commen);
            }
        });
    }
}
