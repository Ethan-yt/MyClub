package com.ethan.myclub.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ethan.myclub.R;
import com.ethan.myclub.user.profile.view.ProfileEditActivity;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by ethan on 2017/4/6.
 */

public abstract class ImageSelectActivity extends BaseActivity {

    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_PICK = 2;
    public static final int REQUEST_CODE_CROP = 3;

    public int mImageWidth = 500;
    public int mImageHeight = 500;

    @BindingAdapter({"selectedImageUri"})
    public static void loadImage(final ImageView view, Uri imageUri) {
        Object target;
        if (imageUri == null) {
            target = R.drawable.img_default_avatar;
        } else {
            target = imageUri;
        }
        Boolean skipMemoryCache = true;
        DiskCacheStrategy diskCacheStrategy = DiskCacheStrategy.NONE;

        if (imageUri != null && imageUri.getScheme().equals("http")) {
            skipMemoryCache = false;
            diskCacheStrategy = DiskCacheStrategy.ALL;
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
                .skipMemoryCache(skipMemoryCache)
                .diskCacheStrategy(diskCacheStrategy)
                .bitmapTransform(new CropCircleTransformation(view.getContext()))
                .into(view);
    }

    private Uri mOutputUri;
    private File mOutputFile;

    public void selectPicture(String fileName, @NonNull OnFinishSelectImageListener onFinishSelectImageListener) {
        mOnFinishSelectImageListener = onFinishSelectImageListener;
        mOutputFile = new File(getExternalCacheDir(), fileName);
        mOutputUri = Uri.fromFile(mOutputFile);
        final View view = LayoutInflater.from(this).inflate(R.layout.view_select_photo, null);
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //mBottomSheetDialog = null;
            }
        });

        mBottomSheetDialog.show();


        view.findViewById(R.id.btn_camera)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //下面这句指定调用相机拍照后的照片存储的路径
                        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputUri);
                        startActivityForResult(takeIntent, ProfileEditActivity.REQUEST_CODE_CAMERA);
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
                        startActivityForResult(pickIntent, ProfileEditActivity.REQUEST_CODE_PICK);
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
        intent.putExtra("outputX", mImageWidth);
        intent.putExtra("outputY", mImageHeight);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputUri);
        intent.putExtra("scaleUpIfNeeded", true); //黑边
        intent.putExtra("noFaceDetection", true); // no face detection
        this.startActivityForResult(intent, ProfileEditActivity.REQUEST_CODE_CROP);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)// 用户点击取消操作
            return;
        switch (requestCode) {
            case ProfileEditActivity.REQUEST_CODE_PICK:// 直接从相册获取
                if (data == null)
                    return;
                else
                    startPhotoCrop(data.getData());
                break;
            case ProfileEditActivity.REQUEST_CODE_CAMERA:// 调用相机拍照
                startPhotoCrop(mOutputUri);
                break;
            case ProfileEditActivity.REQUEST_CODE_CROP:// 取得裁剪后的图片

                if (data != null && data.getExtras() != null)
                    mOnFinishSelectImageListener.onFinish(mOutputFile, mOutputUri);
                break;
        }
    }

    private OnFinishSelectImageListener mOnFinishSelectImageListener;

    public interface OnFinishSelectImageListener {
        void onFinish(File outputFile, Uri outputUri);
    }

}