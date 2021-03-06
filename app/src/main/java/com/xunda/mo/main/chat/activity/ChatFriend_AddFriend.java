package com.xunda.mo.main.chat.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.hyphenate.exceptions.HyphenateException;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.db.DemoDbHelper;
import com.xunda.mo.hx.common.interfaceOrImplement.OnResourceParseCallback;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.chat.viewmodel.ChatViewModel;
import com.xunda.mo.model.Friend_Details_Bean;
import com.xunda.mo.model.baseModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.MyLevel;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatFriend_AddFriend extends BaseInitActivity {
    private EaseUser mUser;
    private boolean mIsFriend;
    private EditText apply_Edit;
    private String addType;
    private String source;
    private LinearLayout garde_Lin;

    /**
     * @param context
     * @param toChatUsername ??????username  ???????????????HXusername
     * @param addType        1:???????????? 2:???????????? 3:??????????????? 4:???????????? 5:???????????? 6:??????????????? 7:??????????????? 8:??????????????????  9:???????????????????????????
     */
    public static void actionStart(Context context, String toChatUsername, String addType) {
        Intent intent = new Intent(context, ChatFriend_AddFriend.class);
        intent.putExtra("toChatUsername", toChatUsername);
        intent.putExtra("addType", addType);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param toChatUsername ??????username  ???????????????HXusername
     * @param addType        1:???????????? 2:???????????? 3:??????????????? 4:???????????? 5:???????????? 6:??????????????? 7:??????????????? 8:??????????????????  9:???????????????????????????
     */
    public static void actionStart(Context context, String toChatUsername, EaseUser user, String addType) {
        Intent intent = new Intent(context, ChatFriend_AddFriend.class);
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
    private TextView nick_nameTxt, cententTxt, leID_Txt, vip_Txt, signature_Txt, grade_Txt, send_mess_Txt;
    private Button right_Btn;
    private TextView friend_tv_content, nick_tv_content, move_Block_Txt;
    private EMConversation conversation;
    private ChatViewModel viewModel;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_chatfriend_addfriend;
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
        apply_Edit = findViewById(R.id.apply_Edit);
        send_mess_Txt.setOnClickListener(new send_mess_TxtClick());
        garde_Lin = findViewById(R.id.garde_Lin);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        toChatUsername = getIntent().getStringExtra("toChatUsername");
        mUser = (EaseUser) getIntent().getSerializableExtra("user");
        mIsFriend = getIntent().getBooleanExtra("isFriend", true);
        addType = getIntent().getStringExtra("addType");
        source = source(addType);
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
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("??????");
        right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.GONE);

        return_Btn.setOnClickListener(new return_Btn());
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }

    private class send_mess_TxtClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
//            if (TextUtils.isEmpty(apply_Edit.getText().toString())) {
//                Toast.makeText(ChatFriend_AddFriend.this, "?????????????????????", Toast.LENGTH_SHORT).show();
//                return;
//            }

            AddFriendMethod(ChatFriend_AddFriend.this,  saveFile.User_FriendAdd_Url);
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
                    Toast.makeText(ChatFriend_AddFriend.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                }
            });
        });

        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true);
        FriendMethod(ChatFriend_AddFriend.this,  saveFile.Friend_info_Url );
    }

    Friend_Details_Bean detailModel;

    @SuppressLint("SetTextI18n")
    public void FriendMethod(Context context, String baseUrl) {
        Map<String,Object> map = new HashMap<>();
        map.put("friendHxName",toChatUsername);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                detailModel = new Gson().fromJson(result, Friend_Details_Bean.class);
                Friend_Details_Bean.DataDTO dataDTO = detailModel.getData();
                Uri uri = Uri.parse(detailModel.getData().getHeadImg());
                person_img.setImageURI(uri);

                String name = TextUtils.isEmpty(dataDTO.getRemarkName()) ? dataDTO.getNickname() : dataDTO.getRemarkName();
                nick_nameTxt.setText(name);
                cententTxt.setText(name);
//                        nick_tv_content.setText(name);
                leID_Txt.setText("Mo ID:" + dataDTO.getUserNum().intValue());
                signature_Txt.setText("???????????????" + dataDTO.getSignature());
//                        friend_tv_content.setText(dataDTO.getSource());
                grade_Txt.setText("LV" + dataDTO.getGrade().intValue());
                if (dataDTO.getVipType() == 0) {
                    vip_Txt.setVisibility(View.GONE);
                } else {
                    vip_Txt.setVisibility(View.VISIBLE);
                }
                MyLevel.setGrade(garde_Lin, dataDTO.getGrade().intValue(), context);
            }
            @Override
            public void failed(String... args) {

            }
        });

    }



    private class leID_TxtOnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text", StaticData.getNumbers(leID_Txt.getText().toString()));
            manager.setPrimaryClip(clipData);
            Toast.makeText(ChatFriend_AddFriend.this, "?????????????????????", Toast.LENGTH_SHORT).show();
        }
    }

    //????????????
    public void AddFriendMethod(Context context, String baseUrl) {
        Map<String,Object> map = new HashMap<>();
        map.put("addType", addType);
        map.put("remark", apply_Edit.getText().toString());
        map.put("source", source);
        map.put("userId", detailModel.getData().getUserId());
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                baseModel baseBean = new Gson().fromJson(result, baseModel.class);
                Toast.makeText(context, baseBean.getMsg(), Toast.LENGTH_SHORT).show();
                //??????????????????????????????username???????????????
                String  reason = apply_Edit.getText().toString();
                try {
                    DemoHelper.getInstance().getContactManager().addContact(toChatUsername,reason);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                finish();
            }
            @Override
            public void failed(String... args) {
            }
        });
    }
    private String source(String addType) {
        String name = "";
        switch (addType) {
            case "1":
                name = "????????????";
                break;
            case "2":
                name = "????????????";
                break;
            case "3":
                name = "???????????????";
                break;
            case "4":
                name = "????????????";
                break;
            case "5":
                name = "????????????";
                break;
            case "6":
                name = "???????????????";
                break;
            case "7":
                name = "???????????????";
                break;
            case "8":
                name = "??????????????????";
                break;
            case "9":
                name = "???????????????????????????";
                break;
            default:
                name = addType;
                break;
        }
        return name;
    }


}

