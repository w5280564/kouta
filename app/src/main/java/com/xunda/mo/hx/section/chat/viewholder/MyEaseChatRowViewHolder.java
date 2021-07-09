package com.xunda.mo.hx.section.chat.viewholder;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.xunda.mo.hx.section.widget.chatRow.MyEaseChatRow;

import java.util.List;


public class MyEaseChatRowViewHolder extends EaseMessageAdapter.ViewHolder<EMMessage> implements MyEaseChatRow.EaseChatRowActionCallback {
    private static final String TAG = MyEaseChatRowViewHolder.class.getSimpleName();
    private Context context;
    private MyEaseChatRow chatRow;
    private EMMessage message;
    private MessageListItemClickListener mItemClickListener;

    public MyEaseChatRowViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView);
        // 解决view宽和高不显示的问题
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(params);
        this.context = itemView.getContext();
        this.mItemClickListener = itemClickListener;
    }

    @Override
    public void initView(View itemView) {
        this.chatRow = (MyEaseChatRow) itemView;
    }

    @Override
    public void setData(EMMessage item, int position) {
        message = item;
        chatRow.setUpView(item, position, mItemClickListener, this);
        handleMessage();
    }

    @Override
    public void setDataList(List<EMMessage> data, int position) {
        super.setDataList(data, position);
        chatRow.setTimestamp(position == 0 ? null : data.get(position - 1));
    }

    @Override
    public void onResendClick(EMMessage message) {

    }

    @Override
    public void onBubbleClick(EMMessage message) {

    }

    @Override
    public void onDetachedFromWindow() {

    }

    private void handleMessage() {
        if (message.direct() == EMMessage.Direct.SEND) {
            handleSendMessage(message);
        } else if (message.direct() == EMMessage.Direct.RECEIVE) {
            handleReceiveMessage(message);
        }
    }

    /**
     * send message
     * @param message
     */
    protected void handleSendMessage(final EMMessage message) {
        // Update the view according to the message current status.
        getChatRow().updateView(message);
    }

    /**
     * receive message
     * @param message
     */
    protected void handleReceiveMessage(EMMessage message) {

    }

    public Context getContext() {
        return context;
    }

    public MyEaseChatRow getChatRow() {
        return chatRow;
    }
}
