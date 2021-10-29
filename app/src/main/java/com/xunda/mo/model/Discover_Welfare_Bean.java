package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Discover_Welfare_Bean {

    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("isFriday")
        private Integer isFriday;
        @JsonProperty("isMonday")
        private Integer isMonday;
        @JsonProperty("isSaturday")
        private Integer isSaturday;
        @JsonProperty("isSunday")
        private Integer isSunday;
        @JsonProperty("isThursday")
        private Integer isThursday;
        @JsonProperty("isTuesday")
        private Integer isTuesday;
        @JsonProperty("isWednesday")
        private Integer isWednesday;
        @JsonProperty("lastSignTime")
        private Long lastSignTime;
        @JsonProperty("luckChance")
        private Integer luckChance;
        @JsonProperty("todaySign")
        private Integer todaySign;
        @JsonProperty("userId")
        private String userId;
        @JsonProperty("integral")
        private Integer integral;
    }
}
