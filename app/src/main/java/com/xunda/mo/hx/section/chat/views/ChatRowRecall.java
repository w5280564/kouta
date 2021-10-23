package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.xunda.mo.R;
import com.xunda.mo.main.constant.MyConstant;

public class ChatRowRecall extends EaseChatRow {
    private TextView contentView;

    public ChatRowRecall(Context context, boolean isSender) {
        super(context, isSender);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(R.layout.demo_row_recall_message, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = (TextView) findViewById(R.id.text_content);
    }

    @Override
    protected void onSetUpView() {
        // 设置显示内容
        String messageStr = null;
        if (message.getChatType() == EMMessage.ChatType.Chat) {
            if (isSender()) {
                messageStr = String.format(context.getString(R.string.msg_recall_by_self));
            } else {
                String sendName = message.getStringAttribute(MyConstant.SEND_NAME, "");
                messageStr = String.format(context.getString(R.string.msg_recall_by_user), sendName);
                messageStr = "对方撤回一条消息";
            }

        } else if (message.getChatType() == EMMessage.ChatType.GroupChat) {
            String sendName = message.getStringAttribute(MyConstant.SEND_NAME, "");
            messageStr = String.format(context.getString(R.string.msg_recall_by_user), sendName);
        }
        contentView.setText(messageStr);
    }
}
