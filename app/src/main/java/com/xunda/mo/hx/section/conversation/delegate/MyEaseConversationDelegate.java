package com.xunda.mo.hx.section.conversation.delegate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.utils.GsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MyEaseConversationDelegate extends EaseDefaultConversationDelegate {

    public MyEaseConversationDelegate(EaseConversationSetStyle setModel) {
        super(setModel);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, String tag) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ease_item_row_chat_history, parent, false);
        return new ViewHolder(view, setModel);
    }


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
//        String mesType = item.getLastMessage().getStringAttribute(MyConstant.MESSAGE_TYPE, "");
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
            }
//            else if (mesType.equals(MyConstant.MESSAGE_TYPE_SYSTEM_NOTICE)) {
//                defaultAvatar = R.mipmap.chat_notice;
//                holder.name.setTextColor(ContextCompat.getColor(context, R.color.blue));
//                HeadName = item.getLastMessage().getStringAttribute(MyConstant.GROUP_NAME, "");
//                Glide.with(context).load(HeadAvatar).placeholder(defaultAvatar).into(holder.avatar);
//            }
            else {
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
                    HeadName = item.getLastMessage().getStringAttribute(MyConstant.SEND_NAME, "");
                    HeadAvatar = item.getLastMessage().getStringAttribute(MyConstant.SEND_HEAD, "");
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
                            String name = TextUtils.isEmpty(JsonObject.getString(MyConstant.REMARK_NAME)) ? user.getNickname() : JsonObject.getString(MyConstant.REMARK_NAME);
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
            Log.e("EaseConversationDelegate", "拓展消息" + GsonUtil.getInstance().toJson(lastMessage.ext()));
            holder.message.setText(EaseSmileUtils.getSmiledText(context, EaseCommonUtils.getMessageDigest(lastMessage, context)));

            setContent(holder.mentioned, holder.message, item);

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

    private void setContent(TextView mentioned, TextView contentView, EMConversation item) {
        MyInfo myInfo = new MyInfo(contentView.getContext());
        EMMessage lastMessage = item.getLastMessage();
        if (TextUtils.equals(lastMessage.getFrom(), MyConstant.ADMIN)) {
            String content = lastMessage.getStringAttribute("content", "");
            contentView.setText(content);
        }
//        if (lastMessage.getBooleanAttribute(DemoConstant.MESSAGE_TYPE_RECALL, false)) {
        String lastMessType = lastMessage.getStringAttribute(MyConstant.MESSAGE_TYPE, "");
        String lastFireType = lastMessage.getStringAttribute(MyConstant.FIRE_TYPE, "");
        boolean isRecall = lastMessType.equals(MyConstant.Message_Recall);
        boolean isHxRecall = lastMessType.equals(DemoConstant.MESSAGE_TYPE_RECALL);
        String sendName = lastMessage.getStringAttribute(MyConstant.SEND_NAME, "");
        String messageStr = "";
        if (TextUtils.equals(lastMessType,MyConstant.MESSAGE_TYPE_CREATE_GROUP)){
            messageStr = String.format("'%1$s'创建了群聊",sendName);
            contentView.setText(messageStr);
        }else if (isRecall || isHxRecall) {
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
                    messageStr = String.format(contentView.getContext().getString(R.string.msg_recall_by_user), sendName);
                }
            }
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastFireType, "1")) {
            messageStr = "[Mo 语]";
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TYPE_SCREENSHORTS) || TextUtils.equals(lastMessType, MyConstant.MESSAGE_TYPE_GROUP_SCREENSHORTS)) {
            if (lastMessage.getChatType() == EMMessage.ChatType.Chat) {
                if (lastMessage.direct() == EMMessage.Direct.RECEIVE) {
                    messageStr = "对方进行了截屏";
                }
            } else if (lastMessage.getChatType() == EMMessage.ChatType.GroupChat) {
                if (lastMessage.direct() == EMMessage.Direct.RECEIVE) {
                    messageStr = String.format("'%1$s'进行了截屏", sendName);
                }
            }
            contentView.setText(messageStr);
        } else if (item.getLastMessage().getType() == EMMessage.Type.LOCATION) {
            messageStr = "[位置]";
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TXT_TYPE_AT_GROUP)) {
            mentioned.setTextColor(mentioned.getContext().getColor(R.color.yellowfive));
            String atID = lastMessage.getStringAttribute(MyConstant.AT_ID, "");
            String at_Name = lastMessage.getStringAttribute(MyConstant.AT_NAME, "");
            if (!TextUtils.isEmpty(at_Name)) {
                String name = at_Name.replace(",", "@");
                int atDex = name.lastIndexOf("@");
                int lastDex = name.length();
                StringBuilder stringBuilder = new StringBuilder(name);
                stringBuilder.replace(atDex, lastDex, "");
                String lastName = "@" + stringBuilder.toString();

                if (EaseAtMessageHelper.get().hasAtMeMsg(myInfo.getUserInfo().getHxUserName())) {

                }
                if (atID.contains("All") && lastMessage.isUnread()) {
                    mentioned.setVisibility(View.VISIBLE);
                } else if (atID.contains(myInfo.getUserInfo().getHxUserName()) && lastMessage.isUnread()) {
                    mentioned.setVisibility(View.VISIBLE);
                }
                contentView.setText(lastName);
            }
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TYPE_USERCARD)) {
            messageStr = "[名片]";
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.UPDATE_GROUP_NAME)) {
            if (lastMessage.direct() == EMMessage.Direct.SEND) {
                messageStr = "您修改了群聊名称";
            } else {
                messageStr = "群主修改了群聊名称";
            }
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TYPE_DELETUSER)) {
            String name = lastMessage.getStringAttribute(MyConstant.SEND_NAME, "");
            String invitationStr = lastMessage.getStringAttribute(MyConstant.USER_NAME, "");
            messageStr = String.format("'%1$s'将 '%2$s'移出群聊", name, invitationStr);
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TYPE_ADDUSER)) {
            String name = lastMessage.getStringAttribute(MyConstant.SEND_NAME, "");
            String invitationStr = lastMessage.getStringAttribute(MyConstant.USER_NAME, "");
            messageStr = String.format("'%1$s'邀请 '%2$s'加入群聊", name, invitationStr);
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TYPE_ADDADMIN)) {
            String invitationStr = lastMessage.getStringAttribute(MyConstant.USER_NAME, "");
            messageStr = String.format("'%1$s'被添加为管理员", invitationStr);
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TYPE_DELETADMIN)) {
            String invitationStr = lastMessage.getStringAttribute(MyConstant.USER_NAME, "");
            messageStr = String.format("'%1$s'被取消了管理员", invitationStr);
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TYPE_ANONYMOUS_ON)) {
            messageStr = "群主开启了匿名聊天";
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TYPE_ANONYMOUS_OFF)) {
            messageStr = "群主关闭了匿名聊天";
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TYPE_MUTE_ON)) {
            messageStr = "群主开启了群员禁言";
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TYPE_MUTE_OFF)) {
            messageStr = "群主关闭了群员禁言";
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TYPE_PROTECT_ON)) {
            messageStr = "群主开启了群员保护";
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TYPE_PROTECT_OFF)) {
            messageStr = "群主关闭了群员保护";
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TYPE_UPDATE_MASTER)) {
            String userName = lastMessage.getStringAttribute(MyConstant.USER_NAME, "");
            if (lastMessage.direct() == EMMessage.Direct.SEND) {
                messageStr = String.format("您将群主转让给'%1$s'", userName);
            } else {
                messageStr = String.format("群主将群主转让给'%1$s'", userName);
            }
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TYPE_PUSH_ON)) {
            String invitationStr = lastMessage.getStringAttribute(MyConstant.SEND_NAME, "");
            messageStr = String.format("'%1$s'开启了成员加群/退群通知", invitationStr);
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TYPE_PUSH_OFF)) {
            String invitationStr = lastMessage.getStringAttribute(MyConstant.SEND_NAME, "");
            messageStr = String.format("'%1$s'关闭了成员加群/退群通知", invitationStr);
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_GROUP_LEAVE)) {
            String invitationStr = lastMessage.getStringAttribute(MyConstant.SEND_NAME, "");
            messageStr = String.format("'%1$s'离开了群组", invitationStr);
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.GROUP_UPDATE_GROUPDES)) {
            if (lastMessage.direct() == EMMessage.Direct.SEND) {
                messageStr = "您修改了群简介";
            } else {
                messageStr = "群主修改了群简介";
            }
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TYPE_DOUBLE_RECALL)) {
            if (lastMessage.direct() == EMMessage.Direct.SEND) {
                messageStr = "您撤回了所有消息";
            } else {
                messageStr = "对方撤回了所有消息";
            }
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TYPE_GROUP_DOUBLE_RECALL)) {
            if (lastMessage.direct() == EMMessage.Direct.SEND) {
                messageStr = "您撤回了所有消息";
            } else {
                messageStr = String.format("'%1$s'撤回了所有消息", sendName);
            }
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TYPE_APPLY)) {
            String toName = lastMessage.getStringAttribute(MyConstant.TO_NAME, "");
            if (lastMessage.direct() == EMMessage.Direct.SEND) {
                messageStr = String.format("您已同意添加'%1$s'，现在可以开始聊天了", toName);
            } else {
                messageStr = String.format("'%1$s'已同意添加好友，现在可以开始聊天了", sendName);
            }
            contentView.setText(messageStr);
        } else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_GROUP_Message)) {
            messageStr = lastMessage.getStringAttribute(MyConstant.CONTENT, "");
            contentView.setText(messageStr);
        }


    }


}

