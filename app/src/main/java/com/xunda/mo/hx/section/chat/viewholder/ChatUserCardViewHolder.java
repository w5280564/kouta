package com.xunda.mo.hx.section.chat.viewholder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.util.EMLog;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.section.chat.views.chatRowUserCard;
import com.xunda.mo.main.chat.ChatFriend_Detail;
import com.xunda.mo.main.constant.MyConstant;

import java.util.Map;

public class ChatUserCardViewHolder extends EaseChatRowViewHolder {

    public ChatUserCardViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    public static ChatUserCardViewHolder create(ViewGroup parent, boolean isSender,
                                                 MessageListItemClickListener itemClickListener) {
        return new ChatUserCardViewHolder(new chatRowUserCard(parent.getContext(), isSender), itemClickListener);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);
        if(message.getType() == EMMessage.Type.CUSTOM){
            EMCustomMessageBody messageBody = (EMCustomMessageBody) message.getBody();
            String event = messageBody.event();
            if(event.equals(DemoConstant.USER_CARD_EVENT)){
                Map<String,String> params = messageBody.getParams();
//                String uId = params.get(DemoConstant.USER_CARD_ID);
//                String avatar = params.get(DemoConstant.USER_CARD_AVATAR);
//                String nickname = params.get(DemoConstant.USER_CARD_NICK);

                String uId = params.get(MyConstant.UID);
                String avatar = params.get(MyConstant.AVATAR);
                String nickname = params.get(MyConstant.NICK_NAME);
                String HxName = params.get(MyConstant.HX_NAME);
                if(uId != null && uId.length() > 0){
                    if(uId.equals(EMClient.getInstance().getCurrentUser())){
//                        UserDetailActivity.actionStart(getContext(),nickname,avatar);
                    }else{
                        EaseUser user = DemoHelper.getInstance().getUserInfo(uId);
                        if(user == null){
                            user = new EaseUser(uId);
                            user.setAvatar(avatar);
                            user.setNickname(nickname);
                        }
                        boolean isFriend =  DemoHelper.getInstance().getModel().isContact(uId);
                        if(isFriend){
                            user.setContact(0);
                        }else{
                            user.setContact(3);
                        }
//                        ContactDetailActivity.actionStart(getContext(),user);
                        String addType = "8";
                        ChatFriend_Detail.actionStart(getContext(), HxName, user, addType);
                    }
                }else{
                    EMLog.e("ChatUserCardViewHolder","onBubbleClick uId is empty");
                }
            }
        }
    }
}
