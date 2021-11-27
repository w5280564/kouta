package com.xunda.mo.main.group.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.hyphenate.easeui.modules.contact.EaseContactLayout;
import com.hyphenate.easeui.modules.contact.EaseContactListLayout;
import com.hyphenate.easeui.modules.contact.model.EaseContactSetStyle;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseSearchTextView;
import com.hyphenate.easeui.widget.EaseSidebar;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.domain.MyEaseUser;
import com.xunda.mo.main.group.adapter.MyGroupHead_ListAdapter;
import com.xunda.mo.main.group.adapter.MyGroupMembersList_Adapter;
import com.xunda.mo.model.GroupMember_Bean;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.pinyin.PinyinUtils;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.SortMembersList;
import com.xunda.mo.staticdata.xUtils3Http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupAllMembers extends BaseInitActivity {
    List<GroupMember_Bean.DataDTO> groupMember;
    private MyGroupMembersList_Adapter myGroupList_adapter;// 列表适配器
    private MyGroupHead_ListAdapter myContact_Head_listAdapter;//头部适配器
    private EaseContactListLayout contactList;
    private String groupId;
    private int Identity;
    private GruopInfo_Bean groupModel;
    private TextView floating_header;

    public static void actionStart(Context context, List<GroupMember_Bean.DataDTO> groupMember, GruopInfo_Bean groupModel) {
        Intent intent = new Intent(context, GroupAllMembers.class);
        intent.putExtra("groupMember", (Serializable) groupMember);
        intent.putExtra("groupModel", groupModel);
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
        groupMember = (List<GroupMember_Bean.DataDTO>) intent.getSerializableExtra("groupMember");
        groupModel = (GruopInfo_Bean) intent.getSerializableExtra("groupModel");
        if (groupModel != null) {
            groupId = groupModel.getData().getGroupId();
            Identity = groupModel.getData().getIdentity();
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        Button return_Btn = findViewById(R.id.return_Btn);
        return_Btn.setOnClickListener(new return_BtnClick());
        TextView group_add_Txt = findViewById(R.id.group_add_Txt);
        group_add_Txt.setOnClickListener(new group_add_TxtClick());
        TextView group_remove_Txt = findViewById(R.id.group_remove_Txt);
        group_remove_Txt.setOnClickListener(new group_remove_TxtClick());

        EaseSearchTextView query_Edit = findViewById(R.id.query_Edit);
        query_Edit.setOnClickListener(new query_EditClick());
        contact_layout = findViewById(R.id.contact_layout);
        contactList = contact_layout.getContactList();
        EaseSidebar sideBarContact =  contact_layout.findViewById(R.id.side_bar_contact);
         floating_header =  contact_layout.findViewById(R.id.floating_header);
        sideBarContact.setOnTouchEventListener(new sideBarContactTouchEvent());
        addAdapter();

        if (Identity == 3) {
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
        GroupMemberListMethod(GroupAllMembers.this, saveFile.Group_UserList_Url);
    }

    private void addAdapter() {
        EaseContactSetStyle contactSetModel = new EaseContactSetStyle();
        contactSetModel.setShowItemHeader(true);
        ConcatAdapter concatAdapter = new ConcatAdapter();
        myGroupList_adapter = new MyGroupMembersList_Adapter();
        myGroupList_adapter.setSettingModel(contactSetModel);
        myContact_Head_listAdapter = new MyGroupHead_ListAdapter();
        myContact_Head_listAdapter.setSettingModel(contactSetModel);
        concatAdapter.addAdapter(myContact_Head_listAdapter);
        concatAdapter.addAdapter(myGroupList_adapter);
        contactList.setAdapter(concatAdapter);
    }

    private void getGroupList() {
        List<MyEaseUser> data = new ArrayList<>();
        List<MyEaseUser> dataHeadList = new ArrayList<>();
        for (int i = 0; i < groupListModel.getData().size(); i++) {
            GroupMember_Bean.DataDTO dataDTO = groupListModel.getData().get(i);
            MyEaseUser user = new MyEaseUser();
            user.setUsername(dataDTO.getHxUserName());
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
        SortMembersList.getLastDescList(data);
        myContact_Head_listAdapter.setData(dataHeadList);
        myGroupList_adapter.setData(data);

        myContact_Head_listAdapter.setOnItemClickListener((view, position) -> {
            if (!isProtect()) {
                String userID = dataHeadList.get(position).getUserId();
                String hxUserName = dataHeadList.get(position).getUsername();
                GroupFriend_Detail.actionStart(mContext, userID, hxUserName, groupModel);
            }
        });
        myGroupList_adapter.setOnItemClickListener((view, position) -> {
            if (!isProtect()) {
                String userID = data.get(position).getUserId();
                String hxUserName = data.get(position).getUsername();
                GroupFriend_Detail.actionStart(mContext, userID, hxUserName, groupModel);
            }
        });
    }

    private boolean isProtect() {
        int isAnonymous = groupModel.getData().getIsAnonymous();
        int issProtect = groupModel.getData().getIsProtect();
        if (isAnonymous == 1 || issProtect == 1) {
            Toast.makeText(mContext, "成员保护中", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
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
            GroupAllMembers_Add.actionStart(GroupAllMembers.this, groupId);
        }
    }

    private class group_remove_TxtClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            GroupAllMembers_Remove.actionStart(GroupAllMembers.this, groupId);
        }
    }


    private class query_EditClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            GroupAllMembers_Search.actionStart(GroupAllMembers.this, groupMember);

        }
    }

    GroupMember_Bean groupListModel;

    public void GroupMemberListMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
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


    private class sideBarContactTouchEvent implements EaseSidebar.OnTouchEventListener {
        @Override
        public void onActionDown(MotionEvent event, String pointer) {
            showFloatingHeader(pointer);
            moveToRecyclerItem(pointer);
        }

        @Override
        public void onActionMove(MotionEvent event, String pointer) {
            showFloatingHeader(pointer);
            moveToRecyclerItem(pointer);
        }

        @Override
        public void onActionUp(MotionEvent event) {
            hideFloatingHeader();
        }
    }

    private void moveToRecyclerItem(String pointer) {
        List<MyEaseUser> data = myGroupList_adapter.getData();
        if(data == null || data.isEmpty()) {
            return;
        }
        for(int i = 0; i < data.size(); i++) {
            if(TextUtils.equals(EaseCommonUtils.getLetter(data.get(i).getNickname()), pointer)) {
                LinearLayoutManager manager = (LinearLayoutManager) contactList.getLayoutManager();
                if(manager != null) {
                    manager.scrollToPositionWithOffset(i, 0);
                }
            }
        }
    }

    /**
     * 展示滑动的字符
     * @param pointer
     */
    private void showFloatingHeader(String pointer) {
        if(TextUtils.isEmpty(pointer)) {
            hideFloatingHeader();
            return;
        }
        floating_header.setText(pointer);
        floating_header.setVisibility(View.VISIBLE);
    }
    private void hideFloatingHeader() {
        floating_header.setVisibility(View.GONE);
    }


}