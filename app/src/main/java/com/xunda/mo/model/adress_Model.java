package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Data
public class adress_Model {


    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("data")
    private List<DataDTO> data;

    @NoArgsConstructor
    @Data
    @NonNull
    public static class DataDTO {
        @JsonProperty("ciphertext")
        private String ciphertext;
        @JsonProperty("fireType")
        private Integer fireType;
        @JsonProperty("headImg")
        private String headImg;
        @JsonProperty("hxUserName")
        private String hxUserName;
        @JsonProperty("isCiphertext")
        private Integer isCiphertext;
        @JsonProperty("isOnline")
        private Integer isOnline;
        @JsonProperty("lightStatus")
        private Integer lightStatus;
        @JsonProperty("nikeName")
        private String nikeName;
        @JsonProperty("onLineTime")
        private Long onLineTime;
        @JsonProperty("onlineStatus")
        private String onlineStatus;
        @JsonProperty("remarkName")
        private String remarkName;
        @JsonProperty("userId")
        private String userId;
        @JsonProperty("userNum")
        private Integer userNum;
        @JsonProperty("vipType")
        private Integer vipType;

    }
}
