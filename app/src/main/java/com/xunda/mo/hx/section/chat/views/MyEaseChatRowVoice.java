package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.easeui.modules.chat.EaseChatMessageListLayout;
import com.hyphenate.easeui.modules.chat.model.EaseChatItemStyleHelper;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.EaseVoiceLengthUtils;
import com.hyphenate.easeui.utils.GsonUtil;
import com.hyphenate.easeui.utils.ListUtils;
import com.hyphenate.easeui.utils.StringUtil;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowFile;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowVoicePlayer;
import com.hyphenate.util.EMLog;
import com.xunda.mo.R;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.GroupMember_Bean;
import com.xunda.mo.network.saveFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyEaseChatRowVoice extends EaseChatRowFile {
    private static final String TAG = MyEaseChatRowVoice.class.getSimpleName();
    private ImageView voiceImageView;
    private TextView voiceLengthView;
    private ImageView readStatusView;
    private TextView tv_user_role,tv_vip;
    private AnimationDrawable voiceAnimation;

    public MyEaseChatRowVoice(Context context, boolean isSender) {
        super(context, isSender);
    }

    public MyEaseChatRowVoice(Context context, EMMessage message, int position, Object adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(!showSenderType ? R.layout.ease_row_received_voice
                : R.layout.ease_row_sent_voice, this);
    }

    @Override
    protected void onFindViewById() {
        voiceImageView = findViewById(R.id.iv_voice);
        voiceLengthView = findViewById(R.id.tv_length);
        readStatusView = findViewById(R.id.iv_unread_voice);
        tv_user_role = findViewById(R.id.tv_user_role);
        tv_vip = findViewById(com.xunda.mo.R.id.tv_vip);
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

        if (isSender()) {
            MyInfo info = new MyInfo(context);
            try {
                int avatarResId = Integer.parseInt(info.getUserInfo().getHeadImg());
                Glide.with(context).load(avatarResId).into(userAvatarView);
            } catch (Exception e) {
                Glide.with(context).load(info.getUserInfo().getHeadImg())
                        .apply(RequestOptions.placeholderOf(R.mipmap.img_pic_none)
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(userAvatarView);
            }
            //只要不是常规布局形式，就展示昵称
            if (EaseChatItemStyleHelper.getInstance().getStyle().getItemShowType() != EaseChatMessageListLayout.ShowType.NORMAL.ordinal()) {
                EaseUserUtils.setUserNick(message.getFrom(), usernickView);
            }
        } else {
            //添加群其他用户的名字与头像
            if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.conversationId());
                String extMessage = conversation.getExtField();
                List<GroupMember_Bean.DataDTO> memberList = new ArrayList<>();
                if (!TextUtils.isEmpty(extMessage)) {
                    JSONObject JsonObject = null;
                    try {
                        JsonObject = new JSONObject(extMessage);
                        boolean isInsertGroupOrFriendInfo = JsonObject.getBoolean("isInsertGroupOrFriendInfo");
                        if (isInsertGroupOrFriendInfo) {
                            String jsonMemberList = JsonObject.getString("groupMemberList");
                            GroupMember_Bean groupListModel = GsonUtil.getInstance().json2Bean(jsonMemberList, GroupMember_Bean.class);
                            if (groupListModel != null) {
                                memberList.addAll(groupListModel.getData());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                String name = message.getStringAttribute(MyConstant.SEND_NAME, "");
                String headUrl = message.getStringAttribute(MyConstant.SEND_HEAD, "");
                int vipType = message.getIntAttribute(MyConstant.SEND_VIP, 3);

                if (!ListUtils.isEmpty(memberList)) {
                    for (GroupMember_Bean.DataDTO memberObj : memberList) {
                        if (memberObj.getHxUserName().equals(message.getFrom())) {
                            name = memberObj.getNickname();//在群会话页显示的昵称
                            headUrl = memberObj.getHeadImg();
                            message.setAttribute(MyConstant.SEND_NAME, name);
                            if (tv_user_role != null) {
                                if (memberObj.getIdentity() == 1) {
                                    tv_user_role.setVisibility(View.VISIBLE);
                                    tv_user_role.setText("群主");
                                    tv_user_role.setBackgroundResource(R.drawable.shape_bg_all_member_qunzhu);
                                    tv_user_role.setTextColor(ContextCompat.getColor(context,R.color.color_FB8717));
                                } else if (memberObj.getIdentity() == 2) {
                                    tv_user_role.setVisibility(View.VISIBLE);
                                    tv_user_role.setText("管理员");
                                    tv_user_role.setBackgroundResource(R.drawable.shape_bg_all_member_guanliyuan);
                                    tv_user_role.setTextColor(ContextCompat.getColor(context,R.color.color_2391F3));
                                } else {
                                    tv_user_role.setVisibility(View.GONE);
                                }
                            }

                            if (vipType==3) {
                                vipType = memberObj.getVipType();
                            }

                            if (vipType==1){
                                usernickView.setTextColor(ContextCompat.getColor(context,R.color.yellowfive));
                                if (tv_vip!=null) {
                                    tv_vip.setVisibility(VISIBLE);
                                }
                            }else{
                                usernickView.setTextColor(ContextCompat.getColor(context,R.color.greytwo));
                                if (tv_vip!=null) {
                                    tv_vip.setVisibility(GONE);
                                }
                            }
                            break;
                        }
                    }
                }

                if (StringUtil.isBlank(name)) {
                    EaseUserUtils.setUserNick(message.getFrom(), usernickView);
                } else {
                    usernickView.setText(name);
                }


                if (StringUtil.isBlank(headUrl)) {
                    EaseUserUtils.setUserAvatar(context, message.getFrom(), userAvatarView);
                } else {
                    Glide.with(getContext()).load(headUrl).placeholder(R.mipmap.img_pic_none).error(R.mipmap.img_pic_none).into(userAvatarView);
                }
            } else {
                String header_url_message = message.getStringAttribute(MyConstant.SEND_HEAD, "");
                EaseUserUtils.setUserAvatarAndSendHeaderUrl(context, message.getFrom(), header_url_message, userAvatarView);
            }
        }

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
}
