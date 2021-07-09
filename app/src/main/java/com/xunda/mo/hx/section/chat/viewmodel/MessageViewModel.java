package com.xunda.mo.hx.section.chat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.hyphenate.easeui.model.EaseEvent;
import com.xunda.mo.hx.common.livedatas.LiveDataBus;

public class MessageViewModel extends AndroidViewModel {
    private LiveDataBus messageObservable;

    public MessageViewModel(@NonNull Application application) {
        super(application);
        messageObservable = LiveDataBus.get();
    }

    public void setMessageChange(EaseEvent change) {
        messageObservable.with(change.event).postValue(change);
    }

    public LiveDataBus getMessageChange() {
        return messageObservable;
    }

}
