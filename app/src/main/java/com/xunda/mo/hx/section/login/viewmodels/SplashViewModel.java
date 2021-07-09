package com.xunda.mo.hx.section.login.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.xunda.mo.hx.common.net.Resource;
import com.xunda.mo.hx.common.repositories.EMClientRepository;


public class SplashViewModel extends AndroidViewModel {
    private EMClientRepository mRepository;

    public SplashViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMClientRepository();
    }

    public LiveData<Resource<Boolean>> getLoginData() {
        return mRepository.loadAllInfoFromHX();
    }
}
