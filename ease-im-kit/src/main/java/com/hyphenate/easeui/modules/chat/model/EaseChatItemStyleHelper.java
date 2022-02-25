package com.hyphenate.easeui.modules.chat.model;


import android.graphics.drawable.Drawable;

public class EaseChatItemStyleHelper {
    private static EaseChatItemStyleHelper instance;
    private EaseChatSetStyle style;

    private EaseChatItemStyleHelper(){
        style = new EaseChatSetStyle();
        style.setShowAvatar(true);
        style.setShowNickname(false);
    }

    public static EaseChatItemStyleHelper getInstance() {
        if(instance == null) {
            synchronized (EaseChatItemStyleHelper.class) {
                if(instance == null) {
                    instance = new EaseChatItemStyleHelper();
                }
            }
        }
        return instance;
    }

    public EaseChatSetStyle getStyle() {
        return style;
    }

    public void clear() {
        style = null;
        instance = null;
    }

    public void setAvatarSize(float avatarSize) {
        if (style==null) {
            return;
        }
        style.setAvatarSize(avatarSize);
    }

    public void setShapeType(int shapeType) {
        if (style==null) {
            return;
        }
        style.setShapeType(shapeType);
    }

    public void setAvatarRadius(float avatarRadius) {
        if (style==null) {
            return;
        }
        style.setAvatarRadius(avatarRadius);
    }

    public void setBorderWidth(float borderWidth) {
        if (style==null) {
            return;
        }
        style.setBorderWidth(borderWidth);
    }

    public void setBorderColor(int borderColor) {
        if (style==null) {
            return;
        }
        style.setBorderColor(borderColor);
    }

    public void setItemHeight(float itemHeight) {
        if (style==null) {
            return;
        }
        style.setItemHeight(itemHeight);
    }

    public void setBgDrawable(Drawable bgDrawable) {
        if (style==null) {
            return;
        }
        style.setBgDrawable(bgDrawable);
    }

    public void setTextSize(int textSize) {
        if (style==null) {
            return;
        }
        style.setTextSize(textSize);
    }

    public void setTextColor(int textColor) {
        if (style==null) {
            return;
        }
        style.setTextColor(textColor);
    }

    public void setItemMinHeight(int itemMinHeight) {
        if (style==null) {
            return;
        }
        style.setItemMinHeight(itemMinHeight);
    }

    public void setTimeTextSize(int timeTextSize) {
        if (style==null) {
            return;
        }
        style.setTimeTextSize(timeTextSize);
    }

    public void setTimeTextColor(int timeTextColor) {
        if (style==null) {
            return;
        }
        style.setTimeTextColor(timeTextColor);
    }

    public void setTimeBgDrawable(Drawable timeBgDrawable) {
        if (style==null) {
            return;
        }
        style.setTimeBgDrawable(timeBgDrawable);
    }

    public void setAvatarDefaultSrc(Drawable avatarDefaultSrc) {
        if (style==null) {
            return;
        }
        style.setAvatarDefaultSrc(avatarDefaultSrc);
    }

    public void setShowNickname(boolean showNickname) {
        if (style==null) {
            return;
        }
        style.setShowNickname(showNickname);
    }


    public void setReceiverBgDrawable(Drawable receiverBgDrawable) {
        if (style==null) {
            return;
        }
        style.setReceiverBgDrawable(receiverBgDrawable);
    }

    public void setSenderBgDrawable(Drawable senderBgDrawable) {
        if (style==null) {
            return;
        }
        style.setSenderBgDrawable(senderBgDrawable);
    }

    public void setItemShowType(int itemShowType) {
        if (style==null) {
            return;
        }
        style.setItemShowType(itemShowType);
    }
}

