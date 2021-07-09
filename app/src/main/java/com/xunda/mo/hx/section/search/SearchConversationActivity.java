package com.xunda.mo.hx.section.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.exceptions.HyphenateException;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.db.entity.MsgTypeManageEntity;
import com.xunda.mo.hx.common.interfaceOrImplement.OnResourceParseCallback;
import com.xunda.mo.hx.section.chat.activicy.ChatActivity;
import com.xunda.mo.hx.section.conversation.adapter.HomeAdapter;
import com.xunda.mo.hx.section.conversation.viewmodel.ConversationListViewModel;
import com.xunda.mo.hx.section.message.SystemMsgsActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchConversationActivity extends SearchActivity {
    private List<Object> mData;
    private List<Object> result;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SearchConversationActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar.setTitle(getString(R.string.em_search_conversation));
    }

    @Override
    protected EaseBaseRecyclerViewAdapter getAdapter() {
        return new HomeAdapter();
    }

    @Override
    protected void initData() {
        super.initData();
        result = new ArrayList<>();
        ConversationListViewModel viewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
        viewModel.getConversationObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<Object>>() {
                @Override
                public void onSuccess(List<Object> data) {
                    mData = data;
                }
            });
        });
        viewModel.loadConversationList();
    }

    @Override
    public void searchMessages(String search) {
        searchResult(search);
    }

    private void searchResult(String search) {
        if (mData == null || mData.isEmpty()) {
            return;
        }
        EaseThreadManager.getInstance().runOnIOThread(() -> {
            result.clear();
            for (Object obj : mData) {
                if (obj instanceof EMConversation) {
                    EMConversation item = (EMConversation) obj;
                    String username = item.conversationId();
                    if (item.getType() == EMConversation.EMConversationType.GroupChat) {
                        EMGroup group = DemoHelper.getInstance().getGroupManager().getGroup(username);
                        try {
                            if (group != null) {
                                if (item.getAllMsgCount() != 0 && item.getLastMessage().getStringAttribute("groupName").contains(search)) {
                                    result.add(obj);
                                }

//                            if(group.getGroupName().contains(search)) {
//                                result.add(obj);
//                            }
                            } else {
                                if (item.getAllMsgCount() != 0 && item.getLastMessage().getStringAttribute("sendName").contains(search)) {
                                        result.add(obj);
                                }

//                                if (username.contains(search)) {
//                                    result.add(obj);
//                                }
                            }

                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    } else if (item.getType() == EMConversation.EMConversationType.ChatRoom) {
                        EMChatRoom chatRoom = DemoHelper.getInstance().getChatroomManager().getChatRoom(username);
                        if (chatRoom != null) {
                            if (chatRoom.getName().contains(search)) {
                                result.add(obj);
                            }
                        } else {
                            if (username.contains(search)) {
                                result.add(obj);
                            }
                        }
                    } else {

                        try {
                            if (item.getAllMsgCount() != 0 && item.getLastMessage().getStringAttribute("sendName").contains(search)) {
                                result.add(obj);
                            }
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
//                        if (username.contains(search)) {
//                            result.add(obj);
//                        }
                    }
                }
            }
            runOnUiThread(() -> adapter.setData(result));
        });
    }

    @Override
    protected void onChildItemClick(View view, int position) {
        Object item = adapter.getItem(position);
        if (item instanceof EMConversation) {
            ChatActivity.actionStart(mContext, ((EMConversation) item).conversationId(), EaseCommonUtils.getChatType((EMConversation) item));
        } else if (item instanceof MsgTypeManageEntity) {
            SystemMsgsActivity.actionStart(mContext);
        }
    }
}
