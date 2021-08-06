package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GroupBlackList_Bean {
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("data")
    private List<DataDTO> data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("headImg")
        private String headImg;
        @JsonProperty("nickname")
        private String nickname;
        @JsonProperty("userId")
        private String userId;
        @JsonProperty("userNum")
        private Integer userNum;
    }
}
