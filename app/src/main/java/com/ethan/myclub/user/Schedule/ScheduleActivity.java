package com.ethan.myclub.user.Schedule;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.support.design.widget.BottomSheetDialog;
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
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    ScheduleView mScheduleView;
    List<ScheduleModel> mScheduleModels;
    String mCurrentYear;
    int mCurrentTerm;
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
                switch (menuItemId)
                {
                    case R.id.action_inputCurriculum:
                        downloadSchedule();
                        break;
                    case R.id.action_setCurrentWeek:
                        setCurrentWeek();
                        break;
                    case R.id.action_settings:
                        setCurrentSchedule();
                        break;
                }
                return true;
            }
        });

        mScheduleView = (ScheduleView) findViewById(R.id.scheduleView);

        mScheduleModels = read();

        SharedPreferences sharedPreferences = getSharedPreferences("schedule", MODE_PRIVATE);

        mCurrentYear = sharedPreferences.getString("CurrentYear","");
        mCurrentTerm = sharedPreferences.getInt("CurrentTerm",0);

        for (ScheduleModel scheduleModel : mScheduleModels) {
            if(scheduleModel.getYear().equals(mCurrentYear) &&
                    scheduleModel.getTerm() == mCurrentTerm)
                mScheduleView.setScheduleModel(scheduleModel);
        }

    }

    private void setCurrentSchedule() {

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
        mCurrentTerm = intent.getIntExtra("Term",-1);

        save(mScheduleModels);
        SharedPreferences sharedPreferences = getSharedPreferences("schedule", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CurrentYear", mCurrentYear);
        editor.putInt("CurrentTerm", mCurrentTerm);
        editor.apply();

        for (ScheduleModel scheduleModel : mScheduleModels) {
            if(scheduleModel.getYear().equals(mCurrentYear) &&
                    scheduleModel.getTerm() == mCurrentTerm)
                mScheduleView.setScheduleModel(scheduleModel);
        }

    }

    private BottomSheetDialog mBottomSheetDialog;

    public void save(List<ScheduleModel> mScheduleModels) {

        try {
            FileOutputStream outStream = openFileOutput("Schedules.txt", Context.MODE_PRIVATE);

            Parcel parcel = Parcel.obtain();
            parcel.writeList(mScheduleModels);

            byte[] bytes = parcel.marshall();
            parcel.recycle();

            outStream.write(bytes);
            outStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ScheduleModel> read() {
        try {

            FileInputStream fin = openFileInput("Schedules.txt");
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);

            Parcel parcel = Parcel.obtain();
            parcel.unmarshall(buffer, 0, buffer.length);
            parcel.setDataPosition(0); // This is extremely important!

            List mScheduleModels = new ArrayList();
            parcel.readList(mScheduleModels, ScheduleModel.class.getClassLoader());
            parcel.recycle();
            fin.close();
            return mScheduleModels;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList();
    }

    private void setCurrentWeek() {

        NumberPicker np = new NumberPicker(this);
        String[] week_id = new String[20];
        for(int i = 0;i<20;i++)
        {
            week_id[i] = "第"+(i+1)+"周";
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
