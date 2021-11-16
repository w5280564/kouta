package com.xunda.mo.main.me.activity;

import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneSession;
import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneTimeline;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.xiaomi.mipush.sdk.Constants;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.SaveViewUtils;
import com.xunda.mo.staticdata.viewTouchDelegate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class MeAndGroup_QRCode extends BaseInitActivity {
    // APP_ID 替换为你的应用从官方网站申请到的合法appID
    public static final String WECHAT_APP_ID = "wxdd3384bb6c79b2ca";
    public static final String QQ_APP_ID = "101968658";


    private ImageView head_Image;
    private ImageView qrCode_Img;
    public static String user = "user";
    public static String group = "group";
    private String meOrGroup;
    private TextView titleName, nick_Txt, moId_Txt;
    private ConstraintLayout meAndGroup;
    private GruopInfo_Bean groupBean;
    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;
    private Tencent mTencent;
    private File File;
    private final String picName = "/QRCode" + ".jpg";
    private View share_Group;
    private TextView qr_Content;

    public static void actionUserStart(Context context) {
        Intent intent = new Intent(context, MeAndGroup_QRCode.class);
        intent.putExtra("meOrGroup", user);
        context.startActivity(intent);
    }

    public static void actionGroupStart(Context context, GruopInfo_Bean groupBean) {
        Intent intent = new Intent(context, MeAndGroup_QRCode.class);
        intent.putExtra("groupBean", groupBean);
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
        share_Group = findViewById(R.id.share_Group);
        share_Group.setVisibility(View.VISIBLE);
        ImageView qrcode_down = findViewById(R.id.qrcode_down);
        qrcode_down.setOnClickListener(new qrcode_downClick());
        Button return_Btn = findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setOnClickListener(new return_BtnClick());
        ImageView qrcode_share = findViewById(R.id.qrcode_share);
        qrcode_share.setOnClickListener(new qrcode_shareClick());
        qr_Content = findViewById(R.id.qr_Content);

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
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
            qr_Content.setText("使用默言默语APP扫描二维码，加我为好友");
        } else if (TextUtils.equals(meOrGroup, group)) {
            titleName.setText("群二维码");
            GruopInfo_Bean.DataDTO userData = groupBean.getData();
            Glide.with(MeAndGroup_QRCode.this).load(userData.getGroupHeadImg())
                    .transforms(new CenterCrop(), new RoundedCorners(9)).into(head_Image);
            nick_Txt.setText(userData.getGroupName());
            moId_Txt.setText("群ID: " + userData.getGroupNum());
            String qrCodeStr = "group-" + userData.getGroupHxId();
            createEnglishQRCode(qrCodeStr);
            qr_Content.setText("使用默言默语APP扫描二维码，加入群聊");
        }
        regToWx();
        regToQQ();
//        QQPermissionMgr.getInstance().requestPermissions(this);
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
            showMore(mContext,meAndGroup);
//            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
//            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
//            ActivityCompat.requestPermissions(MeAndGroup_QRCode.this, mPermissionList, 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            showMore(mContext,meAndGroup);
        }
    }

    private void showMore(Context mContext,  View view) {
        View contentView = View.inflate(mContext, R.layout.share_popup, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        MorePopup.setWidth(RadioGroup.LayoutParams.MATCH_PARENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
        MorePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        ImageView share_qr_wechat = contentView.findViewById(R.id.share_qr_wechat);
        ImageView share_qr_timeline = contentView.findViewById(R.id.share_qr_timeline);
        ImageView share_qr_qq = contentView.findViewById(R.id.share_qr_qq);
        ImageView share_qr_qzone = contentView.findViewById(R.id.share_qr_qzone);
        TextView cancel_txt = contentView.findViewById(R.id.cancel_txt);

        share_qr_wechat.setOnClickListener(v -> {
            shareWechat(WXSceneSession);
            MorePopup.dismiss();
        });
        share_qr_timeline.setOnClickListener(v -> {
            shareWechat(WXSceneTimeline);
            MorePopup.dismiss();
        });
        share_qr_qq.setOnClickListener(v -> {
            saveBitmap(getShareBitmap(meAndGroup));
            shareToQQ(QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE,File.getPath() + picName);
            MorePopup.dismiss();
        });
        share_qr_qzone.setOnClickListener(v -> {
            saveBitmap(getShareBitmap(meAndGroup));
            shareToQQ(QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN,File.getPath() + picName);
            MorePopup.dismiss();
        });
        cancel_txt.setOnClickListener(v -> {
            MorePopup.dismiss();
        });
    }

    private void shareToQQ(int QQType,String QRPath) {
        Bundle shareParams = new Bundle();
        shareParams.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        shareParams.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, QRPath);
        shareParams.putString(QQShare.SHARE_TO_QQ_APP_NAME, "");
        shareParams.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQType);
        doShareToQQ(shareParams);
    }

    private void doShareToQQ(Bundle params) {
        mTencent.shareToQQ(MeAndGroup_QRCode.this, params, new BaseUiListener());
    }

    private void shareWechat(int WechatType) {
//        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.mo_icon);
        Bitmap bmp = getShareBitmap(meAndGroup);
        //初始化 WXImageObject 和 WXMediaMessage 对象
        WXImageObject imgObj = new WXImageObject(bmp);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        //设置缩略图
//        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
//        bmp.recycle();
//        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = buildTransaction("img");
        req.transaction = "img";  //transaction字段用与唯一标示一个请求
        req.message = msg;
        req.scene = WechatType;
        //调用api接口，发送数据到微信
        api.sendReq(req);
    }


    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, WECHAT_APP_ID, true);
        // 将应用的appId注册到微信
        api.registerApp(WECHAT_APP_ID);
        //建议动态监听微信启动广播进行注册到微信
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 将该app注册到微信
                api.registerApp(Constants.APP_ID);
            }
        }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));

    }

    private void regToQQ() {
        mTencent = Tencent.createInstance(QQ_APP_ID, this.getApplicationContext(), "com.xunda.mo.fileprovider");
    }


    private void ShareQQ(String QRPath) {
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, QRPath);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "返回");
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_TYPE_AUDIO);
        mTencent.shareToQQ(this, params, new BaseUiListener());
    }


    private static class BaseUiListener implements IUiListener {
        protected void doComplete(Object values) {
        }

        @Override
        public void onComplete(Object o) {
            doComplete(o);
        }

        @Override
        public void onError(UiError e) {
        }
        @Override
        public void onCancel() {
        }
        @Override
        public void onWarning(int i) {

        }
    }


    // 核心代码：抓取view图片缓存保存bitmap
    private Bitmap getShareBitmap(View view) {
//        share_Group.setVisibility(View.INVISIBLE);
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        view.setDrawingCacheBackgroundColor(Color.WHITE);
        return view.getDrawingCache();
    }
    // 核心代码：抓取view图片缓存保存bitmap判空代码：
    public static Bitmap loadBitmapFromView(View v) {
        if (v == null) {
            return null;
        }
        v.setDrawingCacheEnabled(true);
        Bitmap screenshot;
        v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(),
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(
                v.getHeight(), View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.buildDrawingCache();
        screenshot = v.getDrawingCache();
        if (screenshot == null) {
            v.setDrawingCacheEnabled(true);
            screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(screenshot);
            c.translate(-v.getScrollX(), -v.getScrollY());
            v.draw(c);
            return screenshot;
        }
        return screenshot;
    }

    //保存图片到本地
    @SuppressWarnings("all")
    public void saveBitmap(Bitmap bm) {
        isHaveSDCard();
        if (isHaveSDCard()) {
//            File = Environment.getExternalStorageDirectory();
            File = this.getExternalFilesDir(null);
        } else {
            File = Environment.getDataDirectory();
        }
        File = new File(File.getPath());
        if (!File.isDirectory()) {
            File.delete();
            File.mkdirs();
        }
        if (!File.exists()) {
            File.mkdirs();
        }
        writeBitmap(File.getPath(), picName, bm);
    }

   //保存图片
   @SuppressWarnings("all")
    public static void writeBitmap(String path, String name, Bitmap bitmap) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File _file = new File(path + name);
        if (_file.exists()) {
            _file.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(_file);
            if (name != null && !"".equals(name)) {
                int index = name.lastIndexOf(".");

                if (index != -1 && (index + 1) < name.length()) {
                    String extension = name.substring(index + 1).toLowerCase();

                    if ("png".equals(extension)) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    } else if ("jpg".equals(extension) || "jpeg".equals(extension)) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //是否有sd卡
    public boolean isHaveSDCard() {
        String SDState = android.os.Environment.getExternalStorageState();
        return SDState.equals(Environment.MEDIA_MOUNTED);
    }

}

