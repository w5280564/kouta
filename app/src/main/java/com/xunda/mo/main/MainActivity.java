package com.xunda.mo.main;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMUserInfo;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easecallkit.base.EaseCallUserInfo;
import com.hyphenate.easecallkit.livedatas.EaseLiveDataBus;
import com.hyphenate.easecallkit.ui.EaseMultipleVideoActivity;
import com.hyphenate.easecallkit.ui.EaseVideoCallActivity;
import com.hyphenate.easecallkit.utils.EaseCallKitUtils;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.manager.EaseSystemMsgManager;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.ui.base.EaseBaseFragment;
import com.hyphenate.easeui.utils.GsonUtil;
import com.hyphenate.easeui.utils.ListUtils;
import com.hyphenate.easeui.utils.StringUtil;
import com.hyphenate.util.EMLog;
import com.xunda.mo.R;
import com.xunda.mo.dialog.ChooseAppMarketDialog;
import com.xunda.mo.dialog.VersionDialog;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.constant.DemoConstant;
import com.xunda.mo.hx.common.db.DemoDbHelper;
import com.xunda.mo.hx.common.db.entity.InviteMessageStatus;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.common.manager.HMSPushHelper;
import com.xunda.mo.hx.common.utils.PreferenceManager;
import com.xunda.mo.hx.common.utils.PushUtils;
import com.xunda.mo.hx.section.base.BaseInitActivity;
import com.xunda.mo.hx.section.chat.ChatPresenter;
import com.xunda.mo.hx.section.contact.fragment.ContactListFragment;
import com.xunda.mo.hx.section.contact.viewmodels.ContactsViewModel;
import com.xunda.mo.hx.section.conversation.ConversationListFragment;
import com.xunda.mo.main.baseView.MyApplication;
import com.xunda.mo.main.constant.MyConstant;
import com.xunda.mo.main.discover.DiscoverFragment;
import com.xunda.mo.main.me.MeFragment;
import com.xunda.mo.main.viewmodels.MainViewModel;
import com.xunda.mo.model.ApkBean;
import com.xunda.mo.model.AppMarketBean;
import com.xunda.mo.model.AppVersionBean;
import com.xunda.mo.model.adress_Model;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.pinyin.PinyinUtils;
import com.xunda.mo.staticdata.SortMembersList;
import com.xunda.mo.staticdata.StaticData;
import com.xunda.mo.staticdata.xUtils3Http;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;


public class MainActivity extends BaseInitActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView navView;
    private EaseBaseFragment mConversationListFragment, mFriendsFragment, mDiscoverFragment, mAboutMeFragment;
    private EaseBaseFragment mCurrentFragment;
    private TextView mTvMainHomeMsg, mTvMainFriendsMsg;
    private int[] badgeIds = {R.layout.demo_badge_home, R.layout.demo_badge_friends, R.layout.demo_badge_discover, R.layout.demo_badge_about_me};
    private int[] msgIds = {R.id.tv_main_home_msg, R.id.tv_main_friends_msg, R.id.tv_main_discover_msg, R.id.tv_main_about_me_msg};
    private MainViewModel viewModel;
    private boolean showMenu = true;//是否显示菜单项

    public static void startAction(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    /**
     * 显示menu的icon，通过反射，设置menu的icon显示
     *
     * @param featureId
     * @param menu
     * @return
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return super.onMenuOpened(featureId, menu);
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        navView = findViewById(R.id.nav_view);
//        mTitleBar = findViewById(R.id.title_bar_main);
        navView.setItemIconTintList(null);
        // 可以动态显示隐藏相应tab
        //navView.getMenu().findItem(R.id.em_main_nav_me).setVisible(false);
//        switchToFriends();

//        switchToHome();
        addToHone();
        checkIfShowSavedFragment(savedInstanceState);
        addTabBadge();

        //设置头像配置属性
        EaseAvatarOptions avatarOptions = new EaseAvatarOptions();
        //设置头像形状为圆形，1代表圆形，2代表方形
        avatarOptions.setAvatarShape(2);
        EaseIM.getInstance().setAvatarOptions(avatarOptions);
    }

    @Override
    protected void initListener() {
        super.initListener();
        navView.setOnNavigationItemSelectedListener(this);
    }

    private void addToHone() {
        switchToHome();
    }


    @Override
    protected void initData() {
        super.initData();
        checkUpdate();
        initViewModel();
        checkUnreadMsg();
        ChatPresenter.getInstance().init();
        // 获取华为 HMS 推送 token
        HMSPushHelper.getInstance().getHMSToken(this);
        //判断是否为来电推送
        if (PushUtils.isRtcCall) {
            if (EaseCallType.getfrom(PushUtils.type) != EaseCallType.CONFERENCE_CALL) {
                EaseVideoCallActivity callActivity = new EaseVideoCallActivity();
                Intent intent = new Intent(getApplicationContext(), callActivity.getClass()).addFlags(FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            } else {
                EaseMultipleVideoActivity callActivity = new EaseMultipleVideoActivity();
                Intent intent = new Intent(getApplication().getApplicationContext(), callActivity.getClass()).addFlags(FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
            PushUtils.isRtcCall = false;
        }
    }


    public void handleUnReadMessageNumber(int unreadMsgCount) {
        if(unreadMsgCount <= 99) {
            mTvMainHomeMsg.setText(String.valueOf(unreadMsgCount));
        }else {
            mTvMainHomeMsg.setText("99+");
        }
    }


    private void checkUnreadMsg() {
        viewModel.checkUnreadMsg();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(mContext).get(MainViewModel.class);
        viewModel.getSwitchObservable().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer response) {
                if (response == null || response == 0) {
                    return;
                }
            }
        });

        viewModel.homeUnReadObservable().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer readCount) {
                Log.e("readCount","readCount>>>"+readCount);
                if (readCount>0) {
                    ShortcutBadger.applyCount(MainActivity.this, readCount);//设置角标
                    mTvMainHomeMsg.setVisibility(View.VISIBLE);
                    handleUnReadMessageNumber(readCount);//item.getUnreadMsgCount()
                } else {
                    ShortcutBadger.applyCount(MainActivity.this, 0);//设置角标
                    mTvMainHomeMsg.setVisibility(View.GONE);
                }
            }
        });


        viewModel.messageChangeObservable().with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, new Observer<EaseEvent>() {
            @Override
            public void onChanged(EaseEvent easeEvent) {
                if (easeEvent == null) {
                    return;
                }
            }
        });

        viewModel.messageChangeObservable().with(MyConstant.ConstantCount, int.class).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer count) {
                if (count > 0) {
                    mTvMainFriendsMsg.setVisibility(View.VISIBLE);
                    mTvMainFriendsMsg.setText(count + "");
                }
            }
        });

        setFriendAdd();
        //加载联系人
        ContactsViewModel contactsViewModel = new ViewModelProvider(mContext).get(ContactsViewModel.class);
        contactsViewModel.loadContactList(true);

        viewModel.messageChangeObservable().with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(DemoConstant.NOTIFY_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);
//
        viewModel.messageChangeObservable().with(DemoConstant.CONVERSATION_DELETE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(DemoConstant.CONVERSATION_READ, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(DemoConstant.CONTACT_DELETE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(MyConstant.MESSAGE_CHANGE_SAVE_MESSAGE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        addressData(MainActivity.this, saveFile.User_Friendlist_Url);
    }

    //添加好友通知
    private void setFriendAdd() {
        List<EMMessage> allMessages = EaseSystemMsgManager.getInstance().getAllMessages();
        int friendCount = 0;
        if (allMessages != null && !allMessages.isEmpty()) {
            for (EMMessage message : allMessages) {
                Map<String, Object> ext = message.ext();
                if (ext != null && ext.get(DemoConstant.SYSTEM_MESSAGE_STATUS).equals(InviteMessageStatus.BEINVITEED.name())) {//"BEINVITEED"
                    friendCount += 1;
                }
            }
            if (friendCount != 0) {
                mTvMainFriendsMsg.setVisibility(View.VISIBLE);
                mTvMainFriendsMsg.setText(friendCount + "");
            }
        }
    }

    private void checkUnReadMsg(EaseEvent event) {
        if (event == null) {
            return;
        }
        viewModel.checkUnreadMsg();
    }

    /**
     * 添加BottomNavigationView中每个item右上角的红点
     */
    private void addTabBadge() {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navView.getChildAt(0);
        int childCount = menuView.getChildCount();
        BottomNavigationItemView itemTab;
        for (int i = 0; i < childCount; i++) {
            itemTab = (BottomNavigationItemView) menuView.getChildAt(i);
            View badge = LayoutInflater.from(mContext).inflate(badgeIds[i], menuView, false);
            switch (i) {
                case 0:
                    mTvMainHomeMsg = badge.findViewById(msgIds[0]);
                    break;
                case 1:
                    mTvMainFriendsMsg = badge.findViewById(msgIds[1]);
                    break;

            }
            itemTab.addView(badge);
        }
    }

    /**
     * 用于展示是否已经存在的Fragment
     *
     * @param savedInstanceState
     */
    private void checkIfShowSavedFragment(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String tag = savedInstanceState.getString("tag");
            if (!TextUtils.isEmpty(tag)) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment instanceof EaseBaseFragment) {
                    replace((EaseBaseFragment) fragment, tag);
                }
            }
        }
    }


    private void switchToHome() {
        if (mConversationListFragment == null) {
            mConversationListFragment = new ConversationListFragment();
        }
        replace(mConversationListFragment, "conversation");
    }

    private void switchToFriends() {
        if (mFriendsFragment == null) {
            mFriendsFragment = new ContactListFragment();
        }
        replace(mFriendsFragment, "contact");
    }

    private void switchToDiscover() {
        if (mDiscoverFragment == null) {
            mDiscoverFragment = new DiscoverFragment();
        }
        replace(mDiscoverFragment, "discover");
    }

    private void switchToAboutMe() {
        if (mAboutMeFragment == null) {
            mAboutMeFragment = new MeFragment();
        }
        //获取自己用户信息
        fetchSelfInfo();
        replace(mAboutMeFragment, "me");
    }

    private void replace(EaseBaseFragment fragment, String tag) {
        if (mCurrentFragment != fragment) {
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            if (mCurrentFragment != null) {
                t.hide(mCurrentFragment);
            }
            mCurrentFragment = fragment;

            if (!fragment.isAdded()) {
                t.add(R.id.fl_main_fragment, fragment, tag).show(fragment).commit();
            } else {
                t.show(fragment).commit();
            }
        }
    }

    private void fetchSelfInfo() {
        String[] userId = new String[1];
        userId[0] = EMClient.getInstance().getCurrentUser();
        EMUserInfo.EMUserInfoType[] userInfoTypes = new EMUserInfo.EMUserInfoType[2];
        userInfoTypes[0] = EMUserInfo.EMUserInfoType.NICKNAME;
        userInfoTypes[1] = EMUserInfo.EMUserInfoType.AVATAR_URL;
        EMClient.getInstance().userInfoManager().fetchUserInfoByAttribute(userId, userInfoTypes, new EMValueCallBack<Map<String, EMUserInfo>>() {
            @Override
            public void onSuccess(Map<String, EMUserInfo> userInfos) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        EMUserInfo userInfo = userInfos.get(EMClient.getInstance().getCurrentUser());
                        //昵称
                        if (userInfo != null && userInfo.getNickName() != null &&
                                userInfo.getNickName().length() > 0) {
                            EaseEvent event = EaseEvent.create(DemoConstant.NICK_NAME_CHANGE, EaseEvent.TYPE.CONTACT);
                            event.message = userInfo.getNickName();
                            LiveDataBus.get().with(DemoConstant.NICK_NAME_CHANGE).postValue(event);
                            PreferenceManager.getInstance().setCurrentUserNick(userInfo.getNickName());
                        }
                        //头像
                        if (userInfo != null && userInfo.getAvatarUrl() != null && userInfo.getAvatarUrl().length() > 0) {

                            EaseEvent event = EaseEvent.create(DemoConstant.AVATAR_CHANGE, EaseEvent.TYPE.CONTACT);
                            event.message = userInfo.getAvatarUrl();
                            LiveDataBus.get().with(DemoConstant.AVATAR_CHANGE).postValue(event);
                            PreferenceManager.getInstance().setCurrentUserAvatar(userInfo.getAvatarUrl());
                        }
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                EMLog.e("MainActivity", "fetchUserInfoByIds error:" + error + " errorMsg:" + errorMsg);
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        showMenu = true;
        boolean showNavigation = false;
        switch (menuItem.getItemId()) {
            case R.id.em_main_nav_home:
                switchToHome();
                showNavigation = true;
                break;
            case R.id.em_main_nav_friends:
                switchToFriends();
                showNavigation = true;
                invalidateOptionsMenu();
                break;
            case R.id.em_main_nav_discover:
                switchToDiscover();
                showNavigation = true;
                break;
            case R.id.em_main_nav_me:
                switchToAboutMe();
                showMenu = false;
                showNavigation = true;
                break;
        }
        invalidateOptionsMenu();
        return showNavigation;
    }



    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentFragment != null) {
            outState.putString("tag", mCurrentFragment.getTag());
        }
    }

    boolean mIsSupportedBade = true;
    /**
     * set badge number
     */
    public void setBadgeNum(int num) {
        try {
            Bundle bunlde = new Bundle();
            bunlde.putString("package", "com.xunda.mo"); // com.test.badge is your package name
            bunlde.putString("class", "com.xunda.mo.main.MainActivity"); // com.test. badge.MainActivity is your apk main activity
            bunlde.putInt("badgenumber", num);
            this.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, bunlde);
        } catch (Exception e) {
            mIsSupportedBade = false;
        }
    }

    //联系人列表
    public void addressData(final Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                adress_Model model = new Gson().fromJson(result, adress_Model.class);
                addContactList(model);
            }

            @Override
            public void failed(String... args) {
            }
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
        SortMembersList.getLastList(data);
        //先清空本地数据库
        DemoDbHelper.getInstance(MyApplication.getInstance()).getUserDao().clearUsers();
        //更新本地数据库信息
        DemoHelper.getInstance().updateUserList(data);
        //更新本地联系人列表
        DemoHelper.getInstance().updateContactList();
    }


//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>版本更新开始>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>



    private ChooseAppMarketDialog mChooseAppMarketDialog;
    private String website;

    /**
     * 检查新版本
     */
    private void checkUpdate() {
        Map<String, Object> map = new HashMap<>();
        map.put("version", String.valueOf(StaticData.getVersionName(mContext)));
        xUtils3Http.get(this, saveFile.versionUpdate, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                AppVersionBean appVersionObj = GsonUtil.getInstance().json2Bean(result, AppVersionBean.class);
                if (appVersionObj==null) {
                    return;
                }
                ApkBean apkObj = appVersionObj.getData();

                if (apkObj==null) {
                    return;
                }

                int isForceUpdate = apkObj.getIsForceUpdate();//0推荐更新1强制2当前版本最新无需更新
                String remark = apkObj.getRemark();
                String version = apkObj.getVersion();
                website = apkObj.getWebsite();
                if(isForceUpdate!=2){
                    showVersionDialog(remark,version,isForceUpdate,apkObj.getPlatform());
                }
            }

            @Override
            public void failed(String... args) {

            }
        });
    }


    private void showVersionDialog(String remark, String version,int isForceUpdate, String platform) {
        VersionDialog dialog = new VersionDialog(this, remark,version, isForceUpdate,
                new VersionDialog.VersionConfirmListener() {
                    @Override
                    public void onDownload() {
                        if (!StringUtil.isBlank(platform)){
                            List<String> mPlatformList = StringUtil.stringToList(platform);
                            if (!ListUtils.isEmpty(mPlatformList)) {
                                if (mPlatformList.size()==1) {
                                    jumpToWebsite();//只有一个平台时，点击下载直接跳官网
                                }else{//否则弹出平台选择弹窗
                                    handlePlatformList(isForceUpdate,mPlatformList);
                                }
                            }
                        }
                    }

                });
        dialog.show();
    }


    private void handlePlatformList(int isForceUpdate,List<String> mPlatformList){
        String deviceBrandName = android.os.Build.BRAND;
        List<AppMarketBean> mMarketList = new ArrayList<>();
        for (int i = 0; i < mPlatformList.size(); i++) {
            String name = mPlatformList.get(i);
            AppMarketBean obj = new AppMarketBean();
            obj.setMarketName(name);
            if (deviceBrandName.equalsIgnoreCase(MyConstant.BRAND_OPPO) && "OPPO".equals(name)) {
                obj.setMarketPakageName("com.heytap.market");
                obj.setIconResource(R.mipmap.icon_oppo);
                obj.setBrandName(MyConstant.BRAND_OPPO);
                mMarketList.add(obj);
                break;
            } else if (deviceBrandName.equalsIgnoreCase(MyConstant.BRAND_VIVO) && "VIVO".equals(name)) {
                obj.setMarketPakageName("com.bbk.appstore");
                obj.setIconResource(R.mipmap.icon_vivo);
                obj.setBrandName(MyConstant.BRAND_VIVO);
                mMarketList.add(obj);
                break;
            } else if (deviceBrandName.equalsIgnoreCase(MyConstant.BRAND_HUAWEI) && "华为".equals(name)) {
                obj.setMarketPakageName("com.huawei.appmarket");
                obj.setIconResource(R.mipmap.icon_huawei);
                obj.setBrandName(MyConstant.BRAND_HUAWEI);
                mMarketList.add(obj);
                break;
            } else if (deviceBrandName.equalsIgnoreCase(MyConstant.BRAND_HONOR) && "华为".equals(name)) {
                obj.setMarketPakageName("com.huawei.appmarket");
                obj.setIconResource(R.mipmap.icon_huawei);
                obj.setBrandName(MyConstant.BRAND_HUAWEI);
                mMarketList.add(obj);
                break;
            }
        }

        if (mPlatformList.contains("应用宝")) {
            AppMarketBean obj = new AppMarketBean();
            obj.setMarketName("应用宝");
            obj.setMarketPakageName("com.tencent.android.qqdownloader");
            obj.setIconResource(R.mipmap.icon_yyb);
            mMarketList.add(obj);
        }

        showChooseMarketDialog(isForceUpdate, mMarketList);
    }

    /**
     * 弹出选择市场框
     */
    public void showChooseMarketDialog(int isForceUpdate, List<AppMarketBean> mMarketList) {


        if (mChooseAppMarketDialog == null) {
            mChooseAppMarketDialog = new ChooseAppMarketDialog(this, mMarketList, isForceUpdate, new ChooseAppMarketDialog.DialogItemChooseListener() {
                @Override
                public void onItemChooseClick(AppMarketBean obj) {
                    if (obj != null) {
                        if ("官网".equals(obj.getMarketName())) {
                            jumpToWebsite();
                        } else if ("应用宝".equals(obj.getMarketName())) {
                            openTencentYingYongBao(obj.getMarketPakageName());
                        } else {
                            launchAppDetail(obj.getMarketPakageName());
                        }
                    }

                }
            });
        }

        if (!mChooseAppMarketDialog.isShowing()) {
            mChooseAppMarketDialog.show();
        }
    }

    private void jumpToWebsite() {
        if (StringUtil.isBlank(website)) {
            return;
        }
        Uri uri = Uri.parse(website);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    /**
     * 跳转到应用市场app详情界面
     *
     * @param marketPkg 应用市场包名
     */
    public void launchAppDetail(String marketPkg) {
        try {
            Uri uri = Uri.parse("market://details?id=" + MyConstant.APP_PKG);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!StringUtil.isBlank(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //打开应用宝
    private void openTencentYingYongBao(String marketPkg) {
        try {
            Uri uri = Uri.parse("market://details?id=" + MyConstant.APP_PKG);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!StringUtil.isBlank(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "请先安装应用宝", Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("https://a.app.qq.com/o/simple.jsp?pkgname=com.xunda.mo");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

}