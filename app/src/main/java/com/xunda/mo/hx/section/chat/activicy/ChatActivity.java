package com.xunda.mo.hx.section.chat.activicy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.provider.EaseUserProfileProvider;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.interfaceOrImplement.OnResourceParseCallback;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.chat.fragment.ChatFragment;
import com.xunda.mo.hx.section.chat.viewmodel.ChatViewModel;
import com.xunda.mo.hx.section.chat.viewmodel.MessageViewModel;
import com.xunda.mo.hx.section.group.GroupHelper;
import com.xunda.mo.main.chat.activity.ChatDetailSet;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.group.activity.GroupDetailSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ChatActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, EaseTitleBar.OnRightClickListener, ChatFragment.OnFragmentInfoListener {
    private EaseTitleBar titleBarMessage;
    private String conversationId;
    private int chatType;
    private ChatFragment fragment;
    private String historyMsgId;
    private ChatViewModel viewModel;
    private boolean isMoCustomer;

    public static void actionStart(Context context, String conversationId, int chatType) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID, conversationId);
        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, chatType);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, String conversationId, int chatType,boolean isMoCustomer) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID, conversationId);
        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, chatType);
        intent.putExtra("isMoCustomer", isMoCustomer);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }



    @Override
    protected int getLayoutId() {
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);//不允许截图
        return R.layout.demo_activity_chat;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        conversationId = intent.getStringExtra(EaseConstant.EXTRA_CONVERSATION_ID);
        chatType = intent.getIntExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        historyMsgId = intent.getStringExtra(DemoConstant.HISTORY_MSG_ID);
        isMoCustomer = intent.getBooleanExtra("isMoCustomer",false);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBarMessage = findViewById(R.id.title_bar_message);
        initChatFragment();
        setTitleBarRight();
    }

    private void initChatFragment() {
        fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID, conversationId);
        bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, chatType);
        bundle.putString(DemoConstant.HISTORY_MSG_ID, historyMsgId);
        bundle.putBoolean(EaseConstant.EXTRA_IS_ROAM, DemoHelper.getInstance().getModel().isMsgRoaming());
        bundle.putBoolean("isMoCustomer", isMoCustomer);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment, "chat").commit();
    }

    private void setTitleBarRight() {
        if (isMoCustomer){
            return;
        }
        titleBarMessage.setRightImageResource(R.drawable.chat_user_info);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBarMessage.setOnBackPressListener(this);
        titleBarMessage.setOnRightClickListener(this);
        fragment.setOnFragmentInfoListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            initIntent(intent);
            initChatFragment();
            initData();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(conversationId);
        MessageViewModel messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        viewModel.getDeleteObservable().observe(this, response -> parseResource(response, new OnResourceParseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                finish();
                EaseEvent event = EaseEvent.create(DemoConstant.CONVERSATION_DELETE, EaseEvent.TYPE.MESSAGE);
                messageViewModel.setMessageChange(event);
            }
        }));
        viewModel.getChatRoomObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMChatRoom>() {
                @Override
                public void onSuccess(@Nullable EMChatRoom data) {
                    setDefaultTitle();
                }
            });
        });
        messageViewModel.getMessageChange().with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isGroupLeave() && TextUtils.equals(conversationId, event.message)) {
                finish();
            }
        });
        messageViewModel.getMessageChange().with(DemoConstant.MESSAGE_FORWARD, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isMessageChange()) {
                showSnackBar(event.event);
            }
        });

        messageViewModel.getMessageChange().with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (conversation == null) {
                finish();
            }
        });

        setDefaultTitle();
    }

    private void showSnackBar(String event) {
        Snackbar.make(titleBarMessage, event, Snackbar.LENGTH_SHORT).show();
    }

    private void setDefaultTitle() {
        if (chatType == DemoConstant.CHATTYPE_GROUP) {
            if (isMoCustomer) {
                titleBarMessage.setTitle("MO客服");
            }
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onRightClick(View view) {
        if (chatType == DemoConstant.CHATTYPE_SINGLE) {
            //跳转到单聊设置页面
//            SingleChatSetActivity.actionStart(mContext, conversationId);
            ChatDetailSet.actionStart(mContext, conversationId);
        } else {
            // 跳转到群组设置
            if (chatType == DemoConstant.CHATTYPE_GROUP) {
//                GroupDetailActivity.actionStart(mContext, conversationId);
                GroupDetailSet.actionStart(mContext, conversationId);


            }
        }
    }

    @Override
    public void onChatError(int code, String errorMsg) {
        if (!TextUtils.isEmpty(errorMsg)) {
            if ("Not in group or chatroom white list".equals(errorMsg)||"User muted".equals(errorMsg)) {
                showToast("您已被群主或管理员禁言");
            }else if("User has no permission for this operation".equals(errorMsg)){
                showToast("您已被对方加入黑名单");
            }else if("User has no permission for this operation".equals(errorMsg)){
                showToast("您已被对方加入黑名单");
            }
        }
    }

    @Override
    public void onOtherTyping(String action) {
        if (TextUtils.equals(action, "TypingBegin")) {
            titleBarMessage.setTitle(getString(com.hyphenate.easeui.R.string.alert_during_typing));
        } else if (TextUtils.equals(action, "TypingEnd")) {
            setDefaultTitle();
        }
    }


}
