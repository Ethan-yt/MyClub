package com.ethan.myclub.views.user.info;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ethan.myclub.R;
import com.ethan.myclub.models.network.ApiResponse;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.utils.dialogs.WaitingDialogHelper;
import com.ethan.myclub.views.main.SnackbarActivity;
import com.ethan.myclub.views.user.AvatarImageView;

import java.io.File;
import java.io.FileNotFoundException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class InfoActivity extends SnackbarActivity {

    private static final int REQUESTCODE_CAMERA = 1;
    private static final int REQUESTCODE_PICK = 2;
    private static final int REQUESTCODE_CROP = 3;
    private View mIvAvatarBg;
    private BottomSheetDialog mBottomSheetDialog;
    private AvatarImageView mIvAvatar;
    private File mAvatarFile;
    private Uri mAvatarUri;
    private boolean mIsAvatarEdited = false;
    private boolean mIsInfoEdited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_toolbar_user_info);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else
                    onBackPressed();
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

        mIvAvatarBg = findViewById(R.id.iv_avatar_bg);
        mIvAvatarBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAvatar();
            }
        });
        mIvAvatar = (AvatarImageView) findViewById(R.id.iv_avatar);

    }

    private void editAvatar() {
        final View view = LayoutInflater.from(InfoActivity.this).inflate(R.layout.item_select_photo, (ViewGroup) mRootLayout);
        mBottomSheetDialog = new BottomSheetDialog(InfoActivity.this);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });

        mBottomSheetDialog.show();

        mAvatarFile = new File(getExternalCacheDir(), "avatar.temp.jpg");
        mAvatarUri = Uri.fromFile(mAvatarFile);

        view.findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //下面这句指定调用相机拍照后的照片存储的路径
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, mAvatarUri);
                startActivityForResult(takeIntent, REQUESTCODE_CAMERA);
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
                        startActivityForResult(pickIntent, REQUESTCODE_PICK);
                        mBottomSheetDialog.dismiss();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)// 用户点击取消操作
            return;
        switch (requestCode) {
            case REQUESTCODE_PICK:// 直接从相册获取
                if (data == null)
                    return;
                else
                    startPhotoCrop(data.getData());
                break;
            case REQUESTCODE_CAMERA:// 调用相机拍照
                startPhotoCrop(mAvatarUri);
                break;
            case REQUESTCODE_CROP:// 取得裁剪后的图片
                setPicToView(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPhotoCrop(Uri uri) {
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
        startActivityForResult(intent, REQUESTCODE_CROP);
    }

    private void setPicToView(Intent picData) {
        if (picData == null)
            return;
        Bundle extras = picData.getExtras();
        if (extras != null) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mAvatarUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            mIvAvatar.setImageBitmap(bitmap);
            mIsAvatarEdited = true;
        }
    }

    private void saveChanges() {
        if (mIsAvatarEdited)
            saveAvatar();
        if (mIsInfoEdited)
            saveInfo();
    }

    private void saveInfo() {

    }

    private void saveAvatar() {
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), mAvatarFile);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("avatar", mAvatarFile.getName(), requestFile);
        ApiHelper.getProxy(this)
                .uploadAvatar(body)
                .subscribe(
                new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        WaitingDialogHelper.show(InfoActivity.this, "上传头像中");
                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        InfoActivity.this.showSnackbar("上传头像失败！" + e.getMessage());
                        e.printStackTrace();
                        WaitingDialogHelper.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        WaitingDialogHelper.dismiss();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (mIsAvatarEdited || mIsInfoEdited)
            showAlertDialog();
        else
            super.onBackPressed();

    }

    private void showAlertDialog() {
        new AlertDialog.Builder(this)
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
                        finish();
                    }
                })
                .show();
    }

    @Override
    protected void setRootLayout() {
        mRootLayout = findViewById(R.id.container);
    }
}
