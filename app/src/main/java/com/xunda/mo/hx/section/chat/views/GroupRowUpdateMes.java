package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.text.TextUtils;
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
        if (TextUtils.equals(Group, MyConstant.UPDATE_GROUP_NAME) && isSender) {
            content = "您修改了群聊名称";
        } else {
            content = "群主修改了群聊名称";
        }

        content_Txt.setText(content);

    }


}

