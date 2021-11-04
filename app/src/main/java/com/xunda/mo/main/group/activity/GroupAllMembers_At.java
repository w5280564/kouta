package com.xunda.mo.main.group.activity;

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

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;

import com.google.gson.Gson;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.modules.contact.EaseContactLayout;
import com.hyphenate.easeui.modules.contact.EaseContactListLayout;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.domain.MyEaseUser;
import com.xunda.mo.main.group.adapter.GroupAt_Adapter;
import com.xunda.mo.main.group.adapter.GroupAt_Head_Adapter;
import com.xunda.mo.main.group.adapter.GroupAt_List_Adapter;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.GroupMember_Bean;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.pinyin.PinyinUtils;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupAllMembers_At extends BaseInitActivity {
    private String groupId;
    private EditText query_Edit;
    private View search_clear;
    private String keyword = "";
    private String[] newmembers;
    Button right_Btn;
    private int Identity;
    private ConcatAdapter concatAdapter;
    private EaseContactLayout contact_layout;
    private EaseContactListLayout contactList;
    private GroupAt_Head_Adapter myContact_Head_listAdapter;
    private GroupAt_List_Adapter List_adapter;
    private GroupAt_Adapter group_At_Adapter;

    public static void actionStart(Context context, String groupId) {
        Intent intent = new Intent(context, GroupAllMembers_At.class);
        intent.putExtra("groupId", groupId);
        context.startActivity(intent);
    }

    public static void actionStartForResult(Fragment fragment, GruopInfo_Bean groupModel, int requestCode) {
        Intent starter = new Intent(fragment.getContext(), GroupAllMembers_At.class);
        starter.putExtra("groupModel", groupModel);
        fragment.startActivityForResult(starter, requestCode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.group_at;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        query_Edit = findViewById(R.id.query_Edit);
        query_Edit.addTextChangedListener(new query_EditChangeListener());
        search_clear = findViewById(R.id.search_clear);
        search_clear.setOnClickListener(new search_clearClick());
        contact_layout = findViewById(R.id.contact_layout);
        contactList = contact_layout.getContactList();
        initTitle();
        initMemberList(GroupAllMembers_At.this);
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(GroupAllMembers_At.this, R.color.white));
        title_Include.setElevation(2f);//阴影
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("选择提醒的人");
        right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setText("完成");
        viewTouchDelegate.expandViewTouchDelegate(right_Btn, 50, 50, 50, 50);

        return_Btn.setOnClickListener(new return_Btn());
        right_Btn.setOnClickListener(new right_Btn());
    }

    private class return_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
//            Intent intent = getIntent();
//            intent.putExtra("username", getUserName());
//            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private class right_Btn extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            Intent intent = getIntent();
            intent.putExtra("username", getUserName());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
//        groupId = intent.getStringExtra("groupId");
        groupModel = (GruopInfo_Bean) intent.getSerializableExtra("groupModel");
        if (groupModel != null) {
            groupId = groupModel.getData().getGroupId();
            Identity = groupModel.getData().getIdentity();
        }

    }

    @Override
    protected void initData() {
        super.initData();
        GroupMemberListMethod(GroupAllMembers_At.this, saveFile.Group_UserList_Url);
        GroupMethod(GroupAllMembers_At.this, saveFile.Group_MyGroupInfo_Url);

    }


    public void initMemberList(Context context) {
        concatAdapter = new ConcatAdapter();
        group_At_Adapter = new GroupAt_Adapter(TextUtils.isEmpty(groupId));
        myContact_Head_listAdapter = new GroupAt_Head_Adapter(TextUtils.isEmpty(groupId));
        List_adapter = new GroupAt_List_Adapter(TextUtils.isEmpty(groupId));
        concatAdapter.addAdapter(group_At_Adapter);
        concatAdapter.addAdapter(myContact_Head_listAdapter);
        concatAdapter.addAdapter(List_adapter);
        contactList.setAdapter(concatAdapter);


        group_At_Adapter.setOnSelectListener((pos, selectedMembers) -> {
            Intent intent = getIntent();
            intent.putExtra("username", getAtAll());
            setResult(RESULT_OK, intent);
            finish();
        });

        List<String> selectHeadList = new ArrayList<>();
        List<String> selectList = new ArrayList<>();
        myContact_Head_listAdapter.setOnSelectListener((pos, selectedMembers) -> {
            MyInfo myInfo = new MyInfo(this);
            if (TextUtils.equals(myInfo.getUserInfo().getUserId(), dataHeadList.get(pos).getUserId())) {
                return;
            }
            selectHeadList.clear();
            selectHeadList.addAll(selectedMembers);
            right_Btn.setText(getString(R.string.finish) + "(" + (selectHeadList.size() + selectList.size()) + ")");
        });

        List_adapter.setOnSelectListener((pos, selectedMembers) -> {
            MyInfo myInfo = new MyInfo(this);
            if (TextUtils.equals(myInfo.getUserInfo().getUserId(), data.get(pos).getUserId())) {
                return;
            }
            selectList.clear();
            selectList.addAll(selectedMembers);
            right_Btn.setText(getString(R.string.finish) + "(" + (selectHeadList.size() + selectList.size()) + ")");
        });

    }

    GroupMember_Bean groupListModel;

    public void GroupMemberListMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                groupListModel = new Gson().fromJson(result, GroupMember_Bean.class);
                sortList();
            }

            @Override
            public void failed(String... args) {

            }
        });
    }

    List<MyEaseUser> data = new ArrayList<>();
    List<MyEaseUser> dataHeadList = new ArrayList<>();

    private void sortList() {
        AddAll();
        MyInfo myInfo = new MyInfo(this);
        myContact_Head_listAdapter.clearData();
        List_adapter.clearData();
        for (int i = 0; i < groupListModel.getData().size(); i++) {
            GroupMember_Bean.DataDTO dataDTO = groupListModel.getData().get(i);
            if (TextUtils.equals(myInfo.getUserInfo().getUserId(), dataDTO.getUserId())) {
                continue;
            }
            MyEaseUser user = new MyEaseUser();
            user.setUsername(dataDTO.getHxUserName());
            String userNickName = dataDTO.getNickname();
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
            int Identity = groupListModel.getData().get(i).getIdentity();
            if (Identity == 1 || Identity == 2) {
                dataHeadList.add(user);
                continue;
            } else {
                data.add(user);
            }
        }
        if (!dataHeadList.isEmpty()) {
            myContact_Head_listAdapter.setData(dataHeadList);
        }
        if (!data.isEmpty()) {
            sortAtList(data);
            List_adapter.setData(data);
        }
    }

    private void AddAll() {
            MyEaseUser user = new MyEaseUser(getString(R.string.all_members));
            user.setNickname(getString(R.string.all_members));
            user.setAvatar(R.drawable.ease_groups_icon+"");
            List<MyEaseUser> users = new ArrayList<>();
            users.add(user);
            group_At_Adapter.setData(users);
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
        MyInfo myInfo = new MyInfo(this);
        myContact_Head_listAdapter.clearData();
        List_adapter.clearData();
        for (int i = 0; i < groupListModel.getData().size(); i++) {
            GroupMember_Bean.DataDTO dataDTO = groupListModel.getData().get(i);
            if (TextUtils.equals(myInfo.getUserInfo().getUserId(), dataDTO.getUserId())) {
                continue;
            }
            String userNickName = dataDTO.getNickname();
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
                int Identity = groupListModel.getData().get(i).getIdentity();
                if (Identity == 1 || Identity == 2) {
                    dataHeadList.add(user);
                    continue;
                } else {
                    data.add(user);
                }
            }
        }
        if (!dataHeadList.isEmpty()) {
            myContact_Head_listAdapter.setData(dataHeadList);
        }
        if (!data.isEmpty()) {
            sortAtList(data);
            List_adapter.setData(data);
        }
    }

    GruopInfo_Bean groupModel;
    public void GroupMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                groupModel = new Gson().fromJson(result, GruopInfo_Bean.class);
            }

            @Override
            public void failed(String... args) {

            }
        });

    }


    @SuppressLint("NewApi")
    private HashMap<String, Object> getUserName() {
        List<MyEaseUser> selectMembers = List_adapter.getUserList();
        List<MyEaseUser> selectHeadMembers = myContact_Head_listAdapter.getUserList();
        HashMap<String,Object> userMap = new HashMap<>();
        allMap(selectMembers,userMap);
        allMap(selectHeadMembers,userMap);
        return userMap;
    }

    private void allMap(List<MyEaseUser> selectMembers,HashMap userMap){
        for (int i = 0; i < selectMembers.size(); i++) {
            String nickName = selectMembers.get(i).getNickname();
            String userID = selectMembers.get(i).getUsername();
            userMap.put(nickName,userID);
        }
    }


    private HashMap<String, Object> getAtAll() {
        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("All","123456789");
        return userMap;
    }


    /**
     * 排序
     *
     * @param list
     */
    private void sortAtList(List<MyEaseUser> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Collections.sort(list, (Comparator<EaseUser>) (lhs, rhs) -> {
            if (lhs.getInitialLetter().equals(rhs.getInitialLetter())) {
                return lhs.getNickname().compareTo(rhs.getNickname());
            } else {
                if ("#".equals(lhs.getInitialLetter())) {
                    return 1;
                } else if ("#".equals(rhs.getInitialLetter())) {
                    return -1;
                }
                return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
            }
        });
    }


}