package com.ethan.myclub.user.info.viewmodel;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.ethan.myclub.R;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.user.info.view.InfoActivity;
import com.ethan.myclub.user.main.view.AvatarImageView;
import com.ethan.myclub.user.schedule.model.Schedule;
import com.ethan.myclub.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.internal.Util;

/**
 * Created by ethan on 2017/3/20.
 */

public class InfoViewModel {

    private static final String TAG = "InfoViewModel";
    private InfoActivity mActivity;

    public InfoViewModel(InfoActivity infoActivity) {
        mActivity = infoActivity;
        Toolbar toolbar = infoActivity.mDataBinding.toolbar;
        toolbar.inflateMenu(R.menu.menu_toolbar_user_info);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
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
        });
    }

    private File mAvatarFile;
    private Uri mAvatarUri;

    private boolean mIsAvatarEdited = false;
    private boolean mIsInfoEdited = false;

    private void saveChanges() {
        if (mIsAvatarEdited)
            saveAvatar();
        else if (mIsInfoEdited)
            saveInfo();
        else
            mActivity.showSnackbar("并没有资料被修改");
    }


    public void editAvatar() {
        final View view = LayoutInflater.from(mActivity).inflate(R.layout.item_select_photo, null);
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(mActivity);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //mBottomSheetDialog = null;
            }
        });

        mBottomSheetDialog.show();

        mAvatarFile = new File(mActivity.getExternalCacheDir(), "avatar.camera.jpg");
        mAvatarUri = Uri.fromFile(mAvatarFile);

        view.findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //下面这句指定调用相机拍照后的照片存储的路径
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, mAvatarUri);
                mActivity.startActivityForResult(takeIntent, InfoActivity.REQUEST_CODE_CAMERA);
                mBottomSheetDialog.dismiss();
            }
        });
        view.findViewById(R.id.btn_pick)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                        // image/jpeg 、 image/png等的类型
                        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        mActivity.startActivityForResult(pickIntent, InfoActivity.REQUEST_CODE_PICK);
                        mBottomSheetDialog.dismiss();
                    }
                });
    }

    private void startPhotoCrop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", true);
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mAvatarUri);
        intent.putExtra("scaleUpIfNeeded", true); //黑边
        intent.putExtra("noFaceDetection", true); // no face detection
        mActivity.startActivityForResult(intent, InfoActivity.REQUEST_CODE_CROP);
    }


    private void setPicToView(Intent picData) {
        if (picData == null)
            return;
        Bundle extras = picData.getExtras();
        if (extras != null) {
            mActivity.mDataBinding.ivAvatar.setImageURI(mAvatarUri);
            mIsAvatarEdited = true;
        }
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

    private void saveAvatar() {
        Observable
                .create(new ObservableOnSubscribe<MultipartBody.Part>() {
                    @Override
                    public void subscribe(ObservableEmitter<MultipartBody.Part> e) throws Exception {
                        String md5 = Utils.getFileMd5(mAvatarFile);
                        RequestBody requestFile =
                                RequestBody.create(MediaType.parse("multipart/form-data"), mAvatarFile);
                        MultipartBody.Part body =
                                MultipartBody.Part.createFormData("avatar", md5 + ".jpg", requestFile);
                        e.onNext(body);
                        e.onComplete();

                    }
                })
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<MultipartBody.Part, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(MultipartBody.Part body) throws Exception {
                        return ApiHelper.getProxy(mActivity)
                                .uploadAvatar(body);
                    }
                })
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
                                if (!mIsInfoEdited)
                                    ActivityCompat.finishAfterTransition(mActivity);
                                else
                                    saveInfo();
                            }
                        });
    }

    private void saveInfo() {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != InfoActivity.RESULT_OK)// 用户点击取消操作
            return;
        switch (requestCode) {
            case InfoActivity.REQUEST_CODE_PICK:// 直接从相册获取
                if (data == null)
                    return;
                else
                    startPhotoCrop(data.getData());
                break;
            case InfoActivity.REQUEST_CODE_CAMERA:// 调用相机拍照
                startPhotoCrop(mAvatarUri);
                break;
            case InfoActivity.REQUEST_CODE_CROP:// 取得裁剪后的图片
                setPicToView(data);
                break;
        }
    }

    public void onBackPressed() {
        if (mIsAvatarEdited || mIsInfoEdited)
            showAlertDialog();
        else
            ActivityCompat.finishAfterTransition(mActivity);
    }
}
