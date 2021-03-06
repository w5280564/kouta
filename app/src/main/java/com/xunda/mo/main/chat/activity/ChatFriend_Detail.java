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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easecallkit.base.EaseCallUserInfo;
import com.hyphenate.easecallkit.livedatas.EaseLiveDataBus;
import com.hyphenate.easecallkit.utils.EaseCallKitUtils;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.StringUtil;
import com.xunda.mo.R;
import com.xunda.mo.dialog.TwoButtonDialog;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.db.DemoDbHelper;
import com.xunda.mo.hx.common.db.entity.EmUserEntity;
import com.xunda.mo.hx.common.interfaceOrImplement.OnResourceParseCallback;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.chat.activicy.ChatActivity;
import com.xunda.mo.hx.section.chat.viewmodel.ChatViewModel;
import com.xunda.mo.hx.section.dialog.EditTextDialogFragment;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.baseView.MyArrowItemView;
import com.xunda.mo.main.baseView.MySwitchItemView;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.group.activity.GroupDetail_Report;
import com.xunda.mo.main.me.activity.ChangeMyNickNameActivity;
import com.xunda.mo.main.me.activity.SetFriendRemarkNameActivity;
import com.xunda.mo.model.Friend_Details_Bean;
import com.xunda.mo.model.baseModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.pinyin.PinyinUtils;
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
    private ActivityResultLauncher mActivityResultLauncher;

    /**
     * @param context
     * @param FriendApplyId ???????????????ID
     * @param addType       1:???????????? 2:???????????? 3:??????????????? 4:???????????? 5:???????????? 6:??????????????? 7:??????????????? 8:??????????????????  9:???????????????????????????
     */
    public static void actionStart(Context context, String FriendApplyId, String addType) {
        Intent intent = new Intent(context, ChatFriend_Detail.class);
        intent.putExtra("FriendApplyId", FriendApplyId);
        intent.putExtra("addType", addType);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param toChatUsername ??????username  ???????????????HXusername
     * @param addType        1:???????????? 2:???????????? 3:??????????????? 4:???????????? 5:???????????? 6:??????????????? 7:??????????????? 8:??????????????????  9:???????????????????????????
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


    /**
     * @param context
     * @param toChatUsername ??????username  ???????????????HXusername
     * @param addType        1:???????????? 2:???????????? 3:??????????????? 4:???????????? 5:???????????? 6:??????????????? 7:??????????????? 8:??????????????????  9:???????????????????????????
     */
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
    private TextView friend_tv_content, nick_tv_content, send_mess_Txt, remove_Txt, add_Txt;
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
        black_Switch.getSwitch().setOnClickListener(new switch_itemClickClick());

        mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result!=null) {
                    int resultCode = result.getResultCode();
                    if (resultCode==RESULT_OK) {
                        Intent mIntent = result.getData();
                        if (mIntent!=null) {
                            String remarkName = mIntent.getStringExtra("newName");

                            if (TextUtils.isEmpty(remarkName)) {
                                remarkName = nickName;
                            }
                            cententTxt.setText(remarkName);
                            nick_tv_content.setText(remarkName);

                            JSONObject obj = new JSONObject();
                            try {
                                obj.put("remarkName", remarkName);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //???????????????????????????
                            String hxUserName = model.getData().getHxUserName();
                            updateConversionExdInfoInFriend(hxUserName,remarkName);
                            LiveDataBus.get().with(DemoConstant.CONTACT_UPDATE).postValue(EaseEvent.create(DemoConstant.CONTACT_UPDATE, EaseEvent.TYPE.CONTACT,hxUserName,remarkName));
                            DemoHelper.getInstance().getUserInfo(hxUserName).setExt(obj.toString());
                        }
                    }
                }
            }
        });
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
        title_Include.setElevation(2f);//??????
        title_Include.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        Button return_Btn = title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        cententTxt = title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("??????");
        right_Btn = title_Include.findViewById(R.id.right_Btn);
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
            if (!TextUtils.isEmpty(conversation.conversationId())) {
                Chat_SelectUserCard.actionStartSingle(ChatFriend_Detail.this, conversation.conversationId(), "3");
            }
        }
    }

    //????????????
    private class nick_ArrowItemViewClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            if (model==null) {
                return;
            }

            if (model.getData()==null) {
                return;
            }

            Intent intent = new Intent(mContext, SetFriendRemarkNameActivity.class);
            intent.putExtra("name",nick_tv_content.getText().toString());
            intent.putExtra("friendUserId",model.getData().getUserId());
            mActivityResultLauncher.launch(intent);
        }
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

    //????????????
    private class remove_TxtClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            showToastDialog();
        }
    }

    /**
     * ??????dialog
     */
    private void showToastDialog() {
        TwoButtonDialog dialog = new TwoButtonDialog(this, "???????????????????????????????????????????????????", "??????", "??????",
                new TwoButtonDialog.ConfirmListener() {

                    @Override
                    public void onClickRight() {
                        RemoveMethod(ChatFriend_Detail.this, saveFile.Friend_Delete_Url);
                    }

                    @Override
                    public void onClickLeft() {

                    }
                });
        dialog.show();

    }


    //???????????????
    private class switch_itemClickClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            BlackMethod(ChatFriend_Detail.this, saveFile.Friend_SetBlack_Url, black_Switch.getSwitch().isChecked(), black_Switch.getSwitch());
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
                    Toast.makeText(ChatFriend_Detail.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                }
            });
        });

        LiveDataBus.get().with(MyConstant.CONTACT_UPDATE, EaseEvent.class).observe(this, new Observer<EaseEvent>() {
            @Override
            public void onChanged(EaseEvent event) {
                if (event == null) {
                    return;
                }


                if (!event.isContactChange()) {
                    return;
                }

                if (StringUtil.isBlank(event.message)) {
                    return;
                }


                if (!event.message.equals(toChatUsername)){
                    return;
                }

                if (!StringUtil.isBlank(event.message2)) {
                    cententTxt.setText(event.message2);
                    nick_tv_content.setText(event.message2);
                }

            }
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
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                model = new Gson().fromJson(result, Friend_Details_Bean.class);
                Friend_Details_Bean.DataDTO dataDTO = model.getData();
                Uri uri = Uri.parse(model.getData().getHeadImg());
                person_img.setImageURI(uri);

                nickName = dataDTO.getNickname();
                nick_nameTxt.setText("?????????" + nickName);
                String name = TextUtils.isEmpty(dataDTO.getRemarkName()) ? nickName : dataDTO.getRemarkName();
                cententTxt.setText(name);
                nick_tv_content.setText(name);
                leID_Txt.setText("Mo ID:" + dataDTO.getUserNum().intValue());
                String signature = dataDTO.getSignature();
                if (!TextUtils.isEmpty(signature)) {
                    signature_Txt.setText("???????????????" + dataDTO.getSignature());
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
                updateDBdTata(dataDTO);
                MyLevel.setGrade(grade_Lin, dataDTO.getGrade().intValue(), context);
            }

            @Override
            public void failed(String... args) {

            }
        });
    }


    private void updateDBdTata(Friend_Details_Bean.DataDTO mUserModel) {
        EmUserEntity entity = new EmUserEntity();
        entity.setUsername(toChatUsername);
        // ??????????????????????????????????????????????????????
        String nickName_HX = TextUtils.isEmpty(mUserModel.getRemarkName()) ? nickName : mUserModel.getRemarkName();
        entity.setNickname(nickName_HX);
        String pinyin = PinyinUtils.getPingYin(nickName_HX);
        String sortString = pinyin.substring(0, 1).toUpperCase();
        if (sortString.matches("[A-Z]")) {
            entity.setInitialLetter(sortString);
        } else {
            entity.setInitialLetter("#");
        }
        entity.setAvatar(mUserModel.getHeadImg());
        entity.setBirth("");
        entity.setContact(0);//???????????? 4??????????????????
        entity.setEmail("");
        entity.setGender(0);
        entity.setBirth("");
        entity.setSign("");
        entity.setPhone("");
        JSONObject obj = new JSONObject();
        try {
            obj.put(MyConstant.LIGHT_STATUS, mUserModel.getLightStatus());
            obj.put(MyConstant.VIP_TYPE, mUserModel.getVipType());
            obj.put(MyConstant.USER_NUM, mUserModel.getUserNum());
            obj.put(MyConstant.USER_ID, mUserModel.getUserId());
            obj.put(MyConstant.REMARK_NAME, mUserModel.getRemarkName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        entity.setExt(obj.toString());


        //??????callKit??????????????????
        EaseCallUserInfo info = new EaseCallUserInfo(nickName_HX, mUserModel.getHeadImg());
        info.setUserId(info.getUserId());
        EaseLiveDataBus.get().with(EaseCallKitUtils.UPDATE_USERINFO).postValue(info);

        //???????????????????????????
        DemoHelper.getInstance().getModel().insert(entity);
        //???????????????????????????
        DemoHelper.getInstance().updateContactList();
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
     * @param isFriend     0???????????? 1?????????
     * @param friendStatus 1??????2?????????3?????????
     */
    private void isFriendSetView(Long isFriend, String friendStatus) {
        if (isFriend == 0) {
            add_Txt.setVisibility(View.VISIBLE);
            send_mess_Txt.setVisibility(View.GONE);
            remove_Txt.setVisibility(View.GONE);
            add_friend_Group.setVisibility(View.GONE);//????????????
        } else {
            if (TextUtils.equals(friendStatus, "3")) {
                add_Txt.setVisibility(View.GONE);
                send_mess_Txt.setVisibility(View.GONE);
                remove_Txt.setVisibility(View.VISIBLE);
                add_friend_Group.setVisibility(View.VISIBLE);//????????????
                black_Switch.getSwitch().setChecked(true);
            } else if (TextUtils.equals(friendStatus, "2")) {
                black_Switch.getSwitch().setChecked(false);
            } else {
                add_Txt.setVisibility(View.GONE);
                send_mess_Txt.setVisibility(View.VISIBLE);
                remove_Txt.setVisibility(View.VISIBLE);
                add_friend_Group.setVisibility(View.VISIBLE);//????????????
                black_Switch.getSwitch().setChecked(false);
            }

        }
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
                GroupDetail_Report.actionStart(mContext, userId, user, type);
                MorePopup.dismiss();
            }
        });
        newregistr_txt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
//                QuestionMethod(mContext, saveFile.Question_Login);
                String type = "1";
                GroupDetail_Report.actionStart(mContext, userId, user, type);
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
            Toast.makeText(ChatFriend_Detail.this, "?????????????????????", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * ????????????
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

                String HxUserName = model.getData().getHxUserName();
                sendCMDFireMess(HxUserName);
                DemoHelper.getInstance().getContactManager().deleteContact(HxUserName, false);
                finish();

            }

            @Override
            public void failed(String... args) {
            }
        });
    }

    private void sendCMDFireMess(String HxUserName) {
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        EMCmdMessageBody cmdBody = new EMCmdMessageBody("????????????");
        cmdMsg.setTo(HxUserName);
        cmdMsg.addBody(cmdBody);
        cmdMsg.setAttribute(MyConstant.Dele_Friend, "1");
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }


    /**
     * ?????????????????????
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



    //????????????????????????????????????
    private void updateConversionExdInfoInFriend(String currentConversationId,String showName) {
        EMConversation currentConversation = EMClient.getInstance().chatManager().getConversation(currentConversationId);
        if (currentConversation!=null) {
            String extField = currentConversation.getExtField();
            JSONObject jsonObject = null;
            if (!StringUtil.isBlank(extField)) {
                try {
                    jsonObject = new JSONObject(extField);
                    jsonObject.put("isInsertGroupOrFriendInfo", true);
                    jsonObject.put("showName", showName);
                } catch (JSONException e) {
                    jsonObject = getJsonObjectFriend(showName);
                }
            } else {
                jsonObject = getJsonObjectFriend(showName);
            }

            if (jsonObject == null) {
                return;
            }
            currentConversation.setExtField(jsonObject.toString());
        }
    }


    @NonNull
    private JSONObject getJsonObjectFriend(String showName) {
        JSONObject jsonObject;
        jsonObject  = new JSONObject();
        try {
            jsonObject.put("isInsertGroupOrFriendInfo", true);
            jsonObject.put("showName", showName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


}

