package com.ethan.myclub.user.schedule;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;

import com.ethan.myclub.R;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.user.schedule.model.Schedule;
import com.ethan.myclub.util.Utils;

import java.util.ArrayList;

public class ScheduleActivity extends BaseActivity {

    public static final int REQUEST_LOGIN = 1;
    public static final String FILE_NAME_SCHEDULE = "Schedules.dat";
    ScheduleView mScheduleView;
    ArrayList<Schedule> mSchedules = new ArrayList<>();
    String mCurrentYear;
    String mCurrentTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        new ToolbarWrapper(this, "时间管理")
                .showBackIcon()
                .setMenuAndListener(R.menu.menu_toolbar_schedule,
                        new Toolbar.OnMenuItemClickListener() {
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
                        })
                .moveFirstChildDown()
                .show();

        mScheduleView = (ScheduleView) findViewById(R.id.scheduleView);

        read();
        refreshScheduleView();
    }

    private void setCurrentSchedule() {
        final SchedulePickerView v = new SchedulePickerView(this);
        v.setSchedules(mSchedules);
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
                        savePreferences();
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
        //startActivity(intent);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ScheduleActivity.REQUEST_LOGIN && resultCode == RESULT_OK) {
            mSchedules = data.getParcelableArrayListExtra("Schedules");
            mCurrentYear = data.getStringExtra("Year");
            mCurrentTerm = data.getStringExtra("Term");
            save();
            refreshScheduleView();
        }

    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        mSchedules = intent.getParcelableArrayListExtra("Schedules");
//        mCurrentYear = intent.getStringExtra("Year");
//        mCurrentTerm = intent.getStringExtra("Term");
//
//        save();
//
//        refreshScheduleView();
//    }

    private BottomSheetDialog mBottomSheetDialog;

    public void save() {
        Parcel parcel = Parcel.obtain();
        parcel.writeList(mSchedules);
        Utils.saveParcelToFile(this, FILE_NAME_SCHEDULE, parcel);
        savePreferences();
    }

    public void savePreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("schedule", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CurrentYear", mCurrentYear);
        editor.putString("CurrentTerm", mCurrentTerm);
        editor.apply();
    }

    public void refreshScheduleView() {

        for (Schedule schedule : mSchedules) {
            if (schedule.getYear().equals(mCurrentYear) &&
                    schedule.getTerm().equals(mCurrentTerm)) {
                mScheduleView.setSchedule(schedule);
                return;
            }
        }
    }

    public void read() {
        Parcel parcel = Utils.readParcelFromFile(this, FILE_NAME_SCHEDULE);
        if (parcel != null) {
            parcel.readList(mSchedules, Schedule.class.getClassLoader());
            parcel.recycle();
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
