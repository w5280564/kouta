package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GruopInfo_Bean {

    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Double code;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("createTime")
        private Long createTime;
        @JsonProperty("findWay")
        private Double findWay;
        @JsonProperty("groupAddr")
        private String groupAddr;
        @JsonProperty("groupAreaCode")
        private Long groupAreaCode;
        @JsonProperty("groupHeadImg")
        private String groupHeadImg;
        @JsonProperty("groupHxId")
        private String groupHxId;
        @JsonProperty("groupId")
        private String groupId;
        @JsonProperty("groupIntroduction")
        private String groupIntroduction;
        @JsonProperty("groupName")
        private String groupName;
        @JsonProperty("groupNum")
        private Long groupNum;
        @JsonProperty("identity")
        private Double identity;
        @JsonProperty("isAllMute")
        private Long isAllMute;
        @JsonProperty("isAnonymous")
        private Long isAnonymous;
        @JsonProperty("isDissolve")
        private Double isDissolve;
        @JsonProperty("isMute")
        private Double isMute;
        @JsonProperty("isProtect")
        private Long isProtect;
        @JsonProperty("isSave")
        private Double isSave;
        @JsonProperty("joinWay")
        private Double joinWay;
        @JsonProperty("lat")
        private String lat;
        @JsonProperty("lng")
        private String lng;
        @JsonProperty("maxUserCount")
        private Double maxUserCount;
        @JsonProperty("myNikeName")
        private String myNikeName;
        @JsonProperty("tag")
        private String tag;
    }
}
