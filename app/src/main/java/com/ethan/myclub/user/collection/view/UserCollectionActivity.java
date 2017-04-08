package com.ethan.myclub.user.collection.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ethan.myclub.R;
import com.ethan.myclub.databinding.ActivityUserCollectionBinding;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.user.collection.viewmodel.UserCollectionViewModel;

public class UserCollectionActivity extends BaseActivity {

    public static final int REQUEST_ACTIVITY_DETAIL = 0x1233;
    public static final int RESULT_CHANGED = 0x4321;
    private UserCollectionViewModel mViewModel;

    public static void start(Activity from) {
        Intent intent = new Intent(from, UserCollectionActivity.class);
        ActivityCompat.startActivity(from, intent, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUserCollectionBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_user_collection);
        mViewModel = new UserCollectionViewModel(this, binding);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_ACTIVITY_DETAIL && resultCode == RESULT_CHANGED)
            mViewModel.update();
    }
}
