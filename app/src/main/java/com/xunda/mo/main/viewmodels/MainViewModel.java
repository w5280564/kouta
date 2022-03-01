package com.xunda.mo.main.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.hx.common.db.DemoDbHelper;
import com.xunda.mo.hx.common.db.dao.InviteMessageDao;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;
import com.xunda.mo.hx.common.livedatas.SingleSourceLiveData;


public class MainViewModel extends AndroidViewModel {
    private InviteMessageDao inviteMessageDao;
    private SingleSourceLiveData<Integer> switchObservable;
    private MutableLiveData<Integer> homeUnReadObservable;

    public MainViewModel(@NonNull Application application) {
        super(application);
        switchObservable = new SingleSourceLiveData<>();
        inviteMessageDao = DemoDbHelper.getInstance(application).getInviteMessageDao();
        homeUnReadObservable = new MutableLiveData<>();
    }

    public LiveData<Integer> getSwitchObservable() {
        return switchObservable;
    }

    /**
     * 设置可见的fragment
     * @param title
     */
    public void setVisibleFragment(Integer title) {
        switchObservable.setValue(title);
    }

    public LiveData<Integer> homeUnReadObservable() {
        return homeUnReadObservable;
    }

    public LiveDataBus messageChangeObservable() {
        return LiveDataBus.get();
    }

    public void checkUnreadMsg() {
        int unreadCount = 0;
        if(inviteMessageDao != null) {
            unreadCount = inviteMessageDao.queryUnreadCount();
        }
        int unreadMessageCount = DemoHelper.getInstance().getChatManager().getUnreadMessageCount();
        homeUnReadObservable.postValue(unreadCount + unreadMessageCount);
    }


}
