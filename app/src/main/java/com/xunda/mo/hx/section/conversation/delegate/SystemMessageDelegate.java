package com.xunda.mo.hx.section.conversation.delegate;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.modules.conversation.delegate.EaseSystemMsgDelegate;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationSetStyle;
import com.hyphenate.easeui.provider.EaseUserProfileProvider;
import com.hyphenate.easeui.utils.EaseDateUtils;
import com.hyphenate.easeui.utils.MyEaseCommonUtils;
import com.hyphenate.easeui.utils.StringUtil;
import com.hyphenate.exceptions.HyphenateException;
import com.xunda.mo.R;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.db.entity.InviteMessageStatus;
import com.xunda.mo.main.constant.MyConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class SystemMessageDelegate extends EaseSystemMsgDelegate{

    public SystemMessageDelegate(EaseConversationSetStyle setModel) {
        super(setModel);
    }

    @Override
    public boolean isForViewType(EaseConversationInfo item, int position) {
        return super.isForViewType(item, position);
    }

    @Override
    protected void onBindConViewHolder(ViewHolder holder, int position, EaseConversationInfo bean) {
        EMConversation item = (EMConversation) bean.getInfo();
        Context context = holder.itemView.getContext();
        if (!setModel.isHideUnreadDot()) {
            showUnreadNum(holder, item.getUnreadMsgCount());
        }
        holder.mentioned.setVisibility(View.GONE);
        Log.e("ouyang","SystemMessageDelegate的会话类型是" + item.getType());
        if (item.getAllMsgCount() != 0) {
            EMMessage lastMessage = item.getLastMessage();
            Log.e("ouyang", "lastMessage的messageType是" + item.getLastMessage().getType());
            Log.e("ouyang", "lastMessage的messageFrom是" + item.getLastMessage().getFrom());
            if (item.getType() == EMConversation.EMConversationType.Chat) {
                if (TextUtils.equals(item.getLastMessage().getFrom(), MyConstant.DEFAULT_SYSTEM_MESSAGE_ID)) {
                    holder.listIteaseLayout.setBackground(MyEaseCommonUtils.isTimestamp(item.getExtField())?ContextCompat.getDrawable(context, R.drawable.ease_conversation_top_bg):null);
                    holder.avatar.setImageResource(com.hyphenate.easeui.R.drawable.em_system_nofinication);
                    holder.name.setTextColor(ContextCompat.getColor(context, R.color.app_main_color_blue));
                    String message = "";
                    String name = "系统消息";
                    if (lastMessage!=null) {
                        try {
                            String statusParams = lastMessage.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS);
                            InviteMessageStatus status = InviteMessageStatus.valueOf(statusParams);
                            Log.e("ouyang", "lastMessage的status是" + status.toString());
                            if (status == InviteMessageStatus.BEINVITEED||status == InviteMessageStatus.BEREFUSED||status == InviteMessageStatus.BEAGREED) {
                                EaseUserProfileProvider userProvider = EaseIM.getInstance().getUserProvider();
                                String friend_name = lastMessage.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_FROM);
                                if (userProvider != null) {
                                    EaseUser friend_user = userProvider.getUser(friend_name);
                                    if (friend_user != null) {
                                        try {
                                            String selectInfoExt = friend_user.getExt();
                                            if (!TextUtils.isEmpty(selectInfoExt)) {
                                                JSONObject JsonObject = new JSONObject(selectInfoExt);//用户资料扩展属性
                                                friend_name = TextUtils.isEmpty(JsonObject.getString("remarkName")) ? friend_user.getNickname() : JsonObject.getString("remarkName");
                                            } else {
                                                friend_name = StringUtil.getStringValue(friend_user.getNickname());
                                            }
                                        } catch (
                                                JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                name = "好友申请";
                                message = context.getString(status.getMsgContent(), friend_name);
                                holder.avatar.setImageResource(R.drawable.em_system_nofinication);
                            } else if (status == InviteMessageStatus.BEAPPLYED) { //application to join group
                                name = "群通知";
                                holder.avatar.setImageResource(R.mipmap.group_notification);
                                message = context.getString(InviteMessageStatus.BEAPPLYED.getMsgContent()
                                        , lastMessage.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_FROM), lastMessage.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_NAME));
                            } else if (status == InviteMessageStatus.GROUPINVITATION) {
                                name = "群通知";
                                holder.avatar.setImageResource(R.mipmap.group_notification);
                                message = context.getString(InviteMessageStatus.GROUPINVITATION.getMsgContent()
                                        , lastMessage.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_INVITER), lastMessage.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_NAME));
                            }
                            holder.name.setText(name);
                            holder.message.setText(message);
                            holder.time.setText(EaseDateUtils.getTimestampString(context, new Date(lastMessage.getMsgTime())));
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }

                    }
                }

                }
            }




    }
}
