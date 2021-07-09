package com.xunda.mo.hx.common.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMChatRoomManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContactManager;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMPushManager;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.db.DemoDbHelper;
import com.xunda.mo.hx.common.db.dao.EmUserDao;
import com.xunda.mo.hx.common.db.dao.InviteMessageDao;
import com.xunda.mo.hx.common.db.dao.MsgTypeManageDao;
import com.xunda.mo.main.baseView.MyApplication;

public class BaseEMRepository {

    /**
     * return a new liveData
     * @param item
     * @param <T>
     * @return
     */
    public <T> LiveData<T> createLiveData(T item) {
        return new MutableLiveData<>(item);
    }

    /**
     * login before
     * @return
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    /**
     * 获取本地标记，是否自动登录
     * @return
     */
    public boolean isAutoLogin() {
        return DemoHelper.getInstance().getAutoLogin();
    }

    /**
     * 获取当前用户
     * @return
     */
    public String getCurrentUser() {
        return DemoHelper.getInstance().getCurrentUser();
    }

    /**
     * EMChatManager
     * @return
     */
    public EMChatManager getChatManager() {
        return DemoHelper.getInstance().getEMClient().chatManager();
    }

    /**
     * EMContactManager
     * @return
     */
    public EMContactManager getContactManager() {
        return DemoHelper.getInstance().getContactManager();
    }

    /**
     * EMGroupManager
     * @return
     */
    public EMGroupManager getGroupManager() {
        return DemoHelper.getInstance().getEMClient().groupManager();
    }

    /**
     * EMChatRoomManager
     * @return
     */
    public EMChatRoomManager getChatRoomManager() {
        return DemoHelper.getInstance().getChatroomManager();
    }


    /**
     * EMPushManager
     * @return
     */
    public EMPushManager getPushManager() {
        return DemoHelper.getInstance().getPushManager();
    }

    /**
     * init room
     */
    public void initDb() {
        DemoDbHelper.getInstance(MyApplication.getInstance()).initDb(getCurrentUser());
    }

    /**
     * EmUserDao
     * @return
     */
    public EmUserDao getUserDao() {
        return DemoDbHelper.getInstance(MyApplication.getInstance()).getUserDao();
    }

    /**
     * get MsgTypeManageDao
     * @return
     */
    public MsgTypeManageDao getMsgTypeManageDao() {
        return DemoDbHelper.getInstance(MyApplication.getInstance()).getMsgTypeManageDao();
    }

    /**
     * get invite message dao
     * @return
     */
    public InviteMessageDao getInviteMessageDao() {
        return DemoDbHelper.getInstance(MyApplication.getInstance()).getInviteMessageDao();
    }

    /**
     * 在主线程执行
     * @param runnable
     */
    public void runOnMainThread(Runnable runnable) {
        EaseThreadManager.getInstance().runOnMainThread(runnable);
    }

    /**
     * 在异步线程
     * @param runnable
     */
    public void runOnIOThread(Runnable runnable) {
        EaseThreadManager.getInstance().runOnIOThread(runnable);
    }

    public Context getContext() {
        return MyApplication.getInstance().getApplicationContext();
    }

}
