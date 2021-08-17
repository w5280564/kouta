package com.xunda.mo.main.me.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.model.UserDetail_Bean;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.SaveViewUtils;
import com.xunda.mo.staticdata.viewTouchDelegate;

import java.io.Serializable;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class MeAndGroup_QRCode extends BaseInitActivity {

    private UserDetail_Bean userBean;
    private ImageView head_Image, qrCode_Img,qrcode_down;
    public static String user = "user";
    public static String group = "group";
    private String meOrGroup;
    private TextView titleName, nick_Txt, moId_Txt;
    private ConstraintLayout meAndGroup;
    private GruopInfo_Bean groupBean;
    private Button return_Btn;

    public static void actionUserStart(Context context) {
        Intent intent = new Intent(context, MeAndGroup_QRCode.class);
        intent.putExtra("meOrGroup", user);
        context.startActivity(intent);
    }
    public static void actionUserStart(Context context, UserDetail_Bean bean) {
        Intent intent = new Intent(context, MeAndGroup_QRCode.class);
        intent.putExtra("bean", (Serializable) bean);
        intent.putExtra("meOrGroup", user);
        context.startActivity(intent);
    }

    public static void actionGroupStart(Context context, GruopInfo_Bean groupBean) {
        Intent intent = new Intent(context, MeAndGroup_QRCode.class);
        intent.putExtra("groupBean", (Serializable) groupBean);
        intent.putExtra("meOrGroup", group);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_meandgroup_qrcode;

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setFitSystemForTheme(true, R.color.yellowTitle);
        setStatusBarTextColor(false);
        meAndGroup = findViewById(R.id.meAndGroup);
        head_Image = findViewById(R.id.head_Image);
        titleName = findViewById(R.id.titleName);
        nick_Txt = findViewById(R.id.nick_Txt);
        moId_Txt = findViewById(R.id.moId_Txt);
        qrCode_Img = findViewById(R.id.qrCode_Img);
        qrcode_down = findViewById(R.id.qrcode_down);
        qrcode_down.setOnClickListener(new qrcode_downClick());
        return_Btn = findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn,50,50,50,50);
        return_Btn.setOnClickListener(new return_BtnClick());
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        userBean = (UserDetail_Bean) intent.getSerializableExtra("bean");
        groupBean = (GruopInfo_Bean) intent.getSerializableExtra("groupBean");
        meOrGroup = intent.getStringExtra("meOrGroup");
    }

    @Override
    protected void initData() {
        super.initData();

        if (TextUtils.equals(meOrGroup, user)) {

            MyInfo myInfo =new MyInfo(MeAndGroup_QRCode.this);
            titleName.setText("我的二维码");
            Glide.with(MeAndGroup_QRCode.this).load(myInfo.getUserInfo().getHeadImg())
                    .transforms(new CenterCrop(), new RoundedCorners(9)).into(head_Image);
            nick_Txt.setText(myInfo.getUserInfo().getNickname());
            moId_Txt.setText("Mo ID: " + myInfo.getUserInfo().getUserNum());
            String qrCodeStr = "user-" + myInfo.getUserInfo().getHxUserName();
            createEnglishQRCode(qrCodeStr);
        }else if (TextUtils.equals(meOrGroup, group)) {
            titleName.setText("群二维码");
            GruopInfo_Bean.DataDTO userData = groupBean.getData();
            Glide.with(MeAndGroup_QRCode.this).load(userData.getGroupHeadImg())
                    .transforms(new CenterCrop(), new RoundedCorners(9)).into(head_Image);
            nick_Txt.setText(userData.getGroupName());
            moId_Txt.setText("群ID: " + userData.getGroupNum());
            String qrCodeStr = "group-" + userData.getGroupHxId();
            createEnglishQRCode(qrCodeStr);
        }
    }

    private class return_BtnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void createEnglishQRCode(String content) {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                return QRCodeEncoder.syncEncodeQRCode(content, BGAQRCodeUtil.dp2px(MeAndGroup_QRCode.this, 150), Color.parseColor("#000000"));
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    Glide.with(MeAndGroup_QRCode.this).load(bitmap).into(qrCode_Img);
                } else {
//                    Toast.makeText(MeAndGroup_QRCode.this, "生成英文二维码失败", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private class qrcode_downClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            SaveViewUtils.saveBitmap(MeAndGroup_QRCode.this,meAndGroup);
        }
    }

}