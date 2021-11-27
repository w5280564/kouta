package com.xunda.mo.main.group.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.ConcatAdapter;

import com.google.gson.Gson;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.modules.contact.EaseContactLayout;
import com.hyphenate.easeui.modules.contact.EaseContactListLayout;
import com.hyphenate.easeui.modules.contact.model.EaseContactSetStyle;
import com.hyphenate.easeui.widget.EaseSearchTextView;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.dialog.DemoDialogFragment;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.hx.section.domain.MyEaseUser;
import com.xunda.mo.main.MainActivity;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.group.adapter.MyGroupHead_ListAdapter;
import com.xunda.mo.main.group.adapter.MyGroupMembersList_Adapter;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.GroupMember_Bean;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.pinyin.PinyinUtils;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupAllMembers_Manage_ChangeGroupLeader extends BaseInitActivity {
    private MyGroupMembersList_Adapter myGroupList_adapter;// 列表适配器
    private MyGroupHead_ListAdapter myContact_Head_listAdapter;//头部适配器
    private ConcatAdapter concatAdapter;
    private EaseContactListLayout contactList;
    private Button return_Btn;
    private TextView group_add_Txt, group_remove_Txt;
    private EaseSearchTextView query_Edit;
    private String groupId;
    private int Identity;
    private GruopInfo_Bean groupModel;

    public static void actionStart(Context context, GruopInfo_Bean groupModel, String groupId, int Identity) {
        Intent intent = new Intent(context, GroupAllMembers_Manage_ChangeGroupLeader.class);
        intent.putExtra("groupModel", groupModel);
        intent.putExtra("groupId", groupId);
        intent.putExtra("Identity", Identity);
        context.startActivity(intent);
    }


    private EaseContactLayout contact_layout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_allmembers;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupModel = (GruopInfo_Bean) intent.getSerializableExtra("groupModel");
        groupId = intent.getStringExtra("groupId");
        Identity = intent.getIntExtra("Identity", 3);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        return_Btn = findViewById(R.id.return_Btn);
        return_Btn.setOnClickListener(new return_BtnClick());
        group_add_Txt = findViewById(R.id.group_add_Txt);
        group_add_Txt.setOnClickListener(new group_add_TxtClick());
        group_remove_Txt = findViewById(R.id.group_remove_Txt);
        group_remove_Txt.setOnClickListener(new group_remove_TxtClick());

        query_Edit = findViewById(R.id.query_Edit);
        query_Edit.setOnClickListener(new query_EditClick());
        contact_layout = findViewById(R.id.contact_layout);
        contactList = contact_layout.getContactList();

        addAdapter();

        if (Identity == 1) {
            group_add_Txt.setVisibility(View.GONE);
            group_remove_Txt.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        GroupMemberListMethod(GroupAllMembers_Manage_ChangeGroupLeader.this, saveFile.Group_UserList_Url );
    }

    private void addAdapter() {
        EaseContactSetStyle contactSetModel = new EaseContactSetStyle();
        contactSetModel.setShowItemHeader(true);
        concatAdapter = new ConcatAdapter();
        myGroupList_adapter = new MyGroupMembersList_Adapter();
        myGroupList_adapter.setSettingModel(contactSetModel);
        myContact_Head_listAdapter = new MyGroupHead_ListAdapter();
        myContact_Head_listAdapter.setSettingModel(contactSetModel);
        concatAdapter.addAdapter(myContact_Head_listAdapter);
        concatAdapter.addAdapter(myGroupList_adapter);
        contactList.setAdapter(concatAdapter);

//        myContactList_adapter.setOnItemClickListener(new demoContactList_adapterClick());
//        myContact_Head_listAdapter.setOnItemClickListener(new myContact_listAdapterClick());
    }

    private void getGroupList() {
        List<MyEaseUser> data = new ArrayList<>();
        List<MyEaseUser> dataHeadList = new ArrayList<>();
        for (int i = 0; i < groupListModel.getData().size(); i++) {
            GroupMember_Bean.DataDTO dataDTO = groupListModel.getData().get(i);
            MyEaseUser user = new MyEaseUser();
            user.setUsername("");
            user.setNickname(dataDTO.getNickname());
            String pinyin = PinyinUtils.getPingYin(dataDTO.getNickname());
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
            int Identity = groupListModel.getData().get(i).getIdentity();
            if (Identity == 1 || Identity == 2) {
                dataHeadList.add(user);
                continue;
            } else {
                data.add(user);
            }
        }
        myContact_Head_listAdapter.setData(dataHeadList);
        myGroupList_adapter.setData(data);

        myContact_Head_listAdapter.setOnItemClickListener((view, position) -> {
            MyInfo myInfo = new MyInfo(GroupAllMembers_Manage_ChangeGroupLeader.this);
            if (TextUtils.equals(myInfo.getUserInfo().getUserId(),dataHeadList.get(position).getUserId())){
                return;
            }
            String userName = dataHeadList.get(position).getNickname();
            String contentStr = String.format("您确定要讲群主移交给%s吗？",userName);
            String userID = dataHeadList.get(position).getUserId();
            isChangeGroupLeader(contentStr,userID,userName);
        });

        myGroupList_adapter.setOnItemClickListener((view, position) -> {
            String userName = data.get(position).getNickname();
            String contentStr = String.format("您确定要讲群主移交给%s吗？",userName);
            String userID = data.get(position).getUserId();
            isChangeGroupLeader(contentStr,userID,userName);
        });
    }

    private class return_BtnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            finish();
        }
    }


    private class group_add_TxtClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            GroupAllMembers_Add.actionStart(GroupAllMembers_Manage_ChangeGroupLeader.this, groupId);
        }
    }

    private class group_remove_TxtClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            GroupAllMembers_Remove.actionStart(GroupAllMembers_Manage_ChangeGroupLeader.this, groupId);
        }
    }


    private class query_EditClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            GroupAllMembers_Search.actionStart(GroupAllMembers_Manage_ChangeGroupLeader.this, groupListModel.getData());

        }
    }

    GroupMember_Bean groupListModel;

    public void GroupMemberListMethod(Context context, String baseUrl) {
        Map<String,Object> map = new HashMap<>();
        map.put("groupId",groupId);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                groupListModel = new Gson().fromJson(result, GroupMember_Bean.class);
                getGroupList();
            }
            @Override
            public void failed(String... args) {

            }
        });
    }

    private void isChangeGroupLeader(String content,String userID,String userName) {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle("提示")
                .showContent(true)
                .setContent(content)
                .setOnConfirmClickListener(new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        ChangeGroupLeaderMethod(GroupAllMembers_Manage_ChangeGroupLeader.this, saveFile.Group_TransferMaster_Url, userID,userName);
                    }
                })
                .showCancelButton(true)
                .show();
    }

    /**
     * 转让群主
     * @param context
     * @param baseUrl
     */
    public void ChangeGroupLeaderMethod(Context context, String baseUrl, String userId,String userName) {
        Map<String,Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("userId", userId);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                sendMes(groupModel,userName);

                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            @Override
            public void failed(String... args) {
            }
        });
    }

    //发送消息
    private void sendMes(GruopInfo_Bean Model,String userName) {
        MyInfo myInfo = new MyInfo(mContext);
        String conversationId = Model.getData().getGroupHxId();
//        EMMessage message = EMMessage.createTxtSendMessage(mContext.getString(R.string.CREATE_GROUP_CONTENT), conversationId);
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        EMTextMessageBody txtBody = new EMTextMessageBody("");
        message.addBody(txtBody);
        message.setTo(conversationId);
        message.setChatType(EMMessage.ChatType.GroupChat);
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_UPDATE_MASTER);
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