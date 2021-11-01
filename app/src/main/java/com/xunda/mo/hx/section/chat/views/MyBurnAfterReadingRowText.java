package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.text.Spannable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView.BufferType;

import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.xunda.mo.staticdata.FireTimerTextView;

public class MyBurnAfterReadingRowText extends EaseChatRow {
    private FireTimerTextView contentView;
    private EaseImageView iv_userhead;
    private ImageView fire_Img;
    private ConstraintLayout cons_Mes;
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
        cons_Mes = findViewById(R.id.cons_Mes);
        contentView = (FireTimerTextView) findViewById(R.id.tv_chatcontent);
        iv_userhead = (EaseImageView) findViewById(R.id.iv_userhead);
        fire_Img = (ImageView) findViewById(R.id.fire_Img);
        mes_group = (Group) findViewById(R.id.mes_group);
    }

    @Override
    public void onSetUpView() {

        if (message.getChatType() == EMMessage.ChatType.Chat) {
            if (isSender()) {
                String headUrl = message.getStringAttribute(MyConstant.SEND_HEAD, "");
                Glide.with(getContext()).load(headUrl).into(userAvatarView);
            } else {
//                cons_Mes.setVisibility(GONE);
                mes_group.setVisibility(GONE);
                iv_userhead.setVisibility(VISIBLE);
                fire_Img.setVisibility(VISIBLE);
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
//            cons_Mes.setVisibility(VISIBLE);
            mes_group.setVisibility(VISIBLE);
            iv_userhead.setVisibility(VISIBLE);
            fire_Img.setVisibility(GONE);
            sendCMDFireMess();
            // 当消息已读之后，发送已读回执，并删除消息
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
//                // 消息所属会话
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getFrom(), EMConversation.EMConversationType.Chat, true);
                conversation.removeMessage(message.getMsgId());
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            contentView.startTimer(() -> {
                LiveDataBus.get().with(MyConstant.FIRE_REFRESH).postValue(true);
            });

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
