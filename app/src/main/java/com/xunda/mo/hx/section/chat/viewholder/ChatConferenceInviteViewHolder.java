package com.xunda.mo.hx.section.chat.viewholder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.xunda.mo.hx.section.chat.views.ChatRowConferenceInvite;

public class ChatConferenceInviteViewHolder extends EaseChatRowViewHolder {

    public ChatConferenceInviteViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    public static ChatConferenceInviteViewHolder create(ViewGroup parent, boolean isSender,
                                                        MessageListItemClickListener itemClickListener) {
        return new ChatConferenceInviteViewHolder(new ChatRowConferenceInvite(parent.getContext(), isSender), itemClickListener);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);
    }
}
