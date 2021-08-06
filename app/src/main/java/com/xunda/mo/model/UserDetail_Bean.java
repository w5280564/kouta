package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserDetail_Bean {

    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Long code;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("age")
        private Long age;
        @JsonProperty("areaCode")
        private String areaCode;
        @JsonProperty("areaName")
        private String areaName;
        @JsonProperty("birthday")
        private String birthday;
        @JsonProperty("deleteStatus")
        private Long deleteStatus;
        @JsonProperty("experience")
        private Long experience;
        @JsonProperty("grade")
        private Double grade;
        @JsonProperty("headImg")
        private String headImg;
        @JsonProperty("hxUserName")
        private String hxUserName;
        @JsonProperty("isNeedUnlock")
        private Long isNeedUnlock;
        @JsonProperty("lightStatus")
        private Long lightStatus;
        @JsonProperty("mailbox")
        private String mailbox;
        @JsonProperty("nickname")
        private String nickname;
        @JsonProperty("phoneNum")
        private String phoneNum;
        @JsonProperty("sex")
        private Double sex;
        @JsonProperty("signature")
        private String signature;
        @JsonProperty("tag")
        private String tag;
        @JsonProperty("userId")
        private String userId;
        @JsonProperty("userNum")
        private Double userNum;
        @JsonProperty("vipType")
        private Long vipType;
    }
}
