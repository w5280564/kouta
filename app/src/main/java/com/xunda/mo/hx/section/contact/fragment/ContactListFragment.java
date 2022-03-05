
package com.xunda.mo.hx.section.contact.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easecallkit.base.EaseCallUserInfo;
import com.hyphenate.easecallkit.livedatas.EaseLiveDataBus;
import com.hyphenate.easecallkit.utils.EaseCallKitUtils;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.manager.EaseSystemMsgManager;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.modules.contact.EaseContactListFragment;
import com.hyphenate.easeui.modules.contact.EaseContactListLayout;
import com.hyphenate.easeui.modules.contact.model.EaseContactCustomBean;
import com.hyphenate.easeui.modules.contact.model.EaseContactSetStyle;
import com.hyphenate.easeui.modules.menu.EasePopupMenuHelper;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.StringUtil;
import com.hyphenate.easeui.widget.EaseImageView;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.db.DemoDbHelper;
import com.xunda.mo.hx.common.db.entity.InviteMessageStatus;
import com.xunda.mo.hx.common.enums.SearchType;
import com.xunda.mo.hx.common.interfaceOrImplement.OnResourceParseCallback;
import com.xunda.mo.hx.common.net.Resource;
import com.xunda.mo.hx.common.utils.ToastUtils;
import com.xunda.mo.hx.section.base.BaseActivity;
import com.xunda.mo.hx.section.chat.activicy.ChatActivity;
import com.xunda.mo.hx.section.contact.activity.AddContactActivity;
import com.xunda.mo.hx.section.contact.activity.ContactDetailActivity;
import com.xunda.mo.hx.section.contact.activity.GroupContactManageActivity;
import com.xunda.mo.hx.section.contact.adapter.MyContactHead_ListAdapter;
import com.xunda.mo.hx.section.contact.adapter.MyContactList_Adapter;
import com.xunda.mo.hx.section.contact.model.MyEaseContactCustomBean;
import com.xunda.mo.hx.section.contact.viewmodels.ContactsViewModel;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.hx.section.search.SearchFriendsActivity;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.baseView.MyApplication;
import com.xunda.mo.main.chat.activity.ChatFriend_Detail;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.friend.activity.Friend_Add;
import com.xunda.mo.main.friend.activity.Friend_BlackMe;
import com.xunda.mo.main.friend.activity.Friend_NewFriends;
import com.xunda.mo.main.friend.activity.MyGroup;
import com.xunda.mo.model.adress_Model;
import com.xunda.mo.model.baseDataModel;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.pinyin.PinyinUtils;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.SortMembersList;
import com.xunda.mo.staticdata.xUtils3Http;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactListFragment extends EaseContactListFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private LinearLayout ll_search;
    public ContactsViewModel mViewModel;
    private MyContactList_Adapter myContactList_adapter;// 列表适配器
    private MyContactHead_ListAdapter myContact_Head_listAdapter;//头部适配器
    private ConcatAdapter concatAdapter;
    private EaseContactListLayout contactList;
    private TextView tv_title;
    private TextView mTvMainFriendsMsg;

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        addTitleView();
        addSearchView();
        //设置无数据时空白页面
        contactLayout.getContactList().getListAdapter().setEmptyLayoutResource(R.layout.demo_layout_friends_empty_list);
//        addHeader();

        //设置为简洁模式
        //contactLayout.showSimple();
        //获取列表控件
        contactList = contactLayout.getContactList();
        //设置条目高度
        contactList.setItemHeight((int) EaseCommonUtils.dip2px(mContext, 66));
        //设置条目背景
        //contactList.setItemBackGround(ContextCompat.getDrawable(mContext, R.color.gray));
        //设置头像样式
        contactList.setAvatarShapeType(EaseImageView.ShapeType.RECTANGLE);
        //设置头像圆角
        contactList.setAvatarRadius((int) EaseCommonUtils.dip2px(mContext, 9));
        contactList.setTitleTextColor(ContextCompat.getColor(mContext, R.color.blacktitle));
        contactList.setHeaderTextSize((int) EaseCommonUtils.dip2px(mContext, 15));
        //设置header背景
        contactList.setHeaderBackGround(ContextCompat.getDrawable(mContext, R.color.white));//头部背景
        contactList.setHeaderTextColor(ContextCompat.getColor(mContext, R.color.blacktitlettwo));//头部字体颜色
        contactList.setHeaderTextSize((int) EaseCommonUtils.dip2px(mContext, 16));//头部字体大小
//        contactLayout.canUseRefresh(false);//取消刷新

        addAdapter();
    }


    //修改成自己定义的头部 与列表适配器
    private void addAdapter() {
        concatAdapter = new ConcatAdapter();
        myContactList_adapter = new MyContactList_Adapter();
        EaseContactSetStyle contactSetModel = new EaseContactSetStyle();
        contactSetModel.setShowItemHeader(true);
        myContactList_adapter.setSettingModel(contactSetModel);
        myContact_Head_listAdapter = new MyContactHead_ListAdapter();
        addMyHead();
        concatAdapter.addAdapter(myContact_Head_listAdapter);
        concatAdapter.addAdapter(myContactList_adapter);
        contactList.setAdapter(concatAdapter);

        myContactList_adapter.setOnItemClickListener(new demoContactList_adapterClick());
        myContact_Head_listAdapter.setOnItemClickListener(new myContact_listAdapterClick());
    }

    //列表条目点击
    private class demoContactList_adapterClick implements OnItemClickListener {
        @Override
        public void onItemClick(View view, int position) {
            EaseUser item = myContactList_adapter.getItem(position);
            String addType = "8";
            ChatFriend_Detail.actionStart(mContext, item.getUsername(), item, addType);
        }
    }

    //列表头部点击
    private class myContact_listAdapterClick implements OnItemClickListener {
        @Override
        public void onItemClick(View view, int position) {
            EaseContactCustomBean item = myContact_Head_listAdapter.getItem(position);
            switch (item.getId()) {
                case R.id.contact_header_item_new_chat:
                    removeAllFriendAddMessage();
                    Friend_NewFriends.actionStart(mContext);
                    break;
                case R.id.contact_header_item_group_list:
//                    GroupContactManageActivity.actionStart(mContext);
                    MyGroup.actionStart(mContext);
                    break;
                case R.id.contact_header_item_creat_group:
//                    ToastUtils.showToast("点击好友分组");
                    break;
                case R.id.contact_header_item_head_file:
                    ToastUtils.showToast("点击文件助手");
                    break;
                case R.id.contact_header_item_head_service:
                    customerServiceData(getActivity(), saveFile.Receptionist_MyMoRoom);
                    break;
//                case R.id.contact_header_item_chat_room_list:
//                    ChatRoomContactManageActivity.actionStart(mContext);
//                    break;
            }
        }
    }


    public void addMyHead() {
        mTvMainFriendsMsg = getActivity().findViewById(R.id.tv_main_friends_msg);
        mTvMainFriendsMsg.setVisibility(View.GONE);
        setNewFriendMessageCount(getFriendAdd());
        myContact_Head_listAdapter.clearData();
        myContact_Head_listAdapter.addItem(R.id.contact_header_item_new_chat, R.mipmap.adress_head_friend, "新朋友", getFriendAdd());
        myContact_Head_listAdapter.addItem(R.id.contact_header_item_group_list, R.mipmap.adress_head_chat, getString(R.string.em_friends_group_chat));
//        myContact_Head_listAdapter.addItem(R.id.contact_header_item_creat_group, R.mipmap.adress_head_group, "好友分组");
        myContact_Head_listAdapter.addItem(R.id.contact_header_item_head_service, R.mipmap.adress_head_service, "Mo 客服");
    }

    private void setNewFriendMessageCount(int addCount) {
        if (addCount > 0) {
            mTvMainFriendsMsg.setVisibility(View.VISIBLE);
            handleUnReadMessageNumber(addCount);
        }else{
            mTvMainFriendsMsg.setVisibility(View.GONE);
        }
    }

    public void handleUnReadMessageNumber(int unreadMsgCount) {
        if(unreadMsgCount <= 99) {
            mTvMainFriendsMsg.setText(String.valueOf(unreadMsgCount));
        }else {
            mTvMainFriendsMsg.setText("99+");
        }
    }


    @Override
    public void onMenuPreShow(EasePopupMenuHelper menuHelper, int position) {
        super.onMenuPreShow(menuHelper, position);
        menuHelper.addItemMenu(1, R.id.action_friend_block, 2, getString(R.string.em_friends_move_into_the_blacklist_new));
        menuHelper.addItemMenu(1, R.id.action_friend_delete, 1, getString(R.string.ease_friends_delete_the_contact));
    }

    //清除好友通知
    private void removeAllFriendAddMessage() {
        List<EMMessage> allMessages = EaseSystemMsgManager.getInstance().getAllMessages();
        if (allMessages != null && !allMessages.isEmpty()) {
            for (EMMessage message : allMessages) {
                Map<String, Object> ext = message.ext();
                if (ext != null && ext.get(DemoConstant.SYSTEM_MESSAGE_STATUS).equals(InviteMessageStatus.BEINVITEED.name())) {//"BEINVITEED"
                    EaseSystemMsgManager.getInstance().removeMessage(message);
                }
            }
        }

        setNewFriendMessageCount(0);
        MyEaseContactCustomBean obj = myContact_Head_listAdapter.getItem(0);
        obj.setCount(0);
        myContact_Head_listAdapter.notifyDataSetChanged();
    }

    //添加好友通知
    private int getFriendAdd() {
        List<EMMessage> allMessages = EaseSystemMsgManager.getInstance().getAllMessages();
        int friendCount = 0;
        if (allMessages != null && !allMessages.isEmpty()) {
            for (EMMessage message : allMessages) {
                Map<String, Object> ext = message.ext();
                if (ext != null && ext.get(DemoConstant.SYSTEM_MESSAGE_STATUS).equals(InviteMessageStatus.BEINVITEED.name())) {//"BEINVITEED"
                    friendCount += 1;
                }
            }
        }
        return friendCount;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item, int position) {
        EaseUser user = contactLayout.getContactList().getItem(position);
        switch (item.getItemId()) {
            case R.id.action_friend_block:
                mViewModel.addUserToBlackList(user.getUsername(), false);
                return true;
            case R.id.action_friend_delete:
                showDeleteDialog(user);
                return true;
        }
        return super.onMenuItemClick(item, position);
    }

    private void addTitleView() {
        View head_view = LayoutInflater.from(mContext).inflate(R.layout.contactlist_head, null);
        llRoot.addView(head_view, 0);
        ConstraintLayout more_Con = head_view.findViewById(R.id.more_Con);
        tv_title = head_view.findViewById(R.id.tv_title);
        more_Con.setOnClickListener(new more_ConClick());
    }

    private class more_ConClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            showMore(getActivity());
        }
    }

    private void addSearchView() {
        //添加搜索会话布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.demo_layout_search, null);
        llRoot.addView(view, 1);
        ll_search = view.findViewById(R.id.ll_search);
        TextView tv_search = view.findViewById(R.id.tv_search);
        tv_search.setHint("搜索昵称/Mo ID");
    }


    @Override
    public void initListener() {
        super.initListener();
        contactLayout.getSwipeRefreshLayout().setOnRefreshListener(this);
        ll_search.setOnClickListener(this);
        contactLayout.getContactList().setOnCustomItemClickListener(new OnItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemClick(View view, int position) {
                EaseContactCustomBean item = contactLayout.getContactList().getCustomAdapter().getItem(position);
                switch (item.getId()) {
                    case R.id.contact_header_item_new_chat:
                        AddContactActivity.startAction(mContext, SearchType.CHAT);
                        break;
                    case R.id.contact_header_item_group_list:
                        GroupContactManageActivity.actionStart(mContext);
                        break;
                }
            }
        });
    }

    @Override
    public void initData() {
        mViewModel = new ViewModelProvider(this).get(ContactsViewModel.class);
        mViewModel.getContactObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> data) {
                    if (data != null){
                    contactLayout.getContactList().setData(data);
                    }
                }

                @Override
                public void onLoading(@Nullable List<EaseUser> data) {
                    super.onLoading(data);
                    if (data != null) {
                        contactLayout.getContactList().setData(data);
                    }
                }
            });
        });

        mViewModel.resultObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    showToast(R.string.em_friends_move_into_blacklist_success);
                    mViewModel.loadContactList(false);
                }
            });
        });

        mViewModel.deleteObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    mViewModel.loadContactList(false);
                }
            });
        });

        mViewModel.messageChangeObservable().with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mViewModel.loadContactList(false);
            }

            if (StringUtil.isBlank(event.message)) {
                return;
            }
            if (event.message.equals("invited")) {
                addMyHead();
            }
        });

        mViewModel.messageChangeObservable().with(DemoConstant.REMOVE_BLACK, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mViewModel.loadContactList(true);
            }
        });


        mViewModel.messageChangeObservable().with(DemoConstant.CONTACT_ADD, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mViewModel.loadContactList(false);
            }
        });


        mViewModel.messageChangeObservable().with(DemoConstant.CONTACT_DELETE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mViewModel.loadContactList(false);
            }
        });

        mViewModel.messageChangeObservable().with(DemoConstant.CONTACT_UPDATE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mViewModel.loadContactList(false);
            }
        });


        mViewModel.loadContactList(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        addressData(getActivity(), saveFile.User_Friendlist_Url);
        addMyHead();
    }

    //刷新
    @Override
    public void onRefresh() {
        addressData(getActivity(), saveFile.User_Friendlist_Url);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_search:
                SearchFriendsActivity.actionStart(mContext);
                break;
        }
    }

    private void showDeleteDialog(EaseUser user) {
        new SimpleDialogFragment.Builder((BaseActivity) mContext)
                .setTitle(R.string.ease_friends_delete_contact_hint)
                .setOnConfirmClickListener(
                        view -> mViewModel.deleteContact(user.getUsername()))
                .showCancelButton(true)
                .show();
    }

    @Override
    public void onItemClick(View view, int position) {
        super.onItemClick(view, position);
        EaseUser item = contactLayout.getContactList().getItem(position);
        ContactDetailActivity.actionStart(mContext, item);
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
     * toast by string res
     *
     * @param messageId
     */
    public void showToast(@StringRes int messageId) {
        ToastUtils.showToast(messageId);
    }



    //联系人列表
    public void addressData(final Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                contactLayout.onRefresh();
                adress_Model model = new Gson().fromJson(result, adress_Model.class);
                addContactList(model);
            }

            @Override
            public void failed(String... args) {
            }
        });

    }

    //客服
    public void customerServiceData(final Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                baseDataModel model = new Gson().fromJson(result, baseDataModel.class);
                //跳转到群组聊天页面
                ChatActivity.actionStart(mContext, model.getData(), DemoConstant.CHATTYPE_GROUP,true);
            }

            @Override
            public void failed(String... args) {
            }
        });
    }


    /**
     * 排序
     *
     * @param list
     */
    private void sortList(List<EaseUser> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Collections.sort(list, new Comparator<EaseUser>() {
            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if (lhs.getInitialLetter().equals(rhs.getInitialLetter())) {
                    return lhs.getNickname().compareTo(rhs.getNickname());
                } else {
                    if ("#".equals(lhs.getInitialLetter())) {
                        return 1;
                    } else if ("#".equals(rhs.getInitialLetter())) {
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }
            }
        });
    }

    //更多
    private void showMore(final Context mContext) {
        View contentView = View.inflate(mContext, R.layout.popup_morefriend, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        MorePopup.setWidth(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
        MorePopup.showAsDropDown(tv_title, 0, 0, Gravity.RIGHT);
        ConstraintLayout add_Con = contentView.findViewById(R.id.add_Con);
        ConstraintLayout dele_Con = contentView.findViewById(R.id.dele_Con);
        add_Con.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, Friend_Add.class);
            startActivity(intent);
            MorePopup.dismiss();
        });
        dele_Con.setOnClickListener(v -> {
            Friend_BlackMe.actionStart(mContext);
            MorePopup.dismiss();
        });

    }

    private void addContactList(adress_Model model) {
        List<EaseUser> data = new ArrayList<>();
        for (int i = 0; i < model.getData().size(); i++) {
            adress_Model.DataDTO dataDTO = model.getData().get(i);
            EaseUser user = new EaseUser();
            user.setUsername(dataDTO.getHxUserName());
            String nickName = TextUtils.isEmpty(dataDTO.getRemarkName()) ? dataDTO.getNickname() : dataDTO.getRemarkName();
            user.setNickname(nickName);
            // 正则表达式，判断首字母是否是英文字母
            String pinyin = PinyinUtils.getPingYin(nickName);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
//                                user.setInitialLetter(PinyinUtils.getFirstSpell(dataDTO.getNickName()));
                user.setInitialLetter(sortString);
            } else {
                user.setInitialLetter("#");
            }
            user.setAvatar(dataDTO.getHeadImg());
            user.setBirth("");
            user.setContact(0);//朋友属性 4是没有预设置
            user.setEmail("");
            user.setGender(0);
            JSONObject obj = new JSONObject();
            try {
                obj.put(MyConstant.LIGHT_STATUS, dataDTO.getLightStatus());
                obj.put(MyConstant.VIP_TYPE, dataDTO.getVipType());
                obj.put(MyConstant.USER_NUM, dataDTO.getUserNum());
                obj.put(MyConstant.ONLINE_STATUS, dataDTO.getOnlineStatus());
                obj.put(MyConstant.USER_ID, dataDTO.getUserId());
                obj.put(MyConstant.REMARK_NAME, dataDTO.getRemarkName());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            user.setExt(obj.toString());
            data.add(user);
            //通知callKit更新头像昵称
            EaseCallUserInfo info = new EaseCallUserInfo(dataDTO.getNickname(), dataDTO.getHeadImg());
            info.setUserId(info.getUserId());
            EaseLiveDataBus.get().with(EaseCallKitUtils.UPDATE_USERINFO).postValue(info);
        }
//        sortList(data);
        SortMembersList.getLastList(data);
        myContactList_adapter.setData(data);
        contactLayout.getSwipeRefreshLayout().setRefreshing(false);

        //先清空本地数据库
        DemoDbHelper.getInstance(MyApplication.getInstance()).getUserDao().clearUsers();
        //更新本地数据库信息
        DemoHelper.getInstance().updateUserList(data);
        //更新本地联系人列表
        DemoHelper.getInstance().updateContactList();
    }


}
