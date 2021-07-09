package com.xunda.mo.hx.section.contact.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hyphenate.easeui.domain.EaseUser;
import com.xunda.mo.hx.common.livedatas.SingleSourceLiveData;
import com.xunda.mo.hx.common.net.Resource;
import com.xunda.mo.hx.common.repositories.EMContactManagerRepository;

import java.util.List;

public class ContactListViewModel extends AndroidViewModel {
    private EMContactManagerRepository mRepository;
    private SingleSourceLiveData<Resource<List<EaseUser>>> contactListObservable;

    public ContactListViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMContactManagerRepository();
        contactListObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<List<EaseUser>>> getContactListObservable() {
        return contactListObservable;
    }

    public void getContactList() {
        contactListObservable.setSource(mRepository.getContactList(false));
    }
}
