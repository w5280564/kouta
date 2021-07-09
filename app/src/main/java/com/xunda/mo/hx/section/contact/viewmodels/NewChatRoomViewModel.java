package com.xunda.mo.hx.section.contact.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hyphenate.chat.EMChatRoom;
import com.xunda.mo.hx.common.livedatas.SingleSourceLiveData;
import com.xunda.mo.hx.common.net.Resource;
import com.xunda.mo.hx.common.repositories.EMChatRoomManagerRepository;

import java.util.List;

public class NewChatRoomViewModel extends AndroidViewModel {
    private EMChatRoomManagerRepository repository;
    private SingleSourceLiveData<Resource<EMChatRoom>> chatRoomObservable;
    public NewChatRoomViewModel(@NonNull Application application) {
        super(application);
        repository = new EMChatRoomManagerRepository();
        chatRoomObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<EMChatRoom>> chatRoomObservable() {
        return chatRoomObservable;
    }

    public void createChatRoom(String subject, String description, String welcomeMessage,
                               int maxUserCount, List<String> members) {
        chatRoomObservable.setSource(repository.createChatRoom(subject, description, welcomeMessage, maxUserCount, members));
    }
}
