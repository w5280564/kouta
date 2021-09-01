package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class NewFriend_Bean {

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
            @JsonProperty("applyStatus")
            private Integer applyStatus;
            @JsonProperty("friendApplyId")
            private String friendApplyId;
            @JsonProperty("headImg")
            private String headImg;
            @JsonProperty("hxUserName")
            private String hxUserName;
            @JsonProperty("lightStatus")
            private Integer lightStatus;
            @JsonProperty("nickname")
            private String nickname;
            @JsonProperty("remark")
            private String remark;
            @JsonProperty("source")
            private String source;
            @JsonProperty("updateTime")
            private Long updateTime;
            @JsonProperty("userNum")
            private Integer userNum;
            @JsonProperty("vipType")
            private Integer vipType;
        }
    }
}
