package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.xunda.mo.R;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.info.MyInfo;

public class ChatRowVoiceCall extends EaseChatRow {
    private TextView contentView;

    public ChatRowVoiceCall(Context context, boolean isSender) {
        super(context, isSender);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(showSenderType ? R.layout.ease_row_sent_voice_call : R.layout.ease_row_received_voice_call, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = (TextView) findViewById(com.hyphenate.easeui.R.id.tv_chatcontent);
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        contentView.setText(txtBody.getMessage());
        if (message.getChatType() == EMMessage.ChatType.Chat) {
            if (isSender()){
                MyInfo myInfo = new MyInfo(context);
                String myHeadUrl = myInfo.getUserInfo().getHeadImg();
                Glide.with(getContext()).load(myHeadUrl).placeholder(R.drawable.mo_icon).into(userAvatarView);
            }
            //添加群聊其他用户的名字与头像
        }else
            if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                usernickView.setText(message.getStringAttribute(MyConstant.SEND_NAME,""));
                String headUrl = message.getStringAttribute(MyConstant.SEND_HEAD,"");
                Glide.with(getContext()).load(headUrl).placeholder(R.drawable.mo_icon).into(userAvatarView);
            }
    }
}
