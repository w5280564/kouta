package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserDetail_Bean implements Serializable {

    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("age")
        private Integer age;
        @JsonProperty("areaCode")
        private String areaCode;
        @JsonProperty("areaName")
        private String areaName;
        @JsonProperty("birthday")
        private String birthday;
        @JsonProperty("createTime")
        private Long createTime;
        @JsonProperty("deletePassword")
        private String deletePassword;
        @JsonProperty("deleteStatus")
        private Integer deleteStatus;
        @JsonProperty("grade")
        private Integer grade;
        @JsonProperty("headImg")
        private String headImg;
        @JsonProperty("hxUserName")
        private String hxUserName;
        @JsonProperty("inPassword")
        private String inPassword;
        @JsonProperty("isHasLockPassword")
        private Integer isHasLockPassword;
        @JsonProperty("isHasLoginPassword")
        private Integer isHasLoginPassword;
        @JsonProperty("isNeedUnlock")
        private Integer isNeedUnlock;
        @JsonProperty("isQuestion")
        private Integer isQuestion;
        @JsonProperty("lightStatus")
        private Integer lightStatus;
        @JsonProperty("mailbox")
        private String mailbox;
        @JsonProperty("nickname")
        private String nickname;
        @JsonProperty("phoneNum")
        private String phoneNum;
        @JsonProperty("sex")
        private Integer sex;
        @JsonProperty("signature")
        private String signature;
        @JsonProperty("tag")
        private String tag;
        @JsonProperty("token")
        private String token;
        @JsonProperty("unlockType")
        private Integer unlockType;
        @JsonProperty("userId")
        private String userId;
        @JsonProperty("userNum")
        private Integer userNum;
        @JsonProperty("vipType")
        private Integer vipType;
    }
}
