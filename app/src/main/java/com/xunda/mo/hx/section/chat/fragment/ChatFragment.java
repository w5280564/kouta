
package com.xunda.mo.hx.section.chat.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
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
import com.hyphenate.easeui.modules.chat.interfaces.OnChatLayoutListener;
import com.hyphenate.easeui.modules.chat.interfaces.OnRecallMessageResultListener;
import com.hyphenate.easeui.modules.menu.EasePopupWindowHelper;
import com.hyphenate.easeui.modules.menu.MenuItemBean;
import com.hyphenate.easeui.utils.EaseCommonUtils;
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
import com.xunda.mo.hx.section.chat.activicy.SelectUserCardActivity;
import com.xunda.mo.hx.section.chat.viewmodel.MessageViewModel;
import com.xunda.mo.hx.section.conference.ConferenceInviteActivity;
import com.xunda.mo.hx.section.dialog.DemoDialogFragment;
import com.xunda.mo.hx.section.dialog.DemoListDialogFragment;
import com.xunda.mo.hx.section.dialog.FullEditDialogFragment;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.hx.section.group.GroupHelper;
import com.xunda.mo.main.MainLogin_Register;
import com.xunda.mo.main.chat.activity.ChatFriend_Detail;
import com.xunda.mo.main.chat.activity.UserDetail_Set;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.info.MyInfo;
import com.xunda.mo.model.ChatUserBean;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import lombok.SneakyThrows;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class ChatFragment extends MyEaseChatFragment implements OnRecallMessageResultListener {
    private static final String TAG = ChatFragment.class.getSimpleName();
    private MessageViewModel viewModel;
    protected ClipboardManager clipboard;

    private static final int REQUEST_CODE_SELECT_AT_USER = 15;
    private static final String[] calls = {"视频通话", "语音通话"};
    private OnFragmentInfoListener infoListener;
    private Dialog dialog;
    EaseChatMessageListLayout messageListLayout;

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

//        messageListLayout.setItemSenderBackground(ContextCompat.getDrawable(mContext, R.drawable.chat_send_bg));
//        messageListLayout.setItemReceiverBackground(ContextCompat.getDrawable(mContext, R.drawable.chat_receive_white_bg));


        if (!saveFile.getShareData("chatBg" + conversationId, requireActivity()).equals("false")) {
            String filePath = saveFile.getShareData("chatBg" + conversationId, requireActivity());
            Glide.with(requireActivity())
                    .asBitmap()
//                .load("https://ahxd-private.obs.cn-east-3.myhuaweicloud.com/user%2F841997676445491200%2FheadImg%2Ffirst.jpg")
                    .load(filePath)
                    .into(new SimpleTarget<Bitmap>() {
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            Drawable drawable = new BitmapDrawable(resource);
                            messageListLayout.setBackground(drawable);
                        }

                    });
        }

        cancel_Btn.setOnClickListener(new cancel_BtnClick());


        //获取到菜单输入父控件
        EaseChatInputMenu chatInputMenu = chatLayout.getChatInputMenu();
        //获取到菜单输入控件
        IChatPrimaryMenu primaryMenu = chatInputMenu.getPrimaryMenu();
        //if(primaryMenu != null) {
        //设置菜单样式为不可用语音模式
        //    primaryMenu.setMenuShowType(EaseInputMenuStyle.ONLY_TEXT);
        //}
//        primaryMenu.
//        MyInfo myInfo = new MyInfo(requireActivity());


        msgListener = new EMMessageListener() {
            @SneakyThrows
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //接收消息的时候获取到扩展属性
                //获取自定义的属性，第2个参数为没有此定义的属性时返回的默认值
                MyInfo myInfo = new MyInfo(requireActivity());
                if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                    for (int i = 0; i < messages.size(); i++) {
                        messages.get(i).getTo();
                        messages.get(i).getStringAttribute("messageType", "chat");
                        messages.get(i).getStringAttribute("sendName", "");
                        messages.get(i).getStringAttribute("sendHead", "");
                        messages.get(i).getStringAttribute("sendLH", "");
                        messages.get(i).getStringAttribute("sendVIP", "");
                        messages.get(i).getStringAttribute("toName", "");
                        messages.get(i).getStringAttribute("toHead", "");
                        messages.get(i).getStringAttribute("toLH", "");
                        messages.get(i).getStringAttribute("toVIP", "");
//                        messages.get(i).getStringAttribute("fireType", "");
                        if (messages.get(i).getTo().equals(conversationId)) {

                        } else {
                            //                        MyInfo myInfo = new MyInfo(requireActivity());


                        }


                    }
                    messageListLayout.setData(messages);


                } else if (chatType == EaseConstant.CHATTYPE_GROUP) {
                    for (int i = 0; i < messages.size(); i++) {
                        messages.get(i).getStringAttribute("messageType", "group");
                        messages.get(i).getStringAttribute("messageType", "group");
                        messages.get(i).getStringAttribute("sendName", "");
                        messages.get(i).getStringAttribute("sendHead", "");
                        messages.get(i).getStringAttribute("sendLH", "");
                        messages.get(i).getStringAttribute("sendVIP", "");
                        messages.get(i).getStringAttribute("groupName", "");
                        messages.get(i).getStringAttribute("groupHead", "");

                    }
                }
                Log.i("messreceived", messages.toString());
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @SneakyThrows
            @Override
            public void onMessageRead(List<EMMessage> messages) {
                //收到已读回执
                messages.get(0).getStringAttribute("sendName");
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
                //收到已送达回执
            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages) {
                //消息被撤回
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };

        EMClient.getInstance().chatManager().addMessageListener(msgListener);

    }


    @SneakyThrows
    @Override
    public void addMsgAttrsBeforeSend(EMMessage message) {
        super.addMsgAttrsBeforeSend(message);
        MyInfo myInfo = new MyInfo(requireActivity());
//        JSONObject obj = new JSONObject();
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
//            obj.put(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_CHAT);
//            obj.put(MyConstant.SEND_NAME, myInfo.getUserInfo().getNikeName());
//            obj.put(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
//            obj.put(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
//            obj.put(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
//            String toName = TextUtils.isEmpty(model.getData().getRemarkName()) ? model.getData().getNikeName() : model.getData().getRemarkName();
//            obj.put(MyConstant.TO_NAME, toName);
//            obj.put(MyConstant.TO_HEAD, model.getData().getHeadImg());
//            obj.put(MyConstant.TO_LH, model.getData().getLightStatus());
//            obj.put(MyConstant.TO_VIP, model.getData().getVipType());
//            obj.put(MyConstant.FIRE_TYPE, model.getData().getFireType());

            message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_CHAT);
            message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNikeName());
            message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
            message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
            message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
            String toName = TextUtils.isEmpty(model.getData().getRemarkName()) ? model.getData().getNikeName() : model.getData().getRemarkName();
            message.setAttribute(MyConstant.TO_NAME, toName);
            message.setAttribute(MyConstant.TO_HEAD, model.getData().getHeadImg());
            message.setAttribute(MyConstant.TO_LH, model.getData().getLightStatus());
            message.setAttribute(MyConstant.TO_VIP, model.getData().getVipType());
//            message.setAttribute(MyConstant.FIRE_TYPE, model.getData().getFireType());
        } else if (chatType == EaseConstant.CHATTYPE_GROUP) {
//            obj.put(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_GROUP);
//            obj.put(MyConstant.SEND_NAME, myInfo.getUserInfo().getNikeName());
//            obj.put(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
//            obj.put(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
//            obj.put(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
//            obj.put(MyConstant.GROUP_NAME, groupModel.getData().getGroupName());
//            obj.put(MyConstant.GROUP_HEAD, groupModel.getData().getGroupHeadImg());

            message.setAttribute(MyConstant.MESSAGE_TYPE, MyConstant.MESSAGE_TYPE_GROUP);
            message.setAttribute(MyConstant.SEND_NAME, myInfo.getUserInfo().getNikeName());
            message.setAttribute(MyConstant.SEND_HEAD, myInfo.getUserInfo().getHeadImg());
            message.setAttribute(MyConstant.SEND_LH, myInfo.getUserInfo().getLightStatus().toString());
            message.setAttribute(MyConstant.SEND_VIP, myInfo.getUserInfo().getVipType());
            message.setAttribute(MyConstant.GROUP_NAME, groupModel.getData().getGroupName());
            message.setAttribute(MyConstant.GROUP_HEAD, groupModel.getData().getGroupHeadImg());
        }
//        message.setAttribute(MyConstant.EXT, obj);
    }


    private EMMessageListener msgListener;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //记得在不需要的时候移除listener，如在activity的onDestroy()时
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
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
        //添加扩展槽
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            //inputMenu.registerExtendMenuItem(R.string.attach_voice_call, R.drawable.em_chat_voice_call_selector, EaseChatInputMenu.ITEM_VOICE_CALL, this);
            chatExtendMenu.registerMenuItem(R.string.attach_media_call, R.drawable.em_chat_video_call_selector, R.id.extend_item_video_call);
        }
        if (chatType == EaseConstant.CHATTYPE_GROUP) { // 音视频会议
            chatExtendMenu.registerMenuItem(R.string.voice_and_video_conference, R.drawable.em_chat_video_call_selector, R.id.extend_item_conference_call);
            //目前普通模式也支持设置主播和观众人数，都建议使用普通模式
            //inputMenu.registerExtendMenuItem(R.string.title_live, R.drawable.em_chat_video_call_selector, EaseChatInputMenu.ITEM_LIVE, this);
        }
        chatExtendMenu.registerMenuItem(R.string.attach_two_withdraw, R.mipmap.chat_two_withdraw, R.id.extend_item_two_withdraw);

        chatExtendMenu.registerMenuItem(R.string.attach_location, R.drawable.ease_chat_location_selector, R.id.extend_item_location);

        //名片扩展
        chatExtendMenu.registerMenuItem(R.string.attach_user_card, R.drawable.em_chat_user_card_selector, R.id.extend_item_user_card);
        chatExtendMenu.registerMenuItem(R.string.attach_file, R.drawable.em_chat_file_selector, R.id.extend_item_file);
        if (chatType == EaseConstant.CHATTYPE_GROUP && EMClient.getInstance().getOptions().getRequireAck()) {
            chatExtendMenu.registerMenuItem(R.string.attach_video, R.drawable.em_chat_video_selector, R.id.extend_item_video);
        }
        //群组类型，开启消息回执，且是owner
        if (chatType == EaseConstant.CHATTYPE_GROUP && EMClient.getInstance().getOptions().getRequireAck()) {
            EMGroup group = DemoHelper.getInstance().getGroupManager().getGroup(conversationId);
            if (GroupHelper.isOwner(group)) {
                chatExtendMenu.registerMenuItem(R.string.em_chat_group_delivery_ack, R.drawable.demo_chat_delivery_selector, R.id.extend_item_delivery);
            }
        }
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
        chatLayout.turnOnTypingMonitor(DemoHelper.getInstance().getModel().isShowMsgTyping());

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

                if (messageListLayout.getCurrentConversation().getLastMessage().getStringAttribute(MyConstant.FIRE_TYPE, "").equals("fireType")) {
                    chatLayout.deleteMessage(messageListLayout.getCurrentConversation().getLastMessage());
                }

                chatLayout.getChatMessageListLayout().refreshMessages();
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

        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            AddFriendMethod(getActivity(), saveFile.BaseUrl + saveFile.Friend_info_Url + "?friendHxName=" + conversationId);
        } else if (chatType == EaseConstant.CHATTYPE_GROUP) {
            GroupMethod(getActivity(), saveFile.BaseUrl + saveFile.Group_MyGroupInfo_Url
                    + "?groupHxId=" + conversationId);
        }
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
                .setOnItemClickListener(new DemoListDialogFragment.OnDialogItemClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        switch (position) {
                            case 0:
                                EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VIDEO_CALL, conversationId, null);
                                break;
                            case 1:
                                EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VOICE_CALL, conversationId, null);
                                break;
                        }
                    }
                })
                .show();
    }

    @Override
    public void onUserAvatarClick(String username) {
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
    }

    @Override
    public void onUserAvatarLongClick(String username) {
        EditText editText = chatLayout.getChatInputMenu().getPrimaryMenu().getEditText();
        editText.setText("");
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
        if (!chatLayout.getChatMessageListLayout().isGroupChat()) {
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
            case R.id.extend_item_user_card:
                EMLog.d(TAG, "select user card");
                Intent userCardIntent = new Intent(this.getContext(), SelectUserCardActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK);
                userCardIntent.putExtra("toUser", conversationId);
                this.requireContext().startActivity(userCardIntent);
                break;
            case R.id.extend_item_two_withdraw:
                Toast.makeText(getActivity(), "点击撤回", Toast.LENGTH_SHORT).show();
                EMClient.getInstance().chatManager().deleteConversation(conversationId, true);
                requireActivity().finish();
                break;
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
                ForwardMessageActivity.actionStart(mContext, message.getMsgId());
                return true;
            case R.id.action_chat_delete:
                showDeleteDialog(message);
                return true;
            case R.id.action_chat_recall:
                showProgressBar();
                chatLayout.recallMessage(message);
                return true;
        }
        return false;
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
                .setOnConfirmClickListener(getString(R.string.delete), new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        chatLayout.deleteMessage(message);
                    }
                })
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
        RequestParams params = new RequestParams(baseUrl);
        if (saveFile.getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", saveFile.getShareData("JSESSIONID", context));
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (resultString != null) {
                    model = new Gson().fromJson(resultString, ChatUserBean.class);
                    if (model.getCode() == -1 || model.getCode() == 500) {
                        Intent intent = new Intent(context, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (model.getCode() == 200) {

                        MyInfo myInfo = new MyInfo(requireActivity());
                        String headUrl = myInfo.getUserInfo().getHeadImg();
                        String nick = myInfo.getUserInfo().getNikeName();
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

                    } else {
                        Toast.makeText(context, model.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {

            }
        });
    }

    GruopInfo_Bean groupModel;

    public void GroupMethod(Context context, String baseUrl) {
        RequestParams params = new RequestParams(baseUrl);
        if (saveFile.getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", saveFile.getShareData("JSESSIONID", context));
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
                if (resultString != null) {
                    groupModel = new Gson().fromJson(resultString, GruopInfo_Bean.class);
                    if (groupModel.getCode() == -1 || groupModel.getCode() == 500) {
                        Intent intent = new Intent(context, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (groupModel.getCode() == 200) {
                        GruopInfo_Bean.DataDTO dataDTO = groupModel.getData();
                        String topStr = dataDTO.getGroupNotice();
                        setTopView(topStr);

                    } else {
                        Toast.makeText(context, groupModel.getMsg(), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void setOnChatLayoutListener(OnChatLayoutListener listener) {
        super.setOnChatLayoutListener(listener);
    }


    private class cancel_BtnClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            top_Constraint.setVisibility(View.GONE);
        }
    }

    /**
     * 是否有置顶消息
     * @param topStr
     */
    private void setTopView(String topStr) {
        if (!TextUtils.isEmpty(topStr)) {
            top_Constraint.setVisibility(View.VISIBLE);
            top_Txt.setText(topStr);
        }
    }
}