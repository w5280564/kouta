package com.xunda.mo.main.group.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.group.adapter.GroupAllMembers_Manage_SetupManage_Adapter;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.GroupMember_Bean;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupAllMembers_Manage_SetupManage extends BaseInitActivity {
    private String groupId;
    private RecyclerView addMembers_Recycler;
    Button right_Btn;
    String UserName;
    private TextView manage_Txt;

    public static void actionStart(Context context, String groupId) {
        Intent intent = new Intent(context, GroupAllMembers_Manage_SetupManage.class);
        intent.putExtra("groupId", groupId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.groupallmembers_manage_setupmanage;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        manage_Txt = findViewById(R.id.manage_Txt);
        addMembers_Recycler = findViewById(R.id.addMembers_Recycler);
        initTitle();
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(GroupAllMembers_Manage_SetupManage.this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("设置管理员");
        right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setText("添加");
        viewTouchDelegate.expandViewTouchDelegate(right_Btn, 50, 50, 50, 50);

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
            GroupAllMembers_Manage_SetManage_Add.actionStart(GroupAllMembers_Manage_SetupManage.this, groupId);
        }
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupId = intent.getStringExtra("groupId");
    }

    @Override
    protected void initData() {
        super.initData();
        GroupMethod(GroupAllMembers_Manage_SetupManage.this,  saveFile.Group_MyGroupInfo_Url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GroupMemberListMethod(GroupAllMembers_Manage_SetupManage.this, saveFile.Group_UserList_Url );
    }

    GroupAllMembers_Manage_SetupManage_Adapter mAdapter;
    List<GroupMember_Bean.DataDTO> manageList = new ArrayList<>();
    public void initlist(final Context context) {
        if (manageList != null) {
            manageList.clear();
        }
        for (int i = 0; i < groupListModel.getData().size(); i++) {
            int Identity = groupListModel.getData().get(i).getIdentity();
            if (Identity == 2) {
                manageList.add(groupListModel.getData().get(i));
            }
        }

        LinearLayoutManager mMangaer = new LinearLayoutManager(context);
        addMembers_Recycler.setLayoutManager(mMangaer);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        addMembers_Recycler.setHasFixedSize(true);
        mAdapter = new GroupAllMembers_Manage_SetupManage_Adapter(context, manageList);
        addMembers_Recycler.setAdapter(mAdapter);

        mAdapter.setOnItemAddRemoveClickLister((view, position) -> {
            UserName = manageList.get(position).getNickname();
            String userId = manageList.get(position).getUserId();
            AddManageMethod(GroupAllMembers_Manage_SetupManage.this,  saveFile.group_MangerSet_Url, userId);
            mAdapter.removeData(position);
            setManageTxt(manageList.size(), groupModel.getData().getMaxManagerCount());
        });
    }

    private void setManageTxt(int Count, int MaxManagerCount) {
        int listSize = Count;
        int maxManageCount = MaxManagerCount;
        String manageStr = String.format("管理员%1$s/%2$s人", listSize, maxManageCount);
        manage_Txt.setText(manageStr);
    }


    GroupMember_Bean groupListModel;
    public void GroupMemberListMethod(Context context, String baseUrl) {
        Map<String,Object> map = new HashMap<>();
        map.put("groupId",groupId);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                groupListModel = new Gson().fromJson(result, GroupMember_Bean.class);
                initlist(GroupAllMembers_Manage_SetupManage.this);
            }
            @Override
            public void failed(String... args) {

            }
        });

    }


    GruopInfo_Bean groupModel;

    public void GroupMethod(Context context, String baseUrl) {
        Map<String,Object> map = new HashMap<>();
        map.put("groupId",groupId);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                groupModel = new Gson().fromJson(result, GruopInfo_Bean.class);
                setManageTxt(manageList.size(), groupModel.getData().getMaxManagerCount());
            }
            @Override
            public void failed(String... args) {

            }
        });
    }


    @SuppressLint("NewApi")
    public void AddManageMethod(Context context, String baseUrl, String userId) {
        Map<String,Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("userId", userId);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                sendMes(groupModel);
            }
            @Override
            public void failed(String... args) {
            }
        });
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
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_DELETADMIN);
        message.setAttribute(MyConstant.USER_NAME, UserName);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        message.setAttribute(MyConstant.GROUP_NAME, Model.getData().getGroupName());
        message.setAttribute(MyConstant.GROUP_HEAD, Model.getData().getGroupHeadImg());
        DemoHelper.getInstance().getChatManager().sendMessage(message);
    }


}