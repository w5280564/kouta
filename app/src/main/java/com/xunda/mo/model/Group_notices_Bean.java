package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Group_notices_Bean {

    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("pages")
    private Integer pages;
    @JsonProperty("data")
    private List<DataDTO> data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("applyStatus")
        private Integer applyStatus;
        @JsonProperty("createTime")
        private Long createTime;
        @JsonProperty("disposeUserId")
        private String disposeUserId;
        @JsonProperty("event")
        private String event;
        @JsonProperty("groupApplyId")
        private String groupApplyId;
        @JsonProperty("groupId")
        private String groupId;
        @JsonProperty("headImg")
        private String headImg;
        @JsonProperty("notifyType")
        private Integer notifyType;
        @JsonProperty("remark")
        private String remark;
        @JsonProperty("titleName")
        private String titleName;
    }
}
