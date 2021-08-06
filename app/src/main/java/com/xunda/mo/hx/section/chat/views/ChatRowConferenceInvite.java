package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.xunda.mo.R;
import com.xunda.mo.main.constant.MyConstant;

public class ChatRowConferenceInvite extends EaseChatRow {

    private TextView contentView;

    public ChatRowConferenceInvite(Context context, boolean isSender) {
        super(context, isSender);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(showSenderType ? R.layout.demo_row_sent_conference_invite : R.layout.demo_row_received_conference_invite, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = (TextView) findViewById(R.id.tv_chatcontent);
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        String messageBody = txtBody.getMessage();
        if (!TextUtils.isEmpty(messageBody) && messageBody.contains("-")) {
            messageBody = messageBody.substring(0, messageBody.indexOf("-") + 1) + "\n" + messageBody.substring(messageBody.indexOf("-") + 1);
        }
        contentView.setText(messageBody);


        //添加群聊其他用户的名字与头像
        if (message.getChatType() == EMMessage.ChatType.GroupChat) {
            usernickView.setText(message.getStringAttribute(MyConstant.SEND_NAME, ""));
            String headUrl = message.getStringAttribute(MyConstant.SEND_HEAD, "");
            Glide.with(getContext()).load(headUrl).placeholder(R.drawable.em_login_logo).error(R.drawable.em_login_logo).into(userAvatarView);

        }

    }
}
