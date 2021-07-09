package com.xunda.mo.hx.section.chat.delegates;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.adapter.EaseAdapterDelegate;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.xunda.mo.hx.section.chat.viewholder.MyEaseChatRowViewHolder;
import com.xunda.mo.hx.section.widget.chatRow.MyEaseChatRow;

/**
 * 本类设计的目的是做为对话消息代理类的基类，添加了对话代理类特有的方法
 * @param <T>
 * @param <VH>
 */
public abstract class MyEaseMessageAdapterDelegate<T, VH extends MyEaseChatRowViewHolder> extends EaseAdapterDelegate<T, VH> {
    private MessageListItemClickListener mItemClickListener;

    public MyEaseMessageAdapterDelegate() {}

    public MyEaseMessageAdapterDelegate(MessageListItemClickListener itemClickListener) {
        this();
        this.mItemClickListener = itemClickListener;
    }

    public MyEaseMessageAdapterDelegate(MessageListItemClickListener itemClickListener,
                                        EaseMessageListItemStyle itemStyle) {
        this(itemClickListener);
    }

    /**
     * create default item style
     * @return
     */
    public EaseMessageListItemStyle createDefaultItemStyle() {
        EaseMessageListItemStyle.Builder builder = new EaseMessageListItemStyle.Builder();
        builder.showAvatar(true)
                .showUserNick(false);
        return builder.build();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, String tag) {
        MyEaseChatRow view = getEaseChatRow(parent, isSender(tag));
        return createViewHolder(view, mItemClickListener);
    }

    private boolean isSender(String tag) {
        return !TextUtils.isEmpty(tag) && TextUtils.equals(tag, EMMessage.Direct.SEND.toString());
    }

    protected abstract MyEaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender);

    protected abstract VH createViewHolder(View view, MessageListItemClickListener itemClickListener);

    public void setListItemClickListener(MessageListItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

}
