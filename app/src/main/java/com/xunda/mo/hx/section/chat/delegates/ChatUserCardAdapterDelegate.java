package com.xunda.mo.hx.section.chat.delegates;

import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.delegate.EaseMessageAdapterDelegate;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.section.chat.viewholder.ChatUserCardViewHolder;
import com.xunda.mo.hx.section.chat.views.chatRowUserCard;


public class ChatUserCardAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {
    @Override
    public boolean isForViewType(EMMessage item, int position) {
        if(item.getType() == EMMessage.Type.CUSTOM){
//            && TextUtils.equals(item.getStringAttribute(MyConstant.MESSAGE_TYPE_USERCARD,""),MyConstant.MESSAGE_TYPE_USERCARD)
            EMCustomMessageBody messageBody = (EMCustomMessageBody) item.getBody();
            String event = messageBody.event();
            return event.equals(DemoConstant.USER_CARD_EVENT)?true:false;
        }
        return false;
    }



    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new chatRowUserCard(parent.getContext(), isSender);
    }



    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new ChatUserCardViewHolder(view, itemClickListener);
    }
}

