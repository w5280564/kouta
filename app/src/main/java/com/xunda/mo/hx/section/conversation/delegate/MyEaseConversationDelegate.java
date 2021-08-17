package com.xunda.mo.hx.section.conversation.delegate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

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
        String showName = null;
        String HeadAvatar = "";
        String HeadName = "";


//            String ext = item.getLastMessage().getStringAttribute(MyConstant.EXT);
//            JSONObject jsonObject = new JSONObject(ext);
        holder.name.setTextColor(ContextCompat.getColor(context, R.color.blacktitle));

        if (item.getType() == EMConversation.EMConversationType.GroupChat) {
            if (EaseAtMessageHelper.get().hasAtMeMsg(username)) {
                holder.mentioned.setText(R.string.were_mentioned);
                holder.mentioned.setVisibility(View.VISIBLE);
            }
            defaultAvatar = R.drawable.ease_group_icon;
            EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
            showName = group != null ? group.getGroupName() : username;
            if (item.getAllMsgCount() != 0) {
                item.conversationId();
                HeadName = item.getLastMessage().getStringAttribute(MyConstant.GROUP_NAME, "");
                HeadAvatar = item.getLastMessage().getStringAttribute(MyConstant.GROUP_HEAD, "");
            }

        } else if (item.getType() == EMConversation.EMConversationType.ChatRoom) {
            defaultAvatar = R.drawable.ease_chat_room_icon;
            EMChatRoom chatRoom = EMClient.getInstance().chatroomManager().getChatRoom(username);
            showName = chatRoom != null && !TextUtils.isEmpty(chatRoom.getName()) ? chatRoom.getName() : username;

        } else if (TextUtils.equals(item.getLastMessage().getFrom(), MyConstant.ADMIN)) {
            HeadName = "群通知";
            defaultAvatar = R.mipmap.group_notification;
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.blue));

        } else {
            defaultAvatar = R.drawable.mo_icon;
            showName = username;
            if (item.getAllMsgCount() != 0) {
                HeadName = item.getLastMessage().getStringAttribute(MyConstant.SEND_NAME, "");
                HeadAvatar = item.getLastMessage().getStringAttribute(MyConstant.SEND_HEAD, "");

            }
        }


//        holder.avatar.setImageResource(defaultAvatar);
//        holder.name.setText(showName);

        Glide.with(context).load(HeadAvatar).error(defaultAvatar).into(holder.avatar);
        holder.name.setText(HeadName);

        EaseConversationInfoProvider infoProvider = EaseIM.getInstance().getConversationInfoProvider();
        if (infoProvider != null) {
            Drawable avatarResource = infoProvider.getDefaultTypeAvatar(item.getType().name());
            if (avatarResource != null) {
                Glide.with(holder.mContext).load(avatarResource).error(defaultAvatar).into(holder.avatar);
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
                        Glide.with(holder.mContext)
                                .load(user.getAvatar())
                                .error(drawable)
                                .into(holder.avatar);
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
            if (TextUtils.equals(item.getLastMessage().getFrom(), MyConstant.ADMIN)) {
                String content = item.getLastMessage().getStringAttribute("content", "");
                holder.message.setText(content);
            }
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


}

