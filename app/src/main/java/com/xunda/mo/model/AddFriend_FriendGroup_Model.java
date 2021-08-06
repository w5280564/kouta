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
    private Integer code;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("endRow")
        private Integer endRow;
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
        private Integer navigateFirstPage;
        @JsonProperty("navigateLastPage")
        private Integer navigateLastPage;
        @JsonProperty("navigatePages")
        private Integer navigatePages;
        @JsonProperty("navigatepageNums")
        private List<Integer> navigatepageNums;
        @JsonProperty("nextPage")
        private Integer nextPage;
        @JsonProperty("pageNum")
        private Integer pageNum;
        @JsonProperty("pageSize")
        private Integer pageSize;
        @JsonProperty("pages")
        private Integer pages;
        @JsonProperty("prePage")
        private Integer prePage;
        @JsonProperty("size")
        private Integer size;
        @JsonProperty("startRow")
        private Integer startRow;
        @JsonProperty("total")
        private Integer total;

        @NoArgsConstructor
        @Data
        public static class ListDTO {
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
    }
}
