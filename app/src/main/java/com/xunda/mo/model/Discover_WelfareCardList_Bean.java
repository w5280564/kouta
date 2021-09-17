package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Discover_WelfareCardList_Bean {
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("integral")
        private Integer integral;
        @JsonProperty("exchangeCardVos")
        private List<ExchangeCardVosDTO> exchangeCardVos;

        @NoArgsConstructor
        @Data
        public static class ExchangeCardVosDTO {
            @JsonProperty("cardName")
            private String cardName;
            @JsonProperty("requiredPoints")
            private Integer requiredPoints;
            @JsonProperty("rules")
            private Double rules;
            @JsonProperty("status")
            private Integer status;
            @JsonProperty("systemCardId")
            private String systemCardId;
            @JsonProperty("type")
            private Integer type;
        }
    }
}
