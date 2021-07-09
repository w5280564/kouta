package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.xunda.mo.R;
import com.xunda.mo.main.constant.MyConstant;

import java.util.Map;

public class chatRowUserCard extends EaseChatRow {
    private TextView nickNameView;
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
        nickNameView = (TextView) findViewById(R.id.user_nick_name);
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
//        String nickName = params.get(DemoConstant.USER_CARD_NICK);
        String nickName = params.get(MyConstant.NICK_NAME);
        nickNameView.setText(nickName);//名片名字
//        String headUrl = params.get(DemoConstant.USER_CARD_AVATAR);
        String headUrl = params.get(MyConstant.AVATAR);
        Glide.with(getContext()).load(headUrl).placeholder(R.drawable.em_login_logo).error(R.drawable.em_login_logo).into(headImageView);
        try {
//            String ext = message.getStringAttribute("ext");
//            JSONObject jsonObject = new JSONObject(ext);

            usernickView.setText(message.getStringAttribute(MyConstant.SEND_NAME));
            String userUrl = message.getStringAttribute(MyConstant.SEND_HEAD);
            Glide.with(getContext()).load(userUrl).placeholder(R.drawable.em_login_logo).error(R.drawable.em_login_logo).into(userAvatarView);
        } catch (Exception e) {

        }


    }
}

