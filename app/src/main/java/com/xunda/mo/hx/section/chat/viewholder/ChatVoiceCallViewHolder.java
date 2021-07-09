package com.xunda.mo.hx.section.chat.viewholder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easecallkit.EaseCallKit;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.xunda.mo.hx.section.chat.views.ChatRowVoiceCall;

public class ChatVoiceCallViewHolder extends EaseChatRowViewHolder {

    public ChatVoiceCallViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    public static ChatVoiceCallViewHolder create(ViewGroup parent, boolean isSender,
                                                        MessageListItemClickListener itemClickListener) {
        return new ChatVoiceCallViewHolder(new ChatRowVoiceCall(parent.getContext(), isSender), itemClickListener);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);
        if(message.direct() == EMMessage.Direct.SEND) {
            EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VOICE_CALL,message.getTo(),null);
        }else {
            EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VOICE_CALL,message.getFrom(),null);
        }
    }
}
