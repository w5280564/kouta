package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Olduser_Model {


    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Double code;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("age")
        private Long age;
        @JsonProperty("areaId")
        private Long areaId;
        @JsonProperty("birthday")
        private String birthday;
        @JsonProperty("createTime")
        private Double createTime;
        @JsonProperty("deleteStatus")
        private Long deleteStatus;
        @JsonProperty("experience")
        private Long experience;
        @JsonProperty("grade")
        private Long grade;
        @JsonProperty("headImg")
        private String headImg;
        @JsonProperty("hxUserName")
        private String hxUserName;
        @JsonProperty("lightStatus")
        private Double lightStatus;
        @JsonProperty("nikeName")
        private String nikeName;
        @JsonProperty("phoneNum")
        private String phoneNum;
        @JsonProperty("sex")
        private Long sex;
        @JsonProperty("signature")
        private String signature;
        @JsonProperty("token")
        private String token;
        @JsonProperty("userId")
        private String userId;
        @JsonProperty("userNum")
        private Double userNum;
        @JsonProperty("vipType")
        private Long vipType;
        @JsonProperty("areaName")
        private String areaName;
        @JsonProperty("mailbox")
        private String mailbox;
        @JsonProperty("tag")
        private String tag;
    }

}
