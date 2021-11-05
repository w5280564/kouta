package com.xunda.mo.main.chat.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.db.DemoDbHelper;
import com.xunda.mo.hx.common.interfaceOrImplement.OnResourceParseCallback;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.chat.activicy.ChatActivity;
import com.xunda.mo.hx.section.chat.viewmodel.ChatViewModel;
import com.xunda.mo.hx.section.dialog.DemoDialogFragment;
import com.xunda.mo.hx.section.dialog.EditTextDialogFragment;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.main.baseView.MySwitchItemView;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.group.activity.GroupDetail_Report;
import com.xunda.mo.model.Friend_Details_Bean;
import com.xunda.mo.model.baseModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.MyLevel;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;

public class ChatFriend_Detail extends BaseInitActivity {
    private EaseUser mUser;
    private boolean mIsFriend;
    private String addType;
    private Group add_friend_Group;
    private String FriendApplyId;
    private ProgressBar site_progressbar;
    private MyArrowItemView nick_ArrowItemView;
    private LinearLayout grade_Lin, label_Lin;
    private String nickName;
    String remarkName;
//    private Switch switch_item;

    /**
     * @param context
     * @param FriendApplyId 服务器好友ID
     * @param addType       1:墨号添加 2:昵称添加 3:手机号添加 4:邮箱添加 5:标签添加 6:通过群添加 7:二维码添加 8:通过名片添加  9:通过等级排行榜添加
     */
    public static void actionStart(Context context, String FriendApplyId, String addType) {
        Intent intent = new Intent(context, ChatFriend_Detail.class);
        intent.putExtra("FriendApplyId", FriendApplyId);
        intent.putExtra("addType", addType);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param toChatUsername 环信username  对应服务器HXusername
     * @param addType        1:墨号添加 2:昵称添加 3:手机号添加 4:邮箱添加 5:标签添加 6:通过群添加 7:二维码添加 8:通过名片添加  9:通过等级排行榜添加
     */
    public static void actionStart(Context context, String toChatUsername, EaseUser user, String addType) {
        Intent intent = new Intent(context, ChatFriend_Detail.class);
        intent.putExtra("toChatUsername", toChatUsername);
        intent.putExtra("user", (EaseUser) user);
        if (user.getContact() == 0) {
            intent.putExtra("isFriend", true);
        } else {
            intent.putExtra("isFriend", false);
        }
        intent.putExtra("addType", addType);
        context.startActivity(intent);
    }

    public static void actionStartActivity(Context context, String toChatUsername, String addType) {
        Intent intent = new Intent(context, ChatFriend_Detail.class);
        intent.putExtra("toChatUsername", toChatUsername);
        intent.putExtra("addType", addType);
        context.startActivity(intent);
    }

    private String toChatUsername;
    private SimpleDraweeView person_img;
    private TextView nick_nameTxt, cententTxt, leID_Txt, vip_Txt, signature_Txt, grade_Txt;
    private Button right_Btn;
    private TextView friend_tv_content, nick_tv_content, send_mess_Txt, remove_Txt, add_Txt, move_Block_Txt;
    private EMConversation conversation;
    private ChatViewModel viewModel;
    private MySwitchItemView black_Switch;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chatfriend_detail;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initTitle();
        person_img = findViewById(R.id.person_img);
        nick_nameTxt = findViewById(R.id.nick_nameTxt);
        leID_Txt = findViewById(R.id.leID_Txt);
        leID_Txt.setOnClickListener(new leID_TxtOnClick());
        vip_Txt = findViewById(R.id.vip_Txt);
        grade_Txt = findViewById(R.id.grade_Txt);
        signature_Txt = findViewById(R.id.signature_Txt);
        send_mess_Txt = findViewById(R.id.send_mess_Txt);
        remove_Txt = findViewById(R.id.remove_Txt);
        remove_Txt.setOnClickListener(new remove_TxtClick());
        add_Txt = findViewById(R.id.add_Txt);
        move_Block_Txt = findViewById(R.id.move_Block_Txt);
        move_Block_Txt.setOnClickListener(new move_Block_TxtClick());
        add_friend_Group = findViewById(R.id.add_friend_Group);
        send_mess_Txt.setOnClickListener(new send_mess_TxtOnClick());
        site_progressbar = findViewById(R.id.site_progressbar);
        grade_Lin = findViewById(R.id.grade_Lin);
        label_Lin = findViewById(R.id.label_Lin);


        MyArrowItemView friend_ArrowItemView = findViewById(R.id.friend_ArrowItemView);
        friend_tv_content = friend_ArrowItemView.findViewById(R.id.tv_content);
        nick_ArrowItemView = findViewById(R.id.nick_ArrowItemView);
        nick_tv_content = nick_ArrowItemView.findViewById(R.id.tv_content);
        nick_ArrowItemView.setOnClickListener(new nick_ArrowItemViewClick());
        MyArrowItemView recommend_ArrowItemView = findViewById(R.id.recommend_ArrowItemView);
        recommend_ArrowItemView.setOnClickListener(new recommend_ArrowItemOnClick());
        add_Txt.setOnClickListener(new add_TxtClick());
        black_Switch = findViewById(R.id.black_Switch);
//        switch_item = black_Switch.findViewById(R.id.switch_item);
//        black_Switch.setOnCheckedChangeListener(new black_SwitchOnCheck());
        black_Switch.getSwitch().setOnClickListener(new switch_itemClickClick());
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        toChatUsername = intent.getStringExtra("toChatUsername");
        FriendApplyId = intent.getStringExtra("FriendApplyId");
        mUser = (EaseUser) intent.getSerializableExtra("user");
        addType = intent.getStringExtra("addType");
        mIsFriend = intent.getBooleanExtra("isFriend", true);
        if (!mIsFriend) {
            List<String> users = null;
            if (DemoDbHelper.getInstance(mContext).getUserDao() != null) {
                users = DemoDbHelper.getInstance(mContext).getUserDao().loadContactUsers();
            }
            mIsFriend = users != null && users.contains(mUser.getUsername());
        }

    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setElevation(2f);//阴影
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
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
            showMore(ChatFriend_Detail.this, right_Btn, 0);
        }
    }

    private class recommend_ArrowItemOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Chat_SelectUserCard.actionStartSingle(ChatFriend_Detail.this, conversation.conversationId(),"3");
        }
    }

    //修改备注
    private class nick_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            changeNick();
        }
    }

    //设置备注
    private void changeNick() {
        new EditTextDialogFragment.Builder(mContext)
                .setContent(nick_ArrowItemView.getTvContent().getText().toString())
                .setConfirmClickListener(new EditTextDialogFragment.ConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view, String content) {
//                        if (!TextUtils.isEmpty(content)) {
//                            itemGroupName.getTvContent().setText(content);
                            String changType = "2";
                            remarkName = content;
                            ChangeUserMethod(ChatFriend_Detail.this, saveFile.Friend_UpdateRemarkName_Url);
//                        }
                    }
                })
                .setTitle("备注昵称")
                .show();
    }

    private class send_mess_TxtOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            ChatActivity.actionStart(mContext, toChatUsername, EaseConstant.CHATTYPE_SINGLE);
        }
    }

    private class add_TxtClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            ChatFriend_AddFriend.actionStart(mContext, toChatUsername, addType);
//                ChatFriend_AddFriend.actionStart(mContext, toChatUsername, mUser, addType);

        }
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
                        RemoveMethod(ChatFriend_Detail.this, saveFile.Friend_Delete_Url);
                    }
                })
                .showCancelButton(true)
                .show();
    }


    //加入黑名单
    private class switch_itemClickClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            BlackMethod(ChatFriend_Detail.this, saveFile.Friend_SetBlack_Url, black_Switch.getSwitch().isChecked(), black_Switch.getSwitch());
        }
    }

    //移除黑名单
    private class move_Block_TxtClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            BlackMethod(ChatFriend_Detail.this, saveFile.Friend_SetBlack_Url, false, black_Switch.getSwitch());
        }
    }

    String userName;
    String userID;

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
                    Toast.makeText(ChatFriend_Detail.this, "聊天记录已清除", Toast.LENGTH_SHORT).show();
                }
            });
        });

        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true);


        if (TextUtils.isEmpty(FriendApplyId)) {
            userName = "friendHxName";
            userID = toChatUsername;
        } else {
            userName = "friendUserId";
            userID = FriendApplyId;
        }
        FriendMethod(ChatFriend_Detail.this, saveFile.Friend_info_Url);
    }

    Friend_Details_Bean model;

    @SuppressLint("SetTextI18n")
    public void FriendMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put(userName, userID);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                model = new Gson().fromJson(result, Friend_Details_Bean.class);
                Friend_Details_Bean.DataDTO dataDTO = model.getData();
                Uri uri = Uri.parse(model.getData().getHeadImg());
                person_img.setImageURI(uri);

                 nickName = TextUtils.isEmpty(dataDTO.getRemarkName()) ? dataDTO.getNickname() : dataDTO.getRemarkName();
                nick_nameTxt.setText("昵称：" + nickName);
                cententTxt.setText(nickName);
                nick_tv_content.setText(nickName);
                leID_Txt.setText("Mo ID:" + dataDTO.getUserNum().intValue());
                String signature = dataDTO.getSignature();
                if (!TextUtils.isEmpty(signature)) {
                    signature_Txt.setText("个性签名：" + dataDTO.getSignature());
                }
                friend_tv_content.setText(dataDTO.getSource());
                grade_Txt.setText("LV." + dataDTO.getGrade().intValue());
                if (dataDTO.getVipType() == 0) {
                    vip_Txt.setVisibility(View.GONE);
                } else {
                    vip_Txt.setVisibility(View.VISIBLE);
//                            .setTextColor(ContextCompat.getColor(context, R.color.yellowfive));
                }
                isFriendSetView(dataDTO.getIsFriend(), dataDTO.getFriendStatus());
                toChatUsername = dataDTO.getHxUserName();
                tagList(label_Lin, context, dataDTO.getTag());

                MyLevel.setGrade(grade_Lin, dataDTO.getGrade().intValue(), context);
            }

            @Override
            public void failed(String... args) {

            }
        });
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


    /**
     * @param isFriend     0不是好友 1是好友
     * @param friendStatus 1正常2已删除3黑名单
     */
    private void isFriendSetView(Long isFriend, String friendStatus) {
        if (isFriend == 0) {
            add_Txt.setVisibility(View.VISIBLE);
            move_Block_Txt.setVisibility(View.GONE);
            send_mess_Txt.setVisibility(View.GONE);
            remove_Txt.setVisibility(View.GONE);
            add_friend_Group.setVisibility(View.GONE);//好友设置
        } else {
            if (TextUtils.equals(friendStatus, "3")) {
                add_Txt.setVisibility(View.GONE);
                move_Block_Txt.setVisibility(View.VISIBLE);
                send_mess_Txt.setVisibility(View.GONE);
                remove_Txt.setVisibility(View.GONE);
                add_friend_Group.setVisibility(View.VISIBLE);//好友设置
                black_Switch.getSwitch().setChecked(true);
                Log.d("block", " 已拉黑");
            } else if (TextUtils.equals(friendStatus, "2")) {
                black_Switch.getSwitch().setChecked(false);

            } else {
                Log.d("block", " 取消拉黑");
                add_Txt.setVisibility(View.GONE);
                move_Block_Txt.setVisibility(View.GONE);
                send_mess_Txt.setVisibility(View.VISIBLE);
                remove_Txt.setVisibility(View.VISIBLE);
                add_friend_Group.setVisibility(View.VISIBLE);//好友设置
                black_Switch.getSwitch().setChecked(false);
            }

        }
    }

    private class clear_TxtOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            clearHistory();
        }
    }

    private void clearHistory() {
        // 是否删除会话
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
        change_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                String type = "2";
                GroupDetail_Report.actionStart(mContext, userId, user,type);
                MorePopup.dismiss();
            }
        });
        newregistr_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
//                QuestionMethod(mContext, saveFile.Question_Login);
                String type = "1";
                GroupDetail_Report.actionStart(mContext, userId, user,type);
                MorePopup.dismiss();
            }
        });
        cancel_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                MorePopup.dismiss();
            }
        });
    }


    private class leID_TxtOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text", StaticData.getNumbers(leID_Txt.getText().toString()));
            manager.setPrimaryClip(clipData);
            Toast.makeText(ChatFriend_Detail.this, "已复制到剪切板", Toast.LENGTH_SHORT).show();
        }
    }

    //举报用户头像
    public void QuestionMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("picture", model.getData().getHeadImg());
        map.put("toReportId", model.getData().getUserNum());
        map.put("type", "1");
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Toast.makeText(context, "反馈已上传", Toast.LENGTH_SHORT).show();
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
                baseModel baseBean = new Gson().fromJson(result, baseModel.class);
                Toast.makeText(context, baseBean.getMsg(), Toast.LENGTH_SHORT).show();

                DemoHelper.getInstance().getContactManager().deleteContact(model.getData().getHxUserName(), false);
                DemoHelper.getInstance().getChatManager().deleteConversation(model.getData().getHxUserName(), true);
                finish();

            }

            @Override
            public void failed(String... args) {
            }
        });
    }

    /**
     * 拉人移除黑名单
     *
     * @param context
     * @param baseUrl
     */
    public void BlackMethod(Context context, String baseUrl, boolean isBlock, View switchView) {
        site_progressbar.setVisibility(View.VISIBLE);
        Map<String, Object> map = new HashMap<>();
        map.put("friendUserId", model.getData().getUserId());
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                if (isBlock) {
                    model.getData().setFriendStatus("3");
                    String HxUserName = model.getData().getHxUserName();
//                    sendCMDFireMess(HxUserName);
//                    DemoHelper.getInstance().getChatManager().deleteConversation(HxUserName, false);
                } else {
                    model.getData().setFriendStatus("1");
                }
                isFriendSetView(model.getData().getIsFriend(), model.getData().getFriendStatus());
                site_progressbar.setVisibility(View.GONE);
                switchView.setEnabled(true);
            }

            @Override
            public void failed(String... args) {
                site_progressbar.setVisibility(View.GONE);
                switchView.setEnabled(true);
            }
        });
    }

    private void sendCMDFireMess(String HxUserName) {
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        EMCmdMessageBody cmdBody = new EMCmdMessageBody("拉黑");
        cmdMsg.setTo(HxUserName);
        cmdMsg.addBody(cmdBody);
        cmdMsg.setAttribute(MyConstant.Black_Friend, "1");
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }


    /**
     * 修改用户信息
     */
    public void ChangeUserMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("friendUserId", model.getData().getUserId());
        map.put("remarkName", remarkName);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                if (TextUtils.isEmpty(remarkName)){
                    remarkName = nickName;
                }
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


}

