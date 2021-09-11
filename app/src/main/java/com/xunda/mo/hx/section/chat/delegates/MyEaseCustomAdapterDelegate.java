package com.xunda.mo.hx.section.chat.delegates;

import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.delegate.EaseMessageAdapterDelegate;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.viewholder.EaseCustomViewHolder;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.xunda.mo.hx.section.chat.views.MyEaseChatRowCustom;
import com.xunda.mo.main.constant.MyConstant;

import java.util.Map;

public class MyEaseCustomAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {

    @Override
    public boolean isForViewType(EMMessage item, int position) {
        if (item.getType() == EMMessage.Type.CUSTOM) {
            Map<String, Object> mapExt = item.ext();
            if (mapExt != null) {
                String messType = (String) mapExt.get(MyConstant.MESSAGE_TYPE);
                if (messType == null) {
                    return false;
                }
                if (messType.equals(MyConstant.MO_CUSTOMER)) {
                    return true;
                }
            }
        }
        return false;
//        return item.getType() == EMMessage.Type.CUSTOM;
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new MyEaseChatRowCustom(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new EaseCustomViewHolder(view, itemClickListener);
    }
}
