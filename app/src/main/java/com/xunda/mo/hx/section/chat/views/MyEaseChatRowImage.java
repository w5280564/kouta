package com.xunda.mo.hx.section.chat.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.modules.chat.EaseChatMessageListLayout;
import com.hyphenate.easeui.modules.chat.model.EaseChatItemStyleHelper;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.GsonUtil;
import com.hyphenate.easeui.utils.ListUtils;
import com.hyphenate.easeui.utils.StringUtil;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowFile;
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
 * image for row
 */
public class MyEaseChatRowImage extends EaseChatRowFile {
    protected ImageView imageView;
    private TextView tv_user_role,tv_vip;

    public MyEaseChatRowImage(Context context, boolean isSender) {
        super(context, isSender);
    }

    public MyEaseChatRowImage(Context context, EMMessage message, int position, Object adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(!showSenderType ? R.layout.ease_row_received_picture
                : R.layout.ease_row_sent_picture, this);
    }

    @Override
    protected void onFindViewById() {
        percentageView = findViewById(R.id.percentage);
        imageView = findViewById(R.id.image);
        tv_user_role = findViewById(R.id.tv_user_role);
        tv_vip = findViewById(com.xunda.mo.R.id.tv_vip);
    }

    
    @Override
    protected void onSetUpView() {
        if(bubbleLayout != null) {
            bubbleLayout.setBackground(null);
        }
        if (isSender()) {
            MyInfo info = new MyInfo(context);
            try {
                int avatarResId = Integer.parseInt(info.getUserInfo().getHeadImg());
                Glide.with(context).load(avatarResId).into(userAvatarView);
            } catch (Exception e) {
                Glide.with(context).load(info.getUserInfo().getHeadImg())
                        .apply(RequestOptions.placeholderOf(R.mipmap.img_pic_none)
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(userAvatarView);
            }
            //只要不是常规布局形式，就展示昵称
            if (EaseChatItemStyleHelper.getInstance().getStyle().getItemShowType() != EaseChatMessageListLayout.ShowType.NORMAL.ordinal()) {
                EaseUserUtils.setUserNick(message.getFrom(), usernickView);
            }
        } else {
            //添加群其他用户的名字与头像
            if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.conversationId());
                String extMessage = conversation.getExtField();
                List<GroupMember_Bean.DataDTO> memberList = new ArrayList<>();
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


                if (StringUtil.isBlank(headUrl)) {
                    EaseUserUtils.setUserAvatar(context, message.getFrom(), userAvatarView);
                }else{
                    Glide.with(getContext()).load(headUrl).placeholder(R.mipmap.img_pic_none).error(R.mipmap.img_pic_none).into(userAvatarView);
                }
            }else{
                String header_url_message = message.getStringAttribute(MyConstant.SEND_HEAD, "");
                EaseUserUtils.setUserAvatarAndSendHeaderUrl(context, message.getFrom(),header_url_message,userAvatarView);
            }
            ViewGroup.LayoutParams params = EaseImageUtils.getImageShowSize(context, message);
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.width = params.width;
            layoutParams.height = params.height;
        }

        showImageView(message);
    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
        super.onViewUpdate(msg);
        //此方法中省略掉了之前的有关非自动下载缩略图后展示图片的逻辑
    }

    @Override
    protected void onMessageSuccess() {
        super.onMessageSuccess();
        //即使是sender，发送成功后也要在执行，防止出现图片尺寸不对的问题
        showImageView(message);
    }

    @Override
    protected void onMessageInProgress() {
        if(message.direct() == EMMessage.Direct.SEND) {
            super.onMessageInProgress();
        }else {
            if(EMClient.getInstance().getOptions().getAutodownloadThumbnail()){
                //imageView.setImageResource(R.drawable.ease_default_image);
            }else {
                progressBar.setVisibility(View.INVISIBLE);
                if(percentageView != null) {
                    percentageView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * load image into image view
     *
     */
    @SuppressLint("StaticFieldLeak")
    private void showImageView(final EMMessage message) {
        EaseImageUtils.showImage(context, imageView, message);
    }
}
