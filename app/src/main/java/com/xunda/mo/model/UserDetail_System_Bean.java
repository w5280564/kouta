package com.xunda.mo.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserDetail_System_Bean {
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("changeHeadImgDays")
        private Integer changeHeadImgDays;
        @JsonProperty("changeNicknameDays")
        private Integer changeNicknameDays;
        @JsonProperty("configId")
        private Integer configId;
        @JsonProperty("groupFileSize")
        private Integer groupFileSize;
        @JsonProperty("initialGroupManageCount")
        private Integer initialGroupManageCount;
        @JsonProperty("initialGroupUserCount")
        private Integer initialGroupUserCount;
        @JsonProperty("isAllowChange")
        private Integer isAllowChange;
        @JsonProperty("maxBlackCount")
        private Integer maxBlackCount;
        @JsonProperty("maxFriendCount")
        private Integer maxFriendCount;
        @JsonProperty("maxSendFileSize")
        private Integer maxSendFileSize;
        @JsonProperty("maxSendVideoSize")
        private Integer maxSendVideoSize;
        @JsonProperty("maxUserCollectionSize")
        private Integer maxUserCollectionSize;
        @JsonProperty("maxUserGroupCount")
        private Integer maxUserGroupCount;
        @JsonProperty("maxVipCollectionSize")
        private Integer maxVipCollectionSize;
        @JsonProperty("maxVipFriendCount")
        private Integer maxVipFriendCount;
        @JsonProperty("maxVipUserGroupCount")
        private Integer maxVipUserGroupCount;
        @JsonProperty("secondLevelGroupManageCount")
        private Integer secondLevelGroupManageCount;
        @JsonProperty("secondLevelGroupUserCount")
        private Integer secondLevelGroupUserCount;
        @JsonProperty("threeLevelGroupManageCount")
        private Integer threeLevelGroupManageCount;
        @JsonProperty("threeLevelGroupUserCount")
        private Integer threeLevelGroupUserCount;
        @JsonProperty("vipGroupFileSize")
        private Integer vipGroupFileSize;
    }
}
