package com.hyphenate.easeui.utils;

import android.text.TextUtils;

import com.hyphenate.easeui.model.ConversationExtFieldBean;


public class MyEaseCommonUtils {



    /**
     * 判断是否是时间戳置顶
     * @param extField
     * @return
     */
    public static boolean isTimestamp(String extField) {
        if(TextUtils.isEmpty(extField)) {
            return false;
        }
        long timestamp = 0L;
        ConversationExtFieldBean obj = GsonUtil.getInstance().json2Bean(extField, ConversationExtFieldBean.class);
        if (obj!=null) {
            boolean isInsertMessageTop = obj.isInsertMessageTop();//1 置顶
            if (isInsertMessageTop) {
                timestamp = obj.getTopTimeMillis();//1 置顶时间戳

            }
        }
        obj = null;
        return timestamp > 0;
    }


    /**
     * 判断是否保存好友或社区了会话信息
     * @param extField
     * @return
     */
    public static boolean isInsertGroupOrFriendInfo(String extField) {
        if(TextUtils.isEmpty(extField)) {
            return false;
        }
        boolean isInsertGroupOrFriendInfo = false;
        ConversationExtFieldBean obj = GsonUtil.getInstance().json2Bean(extField, ConversationExtFieldBean.class);
        if (obj!=null) {
            isInsertGroupOrFriendInfo = obj.isInsertGroupOrFriendInfo();//1 置顶

        }
        obj = null;
        return isInsertGroupOrFriendInfo;
    }


}
