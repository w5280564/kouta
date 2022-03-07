package com.hyphenate.easeui.model;

import android.text.TextUtils;

import com.hyphenate.easeui.constants.EaseConstant;

import java.io.Serializable;

/**
 *
 */
public class EaseEvent implements Serializable {
    public boolean refresh;
    public String event;
    public TYPE type;
    public String message;
    public String message2;

    public EaseEvent() {}

    public EaseEvent(String event, TYPE type, boolean refresh) {
        this.refresh = refresh;
        this.event = event;
        this.type = type;
    }

    public EaseEvent(String event, TYPE type) {
        this.refresh = true;
        this.event = event;
        this.type = type;
    }

    public static EaseEvent create(String event, TYPE type) {
        return new EaseEvent(event, type);
    }

    public static EaseEvent create(String event, TYPE type, String message) {
        EaseEvent easeEvent = new EaseEvent(event, type);
        easeEvent.message = message;
        return easeEvent;
    }

    public static EaseEvent create(String event, TYPE type, String message,String message2) {
        EaseEvent easeEvent = new EaseEvent(event, type);
        easeEvent.message = message;
        easeEvent.message2 = message2;
        return easeEvent;
    }

    public static EaseEvent create(String event, TYPE type, boolean refresh) {
        return new EaseEvent(event, type, refresh);
    }

    public boolean isMessageChange() {
        return type == TYPE.MESSAGE;
    }

    public boolean isGroupChange() {
        return type == TYPE.GROUP;
    }

    public boolean isGroupAdminHostChange() {
        return type == TYPE.GROUP_ADMIN_HOST_CHANGE;
    }

    public boolean isGroupLeave() {
        return type == TYPE.GROUP_LEAVE || TextUtils.equals(event, EaseConstant.GROUP_LEAVE);
    }


    public boolean isContactChange() {
        return type == TYPE.CONTACT;
    }

    public boolean isNotifyChange() {
        return type == TYPE.NOTIFY;
    }

    public boolean isAccountChange() {
        return type == TYPE.ACCOUNT;
    }

    public enum TYPE {
        GROUP, GROUP_LEAVE, CONTACT, MESSAGE, NOTIFY, ACCOUNT,GROUP_ADMIN_HOST_CHANGE
    }
}
