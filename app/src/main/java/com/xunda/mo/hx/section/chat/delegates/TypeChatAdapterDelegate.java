package com.xunda.mo.hx.section.chat.delegates;

import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.delegate.EaseMessageAdapterDelegate;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.viewholder.EaseImageViewHolder;
import com.hyphenate.easeui.viewholder.EaseTextViewHolder;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowImage;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowText;
import com.xunda.mo.hx.common.constant.DemoConstant;

import static com.hyphenate.chat.EMMessage.Type.TXT;

/**
 *
 */
public class TypeChatAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {

    public TypeChatAdapterDelegate() {
    }

    public TypeChatAdapterDelegate(MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        super(itemClickListener, itemStyle);
    }

    @Override
    public boolean isForViewType(EMMessage item, int position) {
        item.getStringAttribute("messageType","");
        item.getStringAttribute("sendName","");
        return item.getStringAttribute("messageType", "").equals("chat");
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new EaseChatRowText(parent.getContext(), isSender);
    }
    @Override
    public EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new EaseTextViewHolder(view, itemClickListener);
    }

}
