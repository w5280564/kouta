package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.xunda.mo.R;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.network.saveFile;

import java.util.Map;

public class chatRowUserCard extends EaseChatRow {
    private TextView nicknameView;
    private TextView userIdView;
    private ImageView headImageView;

    public chatRowUserCard(Context context, boolean isSender) {
        super(context, isSender);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(showSenderType ? R.layout.ease_row_sent_user_card : R.layout.ease_row_received_user_card, this);
    }

    @Override
    protected void onFindViewById() {
        nicknameView = (TextView) findViewById(R.id.user_nick_name);
        userIdView = (TextView) findViewById(R.id.user_id);
        headImageView = (ImageView) findViewById(R.id.head_Image_view);
    }

    @Override
    protected void onSetUpView() {
        EMCustomMessageBody messageBody = (EMCustomMessageBody) message.getBody();
        Map<String, String> params = messageBody.getParams();
//        String uId = params.get(DemoConstant.USER_CARD_ID);
        String uId = params.get("uNum");
        userIdView.setText("Mo ID：    " + uId);
//        String nickname = params.get(DemoConstant.USER_CARD_NICK);
        String nickname = params.get(MyConstant.NICK_NAME);
        nicknameView.setText(nickname);//名片名字
//        String headUrl = params.get(DemoConstant.USER_CARD_AVATAR);
        String headUrl = params.get(MyConstant.AVATAR);
        Glide.with(getContext()).load(headUrl).into(headImageView);
//            String ext = message.getStringAttribute("ext");
//            JSONObject jsonObject = new JSONObject(ext);
        usernickView.setText(message.getStringAttribute(MyConstant.SEND_NAME, ""));
        String userUrl = message.getStringAttribute(MyConstant.SEND_HEAD, "");
        Glide.with(getContext()).load(userUrl).into(userAvatarView);

        if (message.getChatType() == EMMessage.ChatType.GroupChat) {
            //匿名聊天
            if (!saveFile.getShareData(MyConstant.GROUP_CHAT_ANONYMOUS + message.conversationId(), context).equals("false")) {
                Glide.with(getContext()).load(R.drawable.anonymous_chat_icon).placeholder(R.drawable.mo_icon).into(userAvatarView);
            }
        }
    }
}

