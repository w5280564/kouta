package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.easeui.utils.EaseVoiceLengthUtils;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowFile;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowVoicePlayer;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.xunda.mo.R;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.TimerImgView;

public class MyEaseChatRowBARVoice extends EaseChatRowFile {
    private static final String TAG = MyEaseChatRowBARVoice.class.getSimpleName();
    private ImageView voiceImageView;
    private TextView voiceLengthView;
    private ImageView readStatusView;
    private AnimationDrawable voiceAnimation;
    private TimerImgView fire_Img;
    private View mes_group;

    public MyEaseChatRowBARVoice(Context context, boolean isSender) {
        super(context, isSender);
    }

    public MyEaseChatRowBARVoice(Context context, EMMessage message, int position, Object adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(!showSenderType ? R.layout.ease_row_received_barvoice
                : R.layout.ease_row_sent_barvoice, this);
    }

    @Override
    protected void onFindViewById() {
        voiceImageView = ((ImageView) findViewById(R.id.iv_voice));
        voiceLengthView = (TextView) findViewById(R.id.tv_length);
        readStatusView = (ImageView) findViewById(R.id.iv_unread_voice);
        mes_group = findViewById(R.id.mes_group);
        fire_Img = findViewById(R.id.fire_Img);
    }

    @Override
    protected void onSetUpView() {
        EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.getBody();
        int len = voiceBody.getLength();
        int padding = 0;
        if (len > 0) {
            padding = EaseVoiceLengthUtils.getVoiceLength(getContext(), len);
            voiceLengthView.setText(voiceBody.getLength() + "\"");
            voiceLengthView.setVisibility(View.VISIBLE);
        } else {
            voiceLengthView.setVisibility(View.INVISIBLE);
        }
        if (!showSenderType) {
            voiceImageView.setImageResource(R.drawable.ease_chatfrom_voice_playing);
            voiceLengthView.setPadding(padding, 0, 0, 0);
        } else {
            voiceImageView.setImageResource(R.drawable.ease_chatto_voice_playing);
            voiceLengthView.setPadding(0, 0, padding, 0);
        }

        if (message.direct() == EMMessage.Direct.RECEIVE) {
            if (readStatusView != null) {
                if (message.isListened()) {
                    // hide the unread icon
                    readStatusView.setVisibility(View.INVISIBLE);
                } else {
                    readStatusView.setVisibility(View.VISIBLE);
                }
            }

            EMLog.d(TAG, "it is receive msg");
            if (voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                }

            } else {
                progressBar.setVisibility(View.INVISIBLE);
            }
        } else {
            // hide the unread icon
            readStatusView.setVisibility(View.INVISIBLE);
        }

        // To avoid the item is recycled by listview and slide to this item again but the animation is stopped.
        EaseChatRowVoicePlayer voicePlayer = EaseChatRowVoicePlayer.getInstance(getContext());
        if (voicePlayer.isPlaying() && message.getMsgId().equals(voicePlayer.getCurrentPlayingId())) {
            startVoicePlayAnimation();
        }

        if (message.getChatType() == EMMessage.ChatType.Chat) {
            if (isSender()) {
                String headUrl = message.getStringAttribute(MyConstant.SEND_HEAD, "");
                Glide.with(getContext()).load(headUrl).into(userAvatarView);
            } else {
                mes_group.setVisibility(GONE);
                fire_Img.setVisibility(VISIBLE);
                boolean isSleckt = message.getBooleanAttribute("isSleckt", false);
                if (isSleckt) {
                    mes_group.setVisibility(VISIBLE);
                    fire_Img.setVisibility(GONE);
                }
            }
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

        fire_Img.setOnClickListener(v -> {
            int voiceLen = (voiceBody.getLength() * 1000) + 10000;
            fire_Img.setTimer(voiceLen);
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
    protected void onViewUpdate(EMMessage msg) {
        super.onViewUpdate(msg);

        // Only the received message has the attachment download status.
        if (message.direct() == EMMessage.Direct.SEND) {
            return;
        }

        EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) msg.getBody();
        if (voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void startVoicePlayAnimation() {
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            voiceImageView.setImageResource(R.drawable.voice_from_icon);
        } else {
            voiceImageView.setImageResource(R.drawable.voice_to_icon);
        }
        voiceAnimation = (AnimationDrawable) voiceImageView.getDrawable();
        voiceAnimation.start();

        // Hide the voice item not listened status view.
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            readStatusView.setVisibility(View.INVISIBLE);
        }
    }

    public void stopVoicePlayAnimation() {
        if (voiceAnimation != null) {
            voiceAnimation.stop();
        }

        if (message.direct() == EMMessage.Direct.RECEIVE) {
            voiceImageView.setImageResource(R.drawable.ease_chatfrom_voice_playing);
        } else {
            voiceImageView.setImageResource(R.drawable.ease_chatto_voice_playing);
        }
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
