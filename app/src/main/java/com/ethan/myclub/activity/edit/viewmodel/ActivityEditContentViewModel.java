package com.ethan.myclub.activity.edit.viewmodel;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ethan.myclub.R;
import com.ethan.myclub.activity.edit.view.ActivityEditContentActivity;
import com.ethan.myclub.activity.model.Content;
import com.ethan.myclub.databinding.ActivityActivityEditContentBinding;
import com.ethan.myclub.main.ImageSelectActivity;
import com.ethan.myclub.main.MainActivity;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.util.ImageUtils;
import com.ethan.myclub.util.Utils;
import com.google.android.flexbox.FlexboxLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ActivityEditContentViewModel {

    private ActivityEditContentActivity mActivity;
    private ActivityActivityEditContentBinding mBinding;

    private String mActivityId;

    private List<File> files = new ArrayList<>();
    private List<String> contents = new ArrayList<>();


    public ActivityEditContentViewModel(ActivityEditContentActivity activity, ActivityActivityEditContentBinding binding, String activityId) {
        mActivity = activity;
        mBinding = binding;
        mActivityId = activityId;
        mBinding.setViewModel(this);

        mActivity.getToolbarWrapper()
                .setTitle("上传活动内容")
                .showBackIcon()
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
                .show();
    }

    private void saveChanges() {
        List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for (File file : files) {
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            parts.add(body);
        }
        ApiHelper.getProxy(mActivity)
                .uploadActivityContentImage(mActivityId, parts)
                .flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull Object o) throws Exception {
                        Content content = new Content();
                        content.content = contents;

                        return ApiHelper.getProxy(mActivity)
                                .uploadActivityContentText(mActivityId, content);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mActivity.showWaitingDialog("请稍候", "上传中", d);
                    }

                    @Override
                    public void onNext(Object o) {
                        mActivity.showSnackbar("上传成功！");
                        Observable.timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(@NonNull Long aLong) throws Exception {
                                mActivity.finish();
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        mActivity.dismissDialog();
                        mActivity.showSnackbar("出错了：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mActivity.dismissDialog();
                    }
                });


    }

    public void addContent() {
        mActivity.selectPicture("content" + files.size() + ".temp.jpg", 1080, 720, 3, 2, new ImageSelectActivity.OnFinishSelectImageListener() {
            @Override
            public void onFinish(final File outputFile, final Uri outputUri) {

                final EditText et = new EditText(mActivity);
                et.setLines(5);
                et.setGravity(Gravity.TOP);

                new AlertDialog.Builder(mActivity).setTitle("输入当前图片描述")
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String input = et.getText().toString();
                                        if (input.isEmpty()) {
                                            mActivity.showSnackbar("请填写照片描述！");
                                            return;
                                        }
                                        ImageView iv = new ImageView(mActivity);

                                        mBinding.flImgs.addView(iv);
                                        FlexboxLayout.LayoutParams lp = (FlexboxLayout.LayoutParams) iv.getLayoutParams();
                                        lp.height = Utils.dp2px(mActivity, 80);
                                        lp.width = lp.height;
                                        int margin = Utils.dp2px(mActivity, 10);
                                        lp.setMargins(margin, margin, margin, margin);

                                        ImageUtils.loadImageUriRect(iv, outputUri);
                                        files.add(outputFile);
                                        contents.add(input);
                                        mActivity.showSnackbar("添加成功！");
                                    }
                                }
                        )
                        .setNegativeButton("取消", null)
                        .show();
            }
        });


    }
}