package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Security_QuestionList_Model {
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("data")
    private List<DataDTO> data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("createTime")
        private Long createTime;
        @JsonProperty("question")
        private String question;
        @JsonProperty("sysQuestionId")
        private Integer sysQuestionId;
        @JsonProperty("updateTime")
        private Object updateTime;
    }
}
