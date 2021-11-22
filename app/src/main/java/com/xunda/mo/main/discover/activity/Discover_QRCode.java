package com.xunda.mo.main.discover.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.chat.activity.ChatFriend_Detail;
import com.xunda.mo.main.friend.activity.Friend_Group_Detail;
import com.xunda.mo.staticdata.GlideEnGine;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class Discover_QRCode extends BaseInitActivity implements QRCodeView.Delegate {
    private ZXingView mZXingView;
    private ImageView select_Picture_Img;
    private View return_Btn;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Discover_QRCode.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_discover_qrcode;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        return_Btn = findViewById(R.id.return_Btn);
        return_Btn.setOnClickListener(new return_BtnClick());
        mZXingView = findViewById(R.id.zxingview);
        mZXingView.hiddenScanRect();
        mZXingView.setDelegate(this);
        select_Picture_Img = findViewById(R.id.select_Picture_Img);
        select_Picture_Img.setOnClickListener(new select_Picture_ImgClick());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
//        mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别
//        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        mZXingView.startSpot(); // 开始识别
    }

    @Override
    protected void onStop() {
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        if (TextUtils.isEmpty(result)) {
            Toast.makeText(mContext, "无法识别", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(result)) {
            String strName = result.substring(0, result.indexOf("-"));
            String strUserNum = result.substring(strName.length() + 1, result.length());
            if (TextUtils.equals(strName, "user")) {
                String username = strUserNum;
                String addType = "7";
                ChatFriend_Detail.actionStartActivity(mContext, username, addType);
            } else if (TextUtils.equals(strName, "group")) {
                String GroupId = strUserNum;
                Friend_Group_Detail.actionStartHXID(mContext, GroupId);
            }
            finish();
        }
        vibrate();
//        mZXingView.startSpot(); // 开始识别
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }


    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {

    }

    @Override
    public void onScanQRCodeOpenCameraError() {

    }


    private class select_Picture_ImgClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startCamera();
        }
    }

    public void startCamera() {
        //监听授权
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(Discover_QRCode.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(Discover_QRCode.this, permissions, 1);
        } else {
            //打开相机录制视频
            Intent captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            //判断相机是否正常。
            if (captureIntent.resolveActivity(Discover_QRCode.this.getPackageManager()) != null) {
                setPhotoMetod(Discover_QRCode.this);
            }

        }
    }


    private void setPhotoMetod(Context context) {
        int choice = 1;
        PictureSelector.create((Activity) context)
                .openGallery(PictureConfig.TYPE_IMAGE)
                .imageEngine(GlideEnGine.createGlideEngine()) //图片加载空白 加入Glide加载图片
                .imageSpanCount(4)// 每行显示个数 int
                .maxSelectNum(choice)
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isSingleDirectReturn(true)//PictureConfig.SINGLE模式下是否直接返回
                .isAndroidQTransform(true)//Android Q版本下是否需要拷贝文件至应用沙盒内
                .isPreviewImage(true)// 是否可预览图片 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
//                .imageFormat(PictureMimeType.PNG_Q)//拍照图片格式后缀,默认jpeg, PictureMimeType.PNG，Android Q使用PictureMimeType.PNG_Q
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .isCompress(true)// 是否压缩 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    List<LocalMedia> selectList;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    if (!selectList.isEmpty()) {
                        if (isQ()) {
                            mZXingView.decodeQRCode(selectList.get(0).getAndroidQToPath());
                        } else {
                            mZXingView.decodeQRCode(selectList.get(0).getRealPath());
                        }
                    }
                    break;
            }
        }
    }


    private boolean isQ() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true;
        }
        return false;
    }


    private class return_BtnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }
}

