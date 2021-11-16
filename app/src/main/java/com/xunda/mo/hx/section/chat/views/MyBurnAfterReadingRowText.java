package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.text.Spannable;
import android.view.View;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import androidx.constraintlayout.widget.Group;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.manager.EaseDingMessageHelper;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.exceptions.HyphenateException;
import com.xunda.mo.R;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.TimerImgView;

public class MyBurnAfterReadingRowText extends EaseChatRow {
    private TextView contentView;
    private EaseImageView iv_userhead;
    private TimerImgView fire_Img;
    private Group mes_group;

    public MyBurnAfterReadingRowText(Context context, boolean isSender) {
        super(context, isSender);
    }

    public MyBurnAfterReadingRowText(Context context, EMMessage message, int position, Object adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(!showSenderType ? R.layout.ease_row_received_burnafterreading
                : R.layout.ease_row_sent_burnafterreading, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = findViewById(R.id.tv_chatcontent);
        iv_userhead = (EaseImageView) findViewById(R.id.iv_userhead);
        fire_Img = findViewById(R.id.fire_Img);
        mes_group = (Group) findViewById(R.id.mes_group);
    }

    @Override
    public void onSetUpView() {

        if (message.getChatType() == EMMessage.ChatType.Chat) {
            if (isSender()) {
                String headUrl = message.getStringAttribute(MyConstant.SEND_HEAD, "");
                Glide.with(getContext()).load(headUrl).into(userAvatarView);
            } else {

                boolean isSleckt = message.getBooleanAttribute("isSleckt", false);
                if (isSleckt) {
                    mes_group.setVisibility(VISIBLE);
                    fire_Img.setVisibility(GONE);
                } else {
                    mes_group.setVisibility(GONE);
                    fire_Img.setVisibility(VISIBLE);
                }
            }
            //添加群聊其他用户的名字与头像
        } else if (message.getChatType() == EMMessage.ChatType.GroupChat) {
            usernickView.setText(message.getStringAttribute(MyConstant.SEND_NAME, ""));
            String headUrl = message.getStringAttribute(MyConstant.SEND_HEAD, "");
            Glide.with(getContext()).load(headUrl).into(userAvatarView);

            //匿名聊天
            if (!saveFile.getShareData(MyConstant.GROUP_CHAT_ANONYMOUS + message.conversationId(), context).equals("false")) {
                Glide.with(getContext()).load(R.drawable.anonymous_chat_icon).placeholder(R.drawable.mo_icon).into(userAvatarView);
            }
        }

        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        if (txtBody != null) {
            Spannable span = EaseSmileUtils.getSmiledText(context, txtBody.getMessage());
            // 设置内容
            contentView.setText(span, BufferType.SPANNABLE);
        }

        fire_Img.setOnClickListener(v -> {
            int textLen = (int) (contentView.getText().length() * 0.06 * 1000 + 500);
            int voiceLen = Math.max(textLen, 3000) + 10000;//返回一组数中最大的
            fire_Img.setTimer(voiceLen);
            mes_group.setVisibility(VISIBLE);
            fire_Img.setVisibility(GONE);
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getFrom(), EMConversation.EMConversationType.Chat, true);
            message.setAttribute("isSleckt", true);
            conversation.updateMessage(message);
            fire_Img.startTimer(() -> {
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                    conversation.removeMessage(message.getMsgId());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                LiveDataBus.get().with(MyConstant.FIRE_REFRESH).postValue(true);
            });
            sendCMDFireMess();
        });
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


    private void sendCMDFireMess() {
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        EMCmdMessageBody cmdBody = new EMCmdMessageBody("撤回");
        cmdMsg.setTo(message.getFrom());
        cmdMsg.addBody(cmdBody);
        cmdMsg.setAttribute(MyConstant.Dele_Type, true);
        cmdMsg.setAttribute(MyConstant.SendFireRecall_Mess_ID, message.getMsgId());
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }


}
