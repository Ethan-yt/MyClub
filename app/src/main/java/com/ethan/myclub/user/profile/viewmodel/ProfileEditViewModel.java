package com.ethan.myclub.user.profile.viewmodel;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ethan.myclub.R;
import com.ethan.myclub.databinding.ActivityProfileEditBinding;
import com.ethan.myclub.global.Preferences;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.user.profile.view.ProfileEditActivity;
import com.ethan.myclub.util.CacheUtil;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by ethan on 2017/3/20.
 */

public class ProfileEditViewModel {

    private static final String TAG = "ProfileEditViewModel";
    private ProfileEditActivity mActivity;
    public ActivityProfileEditBinding mBinding;

    public ProfileEditViewModel(ProfileEditActivity profileEditActivity, ActivityProfileEditBinding binding) {
        mActivity = profileEditActivity;
        mBinding = binding;
        mBinding.setViewModel(this);
        String imageUrl = mActivity.getIntent().getStringExtra("ImageUrl");
        if (!TextUtils.isEmpty(imageUrl))
            mBinding.setImageUri(Uri.parse(ApiHelper.BASE_URL + imageUrl));

        mActivity.getToolbarWrapper()
                .setTitle("编辑个人资料")
                .setMenuAndListener(R.menu.menu_toolbar_user_info, new Toolbar.OnMenuItemClickListener() {
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
                .showBackIcon()
                .show();
        mAvatarFile = new File(mActivity.getExternalCacheDir(), "avatar.camera.jpg");
        ;
        mAvatarUri = Uri.fromFile(mAvatarFile);
    }

    final private File mAvatarFile;
    final private Uri mAvatarUri;


    private boolean mIsAvatarEdited = false;
    private boolean mIsInfoEdited = false;

    private void saveChanges() {
        CacheUtil.get(mActivity).remove(Preferences.CACHE_USER_INFO);//先清空缓存，修改资料
        if (mIsAvatarEdited)
            saveAvatar();
        else if (mIsInfoEdited)
            saveInfo();
        else
            mActivity.showSnackbar("并没有资料被修改");
    }


    public void editAvatar() {
        final View view = LayoutInflater.from(mActivity).inflate(R.layout.view_select_photo, null);
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(mActivity);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //mBottomSheetDialog = null;
            }
        });

        mBottomSheetDialog.show();


        view.findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //下面这句指定调用相机拍照后的照片存储的路径
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, mAvatarUri);
                mActivity.startActivityForResult(takeIntent, ProfileEditActivity.REQUEST_CODE_CAMERA);
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
                        mActivity.startActivityForResult(pickIntent, ProfileEditActivity.REQUEST_CODE_PICK);
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
        mActivity.startActivityForResult(intent, ProfileEditActivity.REQUEST_CODE_CROP);
    }


    private void setPicToView(Intent picData) {
        if (picData == null)
            return;
        Bundle extras = picData.getExtras();
        if (extras != null) {
            //mBinding.ivAvatar.setImageURI(mAvatarUri);
            mBinding.setImageUri(mAvatarUri);
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
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), mAvatarFile);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("avatar", mAvatarFile.getName(), requestFile);
        ApiHelper.getProxy(mActivity)
                .uploadAvatar(body)
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
        if (resultCode != ProfileEditActivity.RESULT_OK)// 用户点击取消操作
            return;
        switch (requestCode) {
            case ProfileEditActivity.REQUEST_CODE_PICK:// 直接从相册获取
                if (data == null)
                    return;
                else
                    startPhotoCrop(data.getData());
                break;
            case ProfileEditActivity.REQUEST_CODE_CAMERA:// 调用相机拍照
                startPhotoCrop(mAvatarUri);
                break;
            case ProfileEditActivity.REQUEST_CODE_CROP:// 取得裁剪后的图片
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

    @BindingAdapter({"imageUri"})
    public static void loadImage(final ImageView view, Uri imageUri) {
        Object target;
        if (imageUri == null) {
            target = R.drawable.img_default_avatar;
        } else {
            target = imageUri;
        }
        Glide.with(view.getContext())
                .load(target)
                .listener(new RequestListener<Object, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Object model, Target<GlideDrawable> target, boolean isFirstResource) {
                        e.printStackTrace();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Object model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .crossFade()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .bitmapTransform(new CropCircleTransformation(view.getContext()))
                .into(view);
    }
}
