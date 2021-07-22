package com.xunda.mo.hx.section.chat.delegates;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.delegate.EaseMessageAdapterDelegate;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.xunda.mo.hx.section.chat.viewholder.ChatNotificationViewHolder;
import com.xunda.mo.hx.section.chat.views.GroupRowUpdateMes;
import com.xunda.mo.main.constant.MyConstant;

public class MyGroupUpdateMesAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {
    @Override
    public boolean isForViewType(EMMessage item, int position) {
//        return item.getType() == TXT && !item.getStringAttribute(MyConstant.MESSAGE_TYPE_CREATE_GROUP, "").isEmpty();
        if (item.getType() == EMMessage.Type.TXT) {
            String Group = item.getStringAttribute(MyConstant.MESSAGE_TYPE, "");
            return TextUtils.equals(Group, MyConstant.UPDATE_GROUP_NAME) ? true : false;
        }
        return false;
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new GroupRowUpdateMes(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new ChatNotificationViewHolder(view, itemClickListener);
    }
}

