
package com.xunda.mo.hx.section.chat.fragment;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMUserInfo;
import com.hyphenate.easecallkit.EaseCallKit;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.modules.chat.EaseChatInputMenu;
import com.hyphenate.easeui.modules.chat.EaseChatMessageListLayout;
import com.hyphenate.easeui.modules.chat.interfaces.IChatExtendMenu;
import com.hyphenate.easeui.modules.chat.interfaces.IChatPrimaryMenu;
import com.hyphenate.easeui.modules.chat.interfaces.OnRecallMessageResultListener;
import com.hyphenate.easeui.modules.menu.EasePopupWindowHelper;
import com.hyphenate.easeui.modules.menu.MenuItemBean;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.UriUtils;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.common.model.EmojiconExampleGroupData;
import com.xunda.mo.hx.section.base.BaseActivity;
import com.xunda.mo.hx.section.chat.activicy.ForwardMessageActivity;
import com.xunda.mo.hx.section.chat.activicy.ImageGridActivity;
import com.xunda.mo.hx.section.chat.activicy.PickAtUserActivity;
import com.xunda.mo.hx.section.chat.viewmodel.MessageViewModel;
import com.xunda.mo.hx.section.conference.ConferenceInviteActivity;
import com.xunda.mo.hx.section.dialog.DemoListDialogFragment;
import com.xunda.mo.hx.section.dialog.FullEditDialogFragment;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.main.baseView.MyApplication;
import com.xunda.mo.main.chat.activity.ChatComplaint;
import com.xunda.mo.main.chat.activity.ChatFriend_Detail;
import com.xunda.mo.main.chat.activity.Chat_SelectUserCard;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.group.activity.GroupFriend_Detail;
import com.xunda.mo.main.group.activity.Group_Horn;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.main.info.NameResource;
import com.xunda.mo.main.me.activity.Me_VIP;
import com.xunda.mo.main.me.activity.UserDetail_Set;
import com.xunda.mo.model.ChatUserBean;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.model.baseDataModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.MarqueeTextView;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.kotlin.ScreenShotViewModel;
import com.xunda.mo.staticdata.kotlin.ScreentShotInfo;
import com.xunda.mo.staticdata.xUtils3Http;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;


public class ChatFragment extends MyEaseChatFragment implements OnRecallMessageResultListener {
    private static final String TAG = ChatFragment.class.getSimpleName();
    private MessageViewModel viewModel;
    protected ClipboardManager clipboard;

    private static final int REQUEST_CODE_SELECT_AT_USER = 15;
    private static final String[] calls = {"视频通话", "语音通话"};
    private OnFragmentInfoListener infoListener;
    private Dialog dialog;
    EaseChatMessageListLayout messageListLayout;
    private IChatPrimaryMenu primaryMenu;
    private EaseTitleBar titleBarMessage;

    @Override
    public void initView() {
        super.initView();
        clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        viewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        //获取到聊天列表控件
        messageListLayout = chatLayout.getChatMessageListLayout();
        //设置聊天列表背景
//        messageListLayout.setBackground(new ColorDrawable(Color.parseColor("#ffefefef")));
        messageListLayout.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.greyfive));
        //设置默认头像
//        messageListLayout.setAvatarDefaultSrc(ContextCompat.getDrawable(mContext, R.drawable.ease_default_avatar));
        //设置头像形状：0为默认，1为圆形，2为方形
        messageListLayout.setAvatarShapeType(2);
        //设置文本字体大小
        messageListLayout.setItemTextSize((int) EaseCommonUtils.sp2px(mContext, 14));
        //设置文本字体颜色
        messageListLayout.setItemTextColor(ContextCompat.getColor(mContext, R.color.blacktitle));
        //设置时间线的背景
//        messageListLayout.setTimeBackground(ContextCompat.getDrawable(mContext, R.color.gray_normal));
        //设置时间线的文本大小
        //messageListLayout.setTimeTextSize((int) EaseCommonUtils.sp2px(mContext, 18));
        //设置时间线的文本颜色
        //messageListLayout.setTimeTextColor(ContextCompat.getColor(mContext, R.color.black));
        //设置聊天列表样式：两侧及均位于左侧
        //messageListLayout.setItemShowType(EaseChatMessageListLayout.ShowType.LEFT);

        messageListLayout.setItemSenderBackground(ContextCompat.getDrawable(mContext, R.drawable.chat_send_bg));
        messageListLayout.setItemReceiverBackground(ContextCompat.getDrawable(mContext, R.drawable.chat_receive_white_bg));

        horn_Con.setVisibility(View.GONE);

        titleBarMessage = getActivity().findViewById(R.id.title_bar_message);
        horn_Txt.setMarqueeVelocity(1);
//        horn_Txt.setOnMarqueeCompleteListener(new MarqueeTextView.OnMarqueeCompleteListener() {
//            @Override
//            public void onMarqueeComplete() {
//                horn_Con.setVisibility(View.GONE);
//            }
//        });

        Glide.with(mContext).load(R.mipmap.horn_gif_icon).into(horn_icon);

        //获取到菜单输入父控件
        EaseChatInputMenu chatInputMenu = chatLayout.getChatInputMenu();
        //获取到菜单输入控件
        primaryMenu = chatInputMenu.getPrimaryMenu();
        ImageView voiceBtn = chatInputMenu.findViewById(R.id.btn_set_mode_voice);
        voiceBtn.setOnClickListener(v -> startAudio());


        LiveDataBus.get().with(MyConstant.Chat_BG, String.class).observe(requireActivity(), filePath ->
                Glide.with(requireActivity()).asBitmap().load(filePath).into(new SimpleTarget<Bitmap>() {
                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                        Drawable drawable = new BitmapDrawable(resource);
                        messageListLayout.setBackground(drawable);
                    }
                }));


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

        LiveDataBus.get().with(MyConstant.GROUP_CHAT_ANONYMOUS, Boolean.class).observe(requireActivity(), aBoolean -> {
            if (aBoolean) {
                groupModel.getData().setIsAnonymous(1);
                sendAnonymousName(1);
            } else {
                groupModel.getData().setIsAnonymous(0);
                sendAnonymousName(0);
            }
        });
        cancel_Btn.setOnClickListener(new cancel_BtnClick());
        screenShot();
    }


    //消息发送成功
    @Override
    public void onChatSuccess(EMMessage message) {
        super.onChatSuccess(message);
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            if (isMOCustomer()) {
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
            if (isMOCustomer()) {
                serviceSendMes(message, myInfo);
            } else {
                groupSendMes(message, myInfo);
            }
        }

        huweiBadge(message);
    }

    // 设置自定义推送提示
    private void huweiBadge(EMMessage message) {
        JSONObject extObject = new JSONObject();
        try {
            extObject.put("em_huawei_push_badge_class", "com.xunda.mo.main.MainActivity");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 将推送扩展设置到消息中
        message.setAttribute("em_apns_ext", extObject);
    }

    //是否是客服会话
    private boolean isMOCustomer() {
        EMMessage conMsg = messageListLayout.getCurrentConversation().getLatestMessageFromOthers();
        if (conMsg == null) {
            return false;
        }
        Map<String, Object> mapExt = conMsg.ext();
        if (mapExt != null && !mapExt.isEmpty()) {
            String messType = (String) mapExt.get(MyConstant.MESSAGE_TYPE);
            if (!TextUtils.isEmpty(messType) && messType.equals(MyConstant.MO_CUSTOMER)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }


    //单人聊天发送扩展字段
    private void singleSendMes(EMMessage message, MyInfo myInfo) {
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_CHAT);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        String toName = TextUtils.isEmpty(model.getData().getRemarkName()) ? model.getData().getNickname() : model.getData().getRemarkName();
        message.setAttribute(MyConstant.TO_NAME, toName);
        message.setAttribute(MyConstant.TO_HEAD, model.getData().getHeadImg());
        message.setAttribute(MyConstant.TO_LH, model.getData().getLightStatus());
        message.setAttribute(MyConstant.TO_VIP, model.getData().getVipType());
        isBurnAfterReading(model.getData().getFireType(), message);
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
        if (groupModel == null) {
            return;
        }
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_GROUP);
        message.setAttribute(MyConstant.SEND_NAME, sendAnonymousName(groupModel.getData().getIsAnonymous()));
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        message.setAttribute(MyConstant.GROUP_NAME, groupModel.getData().getGroupName());
        message.setAttribute(MyConstant.GROUP_HEAD, groupModel.getData().getGroupHeadImg());
        message.setAttribute(MyConstant.IDENTITY, groupModel.getData().getIdentity());
        if (groupModel.getData().getIsAnonymous() == 1) {
            message.setAttribute(MyConstant.GROUP_IS_ANONYMOUS, "1");
        }
    }

    //单人聊天 Mo消息扩展字段
    private void isBurnAfterReading(String fireType, EMMessage message) {
        if (fireType.equals("2")) {
            message.setAttribute(MyConstant.FIRE_TYPE, true);
            message.setAttribute("isSleckt", false);
        }
    }

    //匿名聊天随机用户名
    private String sendAnonymousName(int IsAnonymous) {
        MyInfo myInfo = new MyInfo(requireActivity());
        String nameStr;
        if (IsAnonymous == 1) {
            if (saveFile.getShareData(MyConstant.GROUP_CHAT_ANONYMOUS + conversationId, requireActivity()).equals("false")) {
                nameStr = NameResource.randomName();
                saveFile.saveShareData(MyConstant.GROUP_CHAT_ANONYMOUS + conversationId, nameStr, requireActivity());
            } else {
                nameStr = saveFile.getShareData(MyConstant.GROUP_CHAT_ANONYMOUS + conversationId, requireActivity());
            }
        } else {
            nameStr = myInfo.getUserInfo().getNickname();
            saveFile.clearShareData(MyConstant.GROUP_CHAT_ANONYMOUS + conversationId, requireActivity());

        }
        return nameStr;
    }

    private String sendAnonymousType(int IsAnonymous) {
        String type = MyConstant.MESSAGE_TYPE_GROUP;
        if (IsAnonymous == 1) {
            type = MyConstant.MESSAGE_TYPE_ANONYMOUS_ON;
        }
        return type;
    }


    public EMMessageListener msgListener;

    @Override
    public void onResume() {
        super.onResume();
        //收到的消息
//        msgListener = MyApplication.getInstance().msgListener;
        msgListener = new EMMessageMethod();
        EMClient.getInstance().chatManager().addMessageListener(msgListener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //记得在不需要的时候移除listener，如在activity的onDestroy()时
//        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
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
            chatExtendMenu.registerMenuItem(R.string.attach_video, R.mipmap.chat_location_video, R.id.extend_item_video);
            chatExtendMenu.registerMenuItem(R.string.attach_location, R.drawable.ease_chat_location_selector, R.id.extend_item_location);
            chatExtendMenu.registerMenuItem(R.string.attach_media_call, R.drawable.em_chat_video_call_selector, R.id.extend_item_video_call);
            chatExtendMenu.registerMenuItem(R.string.attach_two_withdraw, R.mipmap.chat_two_withdraw, R.id.extend_item_two_withdraw);
            chatExtendMenu.registerMenuItem(R.string.attach_user_card, R.drawable.em_chat_user_card_selector, R.id.extend_item_user_card);
            chatExtendMenu.registerMenuItem(R.string.attach_file, R.drawable.em_chat_file_selector, R.id.extend_item_file);
        } else if (chatType == EaseConstant.CHATTYPE_GROUP) {
            if (isMOCustomer()) {
                chatExtendMenu.registerMenuItem(R.string.chat_complaint, R.mipmap.chat_complaint_icon, R.id.chat_complaint);
            } else {
                chatExtendMenu.registerMenuItem(R.string.attach_video, R.mipmap.chat_location_video, R.id.extend_item_video);
                chatExtendMenu.registerMenuItem(R.string.attach_location, R.drawable.ease_chat_location_selector, R.id.extend_item_location);
                chatExtendMenu.registerMenuItem(R.string.attach_two_withdraw, R.mipmap.chat_two_withdraw, R.id.extend_item_two_withdraw);
                chatExtendMenu.registerMenuItem(R.string.attach_user_card, R.drawable.em_chat_user_card_selector, R.id.group_item_user_card);
                chatExtendMenu.registerMenuItem(R.string.attach_horn, R.mipmap.chat_horn, R.id.attach_item_horn);
                chatExtendMenu.registerMenuItem(R.string.attach_file, R.drawable.em_chat_file_selector, R.id.extend_item_file);
            }
        }
//        if (chatType == EaseConstant.CHATTYPE_GROUP && EMClient.getInstance().getOptions().getRequireAck()) {
//            chatExtendMenu.registerMenuItem(R.string.attach_video, R.drawable.em_chat_video_selector, R.id.extend_item_video);
//        }
//        if (chatType == EaseConstant.CHATTYPE_GROUP) { // 音视频会议
//            chatExtendMenu.registerMenuItem(R.string.voice_and_video_conference, R.drawable.em_chat_video_call_selector, R.id.extend_item_conference_call);
//            //目前普通模式也支持设置主播和观众人数，都建议使用普通模式
        //inputMenu.registerExtendMenuItem(R.string.title_live, R.drawable.em_chat_video_call_selector, EaseChatInputMenu.ITEM_LIVE, this);
//        }
        //群组类型，开启消息回执，且是owner
//        if (chatType == EaseConstant.CHATTYPE_GROUP && EMClient.getInstance().getOptions().getRequireAck()) {
//            EMGroup group = DemoHelper.getInstance().getGroupManager().getGroup(conversationId);
//            if (GroupHelper.isOwner(group)) {
//                chatExtendMenu.registerMenuItem(R.string.em_chat_group_delivery_ack, R.drawable.demo_chat_delivery_selector, R.id.extend_item_delivery);
//            }
//        }
        //添加扩展表情
        chatLayout.getChatInputMenu().getEmojiconMenu().addEmojiconGroup(EmojiconExampleGroupData.getData());
    }

    @Override
    public void initListener() {
        super.initListener();
        chatLayout.setOnRecallMessageResultListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        resetChatExtendMenu();
        addItemMenuAction();

        chatLayout.getChatInputMenu().getPrimaryMenu().getEditText().setText(getUnSendMsg());
        chatLayout.turnOnTypingMonitor(DemoHelper.getInstance().getModel().isShowMsgTyping());//正在输入监控

        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
        LiveDataBus.get().with(DemoConstant.MESSAGE_CALL_SAVE, Boolean.class).observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }
            if (event) {
                chatLayout.getChatMessageListLayout().refreshToLatest();
            }
        });

        LiveDataBus.get().with(DemoConstant.CONVERSATION_DELETE, EaseEvent.class).observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }
            if (event.isMessageChange()) {
                chatLayout.getChatMessageListLayout().refreshMessages();
            }
        });

        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }
            if (event.isMessageChange()) {
                chatLayout.getChatMessageListLayout().refreshToLatest();
            }

        });
        LiveDataBus.get().with(DemoConstant.CONVERSATION_READ, EaseEvent.class).observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }
            if (event.isMessageChange()) {
                chatLayout.getChatMessageListLayout().refreshToLatest();
            }
        });

        //更新用户属性刷新列表
        LiveDataBus.get().with(DemoConstant.CONTACT_ADD, EaseEvent.class).observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }
            if (event != null) {
                chatLayout.getChatMessageListLayout().refreshMessages();
            }
        });

        LiveDataBus.get().with(DemoConstant.CONTACT_UPDATE, EaseEvent.class).observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }
            if (event != null) {
                chatLayout.getChatMessageListLayout().refreshMessages();
            }
        });


        LiveDataBus.get().with(MyConstant.MESSAGE_TYPE_DOUBLE_RECALL, EaseEvent.class).observe(requireActivity(), event -> {
            if (event == null) {
                return;
            }
            EMMessage conMsg = chatLayout.getChatMessageListLayout().getCurrentConversation().getLastMessage();
            String isDouble_Recall = conMsg.getStringAttribute(MyConstant.MESSAGE_TYPE, "");
            if (TextUtils.equals(isDouble_Recall, MyConstant.MESSAGE_TYPE_DOUBLE_RECALL)) {
                recallTo();
            }
        });

        LiveDataBus.get().with(MyConstant.BURN_AFTER_READING_SET, Boolean.class).observe(requireActivity(), event -> {
            if (event) {
                model.getData().setFireType("2");
            } else {
                model.getData().setFireType("1");
            }
        });

        //Mo消息刷新数据
        LiveDataBus.get().with(MyConstant.FIRE_REFRESH, Boolean.class).observe(requireActivity(), aBoolean -> {
            if (aBoolean) {
                messageListLayout.refreshToLatest();
            }
        });

        //
        LiveDataBus.get().with(MyConstant.MESS_TYPE_GROUP_HORN, EMMessage.class).observe(requireActivity(), new Observer<EMMessage>() {
            @Override
            public void onChanged(EMMessage message) {
                List<EMMessage> msgs = new ArrayList<>();
                msgs.add(message);
                onMarquee(msgs);
            }
        });

        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            AddFriendMethod(getActivity(), saveFile.Friend_info_Url);
        } else if (chatType == EaseConstant.CHATTYPE_GROUP) {
            if (isMOCustomer()) {

            } else {
                GroupMethod(getActivity(), saveFile.Group_MyGroupInfo_Url);
            }
        }
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
//        for (EMMessage msg:msgs){
//        }
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
        horn_Txt.setOnMarqueeCompleteListener(new MarqueeTextView.OnMarqueeCompleteListener() {
            @Override
            public void onMarqueeComplete() {
                removeHornMes(msgs, horn_Con);
            }
        });
    }

    private void showDeliveryDialog() {
        new FullEditDialogFragment.Builder((BaseActivity) mContext)
                .setTitle(R.string.em_chat_group_read_ack)
                .setOnConfirmClickListener(R.string.em_chat_group_read_ack_send, new FullEditDialogFragment.OnSaveClickListener() {
                    @Override
                    public void onSaveClick(View view, String content) {
                        chatLayout.sendTextMessage(content, true);
                    }
                })
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
        String toName = TextUtils.isEmpty(model.getData().getRemarkName()) ? model.getData().getNickname() : model.getData().getRemarkName();
        ext.put(MyConstant.TO_NAME, toName);
        ext.put(MyConstant.TO_HEAD, model.getData().getHeadImg());
        ext.put(MyConstant.TO_LH, model.getData().getLightStatus());
        ext.put(MyConstant.TO_VIP, model.getData().getVipType());
        return ext;
    }

    //通知开通VIP
    private void changeVip() {
        new SimpleDialogFragment.Builder((BaseActivity) mContext)
                .setTitle("提示通知")
                .showContent(true)
                .setContent("该功能为会员特权功能，请开通会员后使用")
                .setOnConfirmClickListener(view -> {
                    Me_VIP.actionStart(mContext);
                })
                .showCancelButton(true)
                .show();
    }

    //撤回
    private void changeRecall() {
        new SimpleDialogFragment.Builder((BaseActivity) mContext)
                .setTitle("双向撤回")
                .showContent(true)
                .setContent("聊天记录一键双向撤回，同时删除你和对方设备上的所有聊天记录，撤回数据多次覆盖删除，不可恢复")
                .setOnConfirmClickListener(view -> {
                    doubleRecall();
                })
                .showCancelButton(true)
                .show();
    }

    //群组撤回
    private void changeGroupRecall() {
        new SimpleDialogFragment.Builder((BaseActivity) mContext)
                .setTitle("双向撤回")
                .showContent(true)
                .setContent("聊天记录一键双向撤回，同时删除你和对方设备上的所有聊天记录，撤回数据多次覆盖删除，不可恢复")
                .setOnConfirmClickListener(view -> {
                    doubleGroupRecall();
                })
                .showCancelButton(true)
                .show();
    }

    @Override
    public void onUserAvatarClick(String username) {
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
            if (isMOCustomer()) {
                return;
            }
            int isAnonymous = groupModel.getData().getIsAnonymous();
            int issProtect = groupModel.getData().getIsProtect();
            if (isAnonymous == 1 || issProtect == 1) {
                return;
            }
            int myIdentity = groupModel.getData().getIdentity();
            String myGroupId = groupModel.getData().getGroupId();
            String userID = "";
            String hxUserName = username;
            GroupFriend_Detail.actionStart(mContext, userID, hxUserName, groupModel);
        }

    }

    @Override
    public void onUserAvatarLongClick(String username) {
        EditText editText = chatLayout.getChatInputMenu().getPrimaryMenu().getEditText();
        editText.setText("");

//        EMGroup emGroup = null;
//        try {
//            emGroup = DemoHelper.getInstance().getGroupManager().getGroupFromServer(username,true);
//        } catch (HyphenateException e) {
//            e.printStackTrace();
//        }
//        editText.setText(emGroup.getMembers().get(0));

//        EMConversation emMessage = DemoHelper.getInstance().getChatManager().getConversation(username);
//        String name = emMessage.getLastMessage().getStringAttribute(MyConstant.SEND_NAME,"");
//        editText.setText(name);
        //        if (user != null){
//            username = user.getNickname();
//        }
//        String name = emConversation.getLastMessage().getStringAttribute(MyConstant.SEND_NAME,"");
//        editText.getText().insert(editText.getSelectionStart(),name);
//        if (true)
//            insertText(editText, AT_PREFIX + username + AT_SUFFIX);
//        else
//            insertText(editText, username + AT_SUFFIX);

    }


    /**
     * insert text to EditText
     *
     * @param edit
     * @param text
     */
    private void insertText(EditText edit, String text) {
        if (edit.isFocused()) {
            edit.getText().insert(edit.getSelectionStart(), text);
        } else {
            edit.getText().insert(edit.getText().length() - 1, text);
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
        if (count == 1 && "@".equals(String.valueOf(s.charAt(start)))) {
            PickAtUserActivity.actionStartForResult(ChatFragment.this, conversationId, REQUEST_CODE_SELECT_AT_USER);
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
//                Intent userCardIntent = new Intent(this.getContext(), SelectUserCardActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK);
//                userCardIntent.putExtra("toUser", conversationId);
//                this.requireContext().startActivity(userCardIntent);
                Chat_SelectUserCard.actionStartSingle(requireActivity(), conversationId, "1");
                break;
            case R.id.group_item_user_card://群发名片
                Chat_SelectUserCard.actionStartGroup(requireActivity(), conversationId, "2", groupModel);
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
            Group_Horn.actionStart(requireActivity(), groupModel);
        }
    }

    //能否投诉客服
    private boolean MoIsComplaint() {
        EMMessage conMsg = messageListLayout.getCurrentConversation().getLatestMessageFromOthers();
        if (conMsg == null) {
            return false;
        }
        String sendName = conMsg.getStringAttribute(MyConstant.SEND_NAME, "");
        if (!TextUtils.isEmpty(sendName)) {
            return true;
        }
        return false;
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
            int myIdentity = groupModel.getData().getIdentity();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_AT_USER:
                    if (data != null) {
                        String username = data.getStringExtra("username");
                        chatLayout.inputAtUsername(username, false);
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

    /**
     * 保存未发送的文本消息内容
     *
     * @param content
     */
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

        if (chatType == DemoConstant.CHATTYPE_CHATROOM) {
            helper.findItemVisible(R.id.action_chat_forward, true);
        }
    }

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
            if (isMOCustomer()) {
                serviceSendMes(message, myInfo);
            } else {
                groupSendMes(message, myInfo);
            }
        }
    }

    //两分钟内撤回发送消息
    private void chatOrGroupRecallMess(EMMessage message) {
//        MyInfo myInfo = new MyInfo(requireActivity());
//        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
//            singleRecallSendMes(message, myInfo, model);
//        } else if (chatType == EaseConstant.CHATTYPE_GROUP) {
//            if (isMOCustomer()) {
//                return;
//            } else {
//                groupRecallSendMes(message, myInfo);
//            }
//        }
        //        showProgressBar();
        chatLayout.recallMessage(message);//不使用环信方法
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

    ChatUserBean model;

    public void AddFriendMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("friendHxName", conversationId);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                model = new Gson().fromJson(result, ChatUserBean.class);
                MyInfo myInfo = new MyInfo(requireActivity());
                String headUrl = myInfo.getUserInfo().getHeadImg();
                String nick = myInfo.getUserInfo().getNickname();
                EMUserInfo userInfo = new EMUserInfo();
                userInfo.setAvatarUrl(headUrl);
                userInfo.setNickName(nick);
                if (headUrl != null) {
                    EMClient.getInstance().userInfoManager().updateOwnInfo(userInfo, new EMValueCallBack<String>() {
                        @Override
                        public void onSuccess(String value) {
                            EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT);
                            event.message = myInfo.getUserInfo().getHxUserName();
                            LiveDataBus.get().with(DemoConstant.AVATAR_CHANGE).postValue(event);
                        }

                        @Override
                        public void onError(int error, String errorMsg) {
                        }
                    });

                }
            }

            @Override
            public void failed(String... args) {

            }
        });
    }

    GruopInfo_Bean groupModel;

    public void GroupMethod(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupHxId", conversationId);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                groupModel = new Gson().fromJson(result, GruopInfo_Bean.class);
                GruopInfo_Bean.DataDTO dataDTO = groupModel.getData();
                String topStr = dataDTO.getGroupNotice();
                setTopView(topStr);
                titleBarMessage.setTitle(dataDTO.getGroupName());
                sendAnonymousName(groupModel.getData().getIsAnonymous());
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

    /**
     * 是否有置顶消息
     *
     * @param topStr
     */
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
                baseDataModel model = new Gson().fromJson(result, baseDataModel.class);
            }

            @Override
            public void failed(String... args) {
            }
        });
    }


    public class EMMessageMethod implements EMMessageListener {
        @SneakyThrows
        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            Log.i("message", messages.toString());
            //接收消息的时候获取到扩展属性
            //获取自定义的属性，第2个参数为没有此定义的属性时返回的默认值
            for (EMMessage msg : messages) {
                if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                    // 消息所属会话
                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(msg.getUserName(), EMConversation.EMConversationType.Chat, true);
                    String isDouble_Recall = msg.getStringAttribute(MyConstant.MESSAGE_TYPE, "");
                    if (TextUtils.equals(isDouble_Recall, MyConstant.MESSAGE_TYPE_DOUBLE_RECALL)) {
//                            EaseEvent event = EaseEvent.create(MyConstant.MESSAGE_TYPE_DOUBLE_RECALL, EaseEvent.TYPE.MESSAGE);
//                            LiveDataBus.get().with(MyConstant.MESSAGE_TYPE_DOUBLE_RECALL).postValue(event);
                        // 删除消息
                        conversation.clearAllMessages();
                        saveMes(conversation.conversationId());
                    }
                } else if (chatType == EaseConstant.CHATTYPE_GROUP) {
                    String isAnonymousOn = msg.getStringAttribute(MyConstant.MESSAGE_TYPE, "");
                    if (TextUtils.equals(isAnonymousOn, MyConstant.MESSAGE_TYPE_ANONYMOUS_ON)) {
                        groupModel.getData().setIsAnonymous(1);
                        sendAnonymousName(1);
                    } else if (TextUtils.equals(isAnonymousOn, MyConstant.MESSAGE_TYPE_ANONYMOUS_OFF)) {
                        groupModel.getData().setIsAnonymous(0);
                        sendAnonymousName(0);
                    }

                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(msg.getUserName(), EMConversation.EMConversationType.Chat, true);
                    String isDouble_Recall = msg.getStringAttribute(MyConstant.MESSAGE_TYPE, "");
                    if (TextUtils.equals(isDouble_Recall, MyConstant.MESSAGE_TYPE_GROUP_DOUBLE_RECALL)) {
                        // 删除消息
//                        conversation.clearAllMessages();
//                        saveGroupMes(conversation.conversationId());
                        recallGroupTo();
                    }

                }
            }

        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
            Log.i("message", "透传");
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
            Log.i("message", messages.toString());
//            for (EMMessage message : messages) {
//                //收到回执删除消息
//                String fireType = message.getStringAttribute(MyConstant.FIRE_TYPE, "");
//                if (TextUtils.equals(fireType, "1")) {
//                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getUserName(), EMConversation.EMConversationType.Chat, true);
//                    conversation.removeMessage(message.getMsgId());
//                    chatLayout.getChatMessageListLayout().refreshToLatest();
//                }
//            }
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
            Log.i("message", "送达");
        }

        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            //消息被撤回
            Log.i("message", "撤回");
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
            Log.i("message", "消息变动");
        }
    }


    //发送单聊撤回消息
    private void doubleRecall() {
        removeMes();
        String mes_type = MyConstant.MESSAGE_TYPE_DOUBLE_RECALL;
        sendMes(model, mes_type);
        messageListLayout.refreshToLatest();
    }

    //收到单聊撤回消息
    public void recallTo() {
        removeMes();
        saveMes(conversationId);
        messageListLayout.refreshToLatest();
    }

    //发送群里撤回消息
    private void doubleGroupRecall() {
        removeMes();
        String mes_type = MyConstant.MESSAGE_TYPE_GROUP_DOUBLE_RECALL;
        sendGroupMes(groupModel, mes_type);
        messageListLayout.refreshToLatest();
    }

    //收到群撤回消息
    public void recallGroupTo() {
        removeMes();
        saveGroupMes(conversationId);
        messageListLayout.refreshToLatest();
    }


    private void removeMes() {
        EMConversation conversation = messageListLayout.getCurrentConversation();
        conversation.clearAllMessages();
    }

    //发送消息通用 注意消息type
    private void sendMes(ChatUserBean Model, String Mes_Type) {
        MyInfo myInfo = new MyInfo(mContext);
        String conversationId = Model.getData().getHxUserName();
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
        String toName = TextUtils.isEmpty(Model.getData().getRemarkName()) ? Model.getData().getNickname() : Model.getData().getRemarkName();
        message.setAttribute(MyConstant.TO_NAME, toName);
        message.setAttribute(MyConstant.TO_HEAD, Model.getData().getHeadImg());
        message.setAttribute(MyConstant.TO_LH, Model.getData().getLightStatus());
        message.setAttribute(MyConstant.TO_VIP, Model.getData().getVipType());
        EMClient.getInstance().chatManager().sendMessage(message);
    }


    private void saveMes(String conversationId) {
        DemoHelper.getInstance().getConversation(conversationId, EMConversation.EMConversationType.Chat, false);
        EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        EMTextMessageBody txtBody = new EMTextMessageBody("对方撤回了所有消息");
        msgNotification.addBody(txtBody);
        msgNotification.setFrom(conversationId);
        msgNotification.setTo(conversationId);
        msgNotification.setUnread(false);
        msgNotification.setChatType(EMMessage.ChatType.Chat);
        msgNotification.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_DOUBLE_RECALL);
        msgNotification.setStatus(EMMessage.Status.SUCCESS);
        EMClient.getInstance().chatManager().saveMessage(msgNotification);
    }

    //发送群撤回消息
    private void sendGroupMes(GruopInfo_Bean groupModel, String mes_Type) {
        if (groupModel == null) {
            return;
        }
        String conversationId = groupModel.getData().getGroupHxId();
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
        message.setAttribute(MyConstant.GROUP_NAME, groupModel.getData().getGroupName());
        message.setAttribute(MyConstant.GROUP_HEAD, groupModel.getData().getGroupHeadImg());
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    private void saveGroupMes(String conversationId) {
        MyInfo myInfo = new MyInfo(mContext);
        EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        EMTextMessageBody txtBody = new EMTextMessageBody("撤回了所有消息");
        msgNotification.addBody(txtBody);
        msgNotification.setFrom(conversationId);
        msgNotification.setTo(conversationId);
        msgNotification.setUnread(false);
        msgNotification.setChatType(EMMessage.ChatType.GroupChat);
        msgNotification.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_GROUP_DOUBLE_RECALL);
//        msgNotification.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        msgNotification.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        msgNotification.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        msgNotification.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        msgNotification.setAttribute(MyConstant.GROUP_NAME, groupModel.getData().getGroupName());
        msgNotification.setAttribute(MyConstant.GROUP_HEAD, groupModel.getData().getGroupHeadImg());
        msgNotification.setStatus(EMMessage.Status.SUCCESS);
        EMClient.getInstance().chatManager().saveMessage(msgNotification);
    }

    public void startAudio() {
        //监听授权
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {//录音权限
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissions, 1);
        } else {
            primaryMenu.showVoiceStatus();
        }
    }

    //单聊 撤回2分钟消息扩展字段
    private void singleRecallSendMes(EMMessage recallMessage, MyInfo myInfo, ChatUserBean Model) {
        //删除要撤回的消息 发送一条消息
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(recallMessage.getUserName(), EMConversation.EMConversationType.Chat, true);
        conversation.removeMessage(recallMessage.getMsgId());
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        EMTextMessageBody txtBody = new EMTextMessageBody("");
        message.addBody(txtBody);
        message.setTo(conversationId);
        message.setChatType(EMMessage.ChatType.Chat);
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.Message_Recall);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        String toName = TextUtils.isEmpty(Model.getData().getRemarkName()) ? Model.getData().getNickname() : Model.getData().getRemarkName();
        message.setAttribute(MyConstant.TO_NAME, toName);
        message.setAttribute(MyConstant.TO_HEAD, Model.getData().getHeadImg());
        message.setAttribute(MyConstant.TO_LH, Model.getData().getLightStatus());
        message.setAttribute(MyConstant.TO_VIP, Model.getData().getVipType());
        EMClient.getInstance().chatManager().sendMessage(message);
        chatLayout.getChatMessageListLayout().refreshToLatest();

    }

    //群组聊天 撤回2分钟消息扩展字段
    private void groupRecallSendMes(EMMessage recallMessage, MyInfo myInfo) {
        if (groupModel == null) {
            return;
        }
        //删除要撤回的消息 发送一条消息
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(recallMessage.getUserName(), EMConversation.EMConversationType.Chat, true);
        conversation.removeMessage(recallMessage.getMsgId());
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        EMTextMessageBody txtBody = new EMTextMessageBody("");
        message.addBody(txtBody);
        message.setTo(conversationId);
        message.setChatType(EMMessage.ChatType.GroupChat);
        message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.Message_Recall);
        message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNickname());
        message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
        message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
        message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
        message.setAttribute(MyConstant.GROUP_NAME, groupModel.getData().getGroupName());
        message.setAttribute(MyConstant.GROUP_HEAD, groupModel.getData().getGroupHeadImg());
        EMClient.getInstance().chatManager().sendMessage(message);
        chatLayout.getChatMessageListLayout().refreshToLatest();
    }

    private void screenShot() {
        ScreenShotViewModel screenShotViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(MyApplication.getInstance())).get(ScreenShotViewModel.class);
        screenShotViewModel.registerContentObserver();
        screenShotViewModel.getDataChanged().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    screenShotViewModel.onCleared();//收到观察消息就注销 不然会收到多条通知
//                    ScreentShotInfo screentShotInfo = new ScreentShotInfo();
//                    screenShotViewModel.getLatestImage(screentShotInfo.getPath());
                    if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                        sendScreenShot();
                    }if (chatType == EaseConstant.CHATTYPE_GROUP) {
                        sendScreenShot();
                    }
                }
            }
        });

        //接收到截屏通知后 主动获取截屏照片
        screenShotViewModel.getScreentShotInfoData().observe(this, new Observer<ScreentShotInfo>() {
            @Override
            public void onChanged(ScreentShotInfo screentShotInfo) {
//                Toast.makeText(mContext, "截屏成功了", Toast.LENGTH_LONG).show();
            }
        });
    }

    //发送截屏CMD消息
    private void sendScreenShot() {
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        String action = "发送截屏CMD消息";//action可以自定义
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
//        String toUsername = "test1";//发送给某个人
        cmdMsg.setTo(conversationId);
        cmdMsg.addBody(cmdBody);
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            cmdMsg.setChatType(EMMessage.ChatType.Chat);
            cmdMsg.setAttribute(MyConstant.MESSAGE_TYPE_SCREENSHORTS, true);
        }else if (chatType == EaseConstant.CHATTYPE_GROUP){
            cmdMsg.setChatType(EMMessage.ChatType.GroupChat);
            cmdMsg.setAttribute(MyConstant.MESSAGE_TYPE_GROUP_SCREENSHORTS, true);
        }
        addMsgAttrsBeforeSend(cmdMsg);
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }


}


