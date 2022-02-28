package com.hyphenate.easeui.model;

public class ConversationExtFieldBean {

    private boolean isInsertMessageTop;
    private boolean isInsertGroupOrFriendInfo;
    private long topTimeMillis;
    private String showImg;
    private String showName;

    public boolean isInsertMessageTop() {
        return isInsertMessageTop;
    }

    public void setInsertMessageTop(boolean insertMessageTop) {
        isInsertMessageTop = insertMessageTop;
    }

    public boolean isInsertGroupOrFriendInfo() {
        return isInsertGroupOrFriendInfo;
    }

    public void setInsertGroupOrFriendInfo(boolean insertGroupOrFriendInfo) {
        isInsertGroupOrFriendInfo = insertGroupOrFriendInfo;
    }

    public long getTopTimeMillis() {
        return topTimeMillis;
    }

    public void setTopTimeMillis(long topTimeMillis) {
        this.topTimeMillis = topTimeMillis;
    }

    public String getShowImg() {
        return showImg;
    }

    public void setShowImg(String showImg) {
        this.showImg = showImg;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }
}
