package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GroupMemberAdd_Bean implements Serializable{

    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("data")
    private List<DataDTO> data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("ciphertext")
        private String ciphertext;
        @JsonProperty("fireType")
        private Integer fireType;
        @JsonProperty("headImg")
        private String headImg;
        @JsonProperty("hxUserName")
        private String hxUserName;
        @JsonProperty("isCiphertext")
        private Integer isCiphertext;
        @JsonProperty("isOnline")
        private Integer isOnline;
        @JsonProperty("lightStatus")
        private Integer lightStatus;
        @JsonProperty("nickname")
        private String nickname;
        @JsonProperty("onLineTime")
        private Integer onLineTime;
        @JsonProperty("onlineStatus")
        private String onlineStatus;
        @JsonProperty("remarkName")
        private String remarkName;
        @JsonProperty("userId")
        private String userId;
        @JsonProperty("userNum")
        private Integer userNum;
        @JsonProperty("vipType")
        private Integer vipType;
    }
}
