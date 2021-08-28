package com.xunda.mo.main.group.activity;

import static com.xunda.mo.staticdata.AppConstant.ak;
import static com.xunda.mo.staticdata.AppConstant.bucketName;
import static com.xunda.mo.staticdata.AppConstant.endPoint;
import static com.xunda.mo.staticdata.AppConstant.sk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.domain.EaseUser;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.AuthTypeEnum;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.main.baseView.MySwitchItemView;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.group.adapter.Group_AddUser_Adapter;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.createGroup_Bean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.GlideEnGine;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;

public class newGroup extends BaseInitActivity {

    private List<String> newmembers;
    private List<EaseUser> mUser;
    private RecyclerView addUser_Recy;
    private SimpleDraweeView groupHead_Sim;
    private MyArrowItemView addGroup_ArrowItemView;
    private TextView addGroup_count;
    private ObsClient obsClient;
    private EditText group_edit;
    private MySwitchItemView mySwitchItemView;
    String userName;
    private Button right_Btn;
    private ProgressBar site_progressbar;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, newGroup.class);
        context.startActivity(starter);
    }

    public static void actionStart(Context context, List<String> newmembers, List<EaseUser> user) {
        Intent intent = new Intent(context, newGroup.class);
        intent.putExtra("newmembers", (Serializable) newmembers);
        intent.putExtra("user", (Serializable) user);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_newgroup;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mUser = (List<EaseUser>) intent.getSerializableExtra("user");
        newmembers = (List<String>) intent.getSerializableExtra("newmembers");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();

        group_edit = findViewById(R.id.group_edit);
        groupHead_Sim = findViewById(R.id.groupHead_Sim);
        groupHead_Sim.setOnClickListener(new groupHead_SimClick());
        mySwitchItemView = findViewById(R.id.mySwitchItemView);
        addGroup_ArrowItemView = findViewById(R.id.addGroup_ArrowItemView);
        addGroup_ArrowItemView.setOnClickListener(new addGroup_ArrowItemViewClick());
        addUser_Recy = findViewById(R.id.addUser_Recy);
        addGroup_count = findViewById(R.id.addGroup_count);
        site_progressbar = findViewById(R.id.site_progressbar);
        initObsClient();
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setElevation(2f);//阴影
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("创建新的群聊");
        right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setText("完成");
        viewTouchDelegate.expandViewTouchDelegate(right_Btn, 50, 50, 50, 50);

        return_Btn.setOnClickListener(new return_Btn());
        right_Btn.setOnClickListener(new right_BtnClick());
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    //创建群组
    private class right_BtnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            if (TextUtils.isEmpty(group_edit.getText())) {
                Toast.makeText(newGroup.this, "群名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            right_Btn.setEnabled(false);
            if (selectList.isEmpty()) {
                CreateGroupMethod(newGroup.this, saveFile.Group_Create_Url);
            } else {
                AsyncTask<Void, Void, String> task = new PostObjectTask();
                task.execute();
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void initData() {
        super.initData();
        addGroup_count.setText(mUser.size() + "/200");
        userIds = String.join(",", newmembers);

//        userIds =  newmembers.stream().map(String::valueOf).collect(Collectors.joining(","));
//        ContactList();
        initlist(newGroup.this);

        List<String> userListStr = new ArrayList<>();
        for (int i = 0; i < mUser.size(); i++) {
            userListStr.add(mUser.get(i).getNickname());
        }
        userName = String.join(",", userListStr);
    }

    private void initObsClient() {
        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setEndPoint(endPoint);
        config.setAuthType(AuthTypeEnum.OBS);
        // 创建ObsClient实例
        obsClient = new ObsClient(ak, sk, config);
    }

    public void initlist(final Context context) {
        LinearLayoutManager mMangaer = new LinearLayoutManager(context);
        addUser_Recy.setLayoutManager(mMangaer);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        addUser_Recy.setHasFixedSize(true);
        Group_AddUser_Adapter mAdapter = new Group_AddUser_Adapter(context, mUser);
        addUser_Recy.setAdapter(mAdapter);
    }


    //选中的用户
//    private void ContactList() {
    //        Map<String, EaseUser> myEaseUser = DemoHelper.getInstance().getContactList();
//        List<EaseUser> myGroupEase = new ArrayList<>();
//        List<EaseUser> myEaseUser = DemoDbHelper.getInstance(newGroup.this).getUserDao().loadAllEaseUsers();
//        try {
//            for (int i = 0; i < myEaseUser.size(); i++) {
//                String selectInfoExt = myEaseUser.get(i).getExt();
//                JSONObject jsonObject = new JSONObject(selectInfoExt);//用户资料扩展属性
//                String userId = jsonObject.getString("userId");
//                for (int k = 0; k < newmembers.length; k++) {
//                    if (TextUtils.equals(newmembers[k], userId)) {
//                        myGroupEase.add(myEaseUser.get(i));
//                    }
//                }
//            }
//
//        } catch (
//                JSONException e) {
//            e.printStackTrace();
//        }
//    }

    private class groupHead_SimClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            startCamera();
        }
    }

    public void startCamera() {
        //监听授权
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(newGroup.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(newGroup.this, permissions, 1);
        } else {
            //打开相机录制视频
            Intent captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            //判断相机是否正常。
            if (captureIntent.resolveActivity(newGroup.this.getPackageManager()) != null) {
                setPhotoMetod(newGroup.this);
            }

        }
    }

    private void setPhotoMetod(Context context) {
        int choice = 1;
        PictureSelector.create((Activity) context)
                .openGallery(PictureConfig.TYPE_ALL)
                .imageEngine(GlideEnGine.createGlideEngine()) //图片加载空白 加入Glide加载图片
                .imageSpanCount(4)// 每行显示个数 int
                .maxSelectNum(choice)
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isSingleDirectReturn(true)//PictureConfig.SINGLE模式下是否直接返回
                .isAndroidQTransform(true)//Android Q版本下是否需要拷贝文件至应用沙盒内
                .isPreviewImage(true)// 是否可预览图片 true or false
                .isCamera(false)// 是否显示拍照按钮 true or false
                .isEnableCrop(true)//开启裁剪
                .cropImageWideHigh(200, 200)//裁剪尺寸
                .withAspectRatio(1, 1)//裁剪比例1：1是正方形
                .freeStyleCropEnabled(true)//裁剪框是否可拖拽
//                .imageFormat(PictureMimeType.PNG_Q)//拍照图片格式后缀,默认jpeg, PictureMimeType.PNG，Android Q使用PictureMimeType.PNG_Q
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .isCompress(true)// 是否压缩 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    List<LocalMedia> selectList = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
//                    if (!selectList.isEmpty()) {
//                        AsyncTask<Void, Void, String> task = new PostObjectTask();
//                        task.execute();
//                    }
                    if (!selectList.isEmpty()) {
                        Uri uri = Uri.parse("file:///" + selectList.get(0).getCutPath());
                        groupHead_Sim.setImageURI(uri);
                    }
                    break;
            }
        }
    }

    private class addGroup_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            addGroup();
        }
    }

    //加群方式 只设置一组数据就好
    private void addGroup() {
        final List<String> mOptionsItems = new ArrayList<>();
        mOptionsItems.add("不允许任何人加群");
        mOptionsItems.add("允许任何人加群");
        mOptionsItems.add("需要身份验证，由管理员审核");
        mOptionsItems.add("只允许群成员邀请加群");
        mOptionsItems.add("只允许群主和管理员邀请加群");
        mOptionsItems.add("只允许VIP或SVIP加群");
        OptionsPickerView pvOptions = new OptionsPickerBuilder(newGroup.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                addGroup_ArrowItemView.getTvContent().setText(mOptionsItems.get(options1));
                joinWay = options1 + 1 + "";
            }
        }).build();
        pvOptions.setPicker(mOptionsItems);
//        pvOptions.setSelectOptions(2);
        pvOptions.show();
    }


    String objectName = "";

    public class PostObjectTask extends AsyncTask<Void, Void, String> {
        @SneakyThrows
        @Override
        protected String doInBackground(Void... voids) {
            StringBuffer sbf = new StringBuffer();
            try {

                MyInfo myInfo = new MyInfo(newGroup.this);
                objectName = "group/headImg/" + myInfo.getUserInfo().getUserId() + "/" + selectList.get(0).getFileName();//对应上传之后的文件名称
                FileInputStream fis = new FileInputStream(new File(selectList.get(0).getCutPath()));
                obsClient.putObject(bucketName, objectName, fis); // localfile为待上传的本地文件路径，需要指定到具体的文件名
                sbf.append(objectName);
                return sbf.toString();
            } catch (ObsException e) {
                sbf.append("\n\n");
                sbf.append("Response Code:" + e.getResponseCode())
                        .append("\n\n").append("Error Message:" + e.getErrorMessage())
                        .append("\n\n").append("Error Code:" + e.getErrorCode())
                        .append("\n\n").append("Request ID:" + e.getErrorRequestId())
                        .append("\n\n").append("Host ID:" + e.getErrorHostId());
                return sbf.toString();
            } catch (Exception e) {
                sbf.append("\n\n");
                sbf.append(e.getMessage());
                return sbf.toString();
            } finally {
                if (obsClient != null) {
                    try {
                        //Close obs client
                        obsClient.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String pic) {
            super.onPostExecute(pic);
//            groupHead_Sim.setEnabled(false);
            pictures = pic;
            CreateGroupMethod(newGroup.this, saveFile.Group_Create_Url);
        }
    }


    /**
     * 创建群组
     *
     * @param context
     * @param baseUrl
     * isAnonymous 0不匿名1匿名
     */
    String pictures = "";
    String joinWay = "3";
    String userIds;

    public void CreateGroupMethod(Context context, String baseUrl) {
        site_progressbar.setVisibility(View.VISIBLE);
        Map<String, Object> map = new HashMap<>();
        map.put("groupName", group_edit.getText().toString());
        map.put("isAnonymous", mySwitchItemView.getSwitch().isChecked() ? 1 : 0);
        map.put("joinWay", joinWay);
        map.put("userIds", userIds);
        map.put("groupHeadImg", pictures);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                createGroup_Bean baseModel = new Gson().fromJson(result, createGroup_Bean.class);
                Toast.makeText(context, "群已创建", Toast.LENGTH_SHORT).show();
                sendMes(baseModel);
                finish();
                site_progressbar.setVisibility(View.GONE);
                right_Btn.setEnabled(true);
            }

            @Override
            public void failed(String... args) {
                site_progressbar.setVisibility(View.GONE);
                right_Btn.setEnabled(true);
            }
        });
    }


    //发送消息 创建群聊发送一条邀请人信息
    private void sendMes(createGroup_Bean Model) {
        MyInfo myInfo = new MyInfo(mContext);
        String conversationId = Model.getData().getGroupHxId();
//        EMMessage message = EMMessage.createTxtSendMessage(mContext.getString(R.string.CREATE_GROUP_CONTENT), conversationId);
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        EMTextMessageBody txtBody = new EMTextMessageBody("");
        message.addBody(txtBody);
        message.setTo(conversationId);
        message.setChatType(EMMessage.ChatType.GroupChat);
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_CREATE_GROUP);
        message.setAttribute(MyConstant.USER_NAME, userName);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        message.setAttribute(MyConstant.GROUP_NAME, Model.getData().getGroupName());
        message.setAttribute(MyConstant.GROUP_HEAD, Model.getData().getGroupHeadImg());

        DemoHelper.getInstance().getChatManager().sendMessage(message);
    }

}