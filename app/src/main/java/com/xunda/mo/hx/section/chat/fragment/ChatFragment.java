
package com.xunda.mo.hx.section.chat.fragment;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.xunda.mo.hx.section.base.MyEaseChatLayout.AT_PREFIX;
import static com.xunda.mo.hx.section.base.MyEaseChatLayout.AT_SUFFIX;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easecallkit.EaseCallKit;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easecallkit.base.EaseCallUserInfo;
import com.hyphenate.easecallkit.livedatas.EaseLiveDataBus;
import com.hyphenate.easecallkit.utils.EaseCallKitUtils;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.modules.chat.EaseChatInputMenu;
import com.hyphenate.easeui.modules.chat.EaseChatMessageListLayout;
import com.hyphenate.easeui.modules.chat.EaseInputEditText;
import com.hyphenate.easeui.modules.chat.interfaces.IChatExtendMenu;
import com.hyphenate.easeui.modules.chat.interfaces.IChatPrimaryMenu;
import com.hyphenate.easeui.modules.chat.interfaces.OnRecallMessageResultListener;
import com.hyphenate.easeui.modules.menu.EasePopupWindowHelper;
import com.hyphenate.easeui.modules.menu.MenuItemBean;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.ListUtils;
import com.hyphenate.easeui.utils.StringUtil;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.UriUtils;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.db.entity.EmUserEntity;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.common.model.EmojiconExampleGroupData;
import com.xunda.mo.hx.section.base.BaseActivity;
import com.xunda.mo.hx.section.chat.activicy.ForwardMessageActivity;
import com.xunda.mo.hx.section.chat.activicy.ImageGridActivity;
import com.xunda.mo.hx.section.conference.ConferenceInviteActivity;
import com.xunda.mo.hx.section.dialog.DemoListDialogFragment;
import com.xunda.mo.hx.section.dialog.FullEditDialogFragment;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.baseView.MyApplication;
import com.xunda.mo.main.chat.activity.ChatComplaint;
import com.xunda.mo.main.chat.activity.ChatFriend_Detail;
import com.xunda.mo.main.chat.activity.Chat_SelectUserCard;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.group.activity.GroupAllMembers_At;
import com.xunda.mo.main.group.activity.GroupFriend_Detail;
import com.xunda.mo.main.group.activity.Group_Horn;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.main.info.NameResource;
import com.xunda.mo.main.me.activity.Me_VIP;
import com.xunda.mo.main.me.activity.UserDetail_Set;
import com.xunda.mo.model.ChatUserBean;
import com.xunda.mo.model.Chat_SensitiveWordBean;
import com.xunda.mo.model.Group_Details_Bean;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.model.baseDataModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.pinyin.PinyinUtils;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.kotlin.ScreenShotViewModel;
import com.xunda.mo.staticdata.kotlin.ScreentShotInfo;
import com.xunda.mo.staticdata.xUtils3Http;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;


public class ChatFragment extends MyEaseChatFragment implements OnRecallMessageResultListener {
    private static final String TAG = ChatFragment.class.getSimpleName();
    protected ClipboardManager clipboard;

    private static final int REQUEST_CODE_SELECT_AT_USER = 15;
    private static final String[] calls = {"视频通话", "语音通话"};
    private OnFragmentInfoListener infoListener;
    private Dialog dialog;
    EaseChatMessageListLayout messageListLayout;
    private IChatPrimaryMenu primaryMenu;
    private EaseTitleBar titleBarMessage;
    private EaseInputEditText et_sendmessage;

    private String friendName, friendHeader,showGroupImg,showGroupName;
    private boolean isMoCustomer;//是否是客服聊天

    @Override
    public void initArguments() {
        super.initArguments();
        Bundle bundle = getArguments();
        if (bundle != null) {
            isMoCustomer = bundle.getBoolean("isMoCustomer");
        }
    }

    @Override
    public void initView() {
        super.initView();
        clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        //获取到聊天列表控件
        messageListLayout = chatLayout.getChatMessageListLayout();
        messageListLayout.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.color_EDF0F3));
        //设置头像形状：0为默认，1为圆形，2为方形
        messageListLayout.setAvatarShapeType(2);
        //设置文本字体大小
        messageListLayout.setItemTextSize((int) EaseCommonUtils.sp2px(mContext, 14));
        //设置文本字体颜色
        messageListLayout.setItemTextColor(ContextCompat.getColor(mContext, R.color.blacktitle));

        messageListLayout.setItemSenderBackground(ContextCompat.getDrawable(mContext, R.drawable.chat_send_bg));
        messageListLayout.setItemReceiverBackground(ContextCompat.getDrawable(mContext, R.drawable.chat_receive_white_bg));

        horn_Con.setVisibility(View.GONE);

        titleBarMessage = requireActivity().findViewById(R.id.title_bar_message);
        horn_Txt.setMarqueeVelocity(1);

        Glide.with(mContext).load(R.mipmap.horn_gif_icon).into(horn_icon);

        //获取到菜单输入父控件
        EaseChatInputMenu chatInputMenu = chatLayout.getChatInputMenu();
        //获取到菜单输入控件
        primaryMenu = chatInputMenu.getPrimaryMenu();
        ImageView voiceBtn = chatInputMenu.findViewById(R.id.btn_set_mode_voice);
        et_sendmessage = chatInputMenu.findViewById(R.id.et_sendmessage);
        et_sendmessage.setOnKeyListener(new etOnKey_Listener());
        Button btn_send = chatInputMenu.findViewById(R.id.btn_send);

        voiceBtn.setOnClickListener(v -> startAudio());



        if (!saveFile.getShareData(MyConstant.Chat_BG + conversationId, requireActivity()).equals("false")) {
            String filePath = saveFile.getShareData(MyConstant.Chat_BG + conversationId, requireActivity());
            Glide.with(requireActivity())
                    .asBitmap()
//                .load("https://ahxd-private.obs.cn-east-3.myhuaweicloud.com/user%2F841997676445491200%2FheadImg%2Ffirst.jpg")
                    .load(filePath)
                    .into(new SimpleTarget<Bitmap>() {
                        public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                            Drawable drawable = new BitmapDrawable(resource);
                            messageListLayout.setBackground(drawable);
                        }

                    });
        }


        cancel_Btn.setOnClickListener(new cancel_BtnClick());
        screenShot();

        btn_send.setOnClickListener(view -> {
            String content;
            content = et_sendmessage.getText().toString();
            String filterContent = forbidSensitiveWord(content);
//            String filterContent = content;
            if (chatType == EaseConstant.CHATTYPE_GROUP) {
                if (!isMoCustomer) {
                    et_sendmessage.setText("");
                    if (isAtMes(filterContent)) {
                        sendATMes(filterContent);
                    } else {
                        chatInputMenu.onSendBtnClicked(filterContent);
                    }
                } else {
//                    sendCustomerMes(filterContent);
                    if (!isMoCustomer) {//没在人工服务时才请求答案
//                        serviceAnswerData(filterContent);
                        chatInputMenu.onSendBtnClicked(filterContent);
                    }
                    chatInputMenu.onSendBtnClicked(filterContent);
                    et_sendmessage.setText("");
                }
            } else {
                chatInputMenu.onSendBtnClicked(filterContent);
                et_sendmessage.setText("");
            }

        });

        initChatData();
    }


    //消息发送成功
    @Override
    public void onChatSuccess(EMMessage message) {
        super.onChatSuccess(message);
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            if (isMoCustomer) {
                String sendTxt = "";
                if (message.getType() == EMMessage.Type.TXT) {
                    EMTextMessageBody sendBody = (EMTextMessageBody) message.getBody();
                    sendTxt = sendBody.getMessage();
                } else if (message.getType() == EMMessage.Type.IMAGE) {
                    EMImageMessageBody img = (EMImageMessageBody) message.getBody();
                    sendTxt = img.getFileName();
                }
                serviceAnswerData(mContext, saveFile.Receptionist_Answer, sendTxt);
            }
        }
    }


    @SneakyThrows
    @Override
    public void addMsgAttrsBeforeSend(EMMessage message) {
        super.addMsgAttrsBeforeSend(message);
        MyInfo myInfo = new MyInfo(requireActivity());
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            singleSendMes(message, myInfo);
        } else if (chatType == EaseConstant.CHATTYPE_GROUP) {
            if (isMoCustomer) {
                serviceSendMes(message, myInfo);
            } else {
                groupSendMes(message, myInfo);
            }
        }

        huaweiBadge(message);
    }

    // 设置自定义推送提示
    private void huaweiBadge(EMMessage message) {
        JSONObject extObject = new JSONObject();
        try {
            extObject.put("em_huawei_push_badge_class", "com.xunda.mo.main.MainActivity");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 将推送扩展设置到消息中
        message.setAttribute("em_apns_ext", extObject);
    }



    //单人聊天发送扩展字段
    private void singleSendMes(EMMessage message, MyInfo myInfo) {
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_CHAT);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        String toName = TextUtils.isEmpty(mFriendModel.getRemarkName()) ? mFriendModel.getNickname() : mFriendModel.getRemarkName();
        message.setAttribute(MyConstant.TO_NAME, toName);
        message.setAttribute(MyConstant.TO_HEAD, mFriendModel.getHeadImg());
        message.setAttribute(MyConstant.TO_LH, mFriendModel.getLightStatus());
        message.setAttribute(MyConstant.TO_VIP, mFriendModel.getVipType());
        isBurnAfterReading(mFriendModel.getFireType(), message);
    }

    //群组聊天 人工客服添加扩展
    private void serviceSendMes(EMMessage message, MyInfo myInfo) {
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MO_CUSTOMER);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
    }

    //群组聊天 通用聊天扩展字段
    private void groupSendMes(EMMessage message, MyInfo myInfo) {
        if (mGroupModel == null) {
            return;
        }
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_GROUP);
        message.setAttribute(MyConstant.SEND_NAME, sendAnonymousName(mGroupModel.getIsAnonymous()));
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        message.setAttribute(MyConstant.GROUP_NAME, mGroupModel.getGroupName());
        message.setAttribute(MyConstant.GROUP_HEAD, mGroupModel.getGroupHeadImg());
        message.setAttribute(MyConstant.IDENTITY, mGroupModel.getIdentity());
        if (mGroupModel.getIsAnonymous() == 1) {
            message.setAttribute(MyConstant.GROUP_IS_ANONYMOUS, "1");
        }
    }

    //单人聊天 Mo消息扩展字段
    private void isBurnAfterReading(int fireType, EMMessage message) {
        if (fireType==2) {
            message.setAttribute(MyConstant.FIRE_TYPE, true);
            message.setAttribute("isSleckt", false);
        }
    }

    //匿名聊天随机用户名
    private String sendAnonymousName(int IsAnonymous) {
        String nameStr;
        if (IsAnonymous == 1) {
            if (saveFile.getShareData(MyConstant.GROUP_CHAT_ANONYMOUS + conversationId, requireActivity()).equals("false")) {
                nameStr = NameResource.randomName();
                saveFile.saveShareData(MyConstant.GROUP_CHAT_ANONYMOUS + conversationId, nameStr, requireActivity());
            } else {
                nameStr = saveFile.getShareData(MyConstant.GROUP_CHAT_ANONYMOUS + conversationId, requireActivity());
            }
        } else {
            nameStr = mGroupModel.getMyNickname();
            saveFile.clearShareData(MyConstant.GROUP_CHAT_ANONYMOUS + conversationId, requireActivity());

        }
        return nameStr;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void addItemMenuAction() {
        MenuItemBean itemMenu = new MenuItemBean(0, R.id.action_chat_forward, 11, getString(R.string.action_forward));
        itemMenu.setResourceId(R.drawable.ease_chat_item_menu_forward);
        chatLayout.addItemMenu(itemMenu);
    }

    private void resetChatExtendMenu() {
        IChatExtendMenu chatExtendMenu = chatLayout.getChatInputMenu().getChatExtendMenu();
        chatExtendMenu.clear();
        chatExtendMenu.registerMenuItem(R.string.attach_picture, R.drawable.ease_chat_image_selector, R.id.extend_item_picture);
        chatExtendMenu.registerMenuItem(R.string.attach_take_pic, R.drawable.ease_chat_takepic_selector, R.id.extend_item_take_picture);
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
//            chatExtendMenu.registerMenuItem(R.string.attach_video, R.mipmap.chat_location_video, R.id.extend_item_video);
            chatExtendMenu.registerMenuItem(R.string.attach_location, R.drawable.ease_chat_location_selector, R.id.extend_item_location);
            chatExtendMenu.registerMenuItem(R.string.attach_media_call, R.drawable.em_chat_video_call_selector, R.id.extend_item_video_call);
            chatExtendMenu.registerMenuItem(R.string.attach_two_withdraw, R.mipmap.chat_two_withdraw, R.id.extend_item_two_withdraw);
            chatExtendMenu.registerMenuItem(R.string.attach_user_card, R.drawable.em_chat_user_card_selector, R.id.extend_item_user_card);
            chatExtendMenu.registerMenuItem(R.string.attach_file, R.drawable.em_chat_file_selector, R.id.extend_item_file);
        } else if (chatType == EaseConstant.CHATTYPE_GROUP) {
            if (isMoCustomer) {
                chatExtendMenu.registerMenuItem(R.string.chat_complaint, R.mipmap.chat_complaint_icon, R.id.chat_complaint);
            } else {
//                chatExtendMenu.registerMenuItem(R.string.attach_video, R.mipmap.chat_location_video, R.id.extend_item_video);
                chatExtendMenu.registerMenuItem(R.string.attach_location, R.drawable.ease_chat_location_selector, R.id.extend_item_location);
                chatExtendMenu.registerMenuItem(R.string.attach_two_withdraw, R.mipmap.chat_two_withdraw, R.id.extend_item_two_withdraw);
                chatExtendMenu.registerMenuItem(R.string.attach_user_card, R.drawable.em_chat_user_card_selector, R.id.group_item_user_card);
                chatExtendMenu.registerMenuItem(R.string.attach_horn, R.mipmap.chat_horn, R.id.attach_item_horn);
                chatExtendMenu.registerMenuItem(R.string.attach_file, R.drawable.em_chat_file_selector, R.id.extend_item_file);
            }
        }
        //添加扩展表情
        chatLayout.getChatInputMenu().getEmojiconMenu().addEmojiconGroup(EmojiconExampleGroupData.getData());
    }

    @Override
    public void initListener() {
        super.initListener();
        chatLayout.setOnChatLayoutListener(this);
        chatLayout.setOnRecallMessageResultListener(this);
    }

    public void initChatData() {
        resetChatExtendMenu();
        addItemMenuAction();

        chatLayout.getChatInputMenu().getPrimaryMenu().getEditText().setText(getUnSendMsg());
        chatLayout.turnOnTypingMonitor(false);//正在输入监控
        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));

        LiveDataBus.get().with(MyConstant.MESSAGE_CHANGE_SAVE_MESSAGE, EaseEvent.class).observe(this, new Observer<EaseEvent>() {
            @Override
            public void onChanged(EaseEvent event) {
                if (event == null) {
                    return;
                }
                if (event.isMessageChange()) {
                    if (!StringUtil.isBlank(event.message)) {
                        if (event.message.equals(conversationId)) {
                            chatLayout.getChatMessageListLayout().refreshToLatest();
                        }
                    }
                }
            }
        });

        LiveDataBus.get().with(MyConstant.Chat_BG, String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String filePath) {
                Glide.with(requireActivity()).asBitmap().load(filePath).into(new SimpleTarget<Bitmap>() {
                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                        Drawable drawable = new BitmapDrawable(resource);
                        messageListLayout.setBackground(drawable);
                    }
                });
            }
        });

        LiveDataBus.get().with(MyConstant.GROUP_CHAT_ANONYMOUS, Boolean.class).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    mGroupModel.setIsAnonymous(1);
                    sendAnonymousName(1);
                } else {
                    mGroupModel.setIsAnonymous(0);
                    sendAnonymousName(0);
                }
            }
        });




        LiveDataBus.get().with(DemoConstant.CONVERSATION_READ, EaseEvent.class).observe(this, new Observer<EaseEvent>() {
            @Override
            public void onChanged(EaseEvent event) {
                if (event == null) {
                    return;
                }
                if (event.isMessageChange()) {
                    chatLayout.getChatMessageListLayout().refreshToLatest();
                }
            }
        });


        LiveDataBus.get().with(DemoConstant.CONTACT_UPDATE, EaseEvent.class).observe(this, new Observer<EaseEvent>() {
            @Override
            public void onChanged(EaseEvent event) {
                if (event == null) {
                    return;
                }


                if (!event.isContactChange()) {
                    return;
                }

                if (StringUtil.isBlank(event.message)) {
                    return;
                }


                if (!event.message.equals(conversationId)){
                    return;
                }

                if (!StringUtil.isBlank(event.message2)) {
                    titleBarMessage.setTitle(event.message2);
                }

                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(event.message);

                if (conversation==null) {
                   return;
                }

                EMMessage lastMessage = conversation.getLastMessage();
                if (lastMessage!=null) {
                    lastMessage.setAttribute(MyConstant.TO_NAME, event.message2);
                    EMClient.getInstance().chatManager().updateMessage(lastMessage);
                }
            }
        });


        LiveDataBus.get().with(MyConstant.MESSAGE_TYPE_DOUBLE_RECALL, String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String event) {
                if (event == null) {
                    return;
                }
                if (event.equals(conversationId)) {
                    messageListLayout.refreshToLatest();
                }
            }
        });


        LiveDataBus.get().with(MyConstant.MESSAGE_TYPE_GROUP_DOUBLE_RECALL, String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String event) {
                if (event == null) {
                    return;
                }
                if (event.equals(conversationId)) {
                    messageListLayout.refreshToLatest();
                }
            }
        });


        LiveDataBus.get().with(MyConstant.BURN_AFTER_READING_SET, Boolean.class).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean event) {
                if (event) {
                    mFriendModel.setFireType(2);
                } else {
                    mFriendModel.setFireType(1);
                }
            }
        });

        //Mo消息刷新数据
        LiveDataBus.get().with(MyConstant.FIRE_REFRESH, Boolean.class).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    messageListLayout.refreshToLatest();
                }
            }
        });

        //删除好友会话
        LiveDataBus.get().with(MyConstant.Dele_Friend, String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String conversationId) {
                if (TextUtils.isEmpty(conversationId)) {
                    DemoHelper.getInstance().getChatManager().deleteConversation(conversationId, true);
//                messageListLayout.refreshToLatest();
                }
            }
        });


        LiveDataBus.get().with(MyConstant.MESS_TYPE_GROUP_HORN, EMMessage.class).observe(this, new Observer<EMMessage>() {
            @Override
            public void onChanged(EMMessage message) {
                List<EMMessage> msgs = new ArrayList<>();
                msgs.add(message);
                ChatFragment.this.onMarquee(msgs);
            }
        });


        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            AddFriendMethod(getActivity(), saveFile.Friend_info_Url);
        } else if (chatType == EaseConstant.CHATTYPE_GROUP) {
            if (!isMoCustomer) {
                GroupMethod(getActivity(), saveFile.Group_MyGroupInfo_Url);
            } else {
//                isService();//是否在人工服务
            }
        }
        sensitiveWord(getActivity(), saveFile.SensitiveWord);
    }

    private void removeHornMes(List<EMMessage> msgs, ConstraintLayout Con) {
        if (!msgs.isEmpty()) {
            msgs.remove(0);
            if (msgs.isEmpty()) {
                Con.setVisibility(View.GONE);
            } else {
                onMarquee(msgs);
            }
        }
    }

    private void onMarquee(List<EMMessage> msgs) {
        horn_Con.setVisibility(View.VISIBLE);
        EMMessage msg = msgs.get(0);
        String sendHead = msg.getStringAttribute(MyConstant.SEND_HEAD, "");
        Glide.with(requireActivity()).load(sendHead).into(head_Img);
        String sendName = msg.getStringAttribute(MyConstant.SEND_NAME, "");
        horn_Name.setText(sendName);
        EMTextMessageBody hornStr = (EMTextMessageBody) msgs.get(0).getBody();
        horn_Txt.setFocusable(true);
        horn_Txt.setFocusableInTouchMode(true);
        horn_Txt.setMarqueeVelocity(1);
        horn_Txt.setSelected(true);
        horn_Txt.setText(hornStr.getMessage());
        horn_Txt.setOnMarqueeCompleteListener(() ->
                removeHornMes(msgs, horn_Con));
    }

    private void showDeliveryDialog() {
        new FullEditDialogFragment.Builder((BaseActivity) mContext)
                .setTitle(R.string.em_chat_group_read_ack)
                .setOnConfirmClickListener(R.string.em_chat_group_read_ack_send, (view, content) -> chatLayout.sendTextMessage(content, true))
                .setConfirmColor(R.color.em_color_brand)
                .setHint(R.string.em_chat_group_read_ack_hint)
                .show();
    }

    private void showSelectDialog() {
        new DemoListDialogFragment.Builder((BaseActivity) mContext)
                .setTitle(R.string.em_single_call_type)
                .setData(calls)
                .setCancelColorRes(R.color.black)
                .setWindowAnimations(R.style.animate_dialog)
                .setOnItemClickListener((view, position) -> {
                    switch (position) {
                        case 0:
                            EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VIDEO_CALL, conversationId, setSingleVideoExt());
                            break;
                        case 1:
                            EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VOICE_CALL, conversationId, setSingleVideoExt());
                            break;
                    }
                })
                .show();
    }

    //单人视频语音聊天添加扩展字段
    private Map<String, Object> setSingleVideoExt() {
        MyInfo myInfo = new MyInfo(requireActivity());
        Map<String, Object> ext = new HashMap<>();
        ext.put(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_CHAT);
        ext.put(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        ext.put(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        ext.put(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        ext.put(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        String toName = TextUtils.isEmpty(mFriendModel.getRemarkName()) ? mFriendModel.getNickname() : mFriendModel.getRemarkName();
        ext.put(MyConstant.TO_NAME, toName);
        ext.put(MyConstant.TO_HEAD, mFriendModel.getHeadImg());
        ext.put(MyConstant.TO_LH, mFriendModel.getLightStatus());
        ext.put(MyConstant.TO_VIP, mFriendModel.getVipType());
        return ext;
    }

    //通知开通VIP
    private void changeVip() {
        new SimpleDialogFragment.Builder((BaseActivity) mContext)
                .setTitle("提示通知")
                .showContent(true)
                .setContent("该功能为会员特权功能，请开通会员后使用")
                .setOnConfirmClickListener(view -> Me_VIP.actionStart(mContext))
                .showCancelButton(true)
                .show();
    }

    //撤回
    private void changeRecall() {
        new SimpleDialogFragment.Builder((BaseActivity) mContext)
                .setTitle("双向撤回")
                .showContent(true)
                .setContent("聊天记录一键双向撤回，同时删除你和对方设备上的所有聊天记录，撤回数据多次覆盖删除，不可恢复")
                .setOnConfirmClickListener(view -> doubleRecall())
                .showCancelButton(true)
                .show();
    }

    //群组撤回
    private void changeGroupRecall() {
        new SimpleDialogFragment.Builder((BaseActivity) mContext)
                .setTitle("双向撤回")
                .showContent(true)
                .setContent("聊天记录一键双向撤回，同时删除你和对方设备上的所有聊天记录，撤回数据多次覆盖删除，不可恢复")
                .setOnConfirmClickListener(view -> doubleGroupRecall())
                .showCancelButton(true)
                .show();
    }

    @Override
    public void onUserAvatarClick(String username) {
        MyInfo myInfo = new MyInfo(mContext);
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            if (!TextUtils.equals(username, DemoHelper.getInstance().getCurrentUser())) {
                EaseUser user = DemoHelper.getInstance().getUserInfo(username);
                if (user == null) {
                    user = new EaseUser(username);
                }
                boolean isFriend = DemoHelper.getInstance().getModel().isContact(username);
                if (isFriend) {
                    user.setContact(0);
                } else {
                    user.setContact(3);
                }
//            ContactDetailActivity.actionStart(mContext, user);
                String addType = "8";
                ChatFriend_Detail.actionStart(mContext, username, user, addType);
            } else {
//            UserDetailActivity.actionStart(mContext, null, null);
                UserDetail_Set.actionStart(mContext);
            }
        } else if (chatType == EaseConstant.CHATTYPE_GROUP) {
            if (isMoCustomer) {
                return;
            }
            int isAnonymous = mGroupModel.getIsAnonymous();
            int issProtect = mGroupModel.getIsProtect();
            if (isAnonymous == 1 || issProtect == 1) {
                return;
            }
            String userID = "";
            String myUsername = myInfo.getUserInfo().getHxUserName();
            if (TextUtils.equals(username, myUsername)) {
                if (TextUtils.isEmpty(myUsername)) {
                    return;
                }
                UserDetail_Set.actionStart(mContext);
            } else {
                if (TextUtils.isEmpty(username) || mGroupModel == null) {
                    return;
                }
                String url = saveFile.Group_UserInfo_Url;
                String groupId = mGroupModel.getGroupId();
                groupFriendMethod(requireActivity(), url, username, groupId);
            }
        }

    }

    @Override
    public void onUserAvatarLongClick(String username) {
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            if (!TextUtils.equals(username, DemoHelper.getInstance().getCurrentUser())) {
//                getMyGroupNicknameByHxIdOrGroupId(username,mGroupModel.getGroupId());
                if (mGroupModel == null) {
                    return;
                }
                showMore(mContext, et_sendmessage, username);
            }
        }

    }


    private class etOnKey_Listener implements View.OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                deleAtMes(et_sendmessage.getText().toString(), et_sendmessage);
            }
            return false;
        }
    }


    @Override
    public boolean onBubbleLongClick(View v, EMMessage message) {
        return false;
    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!messageListLayout.isGroupChat()) {
            return;
        }
        if (TextUtils.isEmpty(s)) {
            return;
        }
//        if (count == 1 && "@".equals(String.valueOf(s.charAt(before)))) {
        if ((start + count) >= s.length() && !TextUtils.isEmpty(s.toString())) {
            String atStr = s.toString().substring(s.length() - 1);
            if (TextUtils.equals(atStr, "@")) {
                if (!isMoCustomer && mGroupModel != null) {
                    GroupAllMembers_At.actionStartForResult(ChatFragment.this, bigGroupModel, REQUEST_CODE_SELECT_AT_USER);
//                    PickAtUserActivity.actionStartForResult(ChatFragment.this, conversationId, REQUEST_CODE_SELECT_AT_USER);
                }
            }
        }


    }


    @Override
    protected void selectVideoFromLocal() {
        super.selectVideoFromLocal();
        Intent intent = new Intent(getActivity(), ImageGridActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
    }

    @Override
    public boolean onBubbleClick(EMMessage message) {
        return false;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onChatExtendMenuItemClick(View view, int itemId) {
        super.onChatExtendMenuItemClick(view, itemId);
        switch (itemId) {
            case R.id.extend_item_video_call:
                showSelectDialog();
                break;
            case R.id.extend_item_conference_call:
                Intent intent = new Intent(getContext(), ConferenceInviteActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(DemoConstant.EXTRA_CONFERENCE_GROUP_ID, conversationId);
                requireContext().startActivity(intent);
                break;
            case R.id.extend_item_delivery://群消息回执
                showDeliveryDialog();
                break;
            case R.id.extend_item_user_card://单人发名片
                EMLog.d(TAG, "select user card");
                Chat_SelectUserCard.actionStartSingle(requireActivity(), conversationId, "1");
                break;
            case R.id.group_item_user_card://群发名片
                Chat_SelectUserCard.actionStartGroup(requireActivity(), conversationId, "2", bigGroupModel);
                break;
            case R.id.extend_item_two_withdraw:
                if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                    singleRecall();
                } else {
                    groupRecall();
                }
                break;
            case R.id.chat_complaint:
                if (MoIsComplaint()) {
                    ChatComplaint.actionStart(requireContext());
                } else {
                    Toast.makeText(requireContext(), "当前无可投诉客服", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.attach_item_horn:
                groupHorn();
                break;
        }
    }

    //是否是VIP VIP使用群喇叭
    private void groupHorn() {
        MyInfo myInfo = new MyInfo(requireContext());
        int vipType = myInfo.getUserInfo().getVipType();
        if (vipType == 0) {
            changeVip();
        } else if (vipType == 1) {
            Group_Horn.actionStart(requireActivity(), bigGroupModel);
        }
    }

    //能否投诉客服
    private boolean MoIsComplaint() {
        EMMessage conMsg = messageListLayout.getCurrentConversation().getLatestMessageFromOthers();
        if (conMsg == null) {
            return false;
        }
        String sendName = conMsg.getStringAttribute(MyConstant.SEND_NAME, "");
        return !TextUtils.isEmpty(sendName);
    }

    private void singleRecall() {
        MyInfo myInfo = new MyInfo(requireContext());
        int vipType = myInfo.getUserInfo().getVipType();
        if (vipType == 0) {
            changeVip();
        } else if (vipType == 1) {
            changeRecall();
        }
    }

    private void groupRecall() {
        MyInfo myInfo = new MyInfo(requireContext());
        int vipType = myInfo.getUserInfo().getVipType();
        if (vipType == 0) {
            changeVip();
        } else if (vipType == 1) {
            int myIdentity = mGroupModel.getIdentity();
            if (myIdentity == 1 || myIdentity == 2) {
                changeGroupRecall();
            } else {
                Toast.makeText(requireContext(), "群主或管理员才可撤回群组消息", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onChatError(int code, String errorMsg) {
        if (infoListener != null) {
            infoListener.onChatError(code, errorMsg);
        }
    }

    @Override
    public void onOtherTyping(String action) {
        if (infoListener != null) {
            infoListener.onOtherTyping(action);
        }
    }

    HashMap<String, Object> userMap = new HashMap<>();

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_AT_USER:
                    if (data != null) {
                        HashMap backMap = (HashMap) data.getSerializableExtra("username");
//                        String username = data.getStringExtra("username");
                        editAtMes(backMap);
                    }
                    break;
                case REQUEST_CODE_SELECT_VIDEO: //send the video
                    if (data != null) {
                        int duration = data.getIntExtra("dur", 0);
                        String videoPath = data.getStringExtra("path");
                        String uriString = data.getStringExtra("uri");
                        EMLog.d(TAG, "path = " + videoPath + " uriString = " + uriString);
                        if (!TextUtils.isEmpty(videoPath)) {
                            chatLayout.sendVideoMessage(Uri.parse(videoPath), duration);
                        } else {
                            Uri videoUri = UriUtils.getLocalUriFromString(uriString);
                            chatLayout.sendVideoMessage(videoUri, duration);
                        }
                    }
                    break;

            }
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        //保存未发送的文本消息内容
        if (mContext != null && mContext.isFinishing()) {
            if (chatLayout.getChatInputMenu() != null) {
                saveUnSendMsg(chatLayout.getInputContent());
                LiveDataBus.get().with(DemoConstant.MESSAGE_NOT_SEND).postValue(true);
            }
        }
    }


    //================================== for video and voice start ====================================

    // 保存未发送的文本消息内容
    private void saveUnSendMsg(String content) {
        DemoHelper.getInstance().getModel().saveUnSendMsg(conversationId, content);
    }

    private String getUnSendMsg() {
        return DemoHelper.getInstance().getModel().getUnSendMsg(conversationId);
    }

    @Override
    public void onPreMenu(EasePopupWindowHelper helper, EMMessage message) {
        //默认两分钟后，即不可撤回
        if (System.currentTimeMillis() - message.getMsgTime() > 2 * 60 * 1000) {
            helper.findItemVisible(R.id.action_chat_recall, false);
        }
        EMMessage.Type type = message.getType();
        helper.findItemVisible(R.id.action_chat_forward, false);
        switch (type) {
            case TXT:
                if (!message.getBooleanAttribute(DemoConstant.MESSAGE_ATTR_IS_VIDEO_CALL, false)
                        && !message.getBooleanAttribute(DemoConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    helper.findItemVisible(R.id.action_chat_forward, true);
                }
                break;
            case IMAGE:
                helper.findItemVisible(R.id.action_chat_forward, true);
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItemBean item, EMMessage message) {
        switch (item.getItemId()) {
            case R.id.action_chat_forward:
                forWard(message);
                ForwardMessageActivity.actionStart(mContext, message.getMsgId(), message);
                return true;
            case R.id.action_chat_delete:
                showDeleteDialog(message);
                return true;
            case R.id.action_chat_recall:
                //2分钟内消息撤回添加扩展字段
                chatOrGroupRecallMess(message);
                return true;
        }
        return false;
    }


    private void forWard(EMMessage message) {
        MyInfo myInfo = new MyInfo(requireActivity());
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            singleSendMes(message, myInfo);
        } else if (chatType == EaseConstant.CHATTYPE_GROUP) {
            if (isMoCustomer) {
                serviceSendMes(message, myInfo);
            } else {
                groupSendMes(message, myInfo);
            }
        }
    }

    //两分钟内撤回发送消息
    private void chatOrGroupRecallMess(EMMessage message) {
        chatLayout.recallMessage(message);
    }

    private void showProgressBar() {
        View view = View.inflate(mContext, R.layout.demo_layout_progress_recall, null);
        dialog = new Dialog(mContext, R.style.dialog_recall);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(view, layoutParams);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void showDeleteDialog(EMMessage message) {
        new SimpleDialogFragment.Builder((BaseActivity) mContext)
                .setTitle(getString(R.string.em_chat_delete_title))
                .setConfirmColor(R.color.red)
                .setOnConfirmClickListener(getString(R.string.delete), view -> chatLayout.deleteMessage(message))
                .showCancelButton(true)
                .show();
    }

    public void setOnFragmentInfoListener(OnFragmentInfoListener listener) {
        this.infoListener = listener;
    }

    @Override
    public void recallSuccess(EMMessage message) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        addMsgAttrsBeforeSend(message);
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.Message_Recall);
        EMClient.getInstance().chatManager().updateMessage(message);
    }

    @Override
    public void recallFail(int code, String errorMsg) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public interface OnFragmentInfoListener {
        void onChatError(int code, String errorMsg);

        void onOtherTyping(String action);
    }

    private ChatUserBean.DataDTO mFriendModel;
    public void AddFriendMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("friendHxName", conversationId);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                ChatUserBean model = new Gson().fromJson(result, ChatUserBean.class);
                if (model==null) {
                    return;
                }
                mFriendModel = model.getData();

                if (mFriendModel==null) {
                    return;
                }

                String remarkName = mFriendModel.getRemarkName();
                friendName = StringUtil.isBlank(remarkName) ? mFriendModel.getNickname() : remarkName;
                friendHeader = mFriendModel.getHeadImg();
                titleBarMessage.setTitle(friendName);

                insertConversionExdInfoInFriend();
                updateDBData();
                messageListLayout.getMessageAdapter().notifyDataSetChanged();
            }

            @Override
            public void failed(String... args) {

            }
        });
    }



    private void updateDBData() {
        EmUserEntity entity = new EmUserEntity();
        entity.setUsername(conversationId);
        // 正则表达式，判断首字母是否是英文字母
        String nickName = TextUtils.isEmpty(mFriendModel.getRemarkName()) ? mFriendModel.getNickname() : mFriendModel.getRemarkName();
        entity.setNickname(nickName);
        String pinyin = PinyinUtils.getPingYin(nickName);
        String sortString = pinyin.substring(0, 1).toUpperCase();
        if (sortString.matches("[A-Z]")) {
            entity.setInitialLetter(sortString);
        } else {
            entity.setInitialLetter("#");
        }
        entity.setAvatar(mFriendModel.getHeadImg());
        entity.setBirth("");
        entity.setContact(0);//朋友属性 4是没有预设置
        entity.setEmail("");
        entity.setGender(0);
        entity.setBirth("");
        entity.setSign("");
        entity.setPhone("");
        JSONObject obj = new JSONObject();
        try {
            obj.put(MyConstant.LIGHT_STATUS, mFriendModel.getLightStatus());
            obj.put(MyConstant.VIP_TYPE, mFriendModel.getVipType());
            obj.put(MyConstant.USER_NUM, mFriendModel.getUserNum());
            obj.put(MyConstant.USER_ID, mFriendModel.getUserId());
            obj.put(MyConstant.REMARK_NAME, mFriendModel.getRemarkName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        entity.setExt(obj.toString());


        //通知callKit更新头像昵称
        EaseCallUserInfo info = new EaseCallUserInfo(mFriendModel.getNickname(), mFriendModel.getHeadImg());
        info.setUserId(info.getUserId());
        EaseLiveDataBus.get().with(EaseCallKitUtils.UPDATE_USERINFO).postValue(info);

        //更新本地数据库信息
        DemoHelper.getInstance().getModel().insert(entity);
        //更新本地联系人列表
        DemoHelper.getInstance().updateContactList();
    }


    //往好友会话列表添加扩展字段
    private void insertConversionExdInfoInFriend() {
        EMConversation currentConversation = EMClient.getInstance().chatManager().getConversation(conversationId);
        if (currentConversation==null) {
            return;
        }

        String extField = currentConversation.getExtField();
        JSONObject jsonObject = null;
        if (!StringUtil.isBlank(extField)) {
            try {
                jsonObject = new JSONObject(extField);
                jsonObject.put("isInsertGroupOrFriendInfo", true);
                jsonObject.put("showImg", friendHeader);
                jsonObject.put("showName", friendName);
                jsonObject.put("vipType", mFriendModel.getVipType());
            } catch (JSONException e) {
                jsonObject = getJsonObjectFriend();
            }
        }else{
            jsonObject = getJsonObjectFriend();
        }

        if (jsonObject==null) {
            return;
        }
        currentConversation.setExtField(jsonObject.toString());
    }

    @NonNull
    private JSONObject getJsonObjectFriend() {
        JSONObject jsonObject;
        jsonObject  = new JSONObject();
        try {
            jsonObject.put("isInsertGroupOrFriendInfo", true);
            jsonObject.put("showImg", friendHeader);
            jsonObject.put("showName", friendName);
            jsonObject.put("vipType", mFriendModel.getVipType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    private GruopInfo_Bean bigGroupModel;
    private GruopInfo_Bean.DataDTO mGroupModel;

    public void GroupMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupHxId", conversationId);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                bigGroupModel = new Gson().fromJson(result, GruopInfo_Bean.class);

                if (bigGroupModel==null) {
                    return;
                }

                mGroupModel = bigGroupModel.getData();
                if (mGroupModel==null) {
                    return;
                }

                String topStr = mGroupModel.getGroupNotice();
                setTopView(topStr);
                showGroupImg = mGroupModel.getGroupHeadImg();
                showGroupName = mGroupModel.getGroupName();
                titleBarMessage.setTitle(String.format("%1$s(%2$s)", showGroupName, mGroupModel.getGroupUserCount()));
                sendAnonymousName(mGroupModel.getIsAnonymous());
                GroupMemberListMethod();
            }

            @Override
            public void failed(String... args) {

            }
        });
    }


    private class cancel_BtnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            top_Constraint.setVisibility(View.GONE);
        }
    }


    public void GroupMemberListMethod() {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", mGroupModel.getGroupId());
        xUtils3Http.post(mContext, saveFile.Group_UserList_Url, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                insertConversionExdInfoInGroup(result);
            }

            @Override
            public void failed(String... args) {

            }
        });
    }


    //往群会话列表添加扩展字段
    private void insertConversionExdInfoInGroup(String jsonMemberList) {
        if (mGroupModel == null) {
            return;
        }

        EMConversation currentConversation = EMClient.getInstance().chatManager().getConversation(conversationId);
        if (currentConversation==null) {
            return;
        }

        String extField = currentConversation.getExtField();
        JSONObject jsonObject = null;
        if (!StringUtil.isBlank(extField)) {
            try {
                jsonObject = new JSONObject(extField);
                jsonObject.put("isInsertGroupOrFriendInfo", true);
                jsonObject.put("showImg", showGroupImg);
                jsonObject.put("showName", showGroupName);
                jsonObject.put("groupMemberList", jsonMemberList);
            } catch (JSONException e) {
                jsonObject = getJsonObjectGroup(jsonMemberList);
            }

        }else{
            jsonObject = getJsonObjectGroup(jsonMemberList);
        }

        if (jsonObject==null) {
            return;
        }

        currentConversation.setExtField(jsonObject.toString());
        messageListLayout.getMessageAdapter().notifyDataSetChanged();
    }

    @NonNull
    private JSONObject getJsonObjectGroup(String jsonMemberList) {
        JSONObject jsonObject;
        jsonObject  = new JSONObject();
        try {
            jsonObject.put("isInsertGroupOrFriendInfo", true);
            jsonObject.put("showImg", showGroupImg);
            jsonObject.put("showName", showGroupName);
            jsonObject.put("groupMemberList", jsonMemberList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    //是否有置顶消息
    private void setTopView(String topStr) {
        if (!TextUtils.isEmpty(topStr)) {
            top_Constraint.setVisibility(View.VISIBLE);
            top_Txt.setText(topStr);
        }
    }


    //问题
    public void serviceAnswerData(final Context context, String baseUrl, String question) {
        Map<String, Object> map = new HashMap<>();
        map.put("question", question);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
            }

            @Override
            public void failed(String... args) {
            }
        });
    }




    //发送单聊撤回消息
    private void doubleRecall() {
        removeMes();
        String mes_type = MyConstant.MESSAGE_TYPE_DOUBLE_RECALL;
        sendMes(mes_type);
        messageListLayout.refreshToLatest();
    }


    //发送群里撤回消息
    private void doubleGroupRecall() {
        removeMes();
        String mes_type = MyConstant.MESSAGE_TYPE_GROUP_DOUBLE_RECALL;
        sendGroupMes(mes_type);
        messageListLayout.refreshToLatest();
    }



    private void removeMes() {
        EMConversation conversation = messageListLayout.getCurrentConversation();
        conversation.clearAllMessages();
    }

    //发送消息通用 注意消息type
    private void sendMes(String Mes_Type) {
        if (mFriendModel==null){
            return;
        }

        MyInfo myInfo = new MyInfo(mContext);
        String conversationId = mFriendModel.getHxUserName();
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        EMTextMessageBody txtBody = new EMTextMessageBody("");
        message.addBody(txtBody);
        message.setTo(conversationId);
        message.setChatType(EMMessage.ChatType.Chat);
        message.setAttribute(MyConstant.MESSAGE_TYPE, Mes_Type);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        String toName = TextUtils.isEmpty(mFriendModel.getRemarkName()) ? mFriendModel.getNickname() : mFriendModel.getRemarkName();
        message.setAttribute(MyConstant.TO_NAME, toName);
        message.setAttribute(MyConstant.TO_HEAD, mFriendModel.getHeadImg());
        message.setAttribute(MyConstant.TO_LH, mFriendModel.getLightStatus());
        message.setAttribute(MyConstant.TO_VIP, mFriendModel.getVipType());
        EMClient.getInstance().chatManager().sendMessage(message);
    }




    //发送群撤回消息
    private void sendGroupMes( String mes_Type) {
        if (mGroupModel == null) {
            return;
        }
        String conversationId = mGroupModel.getGroupHxId();
        MyInfo myInfo = new MyInfo(mContext);
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        EMTextMessageBody txtBody = new EMTextMessageBody("");
        message.addBody(txtBody);
        message.setTo(conversationId);
        message.setChatType(EMMessage.ChatType.GroupChat);
        message.setAttribute(MyConstant.MESSAGE_TYPE, mes_Type);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        message.setAttribute(MyConstant.GROUP_NAME, mGroupModel.getGroupName());
        message.setAttribute(MyConstant.GROUP_HEAD, mGroupModel.getGroupHeadImg());
        EMClient.getInstance().chatManager().sendMessage(message);
    }


    public void startAudio() {
        //监听授权
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {//录音权限
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[0]);
            ActivityCompat.requestPermissions(requireActivity(), permissions, 1);
        } else {
            primaryMenu.showVoiceStatus();
        }
    }


    private void screenShot() {
        ScreenShotViewModel screenShotViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(MyApplication.getInstance())).get(ScreenShotViewModel.class);
        screenShotViewModel.registerContentObserver();
        screenShotViewModel.getDataChanged().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    screenShotViewModel.onCleared();//收到观察消息就注销 不然会收到多条通知
                    if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                        sendScreenShot();
                    }
                    if (chatType == EaseConstant.CHATTYPE_GROUP) {
                        sendScreenShot();
                    }
                }
            }
        });

        //接收到截屏通知后 主动获取截屏照片
        screenShotViewModel.getScreentShotInfoData().observe(this, new Observer<ScreentShotInfo>() {
            @Override
            public void onChanged(ScreentShotInfo screentShotInfo) {

            }
        });
    }

    //发送截屏CMD消息
    private void sendScreenShot() {
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        String action = "发送截屏CMD消息";//action可以自定义
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
        cmdMsg.setTo(conversationId);
        cmdMsg.addBody(cmdBody);
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            cmdMsg.setChatType(EMMessage.ChatType.Chat);
            cmdMsg.setAttribute(MyConstant.CMD_MESSAGE_TYPE_ISSCREENSHORTS, true);
        } else if (chatType == EaseConstant.CHATTYPE_GROUP) {
            cmdMsg.setChatType(EMMessage.ChatType.GroupChat);
            cmdMsg.setAttribute(MyConstant.CMD_MESSAGE_TYPE_GROUPISSCREENSHORTS, true);
        }
        addMsgAttrsBeforeSend(cmdMsg);
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }


    //发送@消息
    @SuppressLint("RestrictedApi")
    private void sendATMes(String content) {
        MyInfo myInfo = new MyInfo(mContext);
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        EMTextMessageBody txtBody = new EMTextMessageBody(content);
        message.addBody(txtBody);
        message.setTo(conversationId);
        message.setChatType(EMMessage.ChatType.GroupChat);
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TXT_TYPE_AT_GROUP);
        message.setAttribute(MyConstant.SEND_NAME, mGroupModel.getMyNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        String atName = aTMesKeyName(aTMesMap(content));
        message.setAttribute(MyConstant.AT_NAME, atName);
        String atValue = aTMesValueName(aTMesMap(content));
        message.setAttribute(MyConstant.AT_ID, atValue);
        message.setAttribute(MyConstant.GROUP_NAME, mGroupModel.getGroupName());
        message.setAttribute(MyConstant.GROUP_HEAD, mGroupModel.getGroupHeadImg());
        DemoHelper.getInstance().getChatManager().sendMessage(message);
        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));//加了这个发出的消息才会实时刷新
    }


    //at页面返回的用户Map
    private void editAtMes(HashMap atMap) {
        if (atMap.isEmpty()) {
            return;
        }
        userMap.putAll(atMap);
        et_sendmessage.setTextColor(requireContext().getColor(R.color.yellowfive));
        Iterator at_user = atMap.keySet().iterator();
        String userFristKey = (String) at_user.next();
        inputAtUserName(et_sendmessage, userFristKey, false);
        while (at_user.hasNext()) {
            String userKey = (String) at_user.next();
            inputAtUserName(et_sendmessage, userKey, true);
        }
    }

    //是否加@
    private void inputAtUserName(EditText editText, String nick, boolean autoAddAtSymbol) {
        if (autoAddAtSymbol)
            insertText(editText, AT_PREFIX + nick + AT_SUFFIX);
        else
            insertText(editText, nick + AT_SUFFIX);
    }

    // 插入at数据到String后面
    private void insertText(@NonNull EditText edit, String text) {
        if (edit.isFocused()) {
            edit.getText().insert(edit.getSelectionStart(), text);
        } else {
            edit.getText().insert(edit.getText().length(), text);
        }
    }

    //发送时候判断是否是at消息
    private boolean isAtMes(@NonNull String atStr) {
        if (atStr.contains("@")) {
            String notAt = atStr.replace("@", "");
            String[] atList = notAt.split(" ");
            HashMap<String, Object> atMap = new HashMap<>();
            for (String s : atList) {
                atMap.put(s, "");
            }
            for (String atKey : atMap.keySet()) {
                if (userMap.containsKey(atKey)) {
                    return true;
                }
            }
        }
        return false;
    }


    //发送at时的ATMap数据
    private HashMap aTMesMap(String atStr) {
        String notAt = "";
        if (atStr.contains("@")) {
            notAt = atStr.replace("@", "");
        }
        String[] atList = notAt.split(" ");
        HashMap<String, Object> atMap = new HashMap<>();
        for (String s : atList) {
            atMap.put(s, "");
        }
        HashMap newMap = new HashMap();
        for (String userKey : atMap.keySet()) {
            if (userMap.containsKey(userKey)) {
                newMap.put(userKey, userMap.get(userKey));
            }
        }
        return newMap;
    }

    private String aTMesKeyName(HashMap atMap) {
        StringBuilder atNameStr = new StringBuilder();
        for (Object o : atMap.keySet()) {
            String userKey = (String) o;
            atNameStr.append(userKey).append(",");
        }
        return atNameStr.toString();
    }

    private String aTMesValueName(HashMap atMap) {
        StringBuilder atNameStr = new StringBuilder();

        for (Object o : atMap.values()) {
            String userKey = (String) o;
//            EaseAtMessageHelper.get().addAtUser(userKey);
            atNameStr.append(userKey).append(",");
        }
        return atNameStr.toString();
    }

    private void deleAtMes(String atStr, EditText etText) {
        StringBuilder stringBuilder = new StringBuilder(atStr);
        if (atStr.contains("@")) {
            int atDex = atStr.lastIndexOf("@");
            int lastDex = atStr.length() - 1;
            stringBuilder.replace(atDex, lastDex, "");
            etText.setText(stringBuilder.toString());
            etText.setSelection(stringBuilder.length());
        }
    }


    /**
     * 屏蔽敏感词汇
     *
     * @param sendBeforeMessage
     */
    private String forbidSensitiveWord(String sendBeforeMessage) {
        if (!ListUtils.isEmpty(chat_sensitiveWordBean.getData())) {
            for (int i = 0; i < chat_sensitiveWordBean.getData().size(); i++) {
                if (sendBeforeMessage.contains(chat_sensitiveWordBean.getData().get(i))) {
                    sendBeforeMessage = sendBeforeMessage.replaceAll(chat_sensitiveWordBean.getData().get(i), "***");
                }
            }
            return sendBeforeMessage;

        }
        return "";
    }

    private void showMore(final Context mContext, View view, String username) {
        View contentView = View.inflate(mContext, R.layout.at_mes_popup, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        MorePopup.setWidth(RadioGroup.LayoutParams.MATCH_PARENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
        MorePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        TextView at_Txt = contentView.findViewById(R.id.at_Txt);
        TextView cancel_txt = contentView.findViewById(R.id.cancel_txt);
        at_Txt.setOnClickListener(v -> {
            MorePopup.dismiss();
            GroupAtName(mContext, saveFile.Group_myGroupNicknameByHx, mGroupModel.getGroupId(), username);
        });

        cancel_txt.setOnClickListener(v -> MorePopup.dismiss());
    }


    public void GroupAtName(Context context, String baseUrl, String groupID, String hxId) {
        Map<String, Object> map = new HashMap<>();
        map.put("hxId", hxId);
        map.put("groupId", groupID);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                baseDataModel baseData = new Gson().fromJson(result, baseDataModel.class);
                et_sendmessage.setTextColor(context.getColor(R.color.yellowfive));
                inputAtUserName(et_sendmessage, baseData.getData(), true);
                userMap.put(baseData.getData(), hxId);
            }

            @Override
            public void failed(String... args) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void groupFriendMethod(Context mContext, String baseUrl, String username, String groupId) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("hxUserName", username);
        xUtils3Http.get(mContext, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Group_Details_Bean model = new Gson().fromJson(result, Group_Details_Bean.class);
                String userID = model.getData().getGroupUserId();
                GroupFriend_Detail.actionStart(mContext, userID, username, bigGroupModel);
            }

            @Override
            public void failed(String... args) {
            }
        });
    }

    Chat_SensitiveWordBean chat_sensitiveWordBean;
    @SuppressLint("SetTextI18n")
    public void sensitiveWord(Context mContext, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        xUtils3Http.get(mContext, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                 chat_sensitiveWordBean = new Gson().fromJson(result, Chat_SensitiveWordBean.class);
            }

            @Override
            public void failed(String... args) {
            }
        });
    }


}


