package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.provider.EaseUserProfileProvider;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.xunda.mo.R;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.info.MyInfo;

public class ChatRowVideoCall extends EaseChatRow {
    private TextView contentView;

    public ChatRowVideoCall(Context context, boolean isSender) {
        super(context, isSender);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(showSenderType ? R.layout.ease_row_sent_video_call : R.layout.ease_row_received_video_call, this);
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
            if (isSender()) {
//                String ext = (String) message.ext().get(MyConstant.EXT);
//                if (ext != null) {
//                    try {
//                        JSONObject jsonObject = new JSONObject(ext);
//                        String headUrl = jsonObject.getString(MyConstant.SEND_HEAD);
//                        Glide.with(getContext()).load(headUrl).placeholder(R.drawable.mo_icon).into(userAvatarView);
//                    } catch (Exception e) {
//                        e.getStackTrace();
//                    }
//                }
//                String headUrl = message.getStringAttribute(MyConstant.SEND_HEAD, "");
                MyInfo myInfo = new MyInfo(context);
                String myHeadUrl = myInfo.getUserInfo().getHeadImg();
                Glide.with(getContext()).load(myHeadUrl).placeholder(R.drawable.mo_icon).into(userAvatarView);

            }
            //添加群聊其他用户的名字与头像
        } else if (message.getChatType() == EMMessage.ChatType.GroupChat) {
            usernickView.setText(message.getStringAttribute(MyConstant.SEND_NAME, ""));
            String headUrl = message.getStringAttribute(MyConstant.SEND_HEAD, "");
            Glide.with(getContext()).load(headUrl).placeholder(R.drawable.mo_icon).into(userAvatarView);
        }
    }


    private void setHeadUrl(Context context, ImageView avatarView) {
        String username = message.conversationId();
        EaseUserProfileProvider userProvider = EaseIM.getInstance().getUserProvider();
        if (userProvider != null) {
            EaseUser user = userProvider.getUser(username);
            if (user != null) {
                if (!TextUtils.isEmpty(user.getAvatar())) {
                    Glide.with(context).load(user.getAvatar()).into(avatarView);
                }
            }
        }
    }
}
