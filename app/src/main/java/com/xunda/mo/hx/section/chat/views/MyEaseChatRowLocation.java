package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.xunda.mo.R;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.network.saveFile;

/**
 * location row
 */
public class MyEaseChatRowLocation extends EaseChatRow {
    private TextView locationView;
    private TextView tvLocationName;
    private EMLocationMessageBody locBody;

    public MyEaseChatRowLocation(Context context, boolean isSender) {
        super(context, isSender);
    }

    public MyEaseChatRowLocation(Context context, EMMessage message, int position, Object adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(!showSenderType ? R.layout.ease_row_received_location
                : R.layout.ease_row_sent_location, this);
    }

    @Override
    protected void onFindViewById() {
    	locationView = (TextView) findViewById(R.id.tv_location);
    	tvLocationName = findViewById(R.id.tv_location_name);
    }

    @Override
    protected void onSetUpView() {
		locBody = (EMLocationMessageBody) message.getBody();
		locationView.setText(locBody.getAddress());

        if (message.getChatType() == EMMessage.ChatType.Chat) {
            if (isSender()){
                String headUrl = message.getStringAttribute(MyConstant.SEND_HEAD, "");
                Glide.with(getContext()).load(headUrl).into(userAvatarView);
            }
            //添加群聊其他用户的名字与头像
        }else if (message.getChatType() == EMMessage.ChatType.GroupChat) {
            usernickView.setText(message.getStringAttribute(MyConstant.SEND_NAME, ""));
            String headUrl = message.getStringAttribute(MyConstant.SEND_HEAD, "");
            Glide.with(getContext()).load(headUrl).into(userAvatarView);

            //匿名聊天
            if (!saveFile.getShareData(MyConstant.GROUP_CHAT_ANONYMOUS + message.conversationId(), context).equals("false")) {
                Glide.with(getContext()).load(R.drawable.anonymous_chat_icon).placeholder(R.drawable.mo_icon).into(userAvatarView);
            }


        }
    }

    @Override
    protected void onMessageCreate() {
        if(progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if(statusView != null) {
            statusView.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onMessageSuccess() {
        if(progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if(statusView != null) {
            statusView.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onMessageError() {
        super.onMessageError();
        if(progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if(statusView != null) {
            statusView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onMessageInProgress() {
        if(progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if(statusView != null) {
            statusView.setVisibility(View.GONE);
        }
    }

}
