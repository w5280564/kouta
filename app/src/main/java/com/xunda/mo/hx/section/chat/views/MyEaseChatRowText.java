package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.text.Spannable;
import android.view.View;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.manager.EaseDingMessageHelper;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.xunda.mo.R;
import com.xunda.mo.main.constant.MyConstant;

public class MyEaseChatRowText extends EaseChatRow {
    private TextView contentView;
    private EaseImageView iv_userhead;

    public MyEaseChatRowText(Context context, boolean isSender) {
        super(context, isSender);
    }

    public MyEaseChatRowText(Context context, EMMessage message, int position, Object adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(!showSenderType ? R.layout.ease_row_received_message
                : R.layout.ease_row_sent_message, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = (TextView) findViewById(R.id.tv_chatcontent);
        iv_userhead = (EaseImageView) findViewById(R.id.iv_userhead);
    }

    @Override
    public void onSetUpView() {
        try {
//            if (!showSenderType) {

            if (message.getChatType() == EMMessage.ChatType.GroupChat){

            }
            if (message.getType()== EMMessage.Type.TXT){

            }

//            String ext = message.getStringAttribute(MyConstant.EXT);
//            JSONObject jsonObject = new JSONObject(ext);

            if (message.getChatType() == EMMessage.ChatType.Chat) {
//                if (message.getStringAttribute("messageType").equals("chat")) {
//                    if (!showSenderType){
//                        usernickView.setText(message.getStringAttribute("toName"));
//                        String headUrl = message.getStringAttribute("toHead");
//                        Glide.with(getContext()).load(headUrl).placeholder(R.drawable.em_login_logo).error(R.drawable.em_login_logo).into(iv_userhead);
//
//                    }else {

//                        usernickView.setText(message.getStringAttribute("sendName"));
//                        String headUrl = message.getStringAttribute("sendHead");
//                        Glide.with(getContext()).load(headUrl).placeholder(R.drawable.em_login_logo).error(R.drawable.em_login_logo).into(iv_userhead);

//                    }




//                }else if (message.getStringAttribute("messageType").equals("group")) {
                //添加群聊其他用户的名字与头像
                }else if (message.getChatType() == EMMessage.ChatType.GroupChat) {



                    usernickView.setText(message.getStringAttribute(MyConstant.SEND_NAME));
                    String headUrl = message.getStringAttribute(MyConstant.SEND_HEAD);
                    Glide.with(getContext()).load(headUrl).placeholder(R.drawable.em_login_logo).error(R.drawable.em_login_logo).into(userAvatarView);
                }
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        if (txtBody != null) {
            Spannable span = EaseSmileUtils.getSmiledText(context, txtBody.getMessage());
            // 设置内容
            contentView.setText(span, BufferType.SPANNABLE);
        }
    }

    @Override
    protected void onMessageCreate() {
        setStatus(View.VISIBLE, View.GONE);
    }

    @Override
    protected void onMessageSuccess() {
        setStatus(View.GONE, View.GONE);

        // Show "1 Read" if this msg is a ding-type msg.
        if (isSender() && EaseDingMessageHelper.get().isDingMessage(message) && ackedView != null) {
            ackedView.setVisibility(VISIBLE);
            int count = message.groupAckCount();
            ackedView.setText(String.format(getContext().getString(R.string.group_ack_read_count), count));
        }

        // Set ack-user list change listener.
        EaseDingMessageHelper.get().setUserUpdateListener(message, userUpdateListener);
    }

    @Override
    protected void onMessageError() {
        setStatus(View.GONE, View.VISIBLE);
    }

    @Override
    protected void onMessageInProgress() {
        setStatus(View.VISIBLE, View.GONE);
    }

    /**
     * set progress and status view visible or gone
     *
     * @param progressVisible
     * @param statusVisible
     */
    private void setStatus(int progressVisible, int statusVisible) {
        if (progressBar != null) {
            progressBar.setVisibility(progressVisible);
        }
        if (statusView != null) {
            statusView.setVisibility(statusVisible);
        }
    }

    private EaseDingMessageHelper.IAckUserUpdateListener userUpdateListener = list -> onAckUserUpdate(list.size());

    public void onAckUserUpdate(final int count) {
        if (ackedView == null) {
            return;
        }
        ackedView.post(() -> {
            if (isSender()) {
                ackedView.setVisibility(VISIBLE);
                ackedView.setText(String.format(getContext().getString(R.string.group_ack_read_count), count));
            }
        });
    }
}
