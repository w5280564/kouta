package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Group_Details_Bean {


    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("areaName")
        private String areaName;
        @JsonProperty("friendStatus")
        private Integer friendStatus;
        @JsonProperty("grade")
        private Integer grade;
        @JsonProperty("groupRemarkName")
        private String groupRemarkName;
        @JsonProperty("groupUserId")
        private String groupUserId;
        @JsonProperty("hxUserName")
        private String hxUserName;
        @JsonProperty("identity")
        private Integer identity;
        @JsonProperty("isBlack")
        private Integer isBlack;
        @JsonProperty("isFriend")
        private Integer isFriend;
        @JsonProperty("isMute")
        private Integer isMute;
        @JsonProperty("loginUserIdentity")
        private Integer loginUserIdentity;
        @JsonProperty("muteEndTime")
        private Long muteEndTime;
        @JsonProperty("nickname")
        private String nickname;
        @JsonProperty("remarkName")
        private String remarkName;
        @JsonProperty("signature")
        private String signature;
        @JsonProperty("tag")
        private String tag;
        @JsonProperty("userHead")
        private String userHead;
        @JsonProperty("userNum")
        private Integer userNum;
        @JsonProperty("vipType")
        private int vipType;
    }
}
