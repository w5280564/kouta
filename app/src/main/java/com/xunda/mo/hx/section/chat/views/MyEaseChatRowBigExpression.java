package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowText;
import com.hyphenate.exceptions.HyphenateException;
import com.xunda.mo.main.constant.MyConstant;

/**
 * big emoji icons
 *
 */
public class MyEaseChatRowBigExpression extends EaseChatRowText {
    private ImageView imageView;

    public MyEaseChatRowBigExpression(Context context, boolean isSender) {
        super(context, isSender);
    }

    public MyEaseChatRowBigExpression(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(!showSenderType ? R.layout.ease_row_received_bigexpression
                : R.layout.ease_row_sent_bigexpression, this);
    }

    @Override
    protected void onFindViewById() {
        percentageView = (TextView) findViewById(R.id.percentage);
        imageView = (ImageView) findViewById(R.id.image);
    }

    @Override
    public void onSetUpView() {
        String emojiconId = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null);
        EaseEmojicon emojicon = null;
        if(EaseIM.getInstance().getEmojiconInfoProvider() != null){
            emojicon =  EaseIM.getInstance().getEmojiconInfoProvider().getEmojiconInfo(emojiconId);
        }
        if(emojicon != null){
            if(emojicon.getBigIcon() != 0){

                Glide.with(context).load(emojicon.getBigIcon())
                        .apply(RequestOptions.placeholderOf(R.drawable.ease_default_expression))
                        .into(imageView);
            }else if(emojicon.getBigIconPath() != null){
                Glide.with(context).load(emojicon.getBigIconPath())
                        .apply(RequestOptions.placeholderOf(R.drawable.ease_default_expression))
                        .into(imageView);
            }else{
                imageView.setImageResource(R.drawable.ease_default_expression);
            }
        }

        try {
            //添加群聊其他用户的名字与头像
            if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                usernickView.setText(message.getStringAttribute(MyConstant.SEND_NAME));
                String headUrl = message.getStringAttribute(MyConstant.SEND_HEAD);
                Glide.with(getContext()).load(headUrl).placeholder(com.xunda.mo.R.drawable.em_login_logo).error(com.xunda.mo.R.drawable.em_login_logo).into(userAvatarView);
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }

    }


}
