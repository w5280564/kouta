package com.xunda.mo.main.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.hyphenate.easeui.domain.EaseUser;
import com.xunda.mo.hx.common.net.Resource;
import com.xunda.mo.hx.common.repositories.EMClientRepository;

public class LoginViewModel extends AndroidViewModel {
    private EMClientRepository mRepository;
    private MediatorLiveData<Resource<EaseUser>> loginObservable;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMClientRepository();
        loginObservable = new MediatorLiveData<>();
    }

    /**
     * 登录环信
     * @param userName
     * @param pwd
     * @param isTokenFlag
     */
    public void login(String userName, String pwd, boolean isTokenFlag) {

        loginObservable.addSource(mRepository.loginToServer(userName, pwd, isTokenFlag), response -> {
            loginObservable.setValue(response);
        });
    }

    public LiveData<Resource<EaseUser>> getLoginObservable() {
        return loginObservable;
    }
}
