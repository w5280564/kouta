package com.xunda.mo.hx.section.conversation.delegate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.manager.EaseAtMessageHelper;
import com.hyphenate.easeui.manager.EasePreferenceManager;
import com.hyphenate.easeui.modules.conversation.delegate.EaseDefaultConversationDelegate;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationSetStyle;
import com.hyphenate.easeui.provider.EaseConversationInfoProvider;
import com.hyphenate.easeui.provider.EaseUserProfileProvider;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseDateUtils;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.xunda.mo.R;
import com.xunda.mo.main.constant.MyConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MyEaseConversationDelegate extends EaseDefaultConversationDelegate {

    public MyEaseConversationDelegate(EaseConversationSetStyle setModel) {
        super(setModel);
    }

//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, String tag) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ease_item_row_chat_history, parent, false);
//        return new ViewHolder(view, setModel);
//    }

    @Override
    public boolean isForViewType(EaseConversationInfo item, int position) {
        return item != null && item.getInfo() instanceof EMConversation;
    }

    @Override
    protected void onBindConViewHolder(ViewHolder holder, int position, EaseConversationInfo bean) {
        EMConversation item = (EMConversation) bean.getInfo();
        Context context = holder.itemView.getContext();
        String username = item.conversationId();
        holder.listIteaseLayout.setBackground(!TextUtils.isEmpty(item.getExtField())
                ? ContextCompat.getDrawable(context, R.drawable.ease_conversation_top_bg)
                : null);
        holder.mentioned.setVisibility(View.GONE);
        int defaultAvatar = 0;
        String showName = "";
        String HeadAvatar = "";
        String HeadName = "";

        Log.e("group", item.conversationId());

        holder.name.setTextColor(ContextCompat.getColor(context, R.color.blacktitle));
        if (item.getType() == EMConversation.EMConversationType.GroupChat) {
            if (EaseAtMessageHelper.get().hasAtMeMsg(username)) {
                holder.mentioned.setText(R.string.were_mentioned);
                holder.mentioned.setVisibility(View.VISIBLE);
            }
//            defaultAvatar = R.drawable.ease_group_icon;
            defaultAvatar = R.drawable.mo_icon;
            EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
            showName = group != null ? group.getGroupName() : username;

            if (showName.equals(MyConstant.MO_NAME)) {
                HeadName = showName;
                defaultAvatar = R.mipmap.adress_head_service;
                holder.name.setTextColor(ContextCompat.getColor(context, R.color.blue));
                Glide.with(context).load(HeadAvatar).placeholder(defaultAvatar).into(holder.avatar);
            } else {

                if (item.getAllMsgCount() != 0) {
                    item.getLastMessage().ext();
                    HeadName = item.getLastMessage().getStringAttribute(MyConstant.GROUP_NAME, "");
                    HeadAvatar = item.getLastMessage().getStringAttribute(MyConstant.GROUP_HEAD, "");
                    Glide.with(context).load(HeadAvatar).placeholder(R.drawable.mo_icon).into(holder.avatar);
                }



//                item.getExtField();
//                String groupHead =  group.getExtension();
//                Glide.with(context).load(groupHead).placeholder(R.drawable.mo_icon).into(holder.avatar);

//                new Thread(() -> {
//                    try {
//                        EMGroup myGroup =   EMClient.getInstance().groupManager().getGroupFromServer(username, true);
//                        String groupHead = myGroup.getExtension();
//                        Glide.with(context).load(groupHead).placeholder(R.drawable.mo_icon).into(holder.avatar);
//                    } catch (HyphenateException e) {
//                        e.printStackTrace();
//                    }
//                }).start();

                //根据群组ID从服务器获取群组基本信息
//                EMClient.getInstance().groupManager().asyncGetGroupFromServer(username, new EMValueCallBack<EMGroup>() {
//                    @Override
//                    public void onSuccess(EMGroup value) {
//                        String groupHead = value.getExtension();
//                        Glide.with(context).load(groupHead).placeholder(R.drawable.mo_icon).into(holder.avatar);
//                    }
//
//                    @Override
//                    public void onError(int error, String errorMsg) {
//                    }
//                });


            }

        } else if (item.getType() == EMConversation.EMConversationType.ChatRoom) {
            defaultAvatar = R.drawable.ease_chat_room_icon;
            EMChatRoom chatRoom = EMClient.getInstance().chatroomManager().getChatRoom(username);
            showName = chatRoom != null && !TextUtils.isEmpty(chatRoom.getName()) ? chatRoom.getName() : username;
            Glide.with(context).load(HeadAvatar).placeholder(defaultAvatar).into(holder.avatar);

        } else if (item.getType() == EMConversation.EMConversationType.Chat) {
            defaultAvatar = R.drawable.mo_icon;
            showName = username;
            if (username.equals(MyConstant.ADMIN)) {
                HeadName = "群通知";
                defaultAvatar = R.mipmap.group_notification;
                holder.name.setTextColor(ContextCompat.getColor(context, R.color.blue));
            } else {
                if (item.getAllMsgCount() != 0) {
//                    HeadName = item.getLastMessage().getStringAttribute(MyConstant.SEND_NAME, "");
//                    HeadAvatar = item.getLastMessage().getStringAttribute(MyConstant.SEND_HEAD, "");
                }
            }
            Glide.with(context).load(HeadAvatar).placeholder(defaultAvatar).into(holder.avatar);
        }
//        holder.avatar.setImageResource(defaultAvatar);
//        holder.name.setText(showName);
//        Glide.with(context).load(HeadAvatar).placeholder(defaultAvatar).into(holder.avatar);
        holder.name.setText(HeadName);

        EaseConversationInfoProvider infoProvider = EaseIM.getInstance().getConversationInfoProvider();
        if (infoProvider != null) {
            Drawable avatarResource = infoProvider.getDefaultTypeAvatar(item.getType().name());
            if (avatarResource != null) {
                Glide.with(holder.mContext).load(avatarResource).placeholder(defaultAvatar).into(holder.avatar);
            }
        }

        //单个会话再加载一次头像名字
        if (item.getType() == EMConversation.EMConversationType.Chat) {
            EaseUserProfileProvider userProvider = EaseIM.getInstance().getUserProvider();
            if (userProvider != null) {
                EaseUser user = userProvider.getUser(username);
                if (user != null) {
                    try {
                        String selectInfoExt = user.getExt();
                        if (!TextUtils.isEmpty(selectInfoExt)) {
                            JSONObject JsonObject = new JSONObject(selectInfoExt);//用户资料扩展属性
                            String name = TextUtils.isEmpty(JsonObject.getString("remarkName")) ? user.getNickname() : JsonObject.getString("remarkName");
                            if (!TextUtils.isEmpty(name)) {
                                holder.name.setText(name);
                            }
                        }
                    } catch (
                            JSONException e) {
                        e.printStackTrace();
                    }
                    if (!TextUtils.isEmpty(user.getAvatar())) {
                        Drawable drawable = holder.avatar.getDrawable();
                        Glide.with(holder.mContext).load(user.getAvatar()).into(holder.avatar);
                    }
                }
            }
        }

        if (!setModel.isHideUnreadDot()) {
            showUnreadNum(holder, item.getUnreadMsgCount());
        }

        if (item.getAllMsgCount() != 0) {
            EMMessage lastMessage = item.getLastMessage();
            holder.message.setText(EaseSmileUtils.getSmiledText(context, EaseCommonUtils.getMessageDigest(lastMessage, context)));

            setContent(holder.message, item);

            holder.time.setText(EaseDateUtils.getTimestampString(context, new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                holder.mMsgState.setVisibility(View.VISIBLE);
            } else {
                holder.mMsgState.setVisibility(View.GONE);
            }
        }

        if (holder.mentioned.getVisibility() != View.VISIBLE) {
            String unSendMsg = EasePreferenceManager.getInstance().getUnSendMsgInfo(username);
            if (!TextUtils.isEmpty(unSendMsg)) {
                holder.mentioned.setText(R.string.were_not_send_msg);
                holder.message.setText(unSendMsg);
                holder.mentioned.setVisibility(View.VISIBLE);
            }
        }
    }

    //会话列表最后一条消息内容
    private void setContent(TextView contentView, EMConversation item) {
        EMMessage lastMessage = item.getLastMessage();
        if (TextUtils.equals(lastMessage.getFrom(), MyConstant.ADMIN)) {
            String content = lastMessage.getStringAttribute("content", "");
            contentView.setText(content);
        }

//        if (lastMessage.getBooleanAttribute(DemoConstant.MESSAGE_TYPE_RECALL, false)) {
        String lastMessType = lastMessage.getStringAttribute(MyConstant.MESSAGE_TYPE, "");
        String lastFireType = lastMessage.getStringAttribute(MyConstant.FIRE_TYPE, "");
        boolean isRecall = lastMessType.equals(MyConstant.Message_Recall);
        if (isRecall) {
            String messageStr = "";
            if (lastMessage.getChatType() == EMMessage.ChatType.Chat) {
                if (lastMessage.direct() == EMMessage.Direct.SEND) {
                    messageStr = "您撤回一条消息";
                } else {
                    messageStr = "对方撤回一条消息";
                }
            } else if (lastMessage.getChatType() == EMMessage.ChatType.GroupChat) {
                if (lastMessage.direct() == EMMessage.Direct.SEND) {
                    messageStr = "您撤回一条消息";
                } else {
                    String sendName = lastMessage.getStringAttribute(MyConstant.SEND_NAME, "");
                    messageStr = String.format(contentView.getContext().getString(R.string.msg_recall_by_user), sendName);
                }
            }
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastFireType, "1")) {
            String messageStr = "[Mo 语]";
            contentView.setText(messageStr);
        }

    }


}

