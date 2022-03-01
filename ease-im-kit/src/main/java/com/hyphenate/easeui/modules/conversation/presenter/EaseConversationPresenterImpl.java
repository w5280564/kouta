package com.hyphenate.easeui.modules.conversation.presenter;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;
import com.hyphenate.easeui.utils.MyEaseCommonUtils;
import com.hyphenate.easeui.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class EaseConversationPresenterImpl extends EaseConversationPresenter {

    /**
     * 注意：默认conversation中设置extField值为时间戳后，是将该会话置顶
     * 如果有不同的逻辑，请自己实现，并调用{@link #sortData(List)}方法即可
     */
    @Override
    public void loadData() {
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        if(conversations.isEmpty()) {
            runOnUI(() -> {
                if(!isDestroy()) {
                    mView.loadConversationListNoData();
                }
            });
            return;
        }
        List<EaseConversationInfo> infos = new ArrayList<>();
        synchronized (this) {
            EaseConversationInfo info = null;
            for (EMConversation conversation : conversations.values()) {
                if(conversation.getAllMessages().size() != 0) {
                    //如果不展示系统消息，则移除相关系统消息
                    if(!showSystemMessage) {
                        if(TextUtils.equals(conversation.conversationId(), EaseConstant.DEFAULT_SYSTEM_MESSAGE_ID)) {
                            continue;
                        }
                    }
                    info = new EaseConversationInfo();
                    info.setInfo(conversation);
                    String extField = conversation.getExtField();
                    long lastMsgTime=conversation.getLastMessage().getMsgTime();
                    if(!TextUtils.isEmpty(extField) && MyEaseCommonUtils.isTimestamp(extField)) {
                        info.setTop(true);
                        long makeTopTime = 0L;
                        JSONObject JsonObject = null;
                        try {
                            JsonObject = new JSONObject(extField);
                            makeTopTime = JsonObject.getLong("topTimeMillis");//1 置顶时间戳
                            if(makeTopTime>lastMsgTime) {
                                info.setTimestamp(makeTopTime);
                            }else{
                                info.setTimestamp(lastMsgTime);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        info.setTimestamp(lastMsgTime);
                    }
                    infos.add(info);
                }
            }
        }
        if(isActive()) {
            runOnUI(()-> mView.loadConversationListSuccess(infos));
        }
    }

    /**
     * 排序数据
     * @param data
     */
    @Override
    public void sortData(List<EaseConversationInfo> data) {
        if(data == null || data.isEmpty()) {
            runOnUI(() -> {
                if(!isDestroy()) {
                    mView.loadConversationListNoData();
                }

            });
            return;
        }
        List<EaseConversationInfo> sortList = new ArrayList<>();
        List<EaseConversationInfo> topSortList = new ArrayList<>();
        synchronized (this) {
            for(EaseConversationInfo info : data) {
                if(info.isTop()) {
                    topSortList.add(info);
                }else {
                    sortList.add(info);
                }
            }
            sortByTimestamp(topSortList);
            sortByTimestamp(sortList);
            sortList.addAll(0, topSortList);
        }
        runOnUI(() -> {
            if(!isDestroy()) {
                //Log.d("data--->3333", JSON.toJSONString(data));


                mView.sortConversationListSuccess(sortList);
            }
        });
    }

    /**
     * 排序
     * @param list
     */
    private void sortByTimestamp(List<EaseConversationInfo> list) {
        if(list == null || list.isEmpty()) {
            return;
        }
        Collections.sort(list, new Comparator<EaseConversationInfo>() {
            @Override
            public int compare(EaseConversationInfo o1, EaseConversationInfo o2) {
                if(o2.getTimestamp() > o1.getTimestamp()) {
                    return 1;
                }else if(o2.getTimestamp() == o1.getTimestamp()) {
                    return 0;
                }else {
                    return -1;
                }
            }
        });
    }

    @Override
    public void makeConversionRead(int position, EaseConversationInfo info) {
        if(info.getInfo() instanceof EMConversation) {
            ((EMConversation) info.getInfo()).markAllMessagesAsRead();
        }
        if(!isDestroy()) {
            mView.refreshList(position);
        }
    }

    @Override
    public void makeConversationTop(int position, EaseConversationInfo info) {
        if (info.getInfo() instanceof EMConversation) {
            insertConversionExdInfo(true, info);
            info.setTop(true);
            info.setTimestamp(System.currentTimeMillis());
        }
        if (!isDestroy()) {
            mView.refreshList();
        }
    }

    //往好友会话列表添加扩展字段
    private void insertConversionExdInfo(boolean isInsertMessageTop, EaseConversationInfo conversation) {
        String extField = ((EMConversation) conversation.getInfo()).getExtField();
        JSONObject jsonObject = null;
        if (!StringUtil.isBlank(extField)) {
            try {
                jsonObject = new JSONObject(extField);
                jsonObject.put("isInsertMessageTop", isInsertMessageTop);
                jsonObject.put("topTimeMillis", isInsertMessageTop ? System.currentTimeMillis() : 0);
            } catch (JSONException e) {
                jsonObject = getJsonObjectTop(isInsertMessageTop);
            }
        } else {
            jsonObject = getJsonObjectTop(isInsertMessageTop);
        }


        ((EMConversation) conversation.getInfo()).setExtField(jsonObject.toString());
    }

    @NonNull
    private JSONObject getJsonObjectTop(boolean isInsertMessageTop) {
        JSONObject jsonObject;
        jsonObject = new JSONObject();
        try {
            jsonObject.put("isInsertMessageTop", isInsertMessageTop);
            jsonObject.put("topTimeMillis", isInsertMessageTop ? System.currentTimeMillis() : 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    @Override
    public void cancelConversationTop(int position, EaseConversationInfo info) {
        if (info.getInfo() instanceof EMConversation) {
            insertConversionExdInfo(false, info);
            info.setTop(false);
            info.setTimestamp(((EMConversation) info.getInfo()).getLastMessage().getMsgTime());
        }
        if (!isDestroy()) {
            mView.refreshList();
        }
    }

    @Override
    public void deleteConversation(int position, EaseConversationInfo info) {
        if(info.getInfo() instanceof EMConversation) {
            //如果是系统通知，则不删除系统消息
            boolean isDelete = EMClient.getInstance().chatManager()
                                .deleteConversation(((EMConversation) info.getInfo()).conversationId()
                                        , true);
            if(!isDestroy()) {
                if(isDelete) {
                    mView.deleteItem(position);
                }else {
                    mView.deleteItemFail(position, "");
                }
            }

        }
    }
}

