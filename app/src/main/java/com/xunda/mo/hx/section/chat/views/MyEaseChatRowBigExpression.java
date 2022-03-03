package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowText;
import com.xunda.mo.R;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.network.saveFile;

/**
 * big emoji icons
 *
 */
public class MyEaseChatRowBigExpression extends BaseChatRowWithNameAndHeader {
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
        percentageView = findViewById(R.id.percentage);
        imageView = findViewById(R.id.image);
        tv_user_role = findViewById(com.xunda.mo.R.id.tv_user_role);
        tv_vip = findViewById(com.xunda.mo.R.id.tv_vip);
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



    }


}
