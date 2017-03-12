package com.ethan.myclub.views.user.info;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.ethan.myclub.R;
import com.ethan.myclub.global.Instances;
import com.ethan.myclub.views.user.AvatarImageView;

import java.io.File;

public class InfoActivity extends AppCompatActivity {

    private static final int REQUESTCODE_CAMERA = 1;
    private static final int REQUESTCODE_PICK = 2;
    private static final int REQUESTCODE_CROP = 3;
    private View mIvAvatarBg;
    private BottomSheetDialog mBottomSheetDialog;
    private AvatarImageView mIvAvatar;
    private File mAvatarFile;

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
                    finish();
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
        final View view = LayoutInflater.from(InfoActivity.this).inflate(R.layout.item_select_photo, null);
        mBottomSheetDialog = new BottomSheetDialog(InfoActivity.this);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });

        mBottomSheetDialog.show();

        view.findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAvatarFile = new File(Instances.sExternalStorageDirectory(), "avatar.temp.png");
                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //下面这句指定调用相机拍照后的照片存储的路径
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mAvatarFile));
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

        switch (requestCode) {
            case REQUESTCODE_PICK:// 直接从相册获取
                if (data == null)// 用户点击取消操作
                    return;
                else
                    startPhotoCrop(data.getData());
                break;
            case REQUESTCODE_CAMERA:// 调用相机拍照
                startPhotoCrop(Uri.fromFile(mAvatarFile));
                break;
            case REQUESTCODE_CROP:// 取得裁剪后的图片
                if (data != null) {
                    setPicToView(data);
                }
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
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CROP);
    }

    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            // 取得SDCard图片路径做显示
            Bitmap photo = extras.getParcelable("data");
            //Drawable drawable = new BitmapDrawable(null, photo);
            //urlpath = FileUtil.saveFile(mContext, "temphead.jpg", photo);
            mIvAvatar.setImageBitmap(photo);
            // 新线程后台上传服务端
        }
    }

    private void saveChanges() {

    }
}
