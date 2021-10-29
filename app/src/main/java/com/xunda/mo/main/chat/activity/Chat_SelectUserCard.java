package com.xunda.mo.main.chat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.model.EaseEvent;
import com.xunda.mo.R;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.domain.MyEaseUser;
import com.xunda.mo.main.chat.adapter.Chat_SelectUserCard_Adapter;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.ChatUserBean;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.model.adress_Model;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.pinyin.PinyinUtils;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat_SelectUserCard extends BaseInitActivity {
    private String conversationId;
    private RecyclerView addMembers_Recycler;
    private EditText query_Edit;
    private View search_clear;
    private String keyword = "";
    private String[] newmembers;
    Button right_Btn;
    private ChatUserBean model;
    private GruopInfo_Bean groupModel;
    private String sendType;

    public static void actionStart(Context context, String conversationId) {
        Intent intent = new Intent(context, Chat_SelectUserCard.class);
        intent.putExtra("conversationId", conversationId);
        context.startActivity(intent);
    }

    /**
     *
     * @param context
     * @param conversationId 会话ID
     * @param sendType 1 把好友发给会话 2把好友发入群 3把Ta推给朋友
     */
    public static void actionStartSingle(Context context, String conversationId, String sendType) {
        Intent intent = new Intent(context, Chat_SelectUserCard.class);
        intent.putExtra("conversationId", conversationId);
        intent.putExtra("sendType", sendType);
//        intent.putExtra("model", model);
        context.startActivity(intent);
    }

    public static void actionStartGroup(Context context, String conversationId, String sendType,GruopInfo_Bean groupModel) {
        Intent intent = new Intent(context, Chat_SelectUserCard.class);
        intent.putExtra("conversationId", conversationId);
        intent.putExtra("sendType", sendType);
        intent.putExtra("groupModel", groupModel);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.group_allmembers_add;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        query_Edit = findViewById(R.id.query_Edit);
        query_Edit.addTextChangedListener(new query_EditChangeListener());
        search_clear = findViewById(R.id.search_clear);
        search_clear.setOnClickListener(new search_clearClick());
        addMembers_Recycler = findViewById(R.id.addMembers_Recycler);
        initTitle();
        initMemberList(Chat_SelectUserCard.this);
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(Chat_SelectUserCard.this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("选择联系人");
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


    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        conversationId = intent.getStringExtra("conversationId");
        sendType = intent.getStringExtra("sendType");
//        model = (ChatUserBean) intent.getSerializableExtra("model");
        groupModel = (GruopInfo_Bean) intent.getSerializableExtra("groupModel");
    }

    @Override
    protected void initData() {
        super.initData();
        friendMemberListMethod(Chat_SelectUserCard.this, saveFile.User_Friendlist_Url);
    }

    Chat_SelectUserCard_Adapter mAdapter;

    public void initMemberList(final Context context) {
        LinearLayoutManager mMangaer = new LinearLayoutManager(context);
        addMembers_Recycler.setLayoutManager(mMangaer);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        addMembers_Recycler.setHasFixedSize(true);
        mAdapter = new Chat_SelectUserCard_Adapter(TextUtils.isEmpty(conversationId));
        addMembers_Recycler.setAdapter(mAdapter);
        mAdapter.setItemOnClickListener((v, position) -> {
            if (TextUtils.equals(sendType, "1")) {
                sendSingleCard(dataList.get(position),sendType);
            } else if (TextUtils.equals(sendType, "2")) {
                sendGroupCard(dataList.get(position));
            }else if (TextUtils.equals(sendType, "3")) {
                sendToFriendCard(dataList.get(position),sendType);
            }
        });

    }

    adress_Model friendListModel;
    public void friendMemberListMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                friendListModel = new Gson().fromJson(result, adress_Model.class);
                sortList();
            }
            @Override
            public void failed(String... args) {
            }
        });
    }

    List<MyEaseUser> dataList;
    private void sortList() {
        dataList = new ArrayList<>();
        for (int i = 0; i < friendListModel.getData().size(); i++) {
            adress_Model.DataDTO dataDTO = friendListModel.getData().get(i);
            MyEaseUser user = new MyEaseUser();
            user.setUsername(dataDTO.getHxUserName());
            String userNickName = TextUtils.isEmpty(dataDTO.getNickname()) ? dataDTO.getRemarkName() : dataDTO.getNickname();
            user.setNickname(userNickName);
            String pinyin = PinyinUtils.getPingYin(userNickName);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                user.setInitialLetter(sortString);
            } else {
                user.setInitialLetter("#");
            }
            user.setAvatar(dataDTO.getHeadImg());
            user.setContact(0);//朋友属性 4是没有预设置
            user.setGender(0);
            user.setVipType(dataDTO.getVipType());
            user.setUserId(dataDTO.getUserId());
            user.setUserNum(dataDTO.getUserNum());
            user.setLightStatus(dataDTO.getLightStatus());
            dataList.add(user);
        }
        if (newmembers != null) {
            mAdapter.setExistMember(Arrays.asList(newmembers));
        } else {
            mAdapter.setData(dataList);
        }
    }


    private class query_EditChangeListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            keyword = s.toString();
            if (!TextUtils.isEmpty(keyword)) {
                search_clear.setVisibility(View.VISIBLE);
                SearchGroupMember(keyword);
            } else {
                search_clear.setVisibility(View.INVISIBLE);
                sortList();
            }
        }
    }

    private class search_clearClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            query_Edit.getText().clear();
            sortList();
        }
    }


    private void SearchGroupMember(String keyword) {
        List<MyEaseUser> searchData = new ArrayList<>();
        for (int i = 0; i < friendListModel.getData().size(); i++) {
            adress_Model.DataDTO dataDTO = friendListModel.getData().get(i);
            String userNickName = TextUtils.isEmpty(dataDTO.getNickname()) ? dataDTO.getRemarkName() : dataDTO.getNickname();
            String userNum = dataDTO.getUserNum().toString();
            if (userNickName.contains(keyword) || userNum.contains(keyword)) {
                MyEaseUser user = new MyEaseUser();
                user.setUsername(dataDTO.getHxUserName());
                user.setNickname(userNickName);
                String pinyin = PinyinUtils.getPingYin(userNickName);
                String sortString = pinyin.substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Z]")) {
                    user.setInitialLetter(sortString);
                } else {
                    user.setInitialLetter("#");
                }
                user.setAvatar(dataDTO.getHeadImg());
                user.setContact(0);//朋友属性 4是没有预设置
                user.setGender(0);
                user.setVipType(dataDTO.getVipType());
                user.setUserId(dataDTO.getUserId());
                user.setUserNum(dataDTO.getUserNum());
                user.setLightStatus(dataDTO.getLightStatus());
                searchData.add(user);
            }
        }
        if (newmembers != null) {
            mAdapter.setExistMember(Arrays.asList(newmembers));
        } else {
            mAdapter.setData(searchData);
        }
    }



    @SuppressLint("NewApi")
    private MyEaseUser getMyEaseUser(String userID) {
        MyEaseUser myEaseUser = new MyEaseUser();
//        List<EaseUser> selectMembers = mAdapter.getUserList();
        List<String> userListStr = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            if (TextUtils.equals(dataList.get(i).getUsername(),userID)){
                myEaseUser = dataList.get(i);
            }
        }
        String userName = String.join(",", userListStr);
        return myEaseUser;
    }


    //发送单人名片
    private void sendSingleCard(MyEaseUser data,String sendType) {
        MyInfo myInfo = new MyInfo(mContext);
        String msgID = conversationId;
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
        message.setTo(msgID);
        message.setChatType(EMMessage.ChatType.Chat);
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_USERCARD);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
//        String toName = TextUtils.isEmpty(data.getRemarkName()) ? Model.getData().getNickname() : Model.getData().getRemarkName();
        message.setAttribute(MyConstant.TO_NAME, data.getNickname());
        message.setAttribute(MyConstant.TO_HEAD, data.getAvatar());
        message.setAttribute(MyConstant.TO_LH, data.getLightStatus() + "");
        message.setAttribute(MyConstant.TO_VIP, data.getVipType());
        EMCustomMessageBody body = new EMCustomMessageBody(DemoConstant.USER_CARD_EVENT);
        Map<String, String> params = new HashMap<>();
        params.put(MyConstant.UID, msgID);
        params.put(MyConstant.USER_ID, data.getUserId());
        params.put(MyConstant.UNUM, data.getUserNum() + "");
        params.put(MyConstant.AVATAR, data.getAvatar());
        params.put(MyConstant.NICK_NAME, data.getNickname());
        params.put(MyConstant.HX_NAME, data.getUsername());
        body.setParams(params);
        message.setBody(body);
        messageBack(message);
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    //把Ta推荐给好友
    private void sendToFriendCard(MyEaseUser data,String sendType) {
        MyEaseUser toEaseUser =  getMyEaseUser(conversationId);//要发送的名片
        MyInfo myInfo = new MyInfo(mContext);
        String msgID =  data.getUsername();
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
        message.setTo(msgID);
        message.setChatType(EMMessage.ChatType.Chat);
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_USERCARD);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
//        String toName = TextUtils.isEmpty(data.getRemarkName()) ? Model.getData().getNickname() : Model.getData().getRemarkName();
        message.setAttribute(MyConstant.TO_NAME, data.getNickname());
        message.setAttribute(MyConstant.TO_HEAD, data.getAvatar());
        message.setAttribute(MyConstant.TO_LH, data.getLightStatus() + "");
        message.setAttribute(MyConstant.TO_VIP, data.getVipType());
        EMCustomMessageBody body = new EMCustomMessageBody(DemoConstant.USER_CARD_EVENT);
        Map<String, String> params = new HashMap<>();
        params.put(MyConstant.UID, msgID);
        params.put(MyConstant.USER_ID, toEaseUser.getUserId());
        params.put(MyConstant.UNUM, toEaseUser.getUserNum() + "");
        params.put(MyConstant.AVATAR, toEaseUser.getAvatar());
        params.put(MyConstant.NICK_NAME, toEaseUser.getNickname());
        params.put(MyConstant.HX_NAME, toEaseUser.getUsername());
        body.setParams(params);
        message.setBody(body);
        messageBack(message);
        EMClient.getInstance().chatManager().sendMessage(message);
    }



    //把名片发送到群内
    private void sendGroupCard(MyEaseUser data) {
        MyInfo myInfo = new MyInfo(mContext);
//        String conversationId = data.getUsername();
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
        message.setTo(conversationId);
        message.setChatType(EMMessage.ChatType.Chat);
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_USERCARD);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        message.setAttribute(MyConstant.GROUP_NAME, groupModel.getData().getGroupName());
        message.setAttribute(MyConstant.GROUP_HEAD, groupModel.getData().getGroupHeadImg());
        EMCustomMessageBody body = new EMCustomMessageBody(DemoConstant.USER_CARD_EVENT);
        Map<String, String> params = new HashMap<>();
        params.put(MyConstant.UID, conversationId);
        params.put(MyConstant.USER_ID, data.getUserId());
        params.put(MyConstant.UNUM, data.getUserNum() + "");
        params.put(MyConstant.AVATAR, data.getAvatar());
        params.put(MyConstant.NICK_NAME, data.getNickname());
        params.put(MyConstant.HX_NAME, data.getUsername());
        body.setParams(params);
        message.setBody(body);
        messageBack(message);
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    private void messageBack(EMMessage msg){
        msg.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                showToast("发送用户名片成功");
                Toast.makeText(mContext,"发送用户名片成功",Toast.LENGTH_SHORT).show();
                LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
                finish();
            }

            @Override
            public void onError(int code, String error) {
                showToast("发送用户名片失败");
                LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

}