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
        private Long navigatePages;
        @JsonProperty("navigatepageNums")
        private List<Long> navigatepageNums;
        @JsonProperty("nextPage")
        private Long nextPage;
        @JsonProperty("pageNum")
        private Double pageNum;
        @JsonProperty("pageSize")
        private Double pageSize;
        @JsonProperty("pages")
        private Long pages;
        @JsonProperty("prePage")
        private Double prePage;
        @JsonProperty("size")
        private Long size;
        @JsonProperty("startRow")
        private Double startRow;
        @JsonProperty("total")
        private Long total;

        @NoArgsConstructor
        @Data
        public static class ListDTO {
            @JsonProperty("applyStatus")
            private Long applyStatus;
            @JsonProperty("friendApplyId")
            private String friendApplyId;
            @JsonProperty("headImg")
            private String headImg;
            @JsonProperty("lightStatus")
            private Double lightStatus;
            @JsonProperty("nickname")
            private String nickname;
            @JsonProperty("remark")
            private String remark;
            @JsonProperty("source")
            private String source;
            @JsonProperty("updateTime")
            private Long updateTime;
            @JsonProperty("userNum")
            private Double userNum;
            @JsonProperty("vipType")
            private Double vipType;
        }
    }
}
