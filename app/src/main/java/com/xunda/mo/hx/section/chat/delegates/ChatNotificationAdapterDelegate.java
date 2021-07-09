package com.xunda.mo.hx.section.chat.delegates;

import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.delegate.EaseMessageAdapterDelegate;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.section.chat.viewholder.ChatNotificationViewHolder;
import com.xunda.mo.hx.section.chat.views.ChatRowNotification;

import static com.hyphenate.chat.EMMessage.Type.TXT;

public class ChatNotificationAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {
    @Override
    public boolean isForViewType(EMMessage item, int position) {
        return item.getType() == TXT && item.getBooleanAttribute(DemoConstant.EM_NOTIFICATION_TYPE, false);
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new ChatRowNotification(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new ChatNotificationViewHolder(view, itemClickListener);
    }
}

