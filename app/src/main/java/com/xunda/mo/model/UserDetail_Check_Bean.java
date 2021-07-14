package com.xunda.mo.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserDetail_Check_Bean {


    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Long code;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("isUpdateHead")
        private Double isUpdateHead;
        @JsonProperty("isUpdateNickName")
        private Long isUpdateNickName;
        @JsonProperty("isUpdateSex")
        private Long isUpdateSex;
    }
}
