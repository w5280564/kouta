package com.xunda.mo.hx.section.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.section.search.adapter.SearchMessageAdapter;

import java.util.List;

public class SearchGroupChatActivity extends SearchActivity {
    private String toUsername;
    private EMConversation conversation;

    public static void actionStart(Context context, String toUsername) {
        Intent intent = new Intent(context, SearchGroupChatActivity.class);
        intent.putExtra("toUsername", toUsername);
        context.startActivity(intent);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        toUsername = getIntent().getStringExtra("toUsername");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar.setTitle(getString(R.string.em_search_group_chat));
    }

    @Override
    protected EaseBaseRecyclerViewAdapter getAdapter() {
        return new SearchMessageAdapter();
    }

    @Override
    protected void initData() {
        super.initData();
        conversation = EMClient.getInstance().chatManager().getConversation(toUsername, EMConversation.EMConversationType.GroupChat, true);
        query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void searchMessages(String search) {
        conversation =  DemoHelper.getInstance().getConversation(toUsername, EMConversation.EMConversationType.GroupChat,true);

        List<EMMessage> mData = conversation.searchMsgFromDB(search, System.currentTimeMillis(), 100, null, EMConversation.EMSearchDirection.UP);
        ((SearchMessageAdapter)adapter).setKeyword(search);
        adapter.setData(mData);
    }

    @Override
    protected void onChildItemClick(View view, int position) {
//        EMMessage item = ((SearchMessageAdapter) adapter).getItem(position);
//        ChatHistoryActivity.actionStart(mContext, toUsername, EaseConstant.CHATTYPE_GROUP, item.getMsgId());
    }
}
