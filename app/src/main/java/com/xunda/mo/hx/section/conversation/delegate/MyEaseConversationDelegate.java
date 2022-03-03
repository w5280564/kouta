package com.xunda.mo.hx.section.conversation.delegate;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easecallkit.utils.EaseMsgUtils;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.manager.EasePreferenceManager;
import com.hyphenate.easeui.modules.conversation.delegate.EaseDefaultConversationDelegate;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationSetStyle;
import com.hyphenate.easeui.provider.EaseUserProfileProvider;
import com.hyphenate.easeui.utils.EaseDateUtils;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.GsonUtil;
import com.hyphenate.easeui.utils.MyEaseCommonUtils;
import com.hyphenate.easeui.utils.StringUtil;
import com.xunda.mo.R;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.info.MyInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MyEaseConversationDelegate extends EaseDefaultConversationDelegate {
    private Context context;
    private MyInfo myInfo;

    public MyEaseConversationDelegate(EaseConversationSetStyle setModel, Context context) {
        super(setModel);
        this.context = context;
        myInfo = new MyInfo(context);
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

    //是否是客服会话
    private boolean isMOCustomer(EMMessage conMsg) {
        if (conMsg == null) {
            return false;
        }
        if (conMsg.getType() == EMMessage.Type.CUSTOM) {
            EMCustomMessageBody messageBody = (EMCustomMessageBody) conMsg.getBody();
            String event = messageBody.event();//自定义消息的event
            if (event.equals("custom") || event.equals("MOCustomer")) {
                return true;
            }
        } else if (conMsg.getType() == EMMessage.Type.TXT) {
            String txt_message_type = conMsg.getStringAttribute(MyConstant.MESSAGE_TYPE, "");//TXT消息的类型
            if (txt_message_type.equals("MOCustomer") || txt_message_type.equals("customerMessageType") || txt_message_type.equals("custom")) {
                return true;
            }
        }


        return false;
    }


    @Override
    protected void onBindConViewHolder(ViewHolder holder, int position, EaseConversationInfo bean) {
        EMConversation item = (EMConversation) bean.getInfo();
        String username = item.conversationId();
        Log.e("EaseConversationDelegate", "会话拓展》》" + item.getExtField());
        holder.listIteaseLayout.setBackground(MyEaseCommonUtils.isTimestamp(item.getExtField()) ? ContextCompat.getDrawable(context, R.drawable.ease_conversation_top_bg) : null);
        holder.mentioned.setVisibility(View.GONE);
        holder.tv_official.setVisibility(View.GONE);
        holder.tv_vip.setVisibility(View.GONE);
        int defaultAvatar = R.mipmap.img_pic_none;
        String HeadAvatar = "";
        String HeadName = "";

        holder.name.setTextColor(ContextCompat.getColor(context, R.color.blacktitle));


        Log.e("EaseConversationDelegate", "会话类型》》" + item.getType());
        if (item.getType() == EMConversation.EMConversationType.GroupChat) {
            if (item.getAllMsgCount() != 0) {
                if (isMOCustomer(item.getLastMessage())) {
                    HeadName = "MO客服";
                    holder.tv_official.setVisibility(View.VISIBLE);
                    holder.name.setTextColor(ContextCompat.getColor(context, R.color.blue));
                    holder.avatar.setImageResource(R.mipmap.adress_head_service);
                    holder.name.setText(HeadName);
                } else {
                    holder.tv_official.setVisibility(View.GONE);
                    HeadName = item.getLastMessage().getStringAttribute(MyConstant.GROUP_NAME, "");
                    HeadAvatar = item.getLastMessage().getStringAttribute(MyConstant.GROUP_HEAD, "");

                    if (StringUtil.isBlank(HeadAvatar) || StringUtil.isBlank(HeadName)) {
                        String extMessage = item.getExtField();
                        if (!TextUtils.isEmpty(extMessage)) {
                            JSONObject JsonObject = null;
                            try {
                                JsonObject = new JSONObject(extMessage);
                                boolean isInsertGroupOrFriendInfo = JsonObject.getBoolean("isInsertGroupOrFriendInfo");
                                if (isInsertGroupOrFriendInfo) {
                                    HeadAvatar = JsonObject.getString("showImg");
                                    HeadName = JsonObject.getString("showName");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    holder.name.setText(HeadName);
                    Glide.with(context).load(HeadAvatar).placeholder(defaultAvatar).error(defaultAvatar).into(holder.avatar);
                }

            }
        } else if (item.getType() == EMConversation.EMConversationType.Chat) {
            if (item.getAllMsgCount() != 0) {
                EMMessage lastMessage = item.getLastMessage();
                String txt_message_type = lastMessage.getStringAttribute(MyConstant.MESSAGE_TYPE, "");
                if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TXT_TYPE_GROUP_NOTIFY)) {
                    HeadName = "群通知";
                    holder.tv_official.setVisibility(View.VISIBLE);
                    holder.avatar.setImageResource(R.mipmap.group_notification);
                    holder.name.setTextColor(ContextCompat.getColor(context, R.color.app_main_color_blue));
                    holder.name.setText(HeadName);
                } else {
                    int vipType;
                    holder.tv_official.setVisibility(View.GONE);
                    boolean isSender = myInfo.getUserInfo().getHxUserName().equals(lastMessage.getFrom());
                    EaseUserProfileProvider userProvider = EaseIM.getInstance().getUserProvider();
                    if (isSender) {
                        String header_url_message = lastMessage.getStringAttribute(MyConstant.TO_HEAD, "");
                        EaseUserUtils.setUserAvatarAndSendHeaderUrl(context, lastMessage.getTo(), header_url_message, holder.avatar);
                        HeadName = lastMessage.getStringAttribute(MyConstant.TO_NAME, "");

                        if (StringUtil.isBlank(HeadName)) {
                            String extMessage = item.getExtField();
                            if (!TextUtils.isEmpty(extMessage)) {
                                JSONObject JsonObject = null;
                                try {
                                    JsonObject = new JSONObject(extMessage);
                                    boolean isInsertGroupOrFriendInfo = JsonObject.getBoolean("isInsertGroupOrFriendInfo");
                                    if (isInsertGroupOrFriendInfo) {
                                        HeadName = JsonObject.getString("showName");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (StringUtil.isBlank(HeadName)) {
                            EaseUser user = userProvider.getUser(username);
                            if (user != null) {
                                try {
                                    String selectInfoExt = user.getExt();
                                    if (!TextUtils.isEmpty(selectInfoExt)) {
                                        JSONObject JsonObject = new JSONObject(selectInfoExt);//用户资料扩展属性
                                        HeadName = TextUtils.isEmpty(JsonObject.getString("remarkName")) ? user.getNickname() : JsonObject.getString("remarkName");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        vipType = lastMessage.getIntAttribute(MyConstant.TO_VIP, 3);;//3表示没有获取到这个扩展字段
                        if (vipType==3) {//没有扩展字段 再从会话扩展取
                            String extMessage = item.getExtField();
                            if (!TextUtils.isEmpty(extMessage)) {
                                JSONObject JsonObject = null;
                                try {
                                    JsonObject = new JSONObject(extMessage);
                                    boolean isInsertGroupOrFriendInfo = JsonObject.getBoolean("isInsertGroupOrFriendInfo");
                                    if (isInsertGroupOrFriendInfo) {
                                        vipType = JsonObject.getInt("vipType");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {//接收方
                        String header_url_message = lastMessage.getStringAttribute(MyConstant.SEND_HEAD, "");
                        EaseUserUtils.setUserAvatarAndSendHeaderUrl(context, lastMessage.getFrom(), header_url_message, holder.avatar);
                        HeadName = lastMessage.getStringAttribute(MyConstant.SEND_NAME, "");

                        if (StringUtil.isBlank(HeadName)) {
                            String extMessage = item.getExtField();
                            if (!TextUtils.isEmpty(extMessage)) {
                                JSONObject JsonObject = null;
                                try {
                                    JsonObject = new JSONObject(extMessage);
                                    boolean isInsertGroupOrFriendInfo = JsonObject.getBoolean("isInsertGroupOrFriendInfo");
                                    if (isInsertGroupOrFriendInfo) {
                                        HeadName = JsonObject.getString("showName");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (StringUtil.isBlank(HeadName)) {
                            EaseUser user = userProvider.getUser(username);
                            if (user != null) {
                                try {
                                    String selectInfoExt = user.getExt();
                                    if (!TextUtils.isEmpty(selectInfoExt)) {
                                        JSONObject JsonObject = new JSONObject(selectInfoExt);//用户资料扩展属性
                                        HeadName = TextUtils.isEmpty(JsonObject.getString("remarkName")) ? user.getNickname() : JsonObject.getString("remarkName");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        vipType = lastMessage.getIntAttribute(MyConstant.SEND_VIP, 3);//3表示没有获取到这个扩展字段
                        if (vipType==3) {//没有扩展字段 再从会话扩展取
                            String extMessage = item.getExtField();
                            if (!TextUtils.isEmpty(extMessage)) {
                                JSONObject JsonObject = null;
                                try {
                                    JsonObject = new JSONObject(extMessage);
                                    boolean isInsertGroupOrFriendInfo = JsonObject.getBoolean("isInsertGroupOrFriendInfo");
                                    if (isInsertGroupOrFriendInfo) {
                                        vipType = JsonObject.getInt("vipType");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    holder.name.setText(StringUtil.getStringValue(HeadName));
                    if (vipType==1){
                        holder.name.setTextColor(ContextCompat.getColor(context,R.color.yellowfive));
                        holder.tv_vip.setVisibility(View.VISIBLE);
                    }
                }
            }
        }


        if (!setModel.isHideUnreadDot()) {
            showUnreadNum(holder, item.getUnreadMsgCount());
        }

        if (item.getAllMsgCount() != 0) {
            EMMessage lastMessage = item.getLastMessage();
            boolean isSender = myInfo.getUserInfo().getHxUserName().equals(lastMessage.getFrom());
            Log.e("EaseConversationDelegate", "拓展消息" + GsonUtil.getInstance().toJson(lastMessage.ext()));
            Log.e("EaseConversationDelegate", "lastMessage的messageType是" + lastMessage.getType());
            if (lastMessage.getType() == EMMessage.Type.CUSTOM) {//自定义消息
                EMCustomMessageBody messageBody = (EMCustomMessageBody) lastMessage.getBody();
                String event = messageBody.event();//自定义消息的event
                Log.e("EaseConversationDelegate", "自定义消息的event是" + event);
                if (event.equals(MyConstant.MESSAGE_TYPE_USERCARD)) {
                    holder.message.setText("[名片]");
                } else if (event.equals(MyConstant.MO_CUSTOMER)) {
                    String content = item.getLastMessage().getStringAttribute("content", "");
                    holder.message.setText(content);
                } else if (event.equals("custom")) {
                    String content = item.getLastMessage().getStringAttribute("content", "");
                    holder.message.setText(content);
                } else {
                    String content = item.getLastMessage().getStringAttribute("content", "");
                    holder.message.setText(content);
                }
            } else if (lastMessage.getType() == EMMessage.Type.TXT) {//TXT消息
                String txt_message_type = lastMessage.getStringAttribute(MyConstant.MESSAGE_TYPE, "");//TXT消息的类型
                String send_name = lastMessage.getStringAttribute(MyConstant.SEND_NAME, "");
                if (!StringUtil.isBlank(txt_message_type)) {
                    String txt_content = "";
                    if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TYPE_CREATE_GROUP)) {
                        String invitationStr = lastMessage.getStringAttribute(MyConstant.USER_NAME, "");
                        if (!isSender) {
                            txt_content = String.format("'%1$s'创建了群，并邀请 '%2$s'加入群", send_name, invitationStr);
                        } else {
                            txt_content = String.format("我创建了群，并邀请 '%s'加入群", invitationStr);
                        }
                    } else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TYPE_APPLY)) {
                        String toName = lastMessage.getStringAttribute(MyConstant.TO_NAME, "");
                        if (isSender) {
                            txt_content = String.format("您已同意了%s的好友申请，现在可以开始聊天了", toName);
                        } else {
                            txt_content = String.format("%s已同意了您的好友申请，现在可以开始聊天了", send_name);
                        }
                    } else if (TextUtils.equals(txt_message_type, MyConstant.UPDATE_GROUP_NAME)) {
                        if (isSender) {
                            txt_content = "您修改了群名称";
                        } else {
                            txt_content = "群主修改了群名称";
                        }
                    } else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TYPE_DELETUSER)) {
                        String invitationStr = lastMessage.getStringAttribute(MyConstant.USER_NAME, "");
                        txt_content = String.format("'%1$s'将 '%2$s'移出群", send_name, invitationStr);
                    } else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TYPE_ADDUSER)) {
                        String invitationStr = lastMessage.getStringAttribute(MyConstant.USER_NAME, "");
                        txt_content = String.format("'%1$s'邀请 '%2$s'加入群", send_name, invitationStr);
                    } else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TYPE_ADDADMIN)) {
                        String invitationStr = lastMessage.getStringAttribute(MyConstant.USER_NAME, "");
                        txt_content = String.format("'%1$s'被添加为管理员", invitationStr);
                    } else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TYPE_DELETADMIN)) {
                        String invitationStr = lastMessage.getStringAttribute(MyConstant.USER_NAME, "");
                        txt_content = String.format("'%1$s'被取消了管理员", invitationStr);
                    } else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TYPE_ANONYMOUS_ON)) {
                        txt_content = "群开启了匿名聊天";
                    } else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TXT_TYPE_GROUP_DESTROY)) {
                        if (isSender) {
                            txt_content = "您解散了群";
                        } else {
                            txt_content = "该群已解散";
                        }
                    } else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TYPE_ANONYMOUS_OFF)) {
                        txt_content = "群关闭了匿名聊天";
                    } else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TYPE_MUTE_ON)) {
                        txt_content = "群开启了群成员禁言";
                    } else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TYPE_MUTE_OFF)) {
                        txt_content = "群关闭了群成员禁言";
                    } else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TYPE_PROTECT_ON)) {
                        txt_content = "群开启了群成员保护";
                    } else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TYPE_PROTECT_OFF)) {
                        txt_content = "群关闭了群成员保护";
                    } else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TYPE_UPDATE_MASTER)) {
                        String to_userName = lastMessage.getStringAttribute(MyConstant.USER_NAME, "");
                        if (isSender) {
                            txt_content = String.format("您将群主转让给了'%1$s'", to_userName);
                        } else {
                            txt_content = String.format("'%1$s'将群主转让给了'%2$s'", send_name, to_userName);
                        }
                    } else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TYPE_PUSH_ON)) {
                        txt_content = String.format("'%1$s'开启了成员加群/退群通知", send_name);
                    } else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TYPE_PUSH_OFF)) {
                        txt_content = String.format("'%1$s'关闭了成员加群/退群通知", send_name);
                    } else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_GROUP_LEAVE)) {
                        txt_content = String.format("'%1$s'离开了群", send_name);
                    } else if (TextUtils.equals(txt_message_type, MyConstant.GROUP_UPDATE_GROUPDES)) {
                        if (isSender) {
                            txt_content = "您修改了群简介";
                        } else {
                            txt_content = "群主修改了群简介";
                        }
                    } else if (TextUtils.equals(txt_message_type, MyConstant.MO_CUSTOMER)) {
                        EMTextMessageBody messageBody = (EMTextMessageBody) lastMessage.getBody();
                        txt_content = messageBody.getMessage();
                    } else if (TextUtils.equals(txt_message_type, MyConstant.Message_Recall)) {
                        if (lastMessage.direct() == EMMessage.Direct.SEND) {
                            txt_content = String.format(context.getString(R.string.msg_recall_by_self));
                        } else {
                            if (item.getType() == EMConversation.EMConversationType.GroupChat) {
                                txt_content = String.format("'%s'撤回了一条消息", send_name);
                            } else if (item.getType() == EMConversation.EMConversationType.Chat) {
                                txt_content = String.format("%s撤回了一条消息", "对方");
                            }
                        }
                    }else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TYPE_SCREENSHORTS) || TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TYPE_GROUP_SCREENSHORTS)) {
                        if (lastMessage.getChatType() == EMMessage.ChatType.Chat) {
                            if (lastMessage.direct() == EMMessage.Direct.RECEIVE) {
                                txt_content = "对方进行了截屏";
                            }
                        } else if (lastMessage.getChatType() == EMMessage.ChatType.GroupChat) {
                            if (lastMessage.direct() == EMMessage.Direct.RECEIVE) {
                                txt_content = String.format("'%1$s'进行了截屏", send_name);
                            }
                        }
                    } else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TYPE_DOUBLE_RECALL)) {
                        if (lastMessage.direct() == EMMessage.Direct.SEND) {
                            txt_content = "您撤回了所有消息";
                        } else {
                            txt_content = "对方撤回了所有消息";
                        }
                    } else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TYPE_GROUP_DOUBLE_RECALL)) {
                        if (lastMessage.direct() == EMMessage.Direct.SEND) {
                            txt_content = "您撤回了所有消息";
                        } else {
                            txt_content = String.format("'%1$s'撤回了所有消息", send_name);
                        }
                    }else if (TextUtils.equals(txt_message_type, MyConstant.MESSAGE_TXT_TYPE_AT_GROUP)) {
                        if (StringUtil.isBlank(send_name)) {
                            EaseUser user = EaseUserUtils.getUserInfo(lastMessage.getFrom());
                            if (user != null && user.getNickname() != null) {
                                send_name = user.getNickname();
                            } else {
                                send_name = lastMessage.getFrom();
                            }
                        }
                        String at_ids = lastMessage.getStringAttribute(MyConstant.AT_ID, "");
                        EMTextMessageBody messageBody = (EMTextMessageBody) lastMessage.getBody();
                        if ((at_ids.contains(myInfo.getUserInfo().getHxUserName()) || at_ids.contains("all")) && lastMessage.isUnread()) {
                            holder.mentioned.setText(R.string.were_mentioned);
                            holder.mentioned.setVisibility(View.VISIBLE);
                        }
                        txt_content = send_name + "：" + messageBody.getMessage();
                    } else if (TextUtils.equals(txt_message_type, "group")) {
                        EMTextMessageBody messageBody = (EMTextMessageBody) lastMessage.getBody();
                        if (isSender) {
                            txt_content = messageBody.getMessage();
                        } else {
                            if (StringUtil.isBlank(send_name)) {
                                EaseUser user = EaseUserUtils.getUserInfo(lastMessage.getFrom());
                                if (user != null && user.getNickname() != null) {
                                    send_name = user.getNickname();
                                } else {
                                    send_name = lastMessage.getFrom();
                                }
                            }
                            txt_content = send_name + "：" + messageBody.getMessage();
                        }
                    } else {
                        txt_content = lastMessage.getStringAttribute("content", "");
                        if (StringUtil.isBlank(txt_content)) {
                            EMTextMessageBody messageBody = (EMTextMessageBody) lastMessage.getBody();
                            if (messageBody != null) {
                                txt_content = messageBody.getMessage();
                            }

                        }
                    }
                    CharSequence final_CharSequence = txt_content;
                    holder.message.setText(EaseSmileUtils.getSmiledText(context, final_CharSequence));
                } else {//音视频通话
                    boolean isRtcCall = lastMessage.getStringAttribute(EaseMsgUtils.CALL_MSG_TYPE, "").equals(EaseMsgUtils.CALL_MSG_INFO) ? true : false;
                    if (isRtcCall) {
                        boolean isVideoCall = lastMessage.getIntAttribute(EaseMsgUtils.CALL_TYPE, 0) == EaseCallType.SINGLE_VIDEO_CALL.code ? true : false;
                        holder.message.setText(isVideoCall ? "[视频通话]" : "[语音通话]");
                    } else {
                        setDefaultMessage(holder, context, lastMessage);
                    }
                }
            } else if (lastMessage.getType() == EMMessage.Type.IMAGE) {
                setOtherType(holder, item, lastMessage, isSender, "[图片]");
            } else if (lastMessage.getType() == EMMessage.Type.VIDEO) {
                setOtherType(holder, item, lastMessage, isSender, "[视频]");
            } else if (lastMessage.getType() == EMMessage.Type.LOCATION) {
                setOtherType(holder, item, lastMessage, isSender, "[位置]");
            } else if (lastMessage.getType() == EMMessage.Type.VOICE) {
                setOtherType(holder, item, lastMessage, isSender, "[语音]");
            } else if (lastMessage.getType() == EMMessage.Type.FILE) {
                setOtherType(holder, item, lastMessage, isSender, "[文件]");
            }

            boolean lastFireType = lastMessage.getBooleanAttribute(MyConstant.FIRE_TYPE, false);
            String messageStr = "";
            if (lastFireType) {
                messageStr = "[Mo 语]";
                holder.message.setText(messageStr);
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




    private void setDefaultMessage(ViewHolder holder, Context context, EMMessage lastMessage) {
        String txt_content_default = lastMessage.getStringAttribute("content", "");
        if (StringUtil.isBlank(txt_content_default)) {
            EMTextMessageBody messageBody = (EMTextMessageBody) lastMessage.getBody();
            txt_content_default = messageBody.getMessage();
        }
        CharSequence final_CharSequence = txt_content_default;
        holder.message.setText(EaseSmileUtils.getSmiledText(context, final_CharSequence));
    }


    private void setOtherType(ViewHolder holder, EMConversation item, EMMessage lastMessage, boolean isSender, String s) {
        if (item.getType() == EMConversation.EMConversationType.GroupChat) {
            if (isSender) {
                holder.message.setText(s);
            } else {
                String send_name = lastMessage.getStringAttribute(MyConstant.SEND_NAME, "");
                if (StringUtil.isBlank(send_name)) {
                    EaseUser user = EaseUserUtils.getUserInfo(lastMessage.getFrom());
                    if (user != null && user.getNickname() != null) {
                        send_name = user.getNickname();
                    } else {
                        send_name = lastMessage.getFrom();
                    }
                }
                holder.message.setText(send_name + "：" + s);
            }
        } else {
            holder.message.setText(s);
        }
    }
}


//
// else if (TextUtils.equals(lastMessType, MyConstant.MESSAGE_TXT_TYPE_AT_GROUP)) {
//         mentioned.setTextColor(mentioned.getContext().getColor(R.color.yellowfive));
//         String atID = lastMessage.getStringAttribute(MyConstant.AT_ID, "");
//         String at_Name = lastMessage.getStringAttribute(MyConstant.AT_NAME, "");
//         if (!TextUtils.isEmpty(at_Name)) {
//         String name = at_Name.replace(",", "@");
//         int atDex = name.lastIndexOf("@");
//         int lastDex = name.length();
//         StringBuilder stringBuilder = new StringBuilder(name);
//         stringBuilder.replace(atDex, lastDex, "");
//         String lastName = "@" + stringBuilder.toString();
//
//         if (EaseAtMessageHelper.get().hasAtMeMsg(myInfo.getUserInfo().getHxUserName())) {
//
//         }
//         if (atID.contains("All") && lastMessage.isUnread()) {
//         mentioned.setVisibility(View.VISIBLE);
//         } else if (atID.contains(myInfo.getUserInfo().getHxUserName()) && lastMessage.isUnread()) {
//         mentioned.setVisibility(View.VISIBLE);
//         }
//         contentView.setText(lastName);
//         }
//         }