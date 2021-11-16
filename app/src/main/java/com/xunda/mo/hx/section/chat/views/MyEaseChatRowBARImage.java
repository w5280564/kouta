package com.xunda.mo.hx.section.chat.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowFile;
import com.hyphenate.exceptions.HyphenateException;
import com.xunda.mo.R;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.TimerImgView;

/**
 * image for row
 */
public class MyEaseChatRowBARImage extends EaseChatRowFile {
    protected ImageView imageView;
    private EMImageMessageBody imgBody;
    private TimerImgView fire_Img;
    private View mes_group;
    private View cons_Mes;

    public MyEaseChatRowBARImage(Context context, boolean isSender) {
        super(context, isSender);
    }

    public MyEaseChatRowBARImage(Context context, EMMessage message, int position, Object adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(!showSenderType ? R.layout.ease_row_received_barpicture
                : R.layout.ease_row_sent_barpicture, this);
    }

    @Override
    protected void onFindViewById() {
        percentageView = findViewById(R.id.percentage);
        imageView = findViewById(R.id.image);
        mes_group = findViewById(R.id.mes_group);
        fire_Img = findViewById(R.id.fire_Img);
        cons_Mes = findViewById(R.id.cons_Mes);
        fire_Img.setOnClickListener(v -> {
            fire_Img.setTimer(10000);
            fire_Img.setVisibility(GONE);
            mes_group.setVisibility(VISIBLE);
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getFrom(), EMConversation.EMConversationType.Chat, true);
            message.setAttribute("isSleckt", true);
            conversation.updateMessage(message);
            // 当消息已读之后，发送已读回执，并删除消息
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
    protected void onSetUpView() {
        if (bubbleLayout != null) {
            bubbleLayout.setBackground(null);
        }

        if (message.getChatType() == EMMessage.ChatType.Chat) {
            if (isSender()) {
                String headUrl = message.getStringAttribute(MyConstant.SEND_HEAD, "");
                Glide.with(getContext()).load(headUrl).placeholder(R.drawable.mo_icon).into(userAvatarView);
            } else {
                mes_group.setVisibility(GONE);
                fire_Img.setVisibility(VISIBLE);
                boolean isSleckt = message.getBooleanAttribute("isSleckt", false);
                if (isSleckt) {
                    mes_group.setVisibility(VISIBLE);
                    fire_Img.setVisibility(GONE);
                }
            }
//            fire_Img.setEnabled(true);
            //添加群聊其他用户的名字与头像
        } else if (message.getChatType() == EMMessage.ChatType.GroupChat) {
            usernickView.setText(message.getStringAttribute(MyConstant.SEND_NAME, ""));
            String headUrl = message.getStringAttribute(MyConstant.SEND_HEAD, "");
            Glide.with(getContext()).load(headUrl).placeholder(R.drawable.mo_icon).into(userAvatarView);

            //匿名聊天
            if (!saveFile.getShareData(MyConstant.GROUP_CHAT_ANONYMOUS + message.conversationId(), context).equals("false")) {
                Glide.with(getContext()).load(R.drawable.anonymous_chat_icon).placeholder(R.drawable.mo_icon).into(userAvatarView);
            }
        }
        imgBody = (EMImageMessageBody) message.getBody();
        if (message.direct() == EMMessage.Direct.RECEIVE) {// received messages
            ViewGroup.LayoutParams params = EaseImageUtils.getImageShowSize(context, message);
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.width = params.width;
            layoutParams.height = params.height;
            return;
        }
        showImageView(message);
    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
        super.onViewUpdate(msg);
        //此方法中省略掉了之前的有关非自动下载缩略图后展示图片的逻辑
    }

    @Override
    protected void onMessageSuccess() {
        super.onMessageSuccess();
        //即使是sender，发送成功后也要在执行，防止出现图片尺寸不对的问题
        showImageView(message);
    }

    @Override
    protected void onMessageInProgress() {
        if (message.direct() == EMMessage.Direct.SEND) {
            super.onMessageInProgress();
        } else {
            if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
                //imageView.setImageResource(R.drawable.ease_default_image);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                if (percentageView != null) {
                    percentageView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * load image into image view
     */
    @SuppressLint("StaticFieldLeak")
    private void showImageView(final EMMessage message) {
        EaseImageUtils.showImage(context, imageView, message);
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
