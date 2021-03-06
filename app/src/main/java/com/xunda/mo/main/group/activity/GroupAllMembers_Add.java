package com.xunda.mo.main.group.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.domain.MyEaseUser;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.group.adapter.GroupAddMember_Adapter;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.GroupMemberAdd_Bean;
import com.xunda.mo.model.GruopAddOrRemove_Bean;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.pinyin.PinyinUtils;
import com.xunda.mo.pinyin.WaveSideBar;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.SortMembersList;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupAllMembers_Add extends BaseInitActivity {
    private String groupId;
    private RecyclerView addMembers_Recycler;
    private EditText query_Edit;
    private View search_clear;
    private String keyword = "";
    private String[] newmembers;
    Button right_Btn;
    private WaveSideBar waveSideBar;
    private LinearLayoutManager mMangaer;

    public static void actionStart(Context context, String groupId) {
        Intent intent = new Intent(context, GroupAllMembers_Add.class);
        intent.putExtra("groupId", groupId);
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
        waveSideBar = findViewById(R.id.waveSideBar);
        waveSideBar.setOnTouchLetterChangeListener(new waveSideBarListener());
        initTitle();
        initMemberList(GroupAllMembers_Add.this);
    }

    private void initTitle() {
        View title_Include = findViewById(R.id.title_Include);
        title_Include.setBackgroundColor(ContextCompat.getColor(GroupAllMembers_Add.this, R.color.white));
        title_Include.setElevation(2f);//??????
        Button return_Btn = (Button) title_Include.findViewById(R.id.return_Btn);
        viewTouchDelegate.expandViewTouchDelegate(return_Btn, 50, 50, 50, 50);
        return_Btn.setVisibility(View.VISIBLE);
        TextView cententTxt = (TextView) title_Include.findViewById(R.id.cententtxt);
        cententTxt.setText("???????????????");
        right_Btn = (Button) title_Include.findViewById(R.id.right_Btn);
        right_Btn.setVisibility(View.VISIBLE);
        right_Btn.setText("??????");
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
            List<String> SelectedMembers = mAdapter.getSelectedMembers();
            if (SelectedMembers == null || SelectedMembers.size() < 1) {
                Toast.makeText(GroupAllMembers_Add.this, "??????????????????", Toast.LENGTH_SHORT).show();
                return;
            }
            v.setEnabled(false);
            AddGroupMemberMethod(GroupAllMembers_Add.this, saveFile.group_MangerUser_Url);
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
        GroupMemberListMethod(GroupAllMembers_Add.this, saveFile.Friend_InviteToGroup_Url);
//        GroupMethod(GroupAllMembers_Add.this, saveFile.Group_MyGroupInfo_Url );
    }

    GroupAddMember_Adapter mAdapter;

    public void initMemberList(final Context context) {
        mMangaer = new LinearLayoutManager(context);
        addMembers_Recycler.setLayoutManager(mMangaer);
        //????????????????????????item????????????????????????????????????????????????????????????
        addMembers_Recycler.setHasFixedSize(true);
//        mAdapter = new GroupAddMember_Adapter(context, data);
        mAdapter = new GroupAddMember_Adapter(TextUtils.isEmpty(groupId));
        addMembers_Recycler.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });

        mAdapter.setOnSelectListener(new GroupAddMember_Adapter.OnSelectListener() {
            @Override
            public void onSelected(View v, List<String> selectedMembers) {
                right_Btn.setText(getString(R.string.finish) + "(" + selectedMembers.size() + ")");
            }
        });

    }

    GroupMemberAdd_Bean groupListModel;

    public void GroupMemberListMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void success(String result) {
                groupListModel = new Gson().fromJson(result, GroupMemberAdd_Bean.class);
                sortList();
            }

            @Override
            public void failed(String... args) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortList() {
        List<MyEaseUser> data = new ArrayList<>();
        List<String> lettersData = new ArrayList<>();
        List<GroupMemberAdd_Bean.DataDTO> memberList = SortMembersList.getMemberList(groupListModel.getData());//???????????????
        for (int i = 0; i < memberList.size(); i++) {
            GroupMemberAdd_Bean.DataDTO dataDTO = memberList.get(i);
            MyEaseUser user = new MyEaseUser();
            user.setUsername("");
            String userNickName = TextUtils.isEmpty(dataDTO.getNickname()) ? dataDTO.getRemarkName() : dataDTO.getNickname();
            user.setNickname(userNickName);
            String pinyin = PinyinUtils.getPingYin(userNickName);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                user.setInitialLetter(sortString);
            } else {
                user.setInitialLetter("#");
            }
            lettersData.add(user.getInitialLetter());
            user.setAvatar(dataDTO.getHeadImg());
            user.setContact(0);//???????????? 4??????????????????
            user.setGender(0);
            user.setVipType(dataDTO.getVipType());
            user.setUserId(dataDTO.getUserId());
            user.setUserNum(dataDTO.getUserNum());
            data.add(user);
        }
        List<String> myList = lettersData.stream().distinct().collect(Collectors.toList());//??????
        waveSideBar.setLetters(myList);
        SortMembersList.getLastDescList(data);
        if (newmembers != null) {
            mAdapter.setExistMember(Arrays.asList(newmembers));
        } else {
            mAdapter.setData(data);
        }
    }


    private class query_EditChangeListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
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
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            query_Edit.getText().clear();
            sortList();
        }
    }


    private void SearchGroupMember(String keyword) {
        List<MyEaseUser> searchData = new ArrayList<>();
        for (int i = 0; i < groupListModel.getData().size(); i++) {
            GroupMemberAdd_Bean.DataDTO dataDTO = groupListModel.getData().get(i);
            String userNickName = TextUtils.isEmpty(dataDTO.getNickname()) ? dataDTO.getRemarkName() : dataDTO.getNickname();
            String userNum = dataDTO.getUserNum().toString();
            if (userNickName.contains(keyword) || userNum.contains(keyword)) {
                MyEaseUser user = new MyEaseUser();
                user.setUsername("");
                user.setNickname(userNickName);
                String pinyin = PinyinUtils.getPingYin(userNickName);
                String sortString = pinyin.substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Z]")) {
                    user.setInitialLetter(sortString);
                } else {
                    user.setInitialLetter("#");
                }
                user.setAvatar(dataDTO.getHeadImg());
                user.setContact(0);//???????????? 4??????????????????
                user.setGender(0);
                user.setVipType(dataDTO.getVipType());
                user.setUserId(dataDTO.getUserId());
                user.setUserNum(dataDTO.getUserNum());
                searchData.add(user);
            }
        }
        if (newmembers != null) {
            mAdapter.setExistMember(Arrays.asList(newmembers));
        } else {
            mAdapter.setData(searchData);
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
    public void AddGroupMemberMethod(Context context, String baseUrl) {
        List<String> SelectedMembers = mAdapter.getSelectedMembers();
        String userIds = String.join(",", SelectedMembers);
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("type", "1");
        map.put("userIds", userIds);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show();
                GruopAddOrRemove_Bean groupBean = new Gson().fromJson(result, GruopAddOrRemove_Bean.class);
                sendMes(groupBean);
                finish();
                right_Btn.setEnabled(true);
            }

            @Override
            public void failed(String... args) {
                right_Btn.setEnabled(true);
            }
        });
    }

    //????????????
    private void sendMes(GruopAddOrRemove_Bean Model) {
        MyInfo myInfo = new MyInfo(mContext);
        String conversationId = Model.getData().getGroupHxId();
//        EMMessage message = EMMessage.createTxtSendMessage(mContext.getString(R.string.CREATE_GROUP_CONTENT), conversationId);
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        EMTextMessageBody txtBody = new EMTextMessageBody("");
        message.addBody(txtBody);
        message.setTo(conversationId);
        message.setChatType(EMMessage.ChatType.GroupChat);
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_ADDUSER);
        message.setAttribute(MyConstant.USER_NAME, getUserName());
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        message.setAttribute(MyConstant.GROUP_NAME, Model.getData().getGroupName());
        message.setAttribute(MyConstant.GROUP_HEAD, Model.getData().getGroupHeadImg());
        DemoHelper.getInstance().getChatManager().sendMessage(message);
    }

    @SuppressLint("NewApi")
    private String getUserName() {
        List<EaseUser> selectMembers = mAdapter.getUserList();
        List<String> userListStr = new ArrayList<>();
        for (int i = 0; i < selectMembers.size(); i++) {
            userListStr.add(selectMembers.get(i).getNickname());
        }
        String userName = String.join(",", userListStr);
        return userName;
    }

    private class waveSideBarListener implements WaveSideBar.OnTouchLetterChangeListener {
        @Override
        public void onLetterChange(String letter) {
            //??????????????????????????????
            int position = mAdapter.getPositionForSection(letter.charAt(0));
            if (position != -1) {
                mMangaer.scrollToPositionWithOffset(position, 0);
            }
        }
    }


}