package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CardFragment_Bean {

    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("data")
    private List<DataDTO> data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("cardName")
        private String cardName;
        @JsonProperty("expirationDate")
        private Long expirationDate;
        @JsonProperty("rules")
        private Double rules;
        @JsonProperty("status")
        private Integer status;
        @JsonProperty("systemCardId")
        private String systemCardId;
        @JsonProperty("type")
        private Integer type;
        @JsonProperty("useTime")
        private Object useTime;
        @JsonProperty("userCardId")
        private String userCardId;
    }
}
