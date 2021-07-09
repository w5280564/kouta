
package com.xunda.mo.hx.section.contact.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.hyphenate.easecallkit.base.EaseCallUserInfo;
import com.hyphenate.easecallkit.livedatas.EaseLiveDataBus;
import com.hyphenate.easecallkit.utils.EaseCallKitUtils;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.modules.contact.EaseContactListFragment;
import com.hyphenate.easeui.modules.contact.EaseContactListLayout;
import com.hyphenate.easeui.modules.contact.model.EaseContactCustomBean;
import com.hyphenate.easeui.modules.contact.model.EaseContactSetStyle;
import com.hyphenate.easeui.modules.menu.EasePopupMenuHelper;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.EaseSearchTextView;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.enums.SearchType;
import com.xunda.mo.hx.common.interfaceOrImplement.OnResourceParseCallback;
import com.xunda.mo.hx.common.net.Resource;
import com.xunda.mo.hx.common.utils.ToastUtils;
import com.xunda.mo.hx.section.base.BaseActivity;
import com.xunda.mo.hx.section.contact.activity.AddContactActivity;
import com.xunda.mo.hx.section.contact.activity.ChatRoomContactManageActivity;
import com.xunda.mo.hx.section.contact.activity.ContactDetailActivity;
import com.xunda.mo.hx.section.contact.activity.GroupContactManageActivity;
import com.xunda.mo.hx.section.contact.adapter.MyContactHead_ListAdapter;
import com.xunda.mo.hx.section.contact.adapter.MyContactList_Adapter;
import com.xunda.mo.hx.section.contact.viewmodels.ContactsViewModel;
import com.xunda.mo.hx.section.dialog.DemoDialogFragment;
import com.xunda.mo.hx.section.dialog.SimpleDialogFragment;
import com.xunda.mo.hx.section.domain.MyEaseUser;
import com.xunda.mo.hx.section.search.SearchFriendsActivity;
import com.xunda.mo.main.MainLogin_Register;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.chat.ChatFriend_Detail;
import com.xunda.mo.main.friend.Friend_Add;
import com.xunda.mo.main.friend.Friend_NewFriends;
import com.xunda.mo.main.friend.MyGroup;
import com.xunda.mo.model.adress_Model;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.pinyin.PinyinUtils;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.xunda.mo.network.saveFile.getShareData;

public class ContactListFragment extends EaseContactListFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private EaseSearchTextView tvSearch;
    public ContactsViewModel mViewModel;
    private MyContactList_Adapter myContactList_adapter;// 列表适配器
    private MyContactHead_ListAdapter myContact_Head_listAdapter;//头部适配器
    private ConcatAdapter concatAdapter;
    private EaseContactListLayout contactList;
    private View more_img;

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
//                    AddContactActivity.startAction(mContext, SearchType.CHAT);
                    Friend_NewFriends.actionStart(mContext);
                    break;
                case R.id.contact_header_item_group_list:
//                    GroupContactManageActivity.actionStart(mContext);
                    MyGroup.actionStart(mContext);
                    break;
                case R.id.contact_header_item_creat_group:
                    ToastUtils.showToast("点击好友分组");
                    break;
                case R.id.contact_header_item_head_file:
                    ToastUtils.showToast("点击文件助手");
                    break;
                case R.id.contact_header_item_head_service:
                    ToastUtils.showToast("点击客服");
                    break;

//                case R.id.contact_header_item_chat_room_list:
//                    ChatRoomContactManageActivity.actionStart(mContext);
//                    break;
            }
        }
    }


    public void addMyHead() {
        myContact_Head_listAdapter.addItem(R.id.contact_header_item_new_chat, R.mipmap.adress_head_friend, "新的朋友");
        myContact_Head_listAdapter.addItem(R.id.contact_header_item_group_list, R.mipmap.adress_head_chat, getString(R.string.em_friends_group_chat));
        myContact_Head_listAdapter.addItem(R.id.contact_header_item_creat_group, R.mipmap.adress_head_group, "好友分组");
        myContact_Head_listAdapter.addItem(R.id.contact_header_item_head_file, R.mipmap.adress_head_file, "文件传输助手");
        myContact_Head_listAdapter.addItem(R.id.contact_header_item_head_service, R.mipmap.adress_head_service, "Mo 客服");
    }

    @Override
    public void onMenuPreShow(EasePopupMenuHelper menuHelper, int position) {
        super.onMenuPreShow(menuHelper, position);
        menuHelper.addItemMenu(1, R.id.action_friend_block, 2, getString(R.string.em_friends_move_into_the_blacklist_new));
        menuHelper.addItemMenu(1, R.id.action_friend_delete, 1, getString(R.string.ease_friends_delete_the_contact));
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
        more_img = head_view.findViewById(R.id.more_img);
        viewTouchDelegate.expandViewTouchDelegate(more_img, 50, 50, 50, 50);
        more_img.setOnClickListener(new more_imgClick());
    }

    private class more_imgClick extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            showMore(getActivity(), more_img, 0);
        }
    }

    private void addSearchView() {
        //添加搜索会话布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.demo_layout_search, null);
        llRoot.addView(view, 1);
        tvSearch = view.findViewById(R.id.tv_search);
        tvSearch.setHint(R.string.em_friend_list_search_hint);
    }


    /**
     * 添加头布局
     */
//    public void addHeader() {
//        contactLayout.getContactList().addCustomItem(R.id.contact_header_item_new_chat, R.mipmap.adress_head_friend, getString(R.string.em_friends_new_chat));
//        contactLayout.getContactList().addCustomItem(R.id.contact_header_item_group_list, R.mipmap.adress_head_chat, getString(R.string.em_friends_group_chat));
//        contactLayout.getContactList().addCustomItem(R.id.contact_header_item_creat_group, R.mipmap.adress_head_group, "好友分组");
//        contactLayout.getContactList().addCustomItem(R.id.contact_header_item_head_file, R.mipmap.adress_head_file, "文件传输助手");
//        contactLayout.getContactList().addCustomItem(R.id.contact_header_item_head_service, R.mipmap.adress_head_service, "Mo 客服");
////        contactLayout.getContactList().addCustomItem(R.id.contact_header_item_chat_room_list, R.drawable.em_friends_chat_room, getString(R.string.em_friends_chat_room));
//    }
    @Override
    public void initListener() {
        super.initListener();
        contactLayout.getSwipeRefreshLayout().setOnRefreshListener(this);
        tvSearch.setOnClickListener(this);
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
                    case R.id.contact_header_item_chat_room_list:
                        ChatRoomContactManageActivity.actionStart(mContext);
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
                    contactLayout.getContactList().setData(data);
                }

                @Override
                public void onLoading(@Nullable List<EaseUser> data) {
                    super.onLoading(data);
                    contactLayout.getContactList().setData(data);
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

        adressData(getActivity(), saveFile.BaseUrl + saveFile.User_Friendlist_Url, "0");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search:
                SearchFriendsActivity.actionStart(mContext);
                break;
        }
    }

    private void showDeleteDialog(EaseUser user) {
        new SimpleDialogFragment.Builder((BaseActivity) mContext)
                .setTitle(R.string.ease_friends_delete_contact_hint)
                .setOnConfirmClickListener(new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        mViewModel.deleteContact(user.getUsername());
                    }
                })
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
     * toast by string
     *
     * @param message
     */
    private void showToast(String message) {
        ToastUtils.showToast(message);
    }

    /**
     * toast by string res
     *
     * @param messageId
     */
    public void showToast(@StringRes int messageId) {
        ToastUtils.showToast(messageId);
    }

    //刷新
    @Override
    public void onRefresh() {
//        mViewModel.loadContactList(true);
        initData();
    }


    //联系人列表
    public void adressData(final Context context, String baseUrl, String projectId) {
        RequestParams params = new RequestParams(baseUrl);
        if (getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", getShareData("JSESSIONID", context));
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {
//                resultString = "{msg:dclO,code:200, data:[{ciphertext:sSE3,fireType:436905937989336.8,headImg:X%kU,hxUserName:5CQo,isCiphertext:-7165580097951464, lightStatus:6140465580380565,remarkName:JCC39l,userId:gSlQQ,userNum:5762441269959617,vipType:-3782876530108332.5}]}";
                if (resultString != null) {
                    adress_Model model = new Gson().fromJson(resultString, adress_Model.class);
                    if (model.getCode() == -1 || model.getCode() == 500) {
                        Intent intent = new Intent(context, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (model.getCode() == 200) {

                        List<EaseUser> data = new ArrayList<>();
                        for (int i = 0; i < model.getData().size(); i++) {
                            adress_Model.DataDTO dataDTO = model.getData().get(i);
                            EaseUser user = new MyEaseUser();
//                            user.setContact(dataDTO.);
                            user.setUsername(dataDTO.getHxUserName());
                            user.setNickname(dataDTO.getNikeName());
                            // 正则表达式，判断首字母是否是英文字母
                            String pinyin = PinyinUtils.getPingYin(dataDTO.getNikeName());
                            String sortString = pinyin.substring(0, 1).toUpperCase();
                            if (sortString.matches("[A-Z]")) {
//                                user.setInitialLetter(PinyinUtils.getFirstSpell(dataDTO.getNikeName()));
                                user.setInitialLetter(sortString);
                            } else {
                                user.setInitialLetter("#");
                            }
                            user.setAvatar(dataDTO.getHeadImg());
                            user.setBirth("");
                            user.setContact(0);//朋友属性 4是没有预设置
                            user.setEmail("");
                            user.setGender(0);
                            user.setBirth("");
                            user.setSign("");
                            user.setExt("");
                            user.setPhone("");
//                            user.setLightStatus(dataDTO.getLightStatus());
//                            user.setVipType(dataDTO.getVipType());
//                            user.setUserNum(dataDTO.getUserNum());

                            JSONObject obj = new JSONObject();
                            try {
                                obj.put("lightStatus", dataDTO.getLightStatus());
                                obj.put("vipType", dataDTO.getVipType());
                                obj.put("userNum", dataDTO.getUserNum());
                                obj.put("userId", dataDTO.getUserId());
                                obj.put("remarkName", dataDTO.getRemarkName());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            user.setExt(obj.toString());

                            data.add(user);

                            //通知callKit更新头像昵称
                            EaseCallUserInfo info = new EaseCallUserInfo(dataDTO.getNikeName(), dataDTO.getHeadImg());
                            info.setUserId(info.getUserId());
                            EaseLiveDataBus.get().with(EaseCallKitUtils.UPDATE_USERINFO).postValue(info);
                        }

                        sortList(data);
                        myContactList_adapter.setData(data);
                        contactLayout.getSwipeRefreshLayout().setRefreshing(false);
//                        contactList.setData(data);
//                        contactList.refreshList();

                        //更新本地数据库信息
                        DemoHelper.getInstance().updateUserList(data);
                        //更新本地联系人列表
                        DemoHelper.getInstance().updateContactList();


//                        //通知UI刷新列表
//                        EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_ADD, EaseEvent.TYPE.CONTACT);
//                        event.message = userInfos.keySet().toString();
//                        //发送联系人更新事件
//                        LiveDataBus.get().with(DemoConstant.CONTACT_ADD).postValue(event);

                    } else {
                        Toast.makeText(context, model.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                String errStr = throwable.getMessage();
                if (errStr.equals("Authorization")) {
                    Intent intent = new Intent(context, MainLogin_Register.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onCancelled(Callback.CancelledException e) {
            }

            @Override
            public void onFinished() {
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
    private void showMore(final Context mContext, final View view, final int pos) {
        View contentView = View.inflate(mContext, R.layout.popup_morefriend, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
//        if (MorePopup.isShowing()){
//            return;
//        }
        MorePopup.setWidth(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
//        MorePopup.showAtLocation(view, Gravity.TOP|Gravity.RIGHT, 20, 12);
        MorePopup.showAsDropDown(view, 20, 12);
        LinearLayout add_lin = contentView.findViewById(R.id.add_lin);
//        TextView newregistr_txt = contentView.findViewById(R.id.newregistr_txt);
        add_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Friend_Add.class);
                startActivity(intent);
                MorePopup.dismiss();
            }
        });

//        contentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MorePopup.dismiss();
//            }
//        });
    }


//    private void warpEMUserInfo(EMUserInfo userInfo){
//        if(userInfo != null && mUser != null){
//            EmUserEntity userEntity = new EmUserEntity();
//            userEntity.setUsername(mUser.getUsername());
//            userEntity.setNickname(userInfo.getNickName());
//            userEntity.setEmail(userInfo.getEmail());
//            userEntity.setAvatar(userInfo.getAvatarUrl());
//            userEntity.setBirth(userInfo.getBirth());
//            userEntity.setGender(userInfo.getGender());
//            userEntity.setExt(userInfo.getExt());
//            userEntity.setSign(userInfo.getSignature());
//            EaseCommonUtils.setUserInitialLetter(userEntity);
//            userEntity.setContact(0);
//
//            //更新本地数据库信息
//            DemoHelper.getInstance().update(userEntity);
//
//            //更新本地联系人列表
//            DemoHelper.getInstance().updateContactList();
//            EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_UPDATE, EaseEvent.TYPE.CONTACT);
//            event.message = mUser.getUsername();
//            //发送联系人更新事件
//            LiveDataBus.get().with(DemoConstant.CONTACT_UPDATE).postValue(event);
//        }
//    }


}
