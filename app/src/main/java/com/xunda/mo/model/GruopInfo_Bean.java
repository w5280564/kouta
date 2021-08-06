package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GruopInfo_Bean implements Serializable {


    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO implements Serializable{
        @JsonProperty("createTime")
        private Long createTime;
        @JsonProperty("findWay")
        private Integer findWay;
        @JsonProperty("groupAddr")
        private String groupAddr;
        @JsonProperty("groupAreaCode")
        private Integer groupAreaCode;
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
        @JsonProperty("groupNotice")
        private String groupNotice;
        @JsonProperty("groupNum")
        private Integer groupNum;
        @JsonProperty("identity")
        private Integer identity;
        @JsonProperty("isAllMute")
        private Integer isAllMute;
        @JsonProperty("isAnonymous")
        private Integer isAnonymous;
        @JsonProperty("isDisturb")
        private Integer isDisturb;
        @JsonProperty("isMute")
        private Integer isMute;
        @JsonProperty("isProtect")
        private Integer isProtect;
        @JsonProperty("isPush")
        private Integer isPush;
        @JsonProperty("isSave")
        private Integer isSave;
        @JsonProperty("joinWay")
        private Integer joinWay;
        @JsonProperty("lat")
        private String lat;
        @JsonProperty("lng")
        private String lng;
        @JsonProperty("maxManagerCount")
        private Integer maxManagerCount;
        @JsonProperty("maxUserCount")
        private Integer maxUserCount;
        @JsonProperty("myNickname")
        private String myNickname;
        @JsonProperty("tag")
        private String tag;
    }
}
