package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.xunda.mo.R;
import com.xunda.mo.main.constant.MyConstant;

public class ChatRowAddMes extends EaseChatRow {
    private TextView content_Txt;

    public ChatRowAddMes(Context context, boolean isSender) {
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

        String name = message.getStringAttribute(MyConstant.SEND_NAME, "");
        String invitationStr = message.getStringAttribute(MyConstant.USER_NAME, "");

        String content = String.format("'%1$s'创建了群聊，并邀请 '%2$s'加入群聊", name, invitationStr);
        String groupStr = "'加入群聊";
        int startLength = content.length() - invitationStr.length() - groupStr.length();
        int endLength = content.length() - groupStr.length();
        setName(content, name.length(), startLength, endLength, content_Txt);
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


}

