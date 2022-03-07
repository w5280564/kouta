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
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.utils.StringUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.AuthTypeEnum;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.group.fragment.GroupEditFragmentInfo;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.model.baseDataModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.GlideEnGine;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;

public class GroupDetail_Edit extends BaseInitActivity {

    protected static final int ADRESS_CODE = 1;
    private ConstraintLayout head_Constraint, label_Constraint;
    private ObsClient obsClient;
    private int Identity;
    private GruopInfo_Bean groupModel;
    private SimpleDraweeView person_img;
    private MyArrowItemView adress_ArrowItemView, brief_ArrowItemView;
    private LinearLayout label_Lin;
    private TextView tv_tag_no;
    private String group_des;
    private View label_arrow;

    public static void actionStart(Context context, int Identity, GruopInfo_Bean groupModel) {
        Intent intent = new Intent(context, GroupDetail_Edit.class);
        intent.putExtra("Identity", Identity);
        intent.putExtra("groupModel", (Serializable) groupModel);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_groupdetail_edit;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupModel = (GruopInfo_Bean) intent.getSerializableExtra("groupModel");
        Identity = intent.getIntExtra("Identity", 5);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        initTitle();
        head_Constraint = findViewById(R.id.head_Constraint);
        head_Constraint.setOnClickListener(new head_ConstraintClick());
        person_img = findViewById(R.id.person_img);
        label_Constraint = findViewById(R.id.label_Constraint);
        label_Constraint.setOnClickListener(new label_ConstraintClick());
        label_Lin = findViewById(R.id.label_Lin);
        label_arrow = findViewById(R.id.label_arrow);
        tv_tag_no = findViewById(R.id.tv_tag_no);
        adress_ArrowItemView = findViewById(R.id.adress_ArrowItemView);
        adress_ArrowItemView.setOnClickListener(new adress_ArrowItemViewClick());
        brief_ArrowItemView = findViewById(R.id.brief_ArrowItemView);
        brief_ArrowItemView.setOnClickListener(new brief_ArrowItemViewClick());

        initObsClient();

        LiveDataBus.get().with(MyConstant.MY_GROUP_LABEL, String.class).observe(this, tag -> {
            if (TextUtils.isEmpty(tag)) {
                tv_tag_no.setVisibility(View.VISIBLE);
                label_Lin.setVisibility(View.GONE);
            } else {
                tv_tag_no.setVisibility(View.GONE);
                label_Lin.setVisibility(View.VISIBLE);
                tagList(label_Lin, GroupDetail_Edit.this, tag);
            }
        });
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(GroupDetail_Edit.this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("群资料");
        Button right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.GONE);
        return_Btn.setOnClickListener(new return_Btn());
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        if (groupModel != null) {
            GruopInfo_Bean.DataDTO dataDTO = groupModel.getData();
            Uri uri = Uri.parse(dataDTO.getGroupHeadImg());
            person_img.setImageURI(uri);
            String addressStr = dataDTO.getGroupAddr().isEmpty() ? "未设置" : dataDTO.getGroupAddr();
            adress_ArrowItemView.getTvContent().setText(addressStr);
            group_des = dataDTO.getGroupIntroduction();
            brief_ArrowItemView.getTip().setText(group_des.isEmpty() ? "群主很懒，还没有群介绍哦~" : group_des);
            String tag = groupModel.getData().getTag();
            if (TextUtils.isEmpty(tag)) {
                tv_tag_no.setVisibility(View.VISIBLE);
                label_Lin.setVisibility(View.GONE);
            } else {
                tv_tag_no.setVisibility(View.GONE);
                label_Lin.setVisibility(View.VISIBLE);
                tagList(label_Lin, GroupDetail_Edit.this, tag);
            }


            //3是普通成员不能修改
            if (Identity == 3) {
                head_Constraint.setEnabled(false);
                adress_ArrowItemView.setEnabled(false);
                brief_ArrowItemView.setEnabled(false);
                label_Constraint.setEnabled(false);
                if (tv_tag_no.getVisibility() == View.GONE) {

                    label_arrow.setVisibility(View.GONE);
                }
            }

        }
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

    private class head_ConstraintClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            startCamera();
        }
    }

    private class adress_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            double Latitude = 0;
            double Longitude = 0;
            if (!groupModel.getData().getLat().isEmpty()) {
                Latitude = Double.parseDouble(groupModel.getData().getLat());
                Longitude = Double.parseDouble(groupModel.getData().getLng());
            }
            String Address = groupModel.getData().getGroupAddr();

            GroupDetail_Edit_Address.actionStartForResult(GroupDetail_Edit.this, ADRESS_CODE);
//            GroupDetail_Edit_Adress.actionStart(mContext, Latitude, Longitude, Address);
        }
    }


    public void tagList(LinearLayout label_Lin, Context mContext, String tag) {
        if (label_Lin != null) {
            label_Lin.removeAllViews();
        }
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        String[] tagS = tag.split(",");
        for (int i = 0; i < tagS.length; i++) {
            TextView textView = new TextView(mContext);
            textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            textView.setBackgroundResource(R.drawable.group_label_radius);
            textView.setText(tagS[i]);
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int margins = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, mContext.getResources().getDisplayMetrics());
            itemParams.setMargins(0, 0, margins, 0);
            textView.setLayoutParams(itemParams);
            textView.setTag(i);
            label_Lin.addView(textView);
            textView.setOnClickListener(v -> {
            });
        }
    }


    private class brief_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            showBriefDialog();
        }
    }

    private void showBriefDialog() {
        GroupEditFragmentInfo.showDialog(mContext,
                "群简介",
                group_des,
                (view, content) -> {
                    group_des = content;
                    brief_ArrowItemView.getTip().setText(!TextUtils.isEmpty(content) ? content : "");
                    String changType = "3";
                    String keyStr = "groupIntroduction";
                    CreateGroupMethod(GroupDetail_Edit.this, saveFile.Group_UpdateInfo_Url, changType, keyStr, content, "", "");
                });
    }


    public void startCamera() {
        //监听授权
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(GroupDetail_Edit.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(GroupDetail_Edit.this, permissions, 1);
        } else {
            //打开相机录制视频
            Intent captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            //判断相机是否正常。
            if (captureIntent.resolveActivity(GroupDetail_Edit.this.getPackageManager()) != null) {
                setPhotoMetod(GroupDetail_Edit.this);
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
                    if (!selectList.isEmpty()) {
                        AsyncTask<Void, Void, String> task = new PostObjectTask();
                        task.execute();
                    }
                    if (!selectList.isEmpty()) {
//                        Uri uri = Uri.parse("file:///" + selectList.get(0).getCutPath());
//                        groupHead_Sim.setImageURI(uri);
                    }
                    break;
                case ADRESS_CODE:
                    onActivityResultForMapLocation(data);
                    break;
            }
        }
    }

    /**
     * 地图定位结果处理
     *
     * @param data
     */
    protected void onActivityResultForMapLocation(@Nullable Intent data) {
        if (data != null) {
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);
            String locationAddress = data.getStringExtra("address");
            String locName = locationAddress.substring(0, locationAddress.indexOf(","));

            if (TextUtils.isEmpty(locationAddress)) {

            }
            String changType = "2";
            String keyStr = "groupAddr";
            CreateGroupMethod(GroupDetail_Edit.this, saveFile.Group_UpdateInfo_Url, changType, keyStr, locName, "", "");
        }
    }


    @SuppressLint("StaticFieldLeak")
    public class PostObjectTask extends AsyncTask<Void, Void, String> {
        @SneakyThrows
        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder sbf = new StringBuilder();
            try {
                MyInfo myInfo = new MyInfo(GroupDetail_Edit.this);
                String objectName = "group/headImg/" + myInfo.getUserInfo().getUserId() + "/" + selectList.get(0).getFileName();//对应上传之后的文件名称
                FileInputStream fis = new FileInputStream(selectList.get(0).getCutPath());
                obsClient.putObject(bucketName, objectName, fis); // localfile为待上传的本地文件路径，需要指定到具体的文件名
                sbf.append(objectName);
                return sbf.toString();
            } catch (ObsException e) {
                return "";
            } catch (Exception e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String pic) {
            super.onPostExecute(pic);
            if (TextUtils.isEmpty(pic)) {
                Toast.makeText(mContext, "上传图片失败", Toast.LENGTH_SHORT).show();
            } else {
                String changType = "1";
                String keyStr = "groupHeadImg";
                CreateGroupMethod(GroupDetail_Edit.this, saveFile.Group_UpdateInfo_Url, changType, keyStr, pic, "", "");
            }
        }
    }

    /**
     * 修改用户信息
     *
     * @param context
     * @param baseUrl
     * @param changType 修改类型（1.头像 2.地址 ）
     * @param keyStr
     * @param valueStr
     */
    public void CreateGroupMethod(Context context, String baseUrl, String changType, String keyStr, String valueStr, String keyCity, String valueCityStr) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupModel.getData().getGroupId());
        map.put(keyStr, valueStr);
        xUtils3Http.post(mContext, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                baseDataModel baseModel = new Gson().fromJson(result, baseDataModel.class);
                Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                if (TextUtils.equals(changType, "1")) {
                    Uri uri = Uri.parse(baseModel.getData());
                    person_img.setImageURI(uri);
                    updateConversionExdAndLastMessageInfoInGroup(groupModel.getData().getGroupHxId(),baseModel.getData());
                    LiveDataBus.get().with(MyConstant.MESSAGE_CHANGE_UPDATE_GROUP_IMAGE).postValue(EaseEvent.create(MyConstant.MESSAGE_CHANGE_UPDATE_GROUP_IMAGE,EaseEvent.TYPE.GROUP));
                } else if (TextUtils.equals(changType, "2")) {
                    String adressStr = valueStr.isEmpty() ? "未设置" : valueStr;
                    adress_ArrowItemView.getTvContent().setText(adressStr);
                } else if (TextUtils.equals(changType, "3")) {
                    sendMes(groupModel);
                }
            }

            @Override
            public void failed(String... args) {
            }
        });
    }


    //修改群会话列表扩展字段
    private void updateConversionExdAndLastMessageInfoInGroup(String currentConversationId,String showImg) {
        EMConversation currentConversation = EMClient.getInstance().chatManager().getConversation(currentConversationId);
        if (currentConversation!=null) {
            updateConversionLastMessage(currentConversation,showImg);
            String extField = currentConversation.getExtField();
            JSONObject jsonObject = null;
            if (!StringUtil.isBlank(extField)) {
                try {
                    jsonObject = new JSONObject(extField);
                    jsonObject.put("isInsertGroupOrFriendInfo", true);
                    jsonObject.put("showImg", showImg);
                } catch (JSONException e) {
                    jsonObject = getJsonObjectGroup(showImg);
                }
            } else {
                jsonObject = getJsonObjectGroup(showImg);
            }

            if (jsonObject == null) {
                return;
            }
            currentConversation.setExtField(jsonObject.toString());
        }
    }

    private void updateConversionLastMessage(EMConversation currentConversation,String showName) {
        EMMessage lastMessage = currentConversation.getLastMessage();
        if (lastMessage!=null) {
            lastMessage.setAttribute(MyConstant.GROUP_HEAD, showName);
            EMClient.getInstance().chatManager().updateMessage(lastMessage);
        }
    }


    @NonNull
    private JSONObject getJsonObjectGroup(String showImg) {
        JSONObject jsonObject;
        jsonObject  = new JSONObject();
        try {
            jsonObject.put("isInsertGroupOrFriendInfo", true);
            jsonObject.put("showImg", showImg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    //发送消息
    private void sendMes(GruopInfo_Bean Model) {
        MyInfo myInfo = new MyInfo(mContext);
        String conversationId = Model.getData().getGroupHxId();
//        EMMessage message = EMMessage.createTxtSendMessage(mContext.getString(R.string.CREATE_GROUP_CONTENT), conversationId);
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        EMTextMessageBody txtBody = new EMTextMessageBody("");
        message.addBody(txtBody);
        message.setTo(conversationId);
        message.setChatType(EMMessage.ChatType.GroupChat);
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.GROUP_UPDATE_GROUPDES);
//        message.setAttribute(MyConstant.USER_NAME, UserName);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        message.setAttribute(MyConstant.GROUP_NAME, Model.getData().getGroupName());
        message.setAttribute(MyConstant.GROUP_HEAD, Model.getData().getGroupHeadImg());
        DemoHelper.getInstance().getChatManager().sendMessage(message);
    }


    private class label_ConstraintClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            GroupDetail_Edit_Label.actionStart(mContext, Identity, groupModel);
        }
    }


}