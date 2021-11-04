package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GruopAddOrRemove_Bean implements Serializable {
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("groupHeadImg")
        private String groupHeadImg;
        @JsonProperty("groupHxId")
        private String groupHxId;
        @JsonProperty("groupName")
        private String groupName;
    }
}
