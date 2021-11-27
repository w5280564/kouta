package com.xunda.mo.main.friend.activity;

import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneSession;
import static com.xunda.mo.main.me.activity.MeAndGroup_QRCode.QQ_APP_ID;
import static com.xunda.mo.main.me.activity.MeAndGroup_QRCode.WECHAT_APP_ID;
import static com.xunda.mo.staticdata.SetStatusBar.FlymeSetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.MIUISetStatusBarLightMode;
import static com.xunda.mo.staticdata.SetStatusBar.StatusBar;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.permissionx.guolindev.PermissionX;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.xiaomi.mipush.sdk.Constants;
import com.xunda.mo.R;
import com.xunda.mo.main.discover.activity.Discover_QRCode;
import com.xunda.mo.main.friend.myAdapter.Friend_Seek_GroupList_Adapter;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.AddFriend_FriendGroup_Model;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.xUtils3Http;
import com.xunda.mo.xrecycle.XRecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Friend_Add extends AppCompatActivity {

    private TextView add_left, add_right;
    private TextView seek_txt, MoID_Txt;
    private View seekPerson_InClue, seekGroup_InClue;
    private int tag;
    private XRecyclerView group_Xrecycler;
    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;
    private Tencent mTencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_add);

        StatusBar(this);
        MIUISetStatusBarLightMode(this.getWindow(), true);
        FlymeSetStatusBarLightMode(this.getWindow(), true);

        initView();
        initData();
    }

    private void initView() {
        Button return_Btn = findViewById(R.id.return_Btn);
        add_left = findViewById(R.id.add_left);
        add_right = findViewById(R.id.add_right);
        seek_txt = findViewById(R.id.seek_txt);
        seekPerson_InClue = findViewById(R.id.seekperson_inclue);
        MoID_Txt = seekPerson_InClue.findViewById(R.id.MoID_Txt);
        RelativeLayout person_qr_rel = seekPerson_InClue.findViewById(R.id.person_qr_rel);
        person_qr_rel.setOnClickListener(new person_qr_relClick());
        RelativeLayout person_phone_rel = seekPerson_InClue.findViewById(R.id.person_phone_rel);
        person_phone_rel.setOnClickListener(new phoneClick());
        RelativeLayout person_whchat_rel = seekPerson_InClue.findViewById(R.id.person_whchat_rel);
        person_whchat_rel.setOnClickListener(new weChatClick());
        RelativeLayout person_qq_rel = seekPerson_InClue.findViewById(R.id.person_qq_rel);
        person_qq_rel.setOnClickListener(new qqClick());

        seekGroup_InClue = findViewById(R.id.seekgroup_inclue);
        View seek_lin = findViewById(R.id.seek_lin);
        group_Xrecycler = seekGroup_InClue.findViewById(R.id.group_Xrecycler);
        group_Xrecycler.setPullRefreshEnabled(false);
        group_Xrecycler.setLoadingMoreEnabled(false);

        tag = 0;
        changeView(0);
        add_left.setOnClickListener(v -> changeView(0));
        add_right.setOnClickListener(v -> changeView(1));

        return_Btn.setOnClickListener(new return_BtnonClickLister());
        seek_lin.setOnClickListener(new seek_linClickLister());
    }

    private class return_BtnonClickLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }


    private int PageIndex;
    private int pageSize;

    private void initData() {
        regToWx();
        regToQQ();
        MyInfo myInfo = new MyInfo(Friend_Add.this);
        MoID_Txt.setText("我的Mo ID：" + myInfo.getUserInfo().getUserNum());

        PageIndex = 1;
        pageSize = 10;
//        AddFriendMethod(Friend_Add.this, saveFile.Group_SearchGroup_Url);
    }

    private class seek_linClickLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (tag == 0) {
                Intent intent = new Intent(Friend_Add.this, Friend_Add_SeekFriend.class);
                startActivity(intent);
            } else if (tag == 1) {
                Intent intent = new Intent(Friend_Add.this, Friend_Add_SeekGroup.class);
                startActivity(intent);
            }
        }
    }

    private void changeView(int i) {
        if (i == 0) {
            //设置背景色及字体颜色
            tag = 0;
            add_left.setBackgroundResource(R.drawable.friend_add_left);
            add_left.setTextColor(getResources().getColor(R.color.white, null));
            add_right.setBackground(null);
            add_right.setTextColor(getResources().getColor(R.color.yellow, null));
            seek_txt.setText("Mo ID/昵称/手机号/标签");
            seekPerson_InClue.setVisibility(View.VISIBLE);
            seekGroup_InClue.setVisibility(View.GONE);
        } else if (i == 1) {
            tag = 1;
            add_right.setBackgroundResource(R.drawable.friend_add_right);
            add_right.setTextColor(getResources().getColor(R.color.white, null));
            add_left.setBackground(null);
            add_left.setTextColor(getResources().getColor(R.color.yellow, null));
            seek_txt.setText("群ID/群名称/群标签");
            seekPerson_InClue.setVisibility(View.GONE);
            seekGroup_InClue.setVisibility(View.VISIBLE);
        }
    }

    Friend_Seek_GroupList_Adapter mAdapter;

    public void initlist(final Context context) {
        LinearLayoutManager mMangaer = new LinearLayoutManager(context);
        group_Xrecycler.setLayoutManager(mMangaer);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        group_Xrecycler.setHasFixedSize(true);
        mAdapter = new Friend_Seek_GroupList_Adapter(context, baseModel, Model);
        group_Xrecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickLitener(new Friend_Seek_GroupList_Adapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                String GroupId = baseModel.get(position).getGroupId();
                Friend_Group_Detail.actionStart(Friend_Add.this, GroupId);

            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
    }


    private List<AddFriend_FriendGroup_Model.DataDTO.ListDTO> baseModel;
    private AddFriend_FriendGroup_Model Model;

    public void AddFriendMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "0");
        map.put("pageNum", PageIndex);
        map.put("pageSize", pageSize);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Model = new Gson().fromJson(result, AddFriend_FriendGroup_Model.class);
                if (PageIndex == 1) {
                    if (baseModel != null) {
                        baseModel.clear();
                    }
                    baseModel = new ArrayList<>();
                }
                if (Model.getData() != null) {
                    baseModel.addAll(Model.getData().getList());
                    if (PageIndex == 1) {
//                        list_xrecycler.refreshComplete();
                        initlist(context);
                    } else {
//                        list_xrecycler.loadMoreComplete();
//                        mAdapter.addMoreData(baseModel);
                    }
                } else {
//                    list_xrecycler.removeAllViews();
//                    list_xrecycler.refreshComplete();
                    Toast.makeText(context, "没有搜到", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failed(String... args) {
            }
        });

    }


    private class person_qr_relClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startCamera(Friend_Add.this);
        }
    }


    public void startCamera(Activity context) {
        //监听授权
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[0]);
            ActivityCompat.requestPermissions(context, permissions, 1);
        } else {
            //打开相机录制视频
            Intent captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            //判断相机是否正常。
            if (captureIntent.resolveActivity(context.getPackageManager()) != null) {
                Discover_QRCode.actionStart(context);
            }

        }
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


    private class phoneClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            permissionX(Friend_Add.this);
        }
    }

    private class weChatClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            shareWechat(WXSceneSession);
        }
    }

    private class qqClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
//            saveBitmap(getShareBitmap(meAndGroup));
            shareToQQ(QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        }
    }

    public void callPhone(Activity activity, String body) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra("sms_body", body);
        activity.startActivity(intent);
    }

    private void permissionX(Context context) {
        PermissionX.init((FragmentActivity) context).permissions(Manifest.permission.SEND_SMS)
                .onForwardToSettings((scope, deniedList) -> {
                    scope.showForwardToSettingsDialog(deniedList, "您需要手动在设置中允许发短信必要的权限", "确定", "取消");
                })
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        callPhone((Activity) context, "我正在使用安全稳定的加密聊天软件默言默语，我的Mo ID是1001028，一起来使用吧！https://www.ahxunda.com");
                    } else {
                        Toast.makeText(this, "权限被拒绝:$" + deniedList, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void shareWechat(int WechatType) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "https://www.ahxunda.com";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "默言默语 ";
        msg.description = "默言默语-多端加密，岂止安全";
        Bitmap thumbBmp = BitmapFactory.decodeResource(getResources(), R.drawable.mo_icon);
        msg.thumbData = bmpToByteArray(thumbBmp, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "webpage";  //transaction字段用与唯一标示一个请求
        req.message = msg;
        req.scene = WechatType;
        api.sendReq(req);
    }

    private void shareToQQ(int QQType) {
        Bundle shareParams = new Bundle();
        shareParams.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        shareParams.putString(QQShare.SHARE_TO_QQ_TITLE, "默言默语");
        shareParams.putString(QQShare.SHARE_TO_QQ_SUMMARY, "默言默语-多端加密，岂止安全");
        shareParams.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "https://www.ahxunda.com");
//        shareParams.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, R.drawable.mo_icon);
        shareParams.putString(QQShare.SHARE_TO_QQ_APP_NAME, "");
        shareParams.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQType);
        mTencent.shareToQQ(this, shareParams, new BaseUiListener());
    }


    private static class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
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

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }

}