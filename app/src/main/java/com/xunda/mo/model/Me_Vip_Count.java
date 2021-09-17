package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Me_Vip_Count {

    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("jyCount")
        private Integer jyCount;
        @JsonProperty("tyCount")
        private Integer tyCount;
        @JsonProperty("vipEndTime")
        private Long vipEndTime;
        @JsonProperty("zkCount")
        private Integer zkCount;
    }
}
