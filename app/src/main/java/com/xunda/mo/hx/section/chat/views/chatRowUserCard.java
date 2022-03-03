package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.xunda.mo.R;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.login.MainLogin_OldUser_Phone;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.StaticData;

import java.util.Map;

public class chatRowUserCard extends BaseChatRowWithNameAndHeader {
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
        nicknameView = findViewById(R.id.user_nick_name);
        userIdView = findViewById(R.id.user_id);
        headImageView = findViewById(R.id.head_Image_view);
        tv_user_role = findViewById(R.id.tv_user_role);
        tv_vip = findViewById(com.xunda.mo.R.id.tv_vip);
    }

    @Override
    protected void onSetUpView() {
        EMCustomMessageBody messageBody = (EMCustomMessageBody) message.getBody();
        Map<String, String> params = messageBody.getParams();
        String uId = params.get("uNum");
        userIdView.setText("Mo ID：" + uId);
        String nickname = params.get(MyConstant.NICK_NAME);
        nicknameView.setText(nickname);//名片名字
        String headUrl = params.get(MyConstant.AVATAR);
        Glide.with(getContext()).load(headUrl).transforms(new CenterCrop(), new RoundedCorners(9)).into(headImageView);

        if (isSender()){
//            bubbleLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.chat_send_bg));
            StaticData.changeShapColor(bubbleLayout, ContextCompat.getColor(context, R.color.white));
        }
    }
}

