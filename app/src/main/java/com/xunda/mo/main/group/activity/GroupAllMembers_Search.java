package com.xunda.mo.main.group.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.domain.MyEaseUser;
import com.xunda.mo.hx.section.search.SearchActivity;
import com.xunda.mo.main.group.adapter.MyGroupMembersList_Adapter;
import com.xunda.mo.model.GroupMember_Bean;
import com.xunda.mo.pinyin.PinyinUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupAllMembers_Search extends SearchActivity {
    private List<GroupMember_Bean.DataDTO> groupMember;

    public static void actionStart(Context context, List<GroupMember_Bean.DataDTO> groupMember) {
        Intent intent = new Intent(context, GroupAllMembers_Search.class);
        intent.putExtra("groupMember", (Serializable) groupMember);
        context.startActivity(intent);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupMember = (List<GroupMember_Bean.DataDTO>) intent.getSerializableExtra("groupMember");
    }


    @Override
    protected EaseBaseRecyclerViewAdapter getAdapter() {
        return new SearchFriendAdapter();
    }

    @Override
    public void searchMessages(String search) {
        SearchGroupMember(search);
    }

    @Override
    protected void onChildItemClick(View view, int position) {

    }


    public class SearchFriendAdapter extends MyGroupMembersList_Adapter {
        @Override
        public int getEmptyLayoutId() {
            return R.layout.demo_layout_no_data_show_nothing;
        }
    }

    private void SearchGroupMember(String keyword) {
        List<MyEaseUser> searchData = new ArrayList<>();
        for (int i = 0; i < groupMember.size(); i++) {
            String userName = groupMember.get(i).getNickname();
            String userNum = groupMember.get(i).getUserNum().toString();
            if(userName.contains(keyword) || userNum.contains(keyword)) {
                GroupMember_Bean.DataDTO dataDTO = groupMember.get(i);
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
                searchData.add(user);
            }
        }

        adapter.setData(searchData);
    }


}