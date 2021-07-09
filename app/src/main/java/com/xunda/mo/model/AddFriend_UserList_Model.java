package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


@lombok.NoArgsConstructor
@lombok.Data
public class AddFriend_UserList_Model {


    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("data")
    private List<DataDTO> data;

    @lombok.NoArgsConstructor
    @lombok.Data
    public static class DataDTO {
        @JsonProperty("groupList")
        private List<GroupListDTO> groupList;
        @JsonProperty("userList")
        private List<UserListDTO> userList;
        @JsonProperty("userType")
        private String userType;

        @lombok.NoArgsConstructor
        @lombok.Data
        public static class GroupListDTO {
            @JsonProperty("count")
            private Integer count;
            @JsonProperty("createTime")
            private Long createTime;
            @JsonProperty("groupHeadImg")
            private String groupHeadImg;
            @JsonProperty("groupId")
            private String groupId;
            @JsonProperty("groupIntroduction")
            private String groupIntroduction;
            @JsonProperty("groupName")
            private String groupName;
            @JsonProperty("groupNum")
            private Integer groupNum;
            @JsonProperty("joinWay")
            private Integer joinWay;
            @JsonProperty("tag")
            private String tag;
        }

        @lombok.NoArgsConstructor
        @lombok.Data
        public static class UserListDTO {
            @JsonProperty("age")
            private Integer age;
            @JsonProperty("areaName")
            private String areaName;
            @JsonProperty("grade")
            private Integer grade;
            @JsonProperty("headImg")
            private String headImg;
            @JsonProperty("lightStatus")
            private Integer lightStatus;
            @JsonProperty("nikeName")
            private String nikeName;
            @JsonProperty("sex")
            private Integer sex;
            @JsonProperty("signature")
            private String signature;
            @JsonProperty("tag")
            private String tag;
            @JsonProperty("userId")
            private String userId;
            @JsonProperty("userNum")
            private Integer userNum;
            @JsonProperty("vipType")
            private Integer vipType;
        }
    }
}
