package com.xunda.mo.main.chat.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseCompat;
import com.hyphenate.easeui.utils.EaseFileUtils;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.interfaceOrImplement.OnResourceParseCallback;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.common.repositories.EMPushManagerRepository;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.chat.viewmodel.ChatViewModel;
import com.xunda.mo.hx.section.dialog.DemoDialogFragment;
import com.xunda.mo.hx.section.dialog.EditTextDialogFragment;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.hx.section.group.activity.GroupPrePickActivity;
import com.xunda.mo.hx.section.search.SearchSingleChatActivity;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.main.baseView.MySwitchItemView;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.group.activity.GroupDetail_Report;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.main.me.activity.Me_VIP;
import com.xunda.mo.model.Friend_Details_Bean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.MyLevel;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class ChatDetailSet extends BaseInitActivity {
    private String toChatUsername;
    private SimpleDraweeView person_img;
    private TextView nick_nameTxt, cententTxt, leID_Txt, vip_Txt, signature_Txt, grade_Txt;
    private Button right_Btn;
    private TextView friend_tv_content, nick_tv_content, clear_Txt, remove_Txt;
    private EMConversation conversation;
    private MySwitchItemView top_Switch, disturb_Switch, vip_Switch;
    private ChatViewModel viewModel;
    private MyArrowItemView nick_ArrowItemView, group_ArrowItemView;
    private LinearLayout grade_Lin;

    public static void actionStart(Context context, String toChatUsername) {
        Intent intent = new Intent(context, ChatDetailSet.class);
        intent.putExtra("toChatUsername", toChatUsername);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chatdetailset;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        initTitle();
        ScrollView my_Scroll = findViewById(R.id.my_Scroll);
        OverScrollDecoratorHelper.setUpOverScroll(my_Scroll);
        person_img = findViewById(R.id.person_img);
        nick_nameTxt = findViewById(R.id.nick_nameTxt);
        leID_Txt = findViewById(R.id.leID_Txt);
        leID_Txt.setOnClickListener(new leID_TxtOnClick());
        vip_Txt = findViewById(R.id.vip_Txt);
        grade_Txt = findViewById(R.id.grade_Txt);
        signature_Txt = findViewById(R.id.signature_Txt);
        clear_Txt = findViewById(R.id.clear_Txt);
        clear_Txt.setOnClickListener(new clear_TxtOnClick());

        MyArrowItemView friend_ArrowItemView = findViewById(R.id.friend_ArrowItemView);
        friend_tv_content = friend_ArrowItemView.findViewById(R.id.tv_content);
        nick_ArrowItemView = findViewById(R.id.nick_ArrowItemView);
        nick_ArrowItemView.setOnClickListener(new nick_ArrowItemViewClick());
        group_ArrowItemView = findViewById(R.id.group_ArrowItemView);
        group_ArrowItemView.setOnClickListener(new group_ArrowItemViewClick());
        nick_tv_content = nick_ArrowItemView.findViewById(R.id.tv_content);
        top_Switch = findViewById(R.id.top_Switch);
        top_Switch.setOnCheckedChangeListener(new top_SwitchOnCheckLister());
        disturb_Switch = findViewById(R.id.disturb_Switch);
//        disturb_Switch.setOnCheckedChangeListener(new disturb_SwitchOnCheckLister());
        disturb_Switch.getSwitch().setOnClickListener(new disturb_SwitchClick());
        vip_Switch = findViewById(R.id.vip_Switch);
        vip_Switch.getSwitch().setOnClickListener(new vip_SwitchClick());
        MyArrowItemView recommend_ArrowItemView = findViewById(R.id.recommend_ArrowItemView);
        recommend_ArrowItemView.setOnClickListener(new recommend_ArrowItemOnClick());
        MyArrowItemView chatRecord_ArrowItemView = findViewById(R.id.chatRecord_ArrowItemView);
        chatRecord_ArrowItemView.setOnClickListener(new chatRecoreClick());
        MyArrowItemView chatBg_ArrowItemView = findViewById(R.id.chatBg_ArrowItemView);
        chatBg_ArrowItemView.setOnClickListener(new chatBg_ArrowViewClick());

        remove_Txt = findViewById(R.id.remove_Txt);
        remove_Txt.setOnClickListener(new remove_TxtClick());
        grade_Lin = findViewById(R.id.grade_Lin);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        toChatUsername = getIntent().getStringExtra("toChatUsername");
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(ChatDetailSet.this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("名字");
        right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setBackgroundResource(R.mipmap.adress_head_more);
        viewTouchDelegate.expandViewTouchDelegate(right_Btn, 50, 50, 50, 50);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        int margisright = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
        layoutParams.setMargins(0, 0, margisright, 0);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        right_Btn.setLayoutParams(layoutParams);

        return_Btn.setOnClickListener(new return_Btn());
        right_Btn.setOnClickListener(new right_Btn());
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    private class right_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            showMore(ChatDetailSet.this, right_Btn, 0);
        }
    }

    @Override
    protected void initData() {
        super.initData();

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        viewModel.getDeleteObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    LiveDataBus.get().with(DemoConstant.CONVERSATION_DELETE).postValue(new EaseEvent(DemoConstant.CONTACT_DECLINE, EaseEvent.TYPE.MESSAGE));
//                    finish();
                    Toast.makeText(ChatDetailSet.this, "聊天记录已清除", Toast.LENGTH_SHORT).show();
                }
            });
        });

        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true);
        top_Switch.getSwitch().setChecked(!TextUtils.isEmpty(conversation.getExtField()));
//        if (saveFile.getShareData("topSwitchCheck", ChatDetailSet.this).equals("false")) {
//            top_Switch.getSwitch().setChecked(false);
//        } else {
//            top_Switch.getSwitch().setChecked(true);
//        }
//        if (saveFile.getShareData("disturbSwitchCheck", ChatDetailSet.this).equals("false")) {
//            disturb_Switch.getSwitch().setChecked(false);
//        } else {
//            disturb_Switch.getSwitch().setChecked(true);
//        }

        AddFriendMethod(ChatDetailSet.this, saveFile.Friend_info_Url);
    }

    //修改备注
    private class nick_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            changeNick();
        }
    }

    //创建群聊
    private class group_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            GroupPrePickActivity.actionStart(mContext);
//            GroupPickContactsActivity.actionStartForResult(mContext, newmembers, ADD_NEW_CONTACTS);
        }
    }

    //设置备注
    private void changeNick() {
        new EditTextDialogFragment.Builder(mContext)
                .setContent(nick_ArrowItemView.getTvContent().getText().toString())
                .setConfirmClickListener(new EditTextDialogFragment.ConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view, String content) {
                        if (!TextUtils.isEmpty(content)) {
//                            itemGroupName.getTvContent().setText(content);
                            String changType = "2";
                            ChangeUserMethod(ChatDetailSet.this, saveFile.Friend_UpdateRemarkName_Url, content);
                        }
                    }
                })
                .setTitle("备注昵称")
                .show();
    }


    //   背景图
    private class chatBg_ArrowViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            selectPicFromLocal();
        }
    }

    private class leID_TxtOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text", StaticData.getNumbers(leID_Txt.getText().toString()));
            manager.setPrimaryClip(clipData);
            Toast.makeText(ChatDetailSet.this, "已复制到剪切板", Toast.LENGTH_SHORT).show();
        }
    }


    private class top_SwitchOnCheckLister implements MySwitchItemView.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(MySwitchItemView buttonView, boolean isChecked) {
            conversation.setExtField(isChecked ? (System.currentTimeMillis() + "") : "");
//            saveFile.saveShareData("topSwitchCheck", isChecked + "", ChatDetailSet.this);
            LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
        }
    }

//    private class disturb_SwitchOnCheckLister implements MySwitchItemView.OnCheckedChangeListener {
//        @Override
//        public void onCheckedChanged(MySwitchItemView buttonView, boolean isChecked) {
//            saveFile.saveShareData("disturbSwitchCheck", isChecked + "", ChatDetailSet.this);
//        }
//    }

    private class disturb_SwitchClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            DisturbMethod(ChatDetailSet.this, saveFile.Friend_Silence_Url, disturb_Switch.getSwitch().isChecked(), disturb_Switch.getSwitch());
        }
    }


    private class vip_SwitchClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
//            v.setEnabled(false);
            MyInfo myInfo = new MyInfo(ChatDetailSet.this);
            int vipType = myInfo.getUserInfo().getVipType();
            if (vipType == 0) {
                vip_Switch.getSwitch().setChecked(false);
                changeVip();
            } else {
                String baseUrl = saveFile.Friend_Fire_Url;
                double_RecallMethod(ChatDetailSet.this, baseUrl, vip_Switch.getSwitch().isChecked(), vip_Switch.getSwitch());
            }
        }
    }

    private class recommend_ArrowItemOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Chat_SelectUserCard.actionStartSingle(ChatDetailSet.this, conversation.conversationId(),"3");
        }
    }

    private class chatRecoreClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            SearchSingleChatActivity.actionStart(mContext, toChatUsername);
        }
    }

    private class clear_TxtOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            clearHistory();
        }
    }

    // 是否删除会话
    private void clearHistory() {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle(R.string.em_chat_delete_conversation)
                .setOnConfirmClickListener(new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        viewModel.deleteConversationById(conversation.conversationId());
                    }
                })
                .showCancelButton(true)
                .show();
    }

    //删除好友
    private class remove_TxtClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            removeFriend();
        }
    }

    private void removeFriend() {
        // 是否删除好友
        new SimpleDialogFragment.Builder(mContext)
                .setTitle("删除好友将会删除与该好友的聊天记录")
                .setOnConfirmClickListener(new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        RemoveMethod(ChatDetailSet.this, saveFile.Friend_Delete_Url);
                    }
                })
                .showCancelButton(true)
                .show();
    }

    //通知开通VIP
    private void changeVip() {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle("提示通知")
                .showContent(true)
                .setContent("该功能为会员特权功能，请开通会员后使用")
                .setOnConfirmClickListener(view -> {
                    Me_VIP.actionStart(mContext);
                })
                .showCancelButton(true)
                .show();
    }


    Friend_Details_Bean model;

    @SuppressLint("SetTextI18n")
    public void AddFriendMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("friendHxName", toChatUsername);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                model = new Gson().fromJson(result, Friend_Details_Bean.class);
                Friend_Details_Bean.DataDTO dataDTO = model.getData();
                Uri uri = Uri.parse(model.getData().getHeadImg());
                person_img.setImageURI(uri);
                String name = TextUtils.isEmpty(dataDTO.getRemarkName()) ? dataDTO.getNickname() : dataDTO.getRemarkName();
                nick_nameTxt.setText("昵称："+name);
                cententTxt.setText(name);
                nick_tv_content.setText(name);
                leID_Txt.setText("Mo ID:" + dataDTO.getUserNum().intValue());
                signature_Txt.setText("个性签名：" + dataDTO.getSignature());
                friend_tv_content.setText(dataDTO.getSource());
                grade_Txt.setText("LV" + dataDTO.getGrade().intValue());
                if (dataDTO.getVipType() == 0) {
                    vip_Txt.setVisibility(View.GONE);
                } else {
                    vip_Txt.setVisibility(View.VISIBLE);
//                            .setTextColor(ContextCompat.getColor(context, R.color.yellowfive));
                }

                if (TextUtils.equals(dataDTO.getFireType(), "2")) {
                    vip_Switch.getSwitch().setChecked(true);
                }
                IsSilenceMethod(dataDTO.getIsSilence());

                MyLevel.setGrade(grade_Lin, dataDTO.getGrade().intValue(), context);
            }

            @Override
            public void failed(String... args) {

            }
        });
    }

    /**
     * 消息免打扰
     *
     * @param isSilence
     */
    private void IsSilenceMethod(Long isSilence) {
        if (isSilence == 0) {
            disturb_Switch.getSwitch().setChecked(false);
        } else {
            disturb_Switch.getSwitch().setChecked(true);
        }
    }


    private void changePush() {
        new EMPushManagerRepository().getPushConfigsFromServer();
        List<String> onPushList = new ArrayList<>();
        String hxUserName = model.getData().getHxUserName();
        onPushList.add(hxUserName);
        boolean isPush = disturb_Switch.getSwitch().isChecked();
        viewModel.setUserNotDisturb(hxUserName, isPush);
    }


    private void showMore(final Context mContext, final View view, final int pos) {
        View contentView = View.inflate(mContext, R.layout.chatdetailset_feedback, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        MorePopup.setWidth(RadioGroup.LayoutParams.MATCH_PARENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
        MorePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        TextView change_txt = contentView.findViewById(R.id.change_txt);
        TextView newregistr_txt = contentView.findViewById(R.id.newregistr_txt);
        TextView cancel_txt = contentView.findViewById(R.id.cancel_txt);
        String userId = model.getData().getUserId();
        String user = "user";
        change_txt.setOnClickListener(v -> {
            String type = "2";
            GroupDetail_Report.actionStart(mContext, userId, user,type);
            MorePopup.dismiss();
        });
        newregistr_txt.setOnClickListener(v -> {
//            reportMethod(mContext, saveFile.Report_CreatReportLog_Url, type);
            String type = "1";
            GroupDetail_Report.actionStart(mContext, userId, user,type);
            MorePopup.dismiss();
        });
        cancel_txt.setOnClickListener(v -> {
            MorePopup.dismiss();
        });


    }


    /**
     * 举报用户 或头像
     *
     * @param context
     * @param baseUrl
     * @param type    1用户头像2用户3群头像4群
     */
    public void reportMethod(Context context, String baseUrl, String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("toReportId", model.getData().getUserNum());
        map.put("type", type);
        if (TextUtils.equals(type, "1")) {
            map.put("picture", model.getData().getHeadImg());
        }
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Toast.makeText(context, "举报已上传", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void failed(String... args) {
            }
        });
    }


    /**
     * 删除好友
     *
     * @param context
     * @param baseUrl
     */
    public void RemoveMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("friendUserId", model.getData().getUserId());
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @SneakyThrows
            @Override
            public void success(String result) {

                String HxUserName = model.getData().getHxUserName();
                sendCMDFireMess(HxUserName);
                DemoHelper.getInstance().getContactManager().deleteContact(HxUserName, false);
//                DemoHelper.getInstance().getChatManager().deleteConversation(HxUserName, true);
                finish();
//                DemoHelper.getInstance().getContactManager().aysncDeleteContact(HxUserName, new DemoEmCallBack() {
//                    @Override
//                    public void onSuccess() {
//                        DemoHelper.getInstance().getChatManager().deleteConversation(HxUserName, true);
//                        finish();
//                    }
//                });
            }

            @Override
            public void failed(String... args) {
            }
        });
    }

    private void sendCMDFireMess(String HxUserName) {
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        EMCmdMessageBody cmdBody = new EMCmdMessageBody("删除好友");
        cmdMsg.setTo(HxUserName);
        cmdMsg.addBody(cmdBody);
        cmdMsg.setAttribute(MyConstant.Dele_Friend, "1");
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }



    protected static final int REQUEST_CODE_LOCAL = 3;

    /**
     * select local image
     */
    protected void selectPicFromLocal() {
        EaseCompat.openImage(this, REQUEST_CODE_LOCAL);
    }

    /**
     * 选择本地图片处理结果
     *
     * @param data
     */
    protected void onActivityResultForLocalPhotos(@Nullable Intent data) {
        if (data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                saveFile.saveShareData(MyConstant.Chat_BG + toChatUsername, selectedImage.toString(), ChatDetailSet.this);
                LiveDataBus.get().with(MyConstant.Chat_BG).setValue(selectedImage.toString());
                finish();
                String filePath = EaseFileUtils.getFilePath(mContext, selectedImage);
                if (!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
//                    chatLayout.sendImageMessage(Uri.parse(filePath));
                } else {
//                    EaseFileUtils.saveUriPermission(mContext, selectedImage, data);
//                    chatLayout.sendImageMessage(selectedImage);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCAL) { // send local image
            onActivityResultForLocalPhotos(data);
        }
    }

    /**
     * 消息免打扰
     *
     * @param context
     * @param baseUrl
     */
    public void DisturbMethod(Context context, String baseUrl, boolean isBlock, View switchView) {
//        site_progressbar.setVisibility(View.VISIBLE);
        Map<String, Object> map = new HashMap<>();
        map.put("friendUserId", model.getData().getUserId());
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @SneakyThrows
            @Override
            public void success(String result) {
                if (isBlock) {
                    model.getData().setIsSilence(1L);
                } else {
                    model.getData().setIsSilence(0L);
                }
                IsSilenceMethod(model.getData().getIsSilence());
                switchView.setEnabled(true);
                changePush();
            }

            @Override
            public void failed(String... args) {
//                site_progressbar.setVisibility(View.GONE);
                switchView.setEnabled(true);
            }
        });
    }

    /**
     * 修改用户信息
     */
    public void ChangeUserMethod(Context context, String baseUrl, String remarkName) {
        Map<String, Object> map = new HashMap<>();
        map.put("friendUserId", model.getData().getUserId());
        map.put("remarkName", remarkName);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
//                        String name = TextUtils.isEmpty(dataDTO.getRemarkName()) ? dataDTO.getNickName() : dataDTO.getRemarkName();
                nick_nameTxt.setText("昵称：" + remarkName);
                cententTxt.setText(remarkName);
                nick_tv_content.setText(remarkName);

                JSONObject obj = new JSONObject();
                try {
                    obj.put("remarkName", remarkName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //修改本地其他用户名
                String hxUserName = model.getData().getHxUserName();
                DemoHelper.getInstance().getUserInfo(hxUserName).setExt(obj.toString());
            }

            @Override
            public void failed(String... args) {
            }
        });
    }

    /**开启Mo消息
     * @param context
     * @param baseUrl
     * @param
     */
    public void double_RecallMethod(Context context, String baseUrl, boolean isBlock, Switch switchView) {
        String fireType = "1";
        if (switchView.isChecked()) {
            fireType = "2";
        }
        Map<String, Object> map = new HashMap<>();
        map.put("fireType", fireType);
        map.put("friendUserId", model.getData().getUserId());
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                LiveDataBus.get().with(MyConstant.BURN_AFTER_READING_SET).setValue(isBlock);
            }

            @Override
            public void failed(String... args) {
            }
        });

    }


}