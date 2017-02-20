package com.ethan.myclub.user.Schedule;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;

import com.ethan.myclub.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    ScheduleView mScheduleView;
    ArrayList<ScheduleModel> mScheduleModels = new ArrayList<>();
    String mCurrentYear;
    String mCurrentTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_schedule);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.inflateMenu(R.menu.menu_toolbar_schedule);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                switch (menuItemId) {
                    case R.id.action_inputCurriculum:
                        downloadSchedule();
                        break;
                    case R.id.action_setCurrentWeek:
                        setCurrentWeek();
                        break;
                    case R.id.action_settings:
                        // TODO: 2017/2/17 目前仅仅是设置当前学期，要增加其他设置，例如开学日期
                        setCurrentSchedule();
                        break;
                }
                return true;
            }
        });

        mScheduleView = (ScheduleView) findViewById(R.id.scheduleView);

        read();
        refreshScheduleView();
    }

    private void setCurrentSchedule() {
        final SchedulePickerView v = new SchedulePickerView(this);
        v.setSchedules(mScheduleModels);
        v.setYear(mCurrentYear);
        v.setTerm(mCurrentTerm);

        new AlertDialog.Builder(this)
                .setTitle("请选择当前的学年学期")
                .setView(v)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mCurrentYear = v.getYear();
                        mCurrentTerm = v.getTerm();
                        savePreferances();
                        refreshScheduleView();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    private void downloadSchedule() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mScheduleModels = intent.getParcelableArrayListExtra("Schedules");
        mCurrentYear = intent.getStringExtra("Year");
        mCurrentTerm = intent.getStringExtra("Term");

        save();

        refreshScheduleView();
    }

    private BottomSheetDialog mBottomSheetDialog;

    public void save() {
        try {
            FileOutputStream outStream = openFileOutput("Schedules.txt", Context.MODE_PRIVATE);

            Parcel parcel = Parcel.obtain();
            parcel.writeList(mScheduleModels);

            byte[] bytes = parcel.marshall();
            parcel.recycle();

            outStream.write(bytes);
            outStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        savePreferances();
    }

    public void savePreferances()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("schedule", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CurrentYear", mCurrentYear);
        editor.putString("CurrentTerm", mCurrentTerm);
        editor.apply();
    }
    public void refreshScheduleView() {

        for (ScheduleModel scheduleModel : mScheduleModels) {
            if (scheduleModel.getYear().equals(mCurrentYear) &&
                    scheduleModel.getTerm().equals(mCurrentTerm)) {
                mScheduleView.setScheduleModel(scheduleModel);
                return;
            }
        }
    }

    public void read() {
        try {

            FileInputStream fin = openFileInput("Schedules.txt");
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);

            Parcel parcel = Parcel.obtain();
            parcel.unmarshall(buffer, 0, buffer.length);
            parcel.setDataPosition(0); // This is extremely important!

            parcel.readList(mScheduleModels, ScheduleModel.class.getClassLoader());
            parcel.recycle();
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("schedule", MODE_PRIVATE);

        mCurrentYear = sharedPreferences.getString("CurrentYear", "");
        mCurrentTerm = sharedPreferences.getString("CurrentTerm", "");
    }

    private void setCurrentWeek() {

        NumberPicker np = new NumberPicker(this);
        String[] week_id = new String[20];
        for (int i = 0; i < 20; i++) {
            week_id[i] = "第" + (i + 1) + "周";
        }

        np.setDisplayedValues(week_id);
        np.setMinValue(0);
        np.setMaxValue(week_id.length - 1);

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(np);
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });

        mBottomSheetDialog.show();
    }
}
