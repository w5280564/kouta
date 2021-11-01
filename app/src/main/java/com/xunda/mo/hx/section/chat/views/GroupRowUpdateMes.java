package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.xunda.mo.R;
import com.xunda.mo.main.constant.MyConstant;

public class GroupRowUpdateMes extends EaseChatRow {
    private TextView content_Txt;

    public GroupRowUpdateMes(Context context, boolean isSender) {
        super(context, isSender);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(R.layout.demo_row_mes, this);
    }

    @Override
    protected void onFindViewById() {
        content_Txt = findViewById(R.id.content_Txt);
    }

    @Override
    protected void onSetUpView() {
//        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
//        contentView.setText(txtBody.getMessage());
        String content = "";
        String mess_Type = message.getStringAttribute(MyConstant.MESSAGE_TYPE, "");

        if (TextUtils.equals(mess_Type, MyConstant.UPDATE_GROUP_NAME)) {
            if (TextUtils.equals(mess_Type, MyConstant.UPDATE_GROUP_NAME) && isSender) {
                content = "您修改了群聊名称";
            } else {
                content = "群主修改了群聊名称";
            }
            content_Txt.setText(content);
        } else if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_TYPE_DELETUSER)) {
            String name = message.getStringAttribute(MyConstant.SEND_NAME, "");
            String invitationStr = message.getStringAttribute(MyConstant.USER_NAME, "");
            content = String.format("'%1$s'将 '%2$s'移出群聊", name, invitationStr);
            String groupStr = "'移出群聊";
            int startLength = content.length() - invitationStr.length() - groupStr.length();
            int endLength = content.length() - groupStr.length();
            setName(content, name.length(), startLength, endLength, content_Txt);
        } else if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_TYPE_ADDUSER)) {
            String name = message.getStringAttribute(MyConstant.SEND_NAME, "");
            String invitationStr = message.getStringAttribute(MyConstant.USER_NAME, "");
            content = String.format("'%1$s'邀请 '%2$s'加入群聊", name, invitationStr);
            String groupStr = "'加入群聊";
            int startLength = content.length() - invitationStr.length() - groupStr.length();
            int endLength = content.length() - groupStr.length();
            setName(content, name.length(), startLength, endLength, content_Txt);
        } else if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_TYPE_ADDADMIN)) {
            String invitationStr = message.getStringAttribute(MyConstant.USER_NAME, "");
            content = String.format("'%1$s'被添加为管理员", invitationStr);
            setName(content, invitationStr.length(), content_Txt);
        } else if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_TYPE_DELETADMIN)) {
            String invitationStr = message.getStringAttribute(MyConstant.USER_NAME, "");
            content = String.format("'%1$s'被取消了管理员", invitationStr);
            setName(content, invitationStr.length(), content_Txt);
        } else if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_TYPE_ANONYMOUS_ON)) {
            content = "群主开启了匿名聊天";
            content_Txt.setText(content);
        } else if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_TYPE_ANONYMOUS_OFF)) {
            content = "群主关闭了匿名聊天";
            content_Txt.setText(content);
        } else if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_TYPE_MUTE_ON)) {
            content = "群主开启了群员禁言";
            content_Txt.setText(content);
        } else if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_TYPE_MUTE_OFF)) {
            content = "群主关闭了群员禁言";
            content_Txt.setText(content);
        } else if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_TYPE_PROTECT_ON)) {
            content = "群主开启了群员保护";
            content_Txt.setText(content);
        } else if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_TYPE_PROTECT_OFF)) {
            content = "群主关闭了群员保护";
            content_Txt.setText(content);
        } else if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_TYPE_UPDATE_MASTER)) {
            String userName = message.getStringAttribute(MyConstant.USER_NAME, "");
            if (isSender()) {
                content = String.format("您将群主转让给'%1$s'", userName);
            } else {
                content = String.format("群主将群主转让给'%1$s'", userName);
            }
            int startLength = content.length() - 1 - userName.length();
            int endLength = content.length() - 1;
            setName(content, startLength, endLength, content_Txt);
        } else if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_TYPE_PUSH_ON)) {
            String invitationStr = message.getStringAttribute(MyConstant.SEND_NAME, "");
            content = String.format("'%1$s'开启了成员加群/退群通知", invitationStr);
            setName(content, invitationStr.length(), content_Txt);
        } else if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_TYPE_PUSH_OFF)) {
            String invitationStr = message.getStringAttribute(MyConstant.SEND_NAME, "");
            content = String.format("'%1$s'关闭了成员加群/退群通知", invitationStr);
            setName(content, invitationStr.length(), content_Txt);
        } else if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_GROUP_LEAVE)) {
            String invitationStr = message.getStringAttribute(MyConstant.SEND_NAME, "");
            content = String.format("'%1$s'离开了群组", invitationStr);
            setName(content, invitationStr.length(), content_Txt);
        } else if (TextUtils.equals(mess_Type, MyConstant.GROUP_UPDATE_GROUPDES)) {
            if (TextUtils.equals(mess_Type, MyConstant.GROUP_UPDATE_GROUPDES) && isSender) {
                content = "您修改了群简介";
            } else {
                content = "群主修改了群简介";
            }
            content_Txt.setText(content);
        } else if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_TYPE_DOUBLE_RECALL)) {
            String SEND_NAME = message.getStringAttribute(MyConstant.SEND_NAME, "");
            if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_TYPE_DOUBLE_RECALL) && isSender) {
//            if (TextUtils.equals(Group, MyConstant.MESSAGE_TYPE_DOUBLE_RECALL) && !TextUtils.isEmpty(SEND_NAME)) {
                content = "您撤回了所有消息";
            } else {
                content = "对方撤回了所有消息";
            }
            content_Txt.setText(content);
        } else if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_TYPE_GROUP_DOUBLE_RECALL)) {
            String sendName = message.getStringAttribute(MyConstant.SEND_NAME, "");
            if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_TYPE_GROUP_DOUBLE_RECALL) && isSender) {
                content = "您撤回了所有消息";
            } else {
                content = String.format("'%1$s'撤回了所有消息", sendName);
            }
            content_Txt.setText(content);
        } else if (TextUtils.equals(mess_Type, MyConstant.APPLY)) {
            String sendName = message.getStringAttribute(MyConstant.SEND_NAME, "");
            if (isSender()) {
                content = String.format("您已同意添加'%1$s'，现在可以开始聊天了", sendName);
            }else {
                content = String.format("'%1$s'已同意添加好友，现在可以开始聊天了", sendName);
            }
            content_Txt.setText(content);
        }else if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_GROUP_Message)) {
            String contentStr = message.getStringAttribute(MyConstant.CONTENT, "");
//                content = String.format("'%1$s'，加入群聊", sendName);
            content_Txt.setText(contentStr);
        }else if (TextUtils.equals(mess_Type, MyConstant.Message_Recall)) {
            String sendName = message.getStringAttribute(MyConstant.SEND_NAME, "");
            String messageStr = "";
            if (message.getChatType() == EMMessage.ChatType.Chat) {
                if (isSender()){
                    messageStr = "您撤回一条消息";
                }else {
                    messageStr = "对方撤回一条消息";
                }
            }else if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                if (isSender()) {
                    messageStr = "您撤回一条消息";
                }else {
                    messageStr = String.format("'%1$s'撤回了一条消息", sendName);
                }
            }
            content_Txt.setText(messageStr);
        }else if (TextUtils.equals(mess_Type, MyConstant.MESSAGE_TYPE_SCREENSHORTS)|| TextUtils.equals(mess_Type, MyConstant.MESSAGE_TYPE_GROUP_SCREENSHORTS)) {
            String sendName = message.getStringAttribute(MyConstant.SEND_NAME, "");
            String messageStr = "";
            if (message.getChatType() == EMMessage.ChatType.Chat) {
                if (!isSender()){
                    messageStr = "对方进行了截屏";
                }
            }else if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                if (!isSender()) {
                    messageStr = String.format("'%1$s'进行了截屏", sendName);
                }
            }
            content_Txt.setText(messageStr);
        }

        bubbleLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.chat_rowmes_bg));
    }


    /**
     * @param name          要显示的数据
     * @param nameLength    要改颜色的字体长度
     * @param contentLength 邀请的好友改色起始位置
     * @param endLength     邀请的好友改色结束位置
     * @param viewName
     */
    private void setName(String name, int nameLength, int contentLength, int endLength, TextView viewName) {
        SpannableString spannableString = new SpannableString(name);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(context.getColor(R.color.yellowfive));
        spannableString.setSpan(foregroundColorSpan, 1, nameLength + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(context.getColor(R.color.yellowfive)), contentLength, endLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        viewName.setText(spannableString);
    }

    private void setName(String name, int nameLength, TextView viewName) {
        SpannableString spannableString = new SpannableString(name);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(context.getColor(R.color.yellowfive));
        spannableString.setSpan(foregroundColorSpan, 1, nameLength + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        viewName.setText(spannableString);
    }

    private void setName(String name, int startLength, int endLength, TextView viewName) {
        SpannableString spannableString = new SpannableString(name);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(context.getColor(R.color.yellowfive));
        spannableString.setSpan(foregroundColorSpan, startLength, endLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        viewName.setText(spannableString);
    }


}

