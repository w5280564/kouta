package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class AddFriend_FriendGroup_Model {


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
        private Double navigateFirstPage;
        @JsonProperty("navigateLastPage")
        private Double navigateLastPage;
        @JsonProperty("navigatePages")
        private Double navigatePages;
        @JsonProperty("navigatepageNums")
        private List<Long> navigatepageNums;
        @JsonProperty("nextPage")
        private Long nextPage;
        @JsonProperty("pageNum")
        private Long pageNum;
        @JsonProperty("pageSize")
        private Long pageSize;
        @JsonProperty("pages")
        private Double pages;
        @JsonProperty("prePage")
        private Long prePage;
        @JsonProperty("size")
        private Long size;
        @JsonProperty("startRow")
        private Long startRow;
        @JsonProperty("total")
        private Double total;

        @NoArgsConstructor
        @Data
        public static class ListDTO {
            @JsonProperty("count")
            private Double count;
            @JsonProperty("createTime")
            private Double createTime;
            @JsonProperty("groupHeadImg")
            private String groupHeadImg;
            @JsonProperty("groupId")
            private String groupId;
            @JsonProperty("groupIntroduction")
            private String groupIntroduction;
            @JsonProperty("groupName")
            private String groupName;
            @JsonProperty("groupNum")
            private Long groupNum;
            @JsonProperty("joinWay")
            private Double joinWay;
            @JsonProperty("tag")
            private String tag;
        }
    }
}
