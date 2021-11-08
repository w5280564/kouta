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
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
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
    private ImageView head_Image, qrCode_Img, qrcode_down;
    public static String user = "user";
    public static String group = "group";
    private String meOrGroup;
    private TextView titleName, nick_Txt, moId_Txt;
    private ConstraintLayout meAndGroup;
    private GruopInfo_Bean groupBean;
    private Button return_Btn;
    private ImageView qrcode_share;

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
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setOnClickListener(new return_BtnClick());
        qrcode_share = findViewById(R.id.qrcode_share);
        qrcode_share.setOnClickListener(new qrcode_shareClick());

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

            MyInfo myInfo = new MyInfo(MeAndGroup_QRCode.this);
            titleName.setText("我的二维码");
            Glide.with(MeAndGroup_QRCode.this).load(myInfo.getUserInfo().getHeadImg())
                    .transforms(new CenterCrop(), new RoundedCorners(9)).into(head_Image);
            nick_Txt.setText(myInfo.getUserInfo().getNickname());
            moId_Txt.setText("Mo ID: " + myInfo.getUserInfo().getUserNum());
            String qrCodeStr = "user-" + myInfo.getUserInfo().getHxUserName();
            createEnglishQRCode(qrCodeStr);
        } else if (TextUtils.equals(meOrGroup, group)) {
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
            SaveViewUtils.saveBitmap(MeAndGroup_QRCode.this, meAndGroup);
        }
    }

    private class qrcode_shareClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
//            UMImage thumb =new UMImage(MeAndGroup_QRCode.this, R.drawable.mo_icon);
//            UMWeb web =new UMWeb("https://www.baidu.com/");
//            web.setTitle("分享");//标题
//            web.setThumb(thumb);//缩略图
//            web.setDescription("分享内容");//描述
//            ShareAction shareAction = new ShareAction(MeAndGroup_QRCode.this);
//            shareAction.withMedia(web);
//            shareAction.setDisplayList(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE);
//            shareAction.share();
//            shareAction.setCallback(shareListener);
//            shareAction.open();

//            new ShareAction(MeAndGroup_QRCode.this).withText("hello").setDisplayList(SHARE_MEDIA.WEIXIN)
//                    .setCallback(shareListener).open();// SHARE_MEDIA.QQ,
            shareBorad();
        }
    }



    //带面板
    private void shareBorad() {
        ShareBoardConfig config = new ShareBoardConfig();
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_NONE);

        UMImage image = new UMImage(this, "https://ws1.sinaimg.cn/large/0065oQSqly1fw8wzdua6rj30sg0yc7gp.jpg");
        image.compressStyle = UMImage.CompressStyle.SCALE;//大小压缩，默认为大小压缩，适合普通很大的图
        new ShareAction(MeAndGroup_QRCode.this)
                .withText("分享")//文本
                .withMedia(image)//分享的图片
                .setDisplayList(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE)//三方列表
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        UMWeb web = new UMWeb("https://www.baidu.com");
                        web.setTitle("来自分享面板标题");
                        web.setDescription("来自分享面板内容");
                        web.setThumb(new UMImage(MeAndGroup_QRCode.this, R.drawable.mo_icon));

                        new ShareAction(MeAndGroup_QRCode.this).withMedia(web)
                                .setPlatform(share_media)
                                .setCallback(shareListener)
                                .share();
                    }
                })
                .open(config);
    }


    private UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(MeAndGroup_QRCode.this, "成功了", Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(MeAndGroup_QRCode.this, "QQ分享失败啦", Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(MeAndGroup_QRCode.this, "取消了", Toast.LENGTH_LONG).show();

        }
    };


}