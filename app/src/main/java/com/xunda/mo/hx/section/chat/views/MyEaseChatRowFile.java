package com.xunda.mo.hx.section.chat.views;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMNormalFileMessageBody;
import com.hyphenate.easeui.modules.chat.EaseChatMessageListLayout;
import com.hyphenate.easeui.modules.chat.model.EaseChatItemStyleHelper;
import com.hyphenate.easeui.utils.EaseEditTextUtils;
import com.hyphenate.easeui.utils.EaseFileUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.GsonUtil;
import com.hyphenate.easeui.utils.ListUtils;
import com.hyphenate.easeui.utils.StringUtil;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.util.TextFormater;
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
 * file for row
 */
public class MyEaseChatRowFile extends EaseChatRow {
    private static final String TAG = MyEaseChatRowFile.class.getSimpleName();
    /**
     * file name
     */
    protected TextView fileNameView;
    /**
     * file's size
     */
    protected TextView fileSizeView;
    private TextView tv_user_role,tv_vip;
    /**
     * file state
     */
    protected TextView fileStateView;
    private EMNormalFileMessageBody fileMessageBody;

    public MyEaseChatRowFile(Context context, boolean isSender) {
        super(context, isSender);
    }

    public MyEaseChatRowFile(Context context, EMMessage message, int position, Object adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(!showSenderType ? R.layout.ease_row_received_file
                : R.layout.ease_row_sent_file, this);
    }

    @Override
    protected void onFindViewById() {
        fileNameView = findViewById(R.id.tv_file_name);
        fileSizeView = findViewById(R.id.tv_file_size);
        fileStateView = findViewById(R.id.tv_file_state);
        percentageView = findViewById(R.id.percentage);
        tv_user_role = findViewById(R.id.tv_user_role);
        tv_vip = findViewById(com.xunda.mo.R.id.tv_vip);
    }

    @Override
    protected void onSetUpView() {
        fileMessageBody = (EMNormalFileMessageBody) message.getBody();
        Uri filePath = fileMessageBody.getLocalUri();
        fileNameView.setText(fileMessageBody.getFileName());
        fileNameView.post(() -> {
            String content = EaseEditTextUtils.ellipsizeMiddleString(fileNameView,
                    fileMessageBody.getFileName(),
                    fileNameView.getMaxLines(),
                    fileNameView.getWidth() - fileNameView.getPaddingLeft() - fileNameView.getPaddingRight());
            fileNameView.setText(content);
        });
        fileSizeView.setText(TextFormater.getDataSize(fileMessageBody.getFileSize()));
        if (message.direct() == EMMessage.Direct.SEND) {
            if (EaseFileUtils.isFileExistByUri(context, filePath)
                    && message.status() == EMMessage.Status.SUCCESS) {
                fileStateView.setText(R.string.have_uploaded);
            } else {
                fileStateView.setText("");
            }
        }
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            if (EaseFileUtils.isFileExistByUri(context, filePath)) {
                fileStateView.setText(R.string.have_downloaded);
            } else {
                fileStateView.setText(R.string.did_not_download);
            }
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
                            GroupMember_Bean groupListModel = GsonUtil.getInstance().json2Bean(jsonMemberList, GroupMember_Bean.class);
                            if (groupListModel != null) {
                                memberList.addAll(groupListModel.getData());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                String name = message.getStringAttribute(MyConstant.SEND_NAME, "");
                String headUrl = message.getStringAttribute(MyConstant.SEND_HEAD, "");
                int vipType = message.getIntAttribute(MyConstant.SEND_VIP, 3);

                if (!ListUtils.isEmpty(memberList)) {
                    for (GroupMember_Bean.DataDTO memberObj : memberList) {
                        if (memberObj.getHxUserName().equals(message.getFrom())) {
                            name = memberObj.getNickname();//在群会话页显示的昵称
                            headUrl = memberObj.getHeadImg();
                            message.setAttribute(MyConstant.SEND_NAME, name);
                            if (tv_user_role != null) {
                                if (memberObj.getIdentity() == 1) {
                                    tv_user_role.setVisibility(View.VISIBLE);
                                    tv_user_role.setText("群主");
                                    tv_user_role.setBackgroundResource(R.drawable.shape_bg_all_member_qunzhu);
                                    tv_user_role.setTextColor(ContextCompat.getColor(context, R.color.color_FB8717));
                                } else if (memberObj.getIdentity() == 2) {
                                    tv_user_role.setVisibility(View.VISIBLE);
                                    tv_user_role.setText("管理员");
                                    tv_user_role.setBackgroundResource(R.drawable.shape_bg_all_member_guanliyuan);
                                    tv_user_role.setTextColor(ContextCompat.getColor(context, R.color.color_2391F3));
                                } else {
                                    tv_user_role.setVisibility(View.GONE);
                                }
                            }

                            if (vipType == 3) {
                                vipType = memberObj.getVipType();
                            }

                            if (vipType == 1) {
                                usernickView.setTextColor(ContextCompat.getColor(context, R.color.yellowfive));
                                if (tv_vip != null) {
                                    tv_vip.setVisibility(VISIBLE);
                                }
                            } else {
                                usernickView.setTextColor(ContextCompat.getColor(context, R.color.greytwo));
                                if (tv_vip != null) {
                                    tv_vip.setVisibility(GONE);
                                }
                            }
                            break;
                        }
                    }
                }

                if (StringUtil.isBlank(name)) {
                    EaseUserUtils.setUserNick(message.getFrom(), usernickView);
                } else {
                    usernickView.setText(name);
                }


                if (StringUtil.isBlank(headUrl)) {
                    EaseUserUtils.setUserAvatar(context, message.getFrom(), userAvatarView);
                } else {
                    Glide.with(getContext()).load(headUrl).placeholder(R.mipmap.img_pic_none).error(R.mipmap.img_pic_none).into(userAvatarView);
                }
            } else {
                String header_url_message = message.getStringAttribute(MyConstant.SEND_HEAD, "");
                EaseUserUtils.setUserAvatarAndSendHeaderUrl(context, message.getFrom(), header_url_message, userAvatarView);
            }
        }
    }

    @Override
    protected void onMessageCreate() {
        super.onMessageCreate();
        progressBar.setVisibility(View.VISIBLE);
        if (percentageView != null)
            percentageView.setVisibility(View.INVISIBLE);
        if (statusView != null)
            statusView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onMessageSuccess() {
        super.onMessageSuccess();
        progressBar.setVisibility(View.INVISIBLE);
        if (percentageView != null)
            percentageView.setVisibility(View.INVISIBLE);
        if (statusView != null)
            statusView.setVisibility(View.INVISIBLE);
        if (message.direct() == EMMessage.Direct.SEND)
            if (fileStateView != null) {
                fileStateView.setText(R.string.have_uploaded);
            }
    }

    @Override
    protected void onMessageError() {
        super.onMessageError();
        progressBar.setVisibility(View.INVISIBLE);
        if (percentageView != null)
            percentageView.setVisibility(View.INVISIBLE);
        if (statusView != null)
            statusView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onMessageInProgress() {
        super.onMessageInProgress();
        if (progressBar.getVisibility() != VISIBLE) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (percentageView != null) {
            percentageView.setVisibility(View.VISIBLE);
            percentageView.setText(message.progress() + "%");
        }
        if (statusView != null)
            statusView.setVisibility(View.INVISIBLE);
    }
}
