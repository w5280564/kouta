package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GroupMember_Bean {

    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Long code;
    @JsonProperty("data")
    private List<DataDTO> data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("headImg")
        private String headImg;
        @JsonProperty("identity")
        private Double identity;
        @JsonProperty("nikeName")
        private String nikeName;
        @JsonProperty("userId")
        private String userId;
        @JsonProperty("vipType")
        private Long vipType;
    }
}
