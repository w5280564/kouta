package com.xunda.mo.hx.section.chat.delegates;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.delegate.EaseMessageAdapterDelegate;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.xunda.mo.hx.section.chat.viewholder.MyEaseImageViewHolder;
import com.xunda.mo.hx.section.chat.views.MyEaseChatRowBARImage;
import com.xunda.mo.main.constant.MyConstant;

/**
 * 图片代理类
 */
public class MyEaseBARImageAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {

    public MyEaseBARImageAdapterDelegate() {
    }

    public MyEaseBARImageAdapterDelegate(MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        super(itemClickListener, itemStyle);
    }

    @Override
    public boolean isForViewType(EMMessage item, int position) {
        if (item.getType() == EMMessage.Type.IMAGE) {
            boolean fireType = item.getBooleanAttribute(MyConstant.FIRE_TYPE, false);
            if (fireType) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new MyEaseChatRowBARImage(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new MyEaseImageViewHolder(view, itemClickListener);
    }
}
