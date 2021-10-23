package com.xunda.mo.hx.section.chat.delegates;

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
            String message_Type = item.getStringAttribute(MyConstant.MESSAGE_TYPE, "");
            switch (message_Type) {
                case MyConstant.UPDATE_GROUP_NAME:
                case MyConstant.MESSAGE_TYPE_DELETUSER:
                case MyConstant.MESSAGE_TYPE_ADDUSER:
                case MyConstant.MESSAGE_TYPE_ADDADMIN:
                case MyConstant.MESSAGE_TYPE_DELETADMIN:
                case MyConstant.MESSAGE_TYPE_ANONYMOUS_ON:
                case MyConstant.MESSAGE_TYPE_ANONYMOUS_OFF:
                case MyConstant.MESSAGE_TYPE_MUTE_ON:
                case MyConstant.MESSAGE_TYPE_MUTE_OFF:
                case MyConstant.MESSAGE_TYPE_PROTECT_ON:
                case MyConstant.MESSAGE_TYPE_PROTECT_OFF:
                case MyConstant.MESSAGE_TYPE_UPDATE_MASTER:
                case MyConstant.MESSAGE_TYPE_PUSH_ON:
                case MyConstant.MESSAGE_TYPE_PUSH_OFF:
                case MyConstant.MESSAGE_GROUP_LEAVE:
                case MyConstant.GROUP_UPDATE_GROUPDES:
                case MyConstant.MESSAGE_TYPE_DOUBLE_RECALL:
                case MyConstant.MESSAGE_TYPE_GROUP_DOUBLE_RECALL:
                case MyConstant.APPLY:
                case MyConstant.MESSAGE_GROUP_Message:
                case MyConstant.Message_Recall:
                    return true;
            }

        }
        return false;
    }



    @Override
    public EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new GroupRowUpdateMes(parent.getContext(), isSender);
    }




    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new ChatNotificationViewHolder(view, itemClickListener);
    }
}

