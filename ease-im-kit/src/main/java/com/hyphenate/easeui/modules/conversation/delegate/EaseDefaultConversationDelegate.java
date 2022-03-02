package com.hyphenate.easeui.modules.conversation.delegate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationSetStyle;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseImageView;

public abstract class EaseDefaultConversationDelegate extends EaseBaseConversationDelegate<EaseConversationInfo, EaseDefaultConversationDelegate.ViewHolder> {

    public EaseDefaultConversationDelegate(EaseConversationSetStyle setModel) {
        super(setModel);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, String tag) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ease_item_row_chat_history, parent, false);
        return new ViewHolder(view, setModel);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, EaseConversationInfo item) {
        super.onBindViewHolder(holder, position, item);
        onBindConViewHolder(holder, position, item);
    }

    protected abstract void onBindConViewHolder(ViewHolder holder, int position, EaseConversationInfo item);

    public static class ViewHolder extends EaseBaseRecyclerViewAdapter.ViewHolder<EaseConversationInfo> {
        public ConstraintLayout listIteaseLayout;
        public EaseImageView avatar;
        public TextView mUnreadMsgNumber;
        public TextView unreadMsgNumberRight;
        public TextView name;
        public TextView time;
        public ImageView mMsgState;
        public TextView mentioned;
        public TextView message;
        public TextView tv_official;
        public Context mContext;
        private final Drawable bgDrawable;

        public ViewHolder(@NonNull View itemView, EaseConversationSetStyle setModel) {
            super(itemView);
            mContext = itemView.getContext();
            listIteaseLayout = findViewById(R.id.list_itease_layout);
            avatar = findViewById(R.id.avatar);
            mUnreadMsgNumber = findViewById(R.id.unread_msg_number);
            unreadMsgNumberRight = findViewById(R.id.unread_msg_number_right);
            name = findViewById(R.id.name);
            time = findViewById(R.id.time);
            mMsgState = findViewById(R.id.msg_state);
            mentioned = findViewById(R.id.mentioned);
            message = findViewById(R.id.message);
            tv_official = findViewById(R.id.tv_official);
            EaseUserUtils.setUserAvatarStyle(avatar);
            bgDrawable = itemView.getBackground();
        }

        @Override
        public void initView(View itemView) {

        }

        @Override
        public void setData(EaseConversationInfo item, int position) {
            item.setOnSelectListener(new EaseConversationInfo.OnSelectListener() {
                @Override
                public void onSelect(boolean isSelected) {
                    if(isSelected) {
                        itemView.setBackgroundResource(R.drawable.ease_conversation_item_selected);
                    }else {
                        if(item.isTop()) {
                            itemView.setBackgroundResource(R.drawable.ease_conversation_top_bg);
                        }else {
                            itemView.setBackground(bgDrawable);
                        }
                    }
                }
            });
        }
    }

    public void showUnreadNum(ViewHolder holder, int unreadMsgCount) {
        if(unreadMsgCount > 0) {
            holder.mUnreadMsgNumber.setText(handleBigNum(unreadMsgCount));
            holder.unreadMsgNumberRight.setText(handleBigNum(unreadMsgCount));
            showUnreadRight(holder, setModel.getUnreadDotPosition() == EaseConversationSetStyle.UnreadDotPosition.RIGHT);
        }else {
            holder.mUnreadMsgNumber.setVisibility(View.GONE);
            holder.unreadMsgNumberRight.setVisibility(View.GONE);
        }
    }

    public String handleBigNum(int unreadMsgCount) {
        if(unreadMsgCount <= 99) {
            return String.valueOf(unreadMsgCount);
        }else {
            return "99+";
        }
    }

    public void showUnreadRight(ViewHolder holder, boolean isRight) {
        if(isRight) {
            holder.mUnreadMsgNumber.setVisibility(View.GONE);
            holder.unreadMsgNumberRight.setVisibility(View.VISIBLE);
        }else {
            holder.mUnreadMsgNumber.setVisibility(View.VISIBLE);
            holder.unreadMsgNumberRight.setVisibility(View.GONE);
        }
    }
}

