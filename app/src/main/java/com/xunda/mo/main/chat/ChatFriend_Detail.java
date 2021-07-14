package com.xunda.mo.main.chat;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
import com.hyphenate.chat.EMConversation;
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
import com.xunda.mo.hx.section.chat.activicy.SelectUserCardActivity;
import com.xunda.mo.hx.section.chat.viewmodel.ChatViewModel;
import com.xunda.mo.hx.section.dialog.DemoDialogFragment;
import com.xunda.mo.hx.section.dialog.EditTextDialogFragment;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.main.MainLogin_Register;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.main.baseView.MySwitchItemView;
import com.xunda.mo.model.Friend_Detalis_Model;
import com.xunda.mo.model.Main_QuestionFeedBack_Model;
import com.xunda.mo.model.baseModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.viewTouchDelegate;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import lombok.SneakyThrows;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ChatFriend_Detail extends BaseInitActivity {
    private EaseUser mUser;
    private boolean mIsFriend;
    private String addType;
    private Group add_friend_Group;
    private String FriendApplyId;
    private ProgressBar site_progressbar;
    private MyArrowItemView nick_ArrowItemView;
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
        toChatUsername = getIntent().getStringExtra("toChatUsername");
//        FriendApplyId = getIntent().getStringExtra("FriendApplyId");
        mUser = (EaseUser) getIntent().getSerializableExtra("user");
        addType = getIntent().getStringExtra("addType");
        mIsFriend = getIntent().getBooleanExtra("isFriend", true);
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
            Intent userCardIntent = new Intent(ChatFriend_Detail.this, SelectUserCardActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK);
            userCardIntent.putExtra("toUser", conversation.conversationId());
            startActivity(userCardIntent);
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
                        if (!TextUtils.isEmpty(content)) {
//                            itemGroupName.getTvContent().setText(content);
                            String changType = "2";
                            ChangeUserMethod(ChatFriend_Detail.this, saveFile.BaseUrl + saveFile.Friend_UpdateRemarkName_Url, content);
                        }
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
//            ChatFriend_AddFriend.actionStart(ChatFriend_Detail.this,toChatUsername);
            ChatFriend_AddFriend.actionStart(mContext, toChatUsername, mUser, addType);
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
                        RemoveMethod(ChatFriend_Detail.this, saveFile.BaseUrl + saveFile.Friend_Delete_Url);
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
            BlackMethod(ChatFriend_Detail.this, saveFile.BaseUrl + saveFile.Friend_SetBlack_Url, black_Switch.getSwitch().isChecked(), black_Switch.getSwitch());
        }
    }

    //移除黑名单
    private class move_Block_TxtClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            BlackMethod(ChatFriend_Detail.this, saveFile.BaseUrl + saveFile.Friend_SetBlack_Url, false, black_Switch.getSwitch());
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
                    Toast.makeText(ChatFriend_Detail.this, "聊天记录已清除", Toast.LENGTH_SHORT).show();
                }
            });
        });

        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true);

        String userName;
        String userID;
        if (TextUtils.isEmpty(FriendApplyId)) {
            userName = "friendHxName";
            userID = toChatUsername;
        } else {
            userName = "friendUserId";
            userID = FriendApplyId;
        }
        FriendMethod(ChatFriend_Detail.this, saveFile.BaseUrl + saveFile.Friend_info_Url + "?" + userName + "=" + userID);
    }

    Friend_Detalis_Model model;

    @SuppressLint("SetTextI18n")
    public void FriendMethod(Context context, String baseUrl) {
        RequestParams params = new RequestParams(baseUrl);
        if (saveFile.getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", saveFile.getShareData("JSESSIONID", context));
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (resultString != null) {
                    model = new Gson().fromJson(resultString, Friend_Detalis_Model.class);
                    if (model.getCode() == -1 || model.getCode() == 500) {
                        Intent intent = new Intent(context, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (model.getCode() == 200) {
                        Friend_Detalis_Model.DataDTO dataDTO = model.getData();
                        Uri uri = Uri.parse(model.getData().getHeadImg());
                        person_img.setImageURI(uri);

                        String name = TextUtils.isEmpty(dataDTO.getRemarkName()) ? dataDTO.getNikeName() : dataDTO.getRemarkName();
                        nick_nameTxt.setText("昵称：" + name);
                        cententTxt.setText(name);
                        nick_tv_content.setText(name);
                        leID_Txt.setText("Mo ID:" + dataDTO.getUserNum().intValue());
                        signature_Txt.setText("个性签名：" + dataDTO.getSignature());
                        friend_tv_content.setText(dataDTO.getSource());
                        grade_Txt.setText("LV." + dataDTO.getGrade().intValue());
                        if (dataDTO.getVipType() == 0) {
                            vip_Txt.setVisibility(View.GONE);
                        } else {
                            vip_Txt.setVisibility(View.VISIBLE);
//                            .setTextColor(ContextCompat.getColor(context, R.color.yellowfive));
                        }

                        isFriendSetView(dataDTO.getIsFriend(), dataDTO.getFriendStatus());

                    } else {
                        Toast.makeText(context, model.getMsg(), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {

            }
        });
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
        change_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {

                MorePopup.dismiss();
            }
        });
        newregistr_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                QuestionMethod(mContext, saveFile.BaseUrl + saveFile.User_PublicQuestionBack_Url);
                MorePopup.dismiss();
            }
        });
        cancel_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                MorePopup.dismiss();
            }
        });
//        contentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MorePopup.dismiss();
//            }
//        });
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
        JSONObject obj = new JSONObject();
        try {
            obj.put("picture", model.getData().getHeadImg());
            obj.put("toReportUserId", model.getData().getUserNum());
            obj.put("type", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(baseUrl);
        params.setAsJsonContent(true);
        params.setBodyContent(obj.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (resultString != null) {
                    Main_QuestionFeedBack_Model baseModel = new Gson().fromJson(resultString, Main_QuestionFeedBack_Model.class);
                    if (baseModel.getCode() == 200) {
//                    Login_Model baseModel = new Gson().fromJson(resultString, Login_Model.class);
//                    if (baseModel.isIsSuccess() && !baseModel.getData().equals("[]")) {
                        Toast.makeText(context, "反馈已上传", Toast.LENGTH_SHORT).show();
                        finish();
//
//                        saveFile.saveShareData("phoneNum", baseModel.getData().getPhoneNum(), MainLogin_Code.this);
//                        Intent intent = new Intent(context, MainActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
                    }

//            }
//                    } else {
//                        Toast.makeText(MainLogin_Code.this, "数据获取失败", Toast.LENGTH_SHORT).show();
//                    }
                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }
//                    JPushInterface.resumePush(Man_Login.this);//注册
//                    JPushInterface.setAliasAndTags(Man_Login.this,JsonGet.getReturnValue(resultString, "userid"),null);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {

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
        JSONObject obj = new JSONObject();
        try {
            obj.put("friendUserId", model.getData().getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(baseUrl);
        if (saveFile.getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", saveFile.getShareData("JSESSIONID", context));
        }
        params.setAsJsonContent(true);
        params.setBodyContent(obj.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @SneakyThrows
            @Override
            public void onSuccess(String resultString) {
                if (!TextUtils.isEmpty(resultString)) {
                    baseModel baseBean = new Gson().fromJson(resultString, baseModel.class);
                    if (baseBean.getCode() == -1) {
                        Intent intent = new Intent(context, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (baseBean.getCode() == 200) {
                        Toast.makeText(context, baseBean.getMsg(), Toast.LENGTH_SHORT).show();

                        DemoHelper.getInstance().getContactManager().deleteContact(model.getData().getHxUserName(), false);
                        DemoHelper.getInstance().getChatManager().deleteConversation(model.getData().getHxUserName(), true);
                        finish();

                    } else {
                        Toast.makeText(context, baseBean.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

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
        JSONObject obj = new JSONObject();
        try {
            obj.put("friendUserId", model.getData().getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(baseUrl);
        if (saveFile.getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", saveFile.getShareData("JSESSIONID", context));
        }
        params.setAsJsonContent(true);
        params.setBodyContent(obj.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @SneakyThrows
            @Override
            public void onSuccess(String resultString) {
                if (!TextUtils.isEmpty(resultString)) {
                    baseModel baseBean = new Gson().fromJson(resultString, baseModel.class);
                    if (baseBean.getCode() == -1) {
                        Intent intent = new Intent(context, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (baseBean.getCode() == 200) {
                        if (isBlock) {
                            model.getData().setFriendStatus("3");
                        } else {
                            model.getData().setFriendStatus("1");
                        }
                        isFriendSetView(model.getData().getIsFriend(), model.getData().getFriendStatus());

//                        initData();

//                        Toast.makeText(context, baseBean.getMsg(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, baseBean.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }
                site_progressbar.setVisibility(View.GONE);
                switchView.setEnabled(true);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                switchView.setEnabled(true);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                switchView.setEnabled(true);
            }

            @Override
            public void onFinished() {
                switchView.setEnabled(true);
            }

        });
    }

    /**
     * 修改用户信息
     */
    public void ChangeUserMethod(Context context, String baseUrl, String remarkName) {
        RequestParams params = new RequestParams(baseUrl);
        params.addBodyParameter("friendUserId", model.getData().getUserId());
        params.addBodyParameter("remarkName", remarkName);
        if (saveFile.getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", saveFile.getShareData("JSESSIONID", context));
        }
        params.setAsJsonContent(true);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (resultString != null) {
                    baseModel baseModel = new Gson().fromJson(resultString, baseModel.class);
                    if (baseModel.getCode() == 200) {
                        Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
//                        String name = TextUtils.isEmpty(dataDTO.getRemarkName()) ? dataDTO.getNikeName() : dataDTO.getRemarkName();
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
                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {
            }
        });
    }


}

