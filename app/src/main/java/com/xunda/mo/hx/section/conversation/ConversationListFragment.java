package com.xunda.mo.hx.section.conversation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.manager.EaseSystemMsgManager;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationSetStyle;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.StringUtil;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.xunda.mo.R;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.interfaceOrImplement.OnResourceParseCallback;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.common.net.Resource;
import com.xunda.mo.hx.common.utils.ToastUtils;
import com.xunda.mo.hx.section.base.BaseActivity;
import com.xunda.mo.hx.section.chat.activicy.ChatActivity;
import com.xunda.mo.hx.section.chat.viewmodel.MessageViewModel;
import com.xunda.mo.hx.section.conversation.viewmodel.ConversationListViewModel;
import com.xunda.mo.hx.section.dialog.DemoDialogFragment;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.hx.section.group.activity.GroupPrePickActivity;
import com.xunda.mo.hx.section.message.SystemMsgsActivity;
import com.xunda.mo.hx.section.search.SearchConversationActivity;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.conversation.Group_Notices;
import com.xunda.mo.main.discover.activity.Discover_QRCode;
import com.xunda.mo.main.friend.activity.Friend_Add;
import com.xunda.mo.main.friend.activity.Friend_NewFriends;
import com.xunda.mo.model.ChatUserBean;
import com.xunda.mo.model.GruopInfo_Bean;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.xUtils3Http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ConversationListFragment extends MyEaseConversationListFragment implements View.OnClickListener {
    private LinearLayout ll_search;

    private ConversationListViewModel mViewModel;
    private TextView tv_title;

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        View head_view = LayoutInflater.from(mContext).inflate(R.layout.conversationlist_head, null);
        llRoot.addView(head_view, 0);
        tv_title = head_view.findViewById(R.id.tv_title);
        head_view.findViewById(R.id.cl_add_img).setOnClickListener(new add_imgClick());
        //添加搜索会话布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.demo_layout_search, null);
        llRoot.addView(view, 1);
        ll_search = view.findViewById(R.id.ll_search);
        TextView tv_search = view.findViewById(R.id.tv_search);
        tv_search.setHint("搜索");
        conversationListLayout.getListAdapter().setEmptyLayoutId(R.layout.ease_layout_default_no_data);

        conversationListLayout.setItemHeight((int) EaseCommonUtils.sp2px(mContext, 66));
        //设置头像尺寸
        conversationListLayout.setAvatarSize(EaseCommonUtils.dip2px(mContext, 44));
        //设置头像样式：0为默认，1为圆形，2为方形(设置方形时，需要配合设置avatarRadius，默认的avatarRadius为50dp)
        conversationListLayout.setAvatarShapeType(EaseImageView.ShapeType.RECTANGLE);
        //设置圆角半径
        conversationListLayout.setAvatarRadius((int) EaseCommonUtils.dip2px(mContext, 4));
        //设置标题字体的颜色
        conversationListLayout.setTitleTextColor(ContextCompat.getColor(mContext, R.color.blacktitle));
        conversationListLayout.setTitleTextSize((int) EaseCommonUtils.sp2px(mContext, 15));//标题字体大小
        conversationListLayout.setContentTextColor(ContextCompat.getColor(mContext, R.color.greytwo));//内容颜色
        //设置是否隐藏未读消息数，默认为不隐藏
        conversationListLayout.hideUnreadDot(false);
        //设置未读消息数展示位置，默认为左侧
        conversationListLayout.showUnreadDotPosition(EaseConversationSetStyle.UnreadDotPosition.LEFT);
        conversationListLayout.showSystemMessage(true);//是否展示系统消息
        initViewModel();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item, int position) {
        EaseConversationInfo info = conversationListLayout.getItem(position);
        Object object = info.getInfo();

        if (object instanceof EMConversation) {
            switch (item.getItemId()) {
                case R.id.action_con_make_top:
                    conversationListLayout.makeConversationTop(position, info);
                    return true;
                case R.id.action_con_cancel_top:
                    conversationListLayout.cancelConversationTop(position, info);
                    return true;
                case R.id.action_con_delete:
                    showDeleteDialog(position, info);
                    return true;
            }
        }
        return super.onMenuItemClick(item, position);
    }

    private void showDeleteDialog(int position, EaseConversationInfo info) {
        new SimpleDialogFragment.Builder((BaseActivity) mContext)
                .setTitle(R.string.delete_conversation)
                .setOnConfirmClickListener(R.string.delete, new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        conversationListLayout.deleteConversation(position, info);
                        LiveDataBus.get().with(DemoConstant.CONVERSATION_DELETE).postValue(new EaseEvent(DemoConstant.CONVERSATION_DELETE, EaseEvent.TYPE.MESSAGE));
                    }
                })
                .showCancelButton(true)
                .show();
    }

    @Override
    public void initListener() {
        super.initListener();
        ll_search.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
    }



    @Override
    public void notifyItemRemove(int position) {
        super.notifyItemRemove(position);
    }

    private void initViewModel() {
        mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);

        mViewModel.getDeleteObservable().observe(getViewLifecycleOwner(), new Observer<Resource<Boolean>>() {
            @Override
            public void onChanged(Resource<Boolean> booleanResource) {
                parseResource(booleanResource, new OnResourceParseCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
                        conversationListLayout.loadDefaultData();
                    }
                });
            }
        });

        mViewModel.getReadObservable().observe(getViewLifecycleOwner(), new Observer<Resource<Boolean>>() {
            @Override
            public void onChanged(Resource<Boolean> booleanResource) {
                parseResource(booleanResource, new OnResourceParseCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
                        conversationListLayout.loadDefaultData();
                    }
                });
            }
        });

        mViewModel.getConversationInfoObservable().observe(getViewLifecycleOwner(), new Observer<Resource<List<EaseConversationInfo>>>() {
            @Override
            public void onChanged(Resource<List<EaseConversationInfo>> listResource) {
                parseResource(listResource, new OnResourceParseCallback<List<EaseConversationInfo>>(true) {
                    @Override
                    public void onSuccess(@Nullable List<EaseConversationInfo> data) {
                        conversationListLayout.setData(data);
                    }
                });
            }
        });


        MessageViewModel messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        LiveDataBus messageChange = messageViewModel.getMessageChange();
        messageChange.with(DemoConstant.NOTIFY_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.CONVERSATION_DELETE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.CONVERSATION_READ, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.CONTACT_ADD, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.CONTACT_DELETE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(MyConstant.MESSAGE_CHANGE_SAVE_MESSAGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.MESSAGE_NOT_SEND, Boolean.class).observe(getViewLifecycleOwner(), this::refreshList);

        messageChange.with(MyConstant.MESSAGE_CHANGE_UPDATE_GROUP_IMAGE, EaseEvent.class).observe(getViewLifecycleOwner(), new Observer<EaseEvent>() {
            @Override
            public void onChanged(EaseEvent event) {
                if (event == null) {
                    return;
                }
                if (event.isGroupChange()) {
                    if (!StringUtil.isBlank(event.message)) {
                        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(event.message);
                        if (conversation!=null) {
                            EMMessage lastMessage = conversation.getLastMessage();
                            if (lastMessage!=null) {
                                lastMessage.setAttribute(MyConstant.GROUP_HEAD, event.message2);
                                EMClient.getInstance().chatManager().updateMessage(lastMessage);
                                conversationListLayout.loadDefaultData();
                            }

                        }
                    }
                }
            }
        });

        messageChange.with(MyConstant.CONTACT_UPDATE, EaseEvent.class).observe(getViewLifecycleOwner(), new Observer<EaseEvent>() {
            @Override
            public void onChanged(EaseEvent event) {
                if (event == null) {
                    return;
                }
                if (event.isContactChange()) {
                    if (!StringUtil.isBlank(event.message)) {
                        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(event.message);
                        if (conversation!=null) {
                            EMMessage lastMessage = conversation.getLastMessage();
                            if (lastMessage!=null) {
                                lastMessage.setAttribute(MyConstant.TO_NAME, event.message2);
                                EMClient.getInstance().chatManager().updateMessage(lastMessage);
                                conversationListLayout.loadDefaultData();
                            }

                        }
                    }
                }
            }
        });
    }

    private void refreshList(Boolean event) {
        if (event == null) {
            return;
        }
        if (event) {
            conversationListLayout.loadDefaultData();
        }
    }

    private void loadList(EaseEvent change) {
        if (change == null) {
            return;
        }
        if (change.isMessageChange() || change.isNotifyChange()
                || change.isGroupLeave()
                || change.isContactChange()
                ||  change.isGroupChange()) {

            conversationListLayout.loadDefaultData();
        }
    }

    /**
     * 解析Resource<T>
     *
     * @param response
     * @param callback
     * @param <T>
     */
    public <T> void parseResource(Resource<T> response, @NonNull OnResourceParseCallback<T> callback) {
        if (mContext instanceof BaseActivity) {
            ((BaseActivity) mContext).parseResource(response, callback);
        }
    }

    /**
     * toast by string
     *
     * @param message
     */
    public void showToast(String message) {
        ToastUtils.showToast(message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_search:
                SearchConversationActivity.actionStart(mContext);
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        super.onItemClick(view, position);
        EaseConversationInfo info = conversationListLayout.getItem(position);
        if (info==null) {
            return;
        }
        Object item = info.getInfo();
        if (item==null) {
            return;
        }
        if (item instanceof EMConversation) {
            EMConversation currentConversation = (EMConversation) item;

            if (TextUtils.equals((currentConversation).getLastMessage().getFrom(), MyConstant.ADMIN)) {
                Group_Notices.actionStart(mContext, (currentConversation).conversationId());
            } else if (EaseSystemMsgManager.getInstance().isSystemConversation(currentConversation)) {
                Friend_NewFriends.actionStart(mContext);
            } else {
                if (EaseCommonUtils.getChatType(currentConversation) == EaseConstant.CHATTYPE_GROUP) {
                    if (isMOCustomer(currentConversation)) {
                        ChatActivity.actionStart(mContext, currentConversation.conversationId(), DemoConstant.CHATTYPE_GROUP,true);//客服
                    } else {
                        GroupMethod(getActivity(), saveFile.Group_MyGroupInfo_Url, currentConversation);
                    }
                } else if (EaseCommonUtils.getChatType(currentConversation) == EaseConstant.CHATTYPE_SINGLE) {
                    AddFriendMethod(position,currentConversation,info);
                }
            }
        }
    }

    //是否是客服会话
    private boolean isMOCustomer(EMConversation item) {
        EMMessage conMsg = item.getLatestMessageFromOthers();
        if (conMsg == null) {
            return false;
        }
        Map<String, Object> mapExt = conMsg.ext();
        if (mapExt != null && !mapExt.isEmpty()) {
            String messType = (String) mapExt.get(MyConstant.MESSAGE_TYPE);
            return !TextUtils.isEmpty(messType) && Objects.equals(messType, MyConstant.MO_CUSTOMER);
        }
        return false;
    }

    @Override
    public void notifyItemChange(int position) {
        super.notifyItemChange(position);
        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
    }

    @Override
    public void notifyAllChange() {
        super.notifyAllChange();
    }

    private class add_imgClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            showMore(getActivity());
        }
    }

    //更多
    private void showMore(final Context mContext) {
        View contentView = View.inflate(mContext, R.layout.popup_more_convers, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        MorePopup.setWidth(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
        MorePopup.showAsDropDown(tv_title, 0, 0, Gravity.RIGHT);
        ConstraintLayout add_Constraint = contentView.findViewById(R.id.add_Constraint);
        ConstraintLayout setUp_Constraint = contentView.findViewById(R.id.setUp_Constraint);
        ConstraintLayout QR_Constraint = contentView.findViewById(R.id.QR_Constraint);

        add_Constraint.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, Friend_Add.class);
            startActivity(intent);
            MorePopup.dismiss();
        });
        //创建群聊
        setUp_Constraint.setOnClickListener(v -> {
            GroupPrePickActivity.actionStart(mContext);
            MorePopup.dismiss();
        });

        QR_Constraint.setOnClickListener(v -> startCamera(requireActivity()));

    }

    public void makeAllMsgRead() {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(DemoConstant.DEFAULT_SYSTEM_MESSAGE_ID, EMConversation.EMConversationType.Chat, true);
        conversation.markAllMessagesAsRead();
        LiveDataBus.get().with(DemoConstant.NOTIFY_CHANGE).postValue(EaseEvent.create(DemoConstant.NOTIFY_CHANGE, EaseEvent.TYPE.NOTIFY));
    }

    public void startCamera(Activity context) {
        //监听授权
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(context, permissions, 1);
        } else {
            //打开相机录制视频
            Intent captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            //判断相机是否正常。
            if (captureIntent.resolveActivity(context.getPackageManager()) != null) {
                Discover_QRCode.actionStart(context);
            }

        }
    }

    public void GroupMethod(Context context, String baseUrl, EMConversation item) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupHxId", item.conversationId());
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                GruopInfo_Bean groupModel = new Gson().fromJson(result, GruopInfo_Bean.class);
                if (groupModel==null) {
                    return;
                }

                GruopInfo_Bean.DataDTO dataDTO = groupModel.getData();

                if (dataDTO!=null) {
                    String showImg = dataDTO.getGroupHeadImg();
                    String showName = dataDTO.getGroupName();
                    refreshGroupMessage(item.getLastMessage(),showImg,showName);
                    ChatActivity.actionStart(mContext, item.conversationId(), EaseCommonUtils.getChatType(item));
                }
            }

            @Override
            public void failed(String... args) {

            }
        });
    }

    //刷新这条群消息
    private void refreshGroupMessage(EMMessage getLastMessage,String groupHeadImg,String getGroupName) {
        getLastMessage.setAttribute(MyConstant.GROUP_HEAD, groupHeadImg);
        getLastMessage.setAttribute(MyConstant.GROUP_NAME, getGroupName);
        EMClient.getInstance().chatManager().updateMessage(getLastMessage);
        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
    }


    public void AddFriendMethod(int position,EMConversation currentConversation,EaseConversationInfo info) {
        Map<String, Object> map = new HashMap<>();
        map.put("friendHxName", currentConversation.conversationId());
        xUtils3Http.post(mContext, saveFile.Friend_info_Url, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                ChatUserBean model = new Gson().fromJson(result, ChatUserBean.class);
                if (model==null) {
                    return;
                }
                ChatUserBean.DataDTO data = model.getData();

                if (data==null) {
                    return;
                }

                int deleteStatus = data.getDeleteStatus();//0正常1已销号2封禁

                if (deleteStatus==1) {
                    deleteConversation(position, info, "对方已注销账号");
                    return;
                }

                if (deleteStatus==2) {
                    deleteConversation(position, info, "对方账号已被封禁");
                    return;
                }

                int isFriend = data.getIsFriend();//0不是好友1是好友

                if (isFriend==0) {
                    deleteConversation(position, info, "你还不是他的好友");
                    return;
                }

                int friendStatus = data.getFriendStatus();//1正常2已删除3黑名单

                if (friendStatus==2) {
                    deleteConversation(position, info, "你还不是他的好友");
                    return;
                }

                String showImg = data.getHeadImg();
                String showName = data.getRemarkName();
                refreshFriendMessage(currentConversation.getLastMessage(),showImg,showName,data.getVipType());
                ChatActivity.actionStart(mContext, currentConversation.conversationId(), EaseCommonUtils.getChatType(currentConversation));
            }

            @Override
            public void failed(String... args) {

            }
        });
    }

    //刷新这好友消息
    private void refreshFriendMessage(EMMessage lastMessage,String friendHeadImg,String friendName,int vipType) {
        lastMessage.setAttribute(MyConstant.TO_NAME, friendName);
        lastMessage.setAttribute(MyConstant.TO_HEAD, friendHeadImg);
        lastMessage.setAttribute(MyConstant.TO_VIP, vipType);
        EMClient.getInstance().chatManager().updateMessage(lastMessage);
        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
    }

    private void deleteConversation(int position, EaseConversationInfo info, String text) {
        Toast.makeText(mContext,text,Toast.LENGTH_SHORT).show();
        conversationListLayout.deleteConversation(position, info);
        LiveDataBus.get().with(DemoConstant.CONVERSATION_DELETE).postValue(new EaseEvent(DemoConstant.CONVERSATION_DELETE, EaseEvent.TYPE.MESSAGE));
    }



    @Override
    public void onRefresh() {
        super.onRefresh();
        conversationListLayout.loadDefaultData();
    }

}
