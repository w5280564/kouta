package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.GsonUtil;
import com.hyphenate.easeui.utils.ListUtils;
import com.hyphenate.easeui.utils.StringUtil;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.xunda.mo.R;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.GroupMember_Bean;
import com.xunda.mo.network.saveFile;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * base chat row view(群或单聊带头像和昵称，非提示性消息)
 */
public abstract class BaseChatRowWithNameAndHeader extends EaseChatRow{
    protected TextView tv_user_role;
    protected TextView tv_vip;

    public BaseChatRowWithNameAndHeader(Context context, boolean isSender) {
        super(context, isSender);
    }

    public BaseChatRowWithNameAndHeader(Context context, EMMessage message, int position, Object adapter) {
        super(context, message, position, adapter);
    }


    @Override
    protected void setAvatarAndNick() {
        userAvatarView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (isSender()) {
            MyInfo mMyInfo = new MyInfo(context);
            Glide.with(getContext()).load(mMyInfo.getUserInfo().getHeadImg()).placeholder(R.mipmap.img_pic_none).error(R.mipmap.img_pic_none).into(userAvatarView);
        } else {
            //添加群其他用户的名字与头像
            if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                List<GroupMember_Bean.DataDTO> memberList = new ArrayList<>();
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.conversationId());
                if (conversation!=null) {
                    String extMessage = conversation.getExtField();

                    if (!TextUtils.isEmpty(extMessage)) {
                        JSONObject JsonObject = null;
                        try {
                            JsonObject = new JSONObject(extMessage);
                            boolean isInsertGroupOrFriendInfo = JsonObject.getBoolean("isInsertGroupOrFriendInfo");
                            if (isInsertGroupOrFriendInfo) {
                                String jsonMemberList = JsonObject.getString("groupMemberList");
                                GroupMember_Bean groupListModel = GsonUtil.getInstance().json2Bean(jsonMemberList,GroupMember_Bean.class);
                                if (groupListModel != null) {
                                    memberList.addAll(groupListModel.getData());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                String name  = message.getStringAttribute(MyConstant.SEND_NAME, "");
                String headUrl = message.getStringAttribute(MyConstant.SEND_HEAD, "");
                int vipType = message.getIntAttribute(MyConstant.SEND_VIP, 3);

                if (!ListUtils.isEmpty(memberList)) {
                    for (GroupMember_Bean.DataDTO memberObj:memberList) {
                        if (memberObj.getHxUserName().equals(message.getFrom())) {
                            name  = memberObj.getNickname();//在群会话页显示的昵称
                            headUrl = memberObj.getHeadImg();
                            message.setAttribute(MyConstant.SEND_NAME,name);
                            if (tv_user_role!=null) {
                                if (memberObj.getIdentity() == 1) {
                                    tv_user_role.setVisibility(View.VISIBLE);
                                    tv_user_role.setText("群主");
                                    tv_user_role.setBackgroundResource(R.drawable.shape_bg_all_member_qunzhu);
                                    tv_user_role.setTextColor(ContextCompat.getColor(context,R.color.color_FB8717));
                                } else if (memberObj.getIdentity() == 2) {
                                    tv_user_role.setVisibility(View.VISIBLE);
                                    tv_user_role.setText("管理员");
                                    tv_user_role.setBackgroundResource(R.drawable.shape_bg_all_member_guanliyuan);
                                    tv_user_role.setTextColor(ContextCompat.getColor(context,R.color.color_2391F3));
                                }else {
                                    tv_user_role.setVisibility(View.GONE);
                                }
                            }

                            if (vipType==3) {
                                vipType = memberObj.getVipType();
                            }

                            if (vipType==1){
                                usernickView.setTextColor(ContextCompat.getColor(context,R.color.yellowfive));
                                if (tv_vip!=null) {
                                    tv_vip.setVisibility(VISIBLE);
                                }
                            }else{
                                usernickView.setTextColor(ContextCompat.getColor(context,R.color.greytwo));
                                if (tv_vip!=null) {
                                    tv_vip.setVisibility(GONE);
                                }
                            }
                            break;
                        }
                    }
                }

                if (StringUtil.isBlank(name)){
                    EaseUserUtils.setUserNick(message.getFrom(), usernickView);
                }else{
                    usernickView.setText(name);
                }
                //匿名聊天
                if (!saveFile.getShareData(MyConstant.GROUP_CHAT_ANONYMOUS + message.conversationId(), context).equals("false")) {
                    userAvatarView.setImageResource(R.drawable.anonymous_chat_icon);
                }else{
                    if (StringUtil.isBlank(headUrl)) {
                        EaseUserUtils.setUserAvatar(context, message.getFrom(), userAvatarView);
                    }else{
                        Glide.with(getContext()).load(headUrl).placeholder(R.mipmap.img_pic_none).error(R.mipmap.img_pic_none).into(userAvatarView);
                    }
                }
            }else{
                String header_url_message = message.getStringAttribute(MyConstant.SEND_HEAD, "");
                EaseUserUtils.setUserAvatarAndSendHeaderUrl(context, message.getFrom(),header_url_message,userAvatarView);
            }

        }
    }


}
