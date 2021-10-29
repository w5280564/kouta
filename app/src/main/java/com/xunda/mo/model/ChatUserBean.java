package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonNaming
public class ChatUserBean implements Serializable {
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Long code;
    @JsonProperty("data")
    private Friend_Details_Bean.DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO implements Serializable{
        @JsonProperty("fireType")
        private String fireType;
        @JsonProperty("grade")
        private Double grade;
        @JsonProperty("headImg")
        private String headImg;
        @JsonProperty("hxUserName")
        private String hxUserName;
        @JsonProperty("lightStatus")
        private Long lightStatus;
        @JsonProperty("nickname")
        private String nickname;
        @JsonProperty("remarkName")
        private String remarkName;
        @JsonProperty("signature")
        private String signature;
        @JsonProperty("source")
        private String source;
        @JsonProperty("tag")
        private String tag;
        @JsonProperty("isFriend")
        private Long isFriend;
        @JsonProperty("teamNames")
        private String teamNames;
        @JsonProperty("userId")
        private String userId;
        @JsonProperty("userNum")
        private Double userNum;
        @JsonProperty("vipType")
        private int vipType;
        @JsonProperty("areaName")
        private String areaName;
        @JsonProperty("friendStatus")
        private String friendStatus;
        @JsonProperty("isSilence")
        private Long isSilence;
    }
}
