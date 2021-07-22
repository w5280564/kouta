package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class createGroup_Bean {

    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Long code;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("createTime")
        private Long createTime;
        @JsonProperty("findWay")
        private Long findWay;
        @JsonProperty("groupAreaId")
        private Double groupAreaId;
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
        @JsonProperty("isAllClose")
        private Long isAllClose;
        @JsonProperty("isAnonymous")
        private Long isAnonymous;
        @JsonProperty("isDissolve")
        private Long isDissolve;
        @JsonProperty("isProtect")
        private Long isProtect;
        @JsonProperty("joinWay")
        private Long joinWay;
        @JsonProperty("maxUserCount")
        private Double maxUserCount;
        @JsonProperty("tag")
        private String tag;
        @JsonProperty("updateTime")
        private Double updateTime;
    }
}
