package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Data
public class AddFriend_FriendList_Model {

    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Long code;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("endRow")
        private Long endRow;
        @JsonProperty("hasNextPage")
        private Boolean hasNextPage;
        @JsonProperty("hasPreviousPage")
        private Boolean hasPreviousPage;
        @JsonProperty("isFirstPage")
        private Boolean isFirstPage;
        @JsonProperty("isLastPage")
        private Boolean isLastPage;
        @JsonProperty("list")
        private List<ListDTO> list;
        @JsonProperty("navigateFirstPage")
        private Long navigateFirstPage;
        @JsonProperty("navigateLastPage")
        private Long navigateLastPage;
        @JsonProperty("navigatePages")
        private Double navigatePages;
        @JsonProperty("navigatepageNums")
        private List<Double> navigatepageNums;
        @JsonProperty("nextPage")
        private Double nextPage;
        @JsonProperty("pageNum")
        private Double pageNum;
        @JsonProperty("pageSize")
        private Long pageSize;
        @JsonProperty("pages")
        private Long pages;
        @JsonProperty("prePage")
        private Long prePage;
        @JsonProperty("size")
        private Long size;
        @JsonProperty("startRow")
        private Double startRow;
        @JsonProperty("total")
        private Long total;

        @NoArgsConstructor
        @Data
        @NonNull
        public static class ListDTO {
            @JsonProperty("age")
            private Double age;
            @JsonProperty("grade")
            private Long grade;
            @JsonProperty("headImg")
            private String headImg;
            @JsonProperty("lightStatus")
            private Long lightStatus;
            @JsonProperty("nickname")
            private String nickname;
            @JsonProperty("sex")
            private Long sex;
            @JsonProperty("signature")
            private String signature;
            @JsonProperty("tag")
            private String tag;
            @JsonProperty("userId")
            private String userId;
            @JsonProperty("userNum")
            private Long userNum;
            @JsonProperty("vipType")
            private Double vipType;
            @JsonProperty("areaName")
            private String areaName;
        }
    }
}
