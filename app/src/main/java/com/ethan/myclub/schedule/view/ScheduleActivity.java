package com.ethan.myclub.schedule.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.ethan.myclub.R;
import com.ethan.myclub.club.my.view.EmptyView;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.schedule.model.Schedule;
import com.ethan.myclub.util.Utils;

import java.util.ArrayList;

public class ScheduleActivity extends BaseActivity {

    public static final int REQUEST_LOGIN = 1;
    public static final String FILE_NAME_SCHEDULE = "Schedules.dat";
    ScheduleView mScheduleView;
    ArrayList<Schedule> mSchedules = new ArrayList<>();
    String mCurrentYear;
    String mCurrentTerm;
    EmptyView mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        initToolbar();

        mScheduleView = (ScheduleView) findViewById(R.id.scheduleView);
        mEmptyView = (EmptyView) findViewById(R.id.ev);
        mEmptyView.showEmptyView("请导入课表", "请先点击右上角获取课程表哦！");
        read();
        refreshScheduleView();
    }

    private void initToolbar() {
        getToolbarWrapper()
                .dismiss()
                .setTitle("时间管理")
                .showBackIcon()
                .setMenu(R.menu.toolbar_user_schedule,
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
                        }, new OnFinishCreateMenu() {
                            @Override
                            public void onFinish(Menu menu) {
                                if (mSchedules.isEmpty()) {
                                    menu.findItem(R.id.action_setCurrentWeek).setVisible(false);
                                    menu.findItem(R.id.action_settings).setVisible(false);
                                }

                            }
                        })
                .show();
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
        LoginActivity.startActivityForResult(this, REQUEST_LOGIN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ScheduleActivity.REQUEST_LOGIN && resultCode == RESULT_OK) {
            initToolbar();
            mSchedules = data.getParcelableArrayListExtra("Schedules");
            mCurrentYear = data.getStringExtra("Year");
            mCurrentTerm = data.getStringExtra("Term");
            save();
            refreshScheduleView();
        }

    }

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
                mEmptyView.setVisibility(View.INVISIBLE);
                mScheduleView.setVisibility(View.VISIBLE);
                mScheduleView.setSchedule(schedule, mCurrentWeek);
                return;
            }
        }
        mScheduleView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
    }

    public void read() {
        try {
            Parcel parcel = Utils.readParcelFromFile(this, FILE_NAME_SCHEDULE);
            if (parcel != null) {
                parcel.readList(mSchedules, Schedule.class.getClassLoader());
                parcel.recycle();
            }
        } catch (Exception ignored) {

        }

        SharedPreferences sharedPreferences = getSharedPreferences("schedule", MODE_PRIVATE);

        mCurrentYear = sharedPreferences.getString("CurrentYear", "");
        mCurrentTerm = sharedPreferences.getString("CurrentTerm", "");
        mCurrentWeek = sharedPreferences.getInt("CurrentWeek", 1);
    }

    int mCurrentWeek = 1;

    private void setCurrentWeek() {

        final NumberPicker np = new NumberPicker(this);
        String[] week_id = new String[20];
        for (int i = 0; i < 20; i++) {
            week_id[i] = "第" + (i + 1) + "周";
        }

        np.setDisplayedValues(week_id);
        np.setMinValue(0);
        np.setMaxValue(week_id.length - 1);
        np.setValue(mCurrentWeek - 1);
        np.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(np);
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mCurrentWeek = np.getValue() + 1;
                SharedPreferences sharedPreferences = getSharedPreferences("schedule", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("CurrentWeek", mCurrentWeek);
                editor.apply();
                refreshScheduleView();
                mBottomSheetDialog = null;
            }
        });

        mBottomSheetDialog.show();
    }

    public static void startActivity(Activity activity, Bundle bundle) {
        Intent intent = new Intent(activity, ScheduleActivity.class);
        ActivityCompat.startActivity(activity, intent, bundle);
    }
}
