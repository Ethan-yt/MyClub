package com.ethan.myclub.activity.edit.viewmodel;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.databinding.ObservableField;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.TimeUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.ethan.myclub.R;
import com.ethan.myclub.activity.edit.view.ActivityEditActivity;
import com.ethan.myclub.activity.edit.view.ActivityEditContentActivity;
import com.ethan.myclub.activity.model.Activity;
import com.ethan.myclub.club.activitylist.view.ClubActivityListActivity;
import com.ethan.myclub.club.model.Tag;
import com.ethan.myclub.club.model.Tag2;
import com.ethan.myclub.databinding.ActivityActivityEditBinding;
import com.ethan.myclub.discover.activity.model.ActivityResult;
import com.ethan.myclub.main.ImageSelectActivity;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.util.Utils;

import java.io.File;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class ActivityEditViewModel {

    private ActivityEditActivity mActivity;
    private ActivityActivityEditBinding mBinding;
    private ActivityResult mActivityResult;

    public ObservableField<Activity> mActivityDetail = new ObservableField<>();

    public ObservableField<Uri> mImage1Uri = new ObservableField<>();
    public ObservableField<Uri> mImage2Uri = new ObservableField<>();
    private File mImage1File;
    private File mImage2File;

    public ObservableField<String> mTags = new ObservableField<>();
    private String originTags;

    private boolean mIsImg1Edited = false;
    private boolean mIsImg2Edited = false;

    private boolean isTagsEdited() {
        return !originTags.equals(mTags.get());
    }

    private Calendar c = Calendar.getInstance();

    public ActivityEditViewModel(ActivityEditActivity activity, ActivityActivityEditBinding binding, ActivityResult activityResult) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);

        mActivity.getToolbarWrapper()
                .setTitle("修改活动")
                .setMenu(R.menu.toolbar_user_info, new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int menuItemId = item.getItemId();
                        switch (menuItemId) {
                            case R.id.action_confirm:
                                saveChanges();
                                break;
                        }
                        return true;
                    }
                })
                .showNavIcon(R.drawable.ic_toolbar_clear_white_24dp, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.onBackPressed();
                    }
                })
                .show();
        mActivityResult = activityResult;

        getDetail();

        mBinding.fabMenu.setClosedOnTouchOutside(true);

    }

    public void uploadContent() {
        mBinding.fabMenu.close(true);
        ActivityEditContentActivity.start(mActivity, String.valueOf(mActivityResult.id));
    }

    public void delete() {
        mBinding.fabMenu.close(true);
        new AlertDialog.Builder(mActivity)
                .setTitle("提示")
                .setMessage("真的要删除这个活动吗？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ApiHelper.getProxy(mActivity)
                                .deleteActivity(String.valueOf(mActivityResult.id))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        new Observer<Object>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {
                                                mActivity.showWaitingDialog("请稍候", "删除活动中", d);
                                            }

                                            @Override
                                            public void onNext(Object o) {

                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                mActivity.showSnackbar("删除活动失败！" + e.getMessage());
                                                e.printStackTrace();
                                                mActivity.dismissDialog();
                                            }

                                            @Override
                                            public void onComplete() {
                                                mActivity.dismissDialog();
                                                mActivity.setResult(ClubActivityListActivity.RESULT_DELETED);
                                                mActivity.finish();
                                            }
                                        });


                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void getDetail() {
        ApiHelper.getProxyWithoutToken(mActivity)
                .getActivity(String.valueOf(mActivityResult.id))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Activity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mActivity.showWaitingDialog("请稍候", "正在获取活动详情", d);
                    }

                    @Override
                    public void onNext(final Activity activity) {
                        if (!TextUtils.isEmpty(activity.homePageImg))
                            mImage1Uri.set(Uri.parse(activity.homePageImg + "?imageView2/0/w/300/h/200"));
                        if (!TextUtils.isEmpty(activity.specialIndexImage))
                            mImage2Uri.set(Uri.parse(activity.specialIndexImage + "?imageView2/0/w/200/h/300"));


                        String tagStr = "";
                        for (Tag tag : activity.tag) {
                            tagStr += tag.tagName + " ";
                        }
                        originTags = tagStr;
                        mTags.set(tagStr);

                        activity.activityTime = Utils.apiDate2StdDate(activity.activityTime);
                        mActivityDetail.set(activity);


                        if (activity.activityTime == null)
                            c.setTimeInMillis(System.currentTimeMillis());
                        else {
                            Date date = Utils.StdDate2Date(activity.activityTime);
                            c.setTime(date);
                        }



                        mBinding.etDatetime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (hasFocus) {
                                    int mYear = c.get(Calendar.YEAR);
                                    int mMonth = c.get(Calendar.MONTH);
                                    int mDay = c.get(Calendar.DAY_OF_MONTH);

                                    new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                            c.set(Calendar.YEAR, year);
                                            c.set(Calendar.MONTH, month);
                                            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                            int mHour = c.get(Calendar.HOUR_OF_DAY);
                                            int mMinute = c.get(Calendar.MINUTE);
                                            new TimePickerDialog(mActivity,
                                                    new TimePickerDialog.OnTimeSetListener() {

                                                        @Override
                                                        public void onTimeSet(TimePicker view,
                                                                              int hourOfDay, int minute) {
                                                            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                            c.set(Calendar.MINUTE, minute);
                                                            c.set(Calendar.SECOND, 0); // 设为 0
                                                            c.set(Calendar.MILLISECOND, 0); // 设为 0
                                                            mActivityDetail.get().setActivityTime(Utils.Date2StdDate(c.getTime()));
                                                            mActivityDetail.notifyChange();
                                                        }
                                                    }, mHour, mMinute, true).show();
                                        }
                                    }, mYear, mMonth, mDay).show();

                                }

                            }
                        });

                    }

                    @Override
                    public void onError(Throwable e) {
                        mActivity.dismissDialog();
                        mActivity.setResult(ClubActivityListActivity.RESULT_ERROR);
                        mActivity.finish();
                    }

                    @Override
                    public void onComplete() {
                        mActivity.dismissDialog();
                    }
                });
    }

    public void editImg1() {
        mActivity.selectPicture("img1.temp.jpg", 1080, 720, 3, 2, new ImageSelectActivity.OnFinishSelectImageListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri) {
                mImage1Uri.set(outputUri);
                mIsImg1Edited = true;
                mImage1File = outputFile;
            }
        });
    }

    public void editImg2() {
        mActivity.selectPicture("img2.temp.jpg", 720, 1080, 2, 3, new ImageSelectActivity.OnFinishSelectImageListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri) {
                mImage2Uri.set(outputUri);
                mIsImg2Edited = true;
                mImage2File = outputFile;
            }
        });
    }


    public void onBackPressed() {
        if (mIsImg1Edited || mIsImg2Edited || mActivityDetail.get().mIsInfoEdited || isTagsEdited())
            showAlertDialog();
        else
            ActivityCompat.finishAfterTransition(mActivity);
    }

    private void showAlertDialog() {
        new AlertDialog.Builder(mActivity)
                .setTitle("提示")
                .setMessage("您的修改还未保存，是否保存？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveChanges();
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.finishAfterTransition(mActivity);
                    }
                })
                .show();
    }

    private void saveChanges() {
        if (mIsImg1Edited)
            saveImg1();
        else if (mIsImg2Edited)
            saveImg2();
        else if (mActivityDetail.get().mIsInfoEdited)
            saveInfo();
        else if (isTagsEdited())
            saveTags();
        else {
            finishEdit();
        }
    }

    private void finishEdit() {
        mActivity.setResult(android.app.Activity.RESULT_OK);
        mActivity.finish();
    }

    private void saveImg1() {
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), mImage1File);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("home_page_img", mImage1File.getName(), requestFile);
        ApiHelper.getProxy(mActivity)
                .uploadActivityImage(String.valueOf(mActivityResult.id), body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<Object>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                mActivity.showWaitingDialog("请稍候", "上传首页图片中", d);
                            }

                            @Override
                            public void onNext(Object o) {

                            }

                            @Override
                            public void onError(Throwable e) {
                                mActivity.showSnackbar("上传首页图片失败！" + e.getMessage());
                                e.printStackTrace();
                                mActivity.dismissDialog();
                            }

                            @Override
                            public void onComplete() {
                                mActivity.dismissDialog();
                                mIsImg1Edited = false;
                                saveChanges();
                            }
                        });
    }

    private void saveImg2() {
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), mImage2File);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("special_index_image", mImage2File.getName(), requestFile);
        ApiHelper.getProxy(mActivity)
                .uploadActivityImage(String.valueOf(mActivityResult.id), body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<Object>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                mActivity.showWaitingDialog("请稍候", "上传精品活动图片中", d);
                            }

                            @Override
                            public void onNext(Object o) {

                            }

                            @Override
                            public void onError(Throwable e) {
                                mActivity.showSnackbar("上传精品活动图片失败！" + e.getMessage());
                                e.printStackTrace();
                                mActivity.dismissDialog();
                            }

                            @Override
                            public void onComplete() {
                                mActivity.dismissDialog();
                                mIsImg2Edited = false;
                                saveChanges();
                            }
                        });
    }

    private void saveInfo() {
        ApiHelper.getProxy(mActivity)
                .modifyActivity(String.valueOf(mActivityResult.id), mActivityDetail.get().getName(), mActivityDetail.get().getJoinMembersMax(), Utils.StdDate2ApiDate(mActivityDetail.get().getActivityTime()), mActivityDetail.get().getBriefIntroduction(), mActivityDetail.get().getLocation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<Object>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                mActivity.showWaitingDialog("请稍候", "修改活动信息中", d);
                            }

                            @Override
                            public void onNext(Object o) {

                            }

                            @Override
                            public void onError(Throwable e) {
                                mActivity.showSnackbar("修改活动信息失败！" + e.getMessage());
                                e.printStackTrace();
                                mActivity.dismissDialog();
                            }

                            @Override
                            public void onComplete() {
                                mActivity.dismissDialog();
                                mActivityDetail.get().mIsInfoEdited = false;
                                saveChanges();
                            }
                        });
    }

    private void saveTags() {
        Tag2 tag = new Tag2();
        String[] tags = mTags.get().trim().split(" ");
        tag.tagName = new ArrayList<>();
        for (String s : tags) {
            if (!TextUtils.isEmpty(s))
                tag.tagName.add(s);
        }
        ApiHelper.getProxy(mActivity)
                .editActivityTags(String.valueOf(mActivityResult.id), tag)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<Object>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                mActivity.showWaitingDialog("请稍候", "修改活动标签中", d);
                            }

                            @Override
                            public void onNext(Object o) {

                            }

                            @Override
                            public void onError(Throwable e) {
                                mActivity.showSnackbar("修改活动标签失败！" + e.getMessage());
                                e.printStackTrace();
                                mActivity.dismissDialog();
                            }

                            @Override
                            public void onComplete() {
                                mActivity.dismissDialog();
                                originTags = mTags.get();
                                saveChanges();
                            }
                        });
    }


}