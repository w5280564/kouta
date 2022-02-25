package com.xunda.mo.model;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonNaming
public class ChatUserBean implements Serializable {

    @SerializedName("msg")
    private String msg;
    @SerializedName("code")
    private Integer code;
    @SerializedName("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @SerializedName("areaName")
        private String areaName;
        @SerializedName("deleteStatus")
        private Integer deleteStatus;
        @SerializedName("fireType")
        private Integer fireType;
        @SerializedName("friendStatus")
        private Integer friendStatus;
        @SerializedName("grade")
        private Integer grade;
        @SerializedName("headImg")
        private String headImg;
        @SerializedName("hxUserName")
        private String hxUserName;
        @SerializedName("isFriend")
        private Integer isFriend;
        @SerializedName("isOnline")
        private Integer isOnline;
        @SerializedName("isSilence")
        private Integer isSilence;
        @SerializedName("lightStatus")
        private Integer lightStatus;
        @SerializedName("nickname")
        private String nickname;
        @SerializedName("onlineStatus")
        private String onlineStatus;
        @SerializedName("remarkName")
        private String remarkName;
        @SerializedName("signature")
        private String signature;
        @SerializedName("source")
        private String source;
        @SerializedName("tag")
        private String tag;
        @SerializedName("teamNames")
        private String teamNames;
        @SerializedName("userId")
        private String userId;
        @SerializedName("userNum")
        private Integer userNum;
        @SerializedName("vipType")
        private Integer vipType;
    }
}
