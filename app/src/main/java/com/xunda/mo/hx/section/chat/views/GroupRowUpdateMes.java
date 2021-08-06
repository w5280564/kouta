package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

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
        String Group = message.getStringAttribute(MyConstant.MESSAGE_TYPE, "");

        if (TextUtils.equals(Group, MyConstant.UPDATE_GROUP_NAME)) {
            if (TextUtils.equals(Group, MyConstant.UPDATE_GROUP_NAME) && isSender) {
                content = "您修改了群聊名称";
            } else {
                content = "群主修改了群聊名称";
            }
            content_Txt.setText(content);
        } else if (TextUtils.equals(Group, MyConstant.MESSAGE_TYPE_DELETUSER)) {
            String name = message.getStringAttribute(MyConstant.SEND_NAME, "");
            String invitationStr = message.getStringAttribute(MyConstant.USER_NAME, "");
            content = String.format("'%1$s'将 '%2$s'移出群聊", name, invitationStr);
            String groupStr = "'移出群聊";
            int startLength = content.length() - invitationStr.length() - groupStr.length();
            int endLength = content.length() - groupStr.length();
            setName(content, name.length(), startLength, endLength, content_Txt);
        } else if (TextUtils.equals(Group, MyConstant.MESSAGE_TYPE_ADDUSER)) {
            String name = message.getStringAttribute(MyConstant.SEND_NAME, "");
            String invitationStr = message.getStringAttribute(MyConstant.USER_NAME, "");
            content = String.format("'%1$s'邀请 '%2$s'加入群聊", name, invitationStr);
            String groupStr = "'加入群聊";
            int startLength = content.length() - invitationStr.length() - groupStr.length();
            int endLength = content.length() - groupStr.length();
            setName(content, name.length(), startLength, endLength, content_Txt);
        } else if (TextUtils.equals(Group, MyConstant.MESSAGE_TYPE_ADDADMIN)) {
            String invitationStr = message.getStringAttribute(MyConstant.USER_NAME, "");
            content = String.format("'%1$s'被添加为管理员", invitationStr);
            setName(content, invitationStr.length(), content_Txt);
        } else if (TextUtils.equals(Group, MyConstant.MESSAGE_TYPE_DELETADMIN)) {
            String invitationStr = message.getStringAttribute(MyConstant.USER_NAME, "");
            content = String.format("'%1$s'被取消了管理员", invitationStr);
            setName(content, invitationStr.length(), content_Txt);
        } else if (TextUtils.equals(Group, MyConstant.MESSAGE_TYPE_ANONYMOUS_ON)) {
            content = "群主开启了匿名聊天";
            content_Txt.setText(content);
        } else if (TextUtils.equals(Group, MyConstant.MESSAGE_TYPE_ANONYMOUS_OFF)) {
            content = "群主关闭了匿名聊天";
            content_Txt.setText(content);
        } else if (TextUtils.equals(Group, MyConstant.MESSAGE_TYPE_MUTE_ON)) {
            content = "群主开启了群员禁言";
            content_Txt.setText(content);
        } else if (TextUtils.equals(Group, MyConstant.MESSAGE_TYPE_MUTE_OFF)) {
            content = "群主关闭了群员禁言";
            content_Txt.setText(content);
        } else if (TextUtils.equals(Group, MyConstant.MESSAGE_TYPE_PROTECT_ON)) {
            content = "群主开启了群员保护";
            content_Txt.setText(content);
        } else if (TextUtils.equals(Group, MyConstant.MESSAGE_TYPE_PROTECT_OFF)) {
            content = "群主关闭了群员保护";
            content_Txt.setText(content);
        } else if (TextUtils.equals(Group, MyConstant.MESSAGE_TYPE_UPDATE_MASTER)) {
            String invitationStr = message.getStringAttribute(MyConstant.USER_NAME, "");
            content = String.format("您将群主转让给'%1$s'", invitationStr);
            int startLength = content.length() - 1 - invitationStr.length();
            int endLength = content.length() - 1;
//            setName(content, invitationStr.length(), content_Txt);
            setName(content, startLength, endLength, content_Txt);
        } else if (TextUtils.equals(Group, MyConstant.MESSAGE_TYPE_PUSH_ON)) {
            String invitationStr = message.getStringAttribute(MyConstant.SEND_NAME, "");
            content = String.format("'%1$s'开启了成员加群/退群通知", invitationStr);
            setName(content, invitationStr.length(), content_Txt);
        } else if (TextUtils.equals(Group, MyConstant.MESSAGE_TYPE_PUSH_OFF)) {
            String invitationStr = message.getStringAttribute(MyConstant.SEND_NAME, "");
            content = String.format("'%1$s'关闭了成员加群/退群通知", invitationStr);
            setName(content, invitationStr.length(), content_Txt);
        } else if (TextUtils.equals(Group, MyConstant.MESSAGE_GROUP_LEAVE)) {
            String invitationStr = message.getStringAttribute(MyConstant.SEND_NAME, "");
            content = String.format("'%1$s'离开了群组", invitationStr);
            setName(content, invitationStr.length(), content_Txt);
        } else if (TextUtils.equals(Group, MyConstant.GROUP_UPDATE_GROUPDES)) {
            if (TextUtils.equals(Group, MyConstant.GROUP_UPDATE_GROUPDES) && isSender) {
                content = "您修改了群简介";
            } else {
                content = "群主修改了群简介";
            }
            content_Txt.setText(content);
        }


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

