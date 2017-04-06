package com.ethan.myclub.club.info.edit.viewmodel;

import android.app.Activity;
import android.content.DialogInterface;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import com.ethan.myclub.R;
import com.ethan.myclub.club.info.edit.view.ClubInfoEditActivity;
import com.ethan.myclub.club.info.model.Club;
import com.ethan.myclub.club.info.model.Tag;
import com.ethan.myclub.club.info.model.Tag2;
import com.ethan.myclub.databinding.ActivityClubInfoEditBinding;
import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.main.ImageSelectActivity;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.util.CacheUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ClubInfoEditViewModel {

    private ClubInfoEditActivity mActivity;
    private ActivityClubInfoEditBinding mBinding;

    public ObservableField<Uri> mImageUri = new ObservableField<>();
    public ObservableField<String> mTags = new ObservableField<>();


    public Club mClub;
    private String originTags;

    public ClubInfoEditViewModel(ClubInfoEditActivity activity, ActivityClubInfoEditBinding binding) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);

        mActivity.getToolbarWrapper()
                .setTitle("修改社团资料")
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
                .showNavIcon(R.drawable.ic_toolbar_clear_white_24dp, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.onBackPressed();
                    }
                })
                .show();

    }

    private File mBadgeFile;
    private boolean mIsBadgeEdited = false;
    private boolean isTagsEdited(){
        return !originTags.equals(mTags.get());
    }

    public void editBadge() {
        mActivity.selectPicture("badge.temp.jpg", new ImageSelectActivity.OnFinishSelectImageListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri) {
                mImageUri.set(outputUri);
                mIsBadgeEdited = true;
                mBadgeFile = outputFile;
            }
        });
    }


    private void saveChanges() {
        if (mIsBadgeEdited)
            saveBadge();
        else if (mClub.mIsInfoEdited)
            saveInfo();
        else if (isTagsEdited())
            saveTags();
        else
            mActivity.showSnackbar("并没有资料被修改");
    }

    private void saveTags() {
        Tag2 tag = new Tag2();
        String[] tags = mTags.get().trim().split(" ");
        tag.tagName = new ArrayList<>();
        for (String s : tags) {
            if(!TextUtils.isEmpty(s))
                tag.tagName.add(s);
        }
        ApiHelper.getProxy(mActivity)
                .editClubTags(String.valueOf(mClub.id),tag)
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Club>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mActivity.showWaitingDialog("请稍候", "修改标签中", d);
                    }

                    @Override
                    public void onNext(Club club) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mActivity.showSnackbar("修改标签失败！" + e.getMessage());
                        e.printStackTrace();
                        mActivity.dismissDialog();
                    }

                    @Override
                    public void onComplete() {
                        mActivity.dismissDialog();
                        finishEdit();
                    }
                });

    }

    private void saveInfo() {
        ApiHelper.getProxy(mActivity)
                .modifyClub(String.valueOf(mClub.id), mClub.clubName, "1", mClub.briefIntroduce, mClub.contact)
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Club>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mActivity.showWaitingDialog("请稍候", "修改信息中", d);
                    }

                    @Override
                    public void onNext(Club club) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mActivity.showSnackbar("修改信息失败！" + e.getMessage());
                        e.printStackTrace();
                        mActivity.dismissDialog();
                    }

                    @Override
                    public void onComplete() {
                        mActivity.dismissDialog();
                        if (!isTagsEdited()) {
                            finishEdit();
                        } else
                            saveTags();
                    }
                });
    }

    private void saveBadge() {
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), mBadgeFile);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("badge", mBadgeFile.getName(), requestFile);
        ApiHelper.getProxy(mActivity)
                .uploadClubBadge(String.valueOf(mClub.id), body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<Object>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                mActivity.showWaitingDialog("请稍候", "上传头像中", d);
                            }

                            @Override
                            public void onNext(Object o) {

                            }

                            @Override
                            public void onError(Throwable e) {
                                mActivity.showSnackbar("上传头像失败！" + e.getMessage());
                                e.printStackTrace();
                                mActivity.dismissDialog();
                            }

                            @Override
                            public void onComplete() {
                                mActivity.dismissDialog();
                                if (!mClub.mIsInfoEdited) {
                                    finishEdit();
                                } else
                                    saveInfo();
                            }
                        });
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

    private void finishEdit() {
        CacheUtil.get(mActivity).remove(Preferences.CACHE_USER_CLUB_LIST);
        mActivity.setResult(Activity.RESULT_OK);
        ActivityCompat.finishAfterTransition(mActivity);
    }

    public void onBackPressed() {
        if (mIsBadgeEdited || mClub.mIsInfoEdited || isTagsEdited())
            showAlertDialog();
        else
            ActivityCompat.finishAfterTransition(mActivity);
    }

    public void setClub(Club club) {
        mClub = club;
        if (!TextUtils.isEmpty(mClub.badge))
            mImageUri.set(Uri.parse(mClub.badge + "?imageView2/0/w/300/h/300"));

        String tagStr = "";
        for (Tag tag : mClub.tag) {
            tagStr += tag.tagName + " ";
        }
        originTags = tagStr;
        mTags.set(tagStr);
    }
}