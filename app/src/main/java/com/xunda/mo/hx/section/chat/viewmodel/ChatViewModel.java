package com.xunda.mo.hx.section.chat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.xunda.mo.hx.common.livedatas.SingleSourceLiveData;
import com.xunda.mo.hx.common.net.Resource;
import com.xunda.mo.hx.common.repositories.EMChatManagerRepository;
import com.xunda.mo.hx.common.repositories.EMChatRoomManagerRepository;
import com.xunda.mo.hx.section.conversation.viewmodel.ConversationListViewModel;

public class ChatViewModel extends ConversationListViewModel {
    private EMChatRoomManagerRepository chatRoomManagerRepository;
    private EMChatManagerRepository chatManagerRepository;
    private SingleSourceLiveData<Resource<EMChatRoom>> chatRoomObservable;
    private SingleSourceLiveData<Resource<Boolean>> makeConversationReadObservable;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        chatRoomManagerRepository = new EMChatRoomManagerRepository();
        chatManagerRepository = new EMChatManagerRepository();
        chatRoomObservable = new SingleSourceLiveData<>();
        makeConversationReadObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<EMChatRoom>> getChatRoomObservable() {
        return chatRoomObservable;
    }

    public void getChatRoom(String roomId) {
        EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(roomId);
        if(room != null) {
            chatRoomObservable.setSource(new MutableLiveData<>(Resource.success(room)));
        }else {
            chatRoomObservable.setSource(chatRoomManagerRepository.getChatRoomById(roomId));
        }
    }

    public void makeConversationReadByAck(String conversationId) {
        makeConversationReadObservable.setSource(chatManagerRepository.makeConversationReadByAck(conversationId));
    }

    public LiveData<Resource<Boolean>> getMakeConversationReadObservable() {
        return makeConversationReadObservable;
    }

}
