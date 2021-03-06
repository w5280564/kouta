package com.xunda.mo.hx.section.chat.viewholder;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.manager.EaseDingMessageHelper;
import com.hyphenate.easeui.ui.EaseDingAckUserListActivity;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowText;
import com.hyphenate.exceptions.HyphenateException;

public class MyBurnAfterReadingTextViewHolder extends EaseChatRowViewHolder {

    public MyBurnAfterReadingTextViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    public static EaseChatRowViewHolder create(ViewGroup parent,
                                               boolean isSender, MessageListItemClickListener itemClickListener) {
        return new MyBurnAfterReadingTextViewHolder(new EaseChatRowText(parent.getContext(), isSender), itemClickListener);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);
        if (!EaseDingMessageHelper.get().isDingMessage(message) ||
                message.getChatType() != EMMessage.ChatType.GroupChat ||
                message.direct() != EMMessage.Direct.SEND) {
            return;
        }

        // If this msg is a ding-type msg, click to show a list who has already read this message.
        Intent i = new Intent(getContext(), EaseDingAckUserListActivity.class);
        i.putExtra("msg", message);
        getContext().startActivity(i);

        if (message.direct() != EMMessage.Direct.SEND){

        }
    }

    @Override
    protected void handleSendMessage(EMMessage message) {
        super.handleSendMessage(message);
    }

    @Override
    protected void handleReceiveMessage(EMMessage message) {
        super.handleReceiveMessage(message);
        if(!EaseIM.getInstance().getConfigsManager().enableSendChannelAck()) {
            //????????????????????????read_ack???????????????????????????????????????channel_ack
            //???????????????????????????onReceiveMessage????????????????????????????????????????????????????????????read_ack??????
            if (!message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat) {
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
//        message.setUnread(false);
//        message.setAcked(false);

        // Send the group-ack cmd type msg if this msg is a ding-type msg.
        EaseDingMessageHelper.get().sendAckMessage(message);
    }
}
